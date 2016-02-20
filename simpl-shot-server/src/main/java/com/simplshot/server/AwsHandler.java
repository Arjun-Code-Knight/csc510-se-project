package com.simplshot.server;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class AwsHandler {
	
	public static String bucketName;
	private static final Logger LOGGER = Logger.getLogger(AwsHandler.class.getName());
	
	public String uploadFile(String fileLocation, String fileName) throws IOException {
        AmazonS3Client s3client = new AmazonS3Client(new ProfileCredentialsProvider());//changed amazons3 to amazons3client 1st word
        String resourceUrl = new String();
        try {
        	LOGGER.info("Uploading a new object to S3 from a file\n");
            File file = new File(fileLocation);
            s3client.putObject(new PutObjectRequest(
            		                 bucketName, fileName, file).withCannedAcl(CannedAccessControlList.PublicRead));
            System.out.println("Upload Successful");
            resourceUrl = s3client.getResourceUrl(bucketName, fileName);
         } catch (AmazonServiceException ase) {
        	 LOGGER.severe("Caught an AmazonServiceException, which " +
            		"means your request made it " +
                    "to Amazon S3, but was rejected with an error response" +
                    " for some reason.");
        	 LOGGER.severe("Error Message:    " + ase.getMessage());
        	 LOGGER.severe("HTTP Status Code: " + ase.getStatusCode());
        	 LOGGER.severe("AWS Error Code:   " + ase.getErrorCode());
        	 LOGGER.severe("Error Type:       " + ase.getErrorType());
        	 LOGGER.severe("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
        	LOGGER.severe("Caught an AmazonClientException, which " +
            		"means the client encountered " +
                    "an internal error while trying to " +
                    "communicate with S3, " +
                    "such as not being able to access the network.");
        	LOGGER.severe("Error Message: " + ace.getMessage());
        }
        return resourceUrl;
    }

}
