package com.simplshot.server;

import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.simplshot.mongo.MongoUtil;

@Path("telemetry")
public class TelemetryServcie {
	
	private static final int ERROR = 500;
	private static final int SUCCESS = 200;
	private static final String SOLUTION1 = "SOLUTION1-DESKTOPWITHOUTSERACH";//just history
	private static final String SOLUTION2 = "SOLUTION2-CHROME";//chrome use specific tags
	private static final String SOLUTION3 = "SOLUTION3-DESKTOPWITHSEARCH";// tesseract
	private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());
	private MongoUtil mongoUtil = MongoUtil.getInstance();
	
	/*
	 * 
	 * Get usage statistics
	 * 
	 */
	@GET	
	@Path("/getusagestats/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get()
	{
		String responseFromMongo = mongoUtil.getUsageStatistics();
		LOGGER.info("Get usage statistics");
		return Response.status(SUCCESS).entity(responseFromMongo).build();
	}

}
