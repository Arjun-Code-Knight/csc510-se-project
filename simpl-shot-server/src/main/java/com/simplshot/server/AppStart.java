package com.simplshot.server;

import java.io.File;
import java.io.FileInputStream;
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

public class AppStart {

	public static URI BASE_URI;
	public static String UPLOAD_DIR;
	public static final String PROPFILE = "simp-shot.properties";
	public static final Logger LOGGER = Logger.getLogger(AppStart.class.getName());
	
    public static void main(String[] args) {
        try {
            System.out.println("Starting Server");
            loadProperties();
            final ResourceConfig resourceConfig = new ResourceConfig(FileServer.class);
            resourceConfig.registerInstances(new LoggingFilter(LOGGER, true));
            resourceConfig.register(MultiPartFeature.class);
            resourceConfig.register(MyResource.class);
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
	    	File propFile = new File(AppStart.class.getClassLoader().getResource(PROPFILE).getFile());
	    	prop.load(new FileInputStream(propFile));
	    	String baseuri = (String) prop.get("BASE_URI");
	    	BASE_URI = URI.create(baseuri);
	    	UPLOAD_DIR = (String) prop.get("UPLOAD_DIR");
    	}
    	catch(Exception ex)
    	{
    		 LOGGER.log(Level.SEVERE, "Error Initializing the base properties", ex);
    		 ex.printStackTrace();
    		 System.exit(0);
    	}
    }
    
}