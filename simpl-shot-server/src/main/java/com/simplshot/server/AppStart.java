package com.simplshot.server;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

public class AppStart {

	public static final URI BASE_URI = URI.create("http://localhost:8080/");
    private static final Logger LOGGER = Logger.getLogger(AppStart.class.getName());

    public static void main(String[] args) {
        try {
            System.out.println("Starting Server");
            final ResourceConfig resourceConfig = new ResourceConfig(FileServer.class);
            resourceConfig.registerInstances(new LoggingFilter(LOGGER, true));
            resourceConfig.register(MultiPartFeature.class);
            resourceConfig.register(MyResource.class);
            final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(BASE_URI, resourceConfig);
            System.in.read();
            server.stop();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
}