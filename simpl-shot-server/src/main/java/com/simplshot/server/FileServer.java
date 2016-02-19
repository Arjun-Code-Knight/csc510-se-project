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

/*
 * 
 * Exposed to receive multipart file as input
 * 
 */
@Path("send")
public class FileServer {
	
	private static final String DIRECTORY = "E:\\";
	private StringBuffer strBuffer;
	private static final String ERROR = "ERROR";
	private static final String SUCCESS = "SUCCESS";
	private static final Logger LOGGER = Logger.getLogger(FileServer.class.getName());
	
	
	public FileServer()
	{
		 strBuffer = new StringBuffer(DIRECTORY);
	}
	
	@POST
	@Path("file")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.TEXT_PLAIN)
	public Response uploadFile(@FormDataParam("attachment") InputStream fileInputStream,@FormDataParam("attachment") FormDataContentDisposition contentDispositionHeader)
	{
		File directory = new File(DIRECTORY);
		strBuffer.append(contentDispositionHeader.getFileName());
		if(directory.exists() && saveFile(fileInputStream,strBuffer.toString()))
		{
			return Response.status(200).entity(SUCCESS).build();
		}else
		{
			return Response.status(500).entity(ERROR).build();
		}
	}
	

	private boolean saveFile(InputStream inpStream, String outputFile)
	{
		if(inpStream != null && !outputFile.equals(DIRECTORY))
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
	
}
