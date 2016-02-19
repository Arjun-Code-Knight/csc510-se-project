package com.simplshot.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

public class FileUploadClient {
	
	private static String IMAGE;
	private static String ENDPOINT;
	private static final String PROPFILE = "simp-shot.properties";
	private static final Logger LOGGER = Logger.getLogger(FileUploadClient.class.getName());
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		FileUploadClient uploadClient = new FileUploadClient();
		uploadClient.loadProperties();
		uploadClient.sendFile();
	}

	public void sendFile()
	{
		File testFile = new File(this.getClass().getClassLoader().getResource(IMAGE).getPath());
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		HttpPost postRequest = new HttpPost(ENDPOINT);
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
	
	public void loadProperties() throws FileNotFoundException, IOException
    {
    	Properties prop = new Properties();
    	File propFile = new File(this.getClass().getClassLoader().getResource(PROPFILE).getPath());
    	prop.load(new FileInputStream(propFile));
    	IMAGE = (String) prop.get("TESTFILENAME");
    	ENDPOINT = (String) prop.get("ENDPOINT");
    }
}

