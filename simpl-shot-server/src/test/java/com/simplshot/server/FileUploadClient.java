package com.simplshot.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
	private static List<String> USERS = new ArrayList<String>(10);
	private static final String PROPFILE = "simp-shot.properties";
	private static final Logger LOGGER = Logger.getLogger(FileUploadClient.class.getName());
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		FileUploadClient uploadClient = new FileUploadClient();
		uploadClient.loadProperties();
		uploadClient.initializeUsers();
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
		fileEntityBuilder.addTextBody("USER", USERS.get((int) (Math.random()%10)));
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
    	prop.load(this.getClass().getClassLoader().getResourceAsStream(PROPFILE));
    	IMAGE = (String) prop.get("TESTFILENAME");
    	ENDPOINT = (String) prop.get("ENDPOINT");
    }
	
	public void initializeUsers()
	{
		USERS.add("USER1");
		USERS.add("USER2");
		USERS.add("USER3");
		USERS.add("USER4");
		USERS.add("USER5");
		USERS.add("USER6");
		USERS.add("USER7");
		USERS.add("USER8");
		USERS.add("USER9");
	}
}

