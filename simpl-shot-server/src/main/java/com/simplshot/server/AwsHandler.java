package com.simplshot.server;

import java.io.File;
import java.io.IOException;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;

public class AwsHandler {
	
	private static String bucketName     = "ankit1590";
	
	public String uploadFile(String fs, String fileName) throws IOException {
        AmazonS3Client s3client = new AmazonS3Client(new ProfileCredentialsProvider());//changed amazons3 to amazons3client 1st word
        String url = new String();
        try {
            System.out.println("Uploading a new object to S3 from a file\n");
            File file = new File(fs);
            PutObjectResult pResult = s3client.putObject(new PutObjectRequest(
            		                 bucketName, fileName, file).withCannedAcl(CannedAccessControlList.PublicRead));
            System.out.println("Upload Successful");
            url = s3client.getResourceUrl(bucketName, fileName);
         } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which " +
            		"means your request made it " +
                    "to Amazon S3, but was rejected with an error response" +
                    " for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which " +
            		"means the client encountered " +
                    "an internal error while trying to " +
                    "communicate with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
        return url;
    }

}
