package com.simplshot.server;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

public class FileServerApplication extends Application{
	
		@Override
		public Set<Class<?>> getClasses() {
			// TODO Auto-generated method stub
			final Set<Class<?>> classes = new HashSet<Class<?>>();
	        // register resources and features
	        classes.add(MultiPartFeature.class);
	        classes.add(FileServer.class);
	        classes.add(MyResource.class);
	        classes.add(UserService.class);
	        classes.add(LoggingFilter.class);
	        return classes;
		}

}
