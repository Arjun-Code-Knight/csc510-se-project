package com.simplshot.server;

import java.io.IOException;
import java.net.URI;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import com.simplshot.mongo.MongoUtil;

public class AppStart {

	public static URI BASE_URI;
	public static String TESSDATA;
	public static String UPLOAD_DIR;
	public static final String PROPFILE = "simp-shot.properties";
	public static final Logger LOGGER = Logger.getLogger(AppStart.class.getName());
	
    public static void main(String[] args) {
        try {
        	LOGGER.info("Starting Server");
            loadProperties();
            final ResourceConfig resourceConfig = new ResourceConfig(FileServer.class);
            resourceConfig.registerInstances(new LoggingFilter(LOGGER, false));
            resourceConfig.register(MultiPartFeature.class);
            resourceConfig.register(MyResource.class);
            resourceConfig.register(UserService.class);
            final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(BASE_URI, resourceConfig);
            System.in.read();
            server.stop();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error Starting Server", ex);
            ex.printStackTrace();
        }
    }
    
    public static void loadProperties()
    {
    	try{
	    	Properties prop = new Properties();
	    	prop.load(AppStart.class.getClassLoader().getResourceAsStream(PROPFILE));
	    	String baseuri = (String) prop.get("BASE_URI");
	    	BASE_URI = URI.create(baseuri);
	    	UPLOAD_DIR = (String) prop.get("UPLOAD_DIR");
	    	TESSDATA = (String) prop.get("TESSDATA");
	    	AwsHandler.bucketName = (String) prop.get("BUCKETNAME");
	    	MongoUtil.mongoHost = (String) prop.get("MONGOHOST");
	    	MongoUtil.mongoPort = (String) prop.get("MONGOPORT");
	    	MongoUtil.mongoDB = (String) prop.get("MONGODB");
	    	MongoUtil.mongoCollection = (String) prop.get("MONGOCOLLECTION");
	    	MongoUtil.mongoTelemtryCollection = (String) prop.get("MONGOTELEMETRY");
    	}
    	catch(Exception ex)
    	{
    		 LOGGER.log(Level.SEVERE, "Error Initializing the base properties", ex);
    		 ex.printStackTrace();
    		 System.exit(0);
    	}
    }
    
}