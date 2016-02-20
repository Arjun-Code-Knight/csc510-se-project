package com.simplshot.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

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
		System.out.println(userName);
		File directory = new File(AppStart.UPLOAD_DIR);
		strBuffer.append(userName+"\\");
		File userDirectory = new File(strBuffer.toString());
		if(!userDirectory.exists())
			userDirectory.mkdirs();
		strBuffer.append(contentDispositionHeader.getFileName());
		if(directory.exists() && saveFile(fileInputStream,strBuffer.toString()))
		{
			String extracts = performOcrProcessing(strBuffer.toString());/*Need to remove stopwords*/
			if(extracts != null)
				extracts = stripUnwantedCharacters(extracts);
			/*Upload and get link*/
			MongoUtil.getInstance().addLinkToUser(userName,strBuffer.toString(),extracts);
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
