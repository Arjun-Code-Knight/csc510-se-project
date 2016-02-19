package com.simplshot.server;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

public class FileUploadClient {
	
	private static final String IMAGE = "testImg.jpg";
	private static final String BASEURL = "http://localhost:8080/send/file";
	private static final Logger LOGGER = Logger.getLogger(FileUploadClient.class.getName());
	
	public static void main(String[] args) {
		FileUploadClient uploadClient = new FileUploadClient();
		uploadClient.sendFile();
	}

	public void sendFile()
	{
		File testFile = new File(this.getClass().getClassLoader().getResource(IMAGE).getFile());
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		HttpPost postRequest = new HttpPost(BASEURL);
		MultipartEntityBuilder fileEntityBuilder = MultipartEntityBuilder.create();
		FileBody fileBody = new FileBody(testFile);
		fileEntityBuilder.addPart("attachment", fileBody);
		postRequest.setEntity(fileEntityBuilder.build());
		try {
			HttpResponse response = httpClient.execute(postRequest);
			int status = response.getStatusLine().getStatusCode();
			if(status == 200)
			{
				LOGGER.info("Success");
			}else
			{
				LOGGER.info("Failure "+status);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.severe("Error Sending File over Http");
			e.printStackTrace();
		}
	}
}

