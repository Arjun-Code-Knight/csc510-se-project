package com.simplshot.server;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.io.IOUtils;
import org.apache.xmlgraphics.util.io.Base64DecodeStream;
import org.bson.types.ObjectId;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.lowagie.text.pdf.codec.Base64;
import com.simplshot.mongo.MongoUtil;
import com.simplshot.ocr.OcrUtility;

/*
 * 
 * Exposed to receive multipart file as input
 * 
 */
@Path("uploadService")
public class FileServer {
	
	private static final String ERROR = "ERROR";
	private static final String SUCCESS = "SUCCESS";
	private static final Logger LOGGER = Logger.getLogger(FileServer.class.getName());
	private StringBuffer strBuffer;
	
	public FileServer()
	{
		 strBuffer = new StringBuffer(AppStart.UPLOAD_DIR);
	}
	
	@POST
	@Path("file")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.TEXT_PLAIN)
	public Response uploadFile(@FormDataParam("attachment") InputStream fileInputStream,@FormDataParam("attachment") FormDataContentDisposition contentDispositionHeader, @FormDataParam("USER") String userName)
	{
		File directory = new File(AppStart.UPLOAD_DIR);
		strBuffer.append(userName+"\\");
		File userDirectory = new File(strBuffer.toString());
		if(!userDirectory.exists())
			userDirectory.mkdirs();
		if(contentDispositionHeader.getFileName() == null)
			strBuffer.append(""+System.currentTimeMillis());
		else
			strBuffer.append(contentDispositionHeader.getFileName());
		if(directory.exists() && saveFile(fileInputStream,strBuffer.toString()))
		{
			String extracts = performOcrProcessing(strBuffer.toString());/*Need to remove stopwords*/
			if(extracts != null)
				extracts = stripUnwantedCharacters(extracts);
			AwsHandler awsHandler = new AwsHandler();
			ObjectId mongoId = new ObjectId();
			String awsFileUrl;
			try {
				if(contentDispositionHeader.getFileName() == null)
					awsFileUrl = awsHandler.uploadFile(strBuffer.toString(), userName+mongoId+System.currentTimeMillis());	
				else
					awsFileUrl = awsHandler.uploadFile(strBuffer.toString(), userName+mongoId+contentDispositionHeader.getFileName());
			} catch (IOException ex) {
				LOGGER.severe("Error Saving to aws");
				ex.printStackTrace();
				return Response.status(500).entity(ERROR).build();
			}
			/*Upload and get link*/
			MongoUtil.getInstance().addLinkToUser(mongoId,userName,awsFileUrl,extracts);/*url*/
			return Response.status(200).entity(SUCCESS).build();
		}else
		{
			return Response.status(500).entity(ERROR).build();
		}
	}
	
	@POST
	@Path("/chrome/file")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.TEXT_PLAIN)
	public Response uploadFileFromChrome(@FormDataParam("attachment") String fileInputStream,@FormDataParam("attachment") FormDataContentDisposition contentDispositionHeader, @FormDataParam("USER") String userName)
	{
		File directory = new File(AppStart.UPLOAD_DIR);
		strBuffer.append(userName+"\\");
		File userDirectory = new File(strBuffer.toString());
		if(!userDirectory.exists())
			userDirectory.mkdirs();
		strBuffer.append(""+System.currentTimeMillis());
		if(directory.exists() && saveBase64File(fileInputStream,strBuffer.toString()))
		{
			AwsHandler awsHandler = new AwsHandler();
			ObjectId mongoId = new ObjectId();
			String awsFileUrl;
			try {
				awsFileUrl = awsHandler.uploadFile(strBuffer.toString()+".jpg", userName+mongoId);	
			} catch (IOException ex) {
				LOGGER.severe("Error Saving to aws");
				ex.printStackTrace();
				return Response.status(500).entity(ERROR).build();
			}
			/*Upload and get link*/
			MongoUtil.getInstance().addLinkToUser(mongoId,userName,awsFileUrl,"");/*url*/
			return Response.status(200).entity(SUCCESS).build();
		}else
		{
			return Response.status(500).entity(ERROR).build();
		}
	}
	

	private boolean saveFile(InputStream inpStream, String outputFile)
	{
		if(inpStream != null && !outputFile.equals(AppStart.UPLOAD_DIR))
		{
			try {
				FileOutputStream fileOut = new FileOutputStream(outputFile);
				IOUtils.copy(inpStream,fileOut);
				fileOut.flush();
				fileOut.close();
				IOUtils.closeQuietly(inpStream);
				return true;
			} catch (IOException e) {
				LOGGER.severe("Error Saving File to disk");
				e.printStackTrace();
			}
		}
		return false;
	}
	
	private boolean saveBase64File(String inpStream, String outputFile)
	{
		LOGGER.info(inpStream);
		if(inpStream != null && !outputFile.equals(AppStart.UPLOAD_DIR))
		{
			try {
				byte[] imgBytes = Base64.decode(inpStream); 
				FileOutputStream fileOut = new FileOutputStream(outputFile+".jpg");
				fileOut.write(imgBytes);
				fileOut.flush();
				fileOut.close();
				return true;
			} catch (IOException e) {
				LOGGER.severe("Error Saving File to disk");
				e.printStackTrace();
			}
		}
		return false;
	}
	
	private String performOcrProcessing(String filePath)
	{
		return OcrUtility.getInstance().processImage(filePath);
	}
	
	private String stripUnwantedCharacters(String extracts)
	{
		String pattern = "[\\W\\s]";
		extracts = extracts.replaceAll(pattern, "");
		LOGGER.info("Fileterd String "+extracts);
		return extracts;
	}
	
}