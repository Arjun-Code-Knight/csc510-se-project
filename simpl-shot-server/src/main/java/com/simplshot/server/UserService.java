package com.simplshot.server;

import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.simplshot.mongo.MongoUtil;

@Path("user")
public class UserService {

	private static final int ERROR = 500;
	private static final int SUCCESS = 200;
	private static final String SOLUTION1 = "SOLUTION1-DESKTOPWITHOUTSERACH";
	private static final String SOLUTION2 = "SOLUTION2-CHROME";
	private static final String SOLUTION3 = "SOLUTION3-DESKTOPWITHSEARCH";
	private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());
	private MongoUtil mongoUtil = MongoUtil.getInstance();
	
	@GET	
	@Produces(MediaType.TEXT_HTML)
	public Response getUserdetails()
	{
		LOGGER.info("Please pass userid and user/{userid} path");
		return Response.status(200).entity("Please pass userid and user/{id} path").build();
		
	}
	
	@GET	
	@Path("/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserdetails(@PathParam("userId") String userId)
	{
		String responseFromMongo = mongoUtil.getUserDetails(userId,SOLUTION1);
		LOGGER.info("The user Id is "+userId);
		return Response.status(SUCCESS).entity(responseFromMongo).build();
		
	}
	
	@GET	
	@Path("/chrome/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserdetailsForChrome(@PathParam("userId") String userId)
	{
		String responseFromMongo = mongoUtil.getUserDetails(userId,SOLUTION2);
		LOGGER.info("The user Id is "+userId);
		return Response.status(SUCCESS).entity(responseFromMongo).build();
		
	}
	
	
	@GET	
	@Path("/{userId}/{search}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserdetails(@PathParam("userId") String userId,@PathParam("search") String searchparam)
	{
		String responseFromMongo = mongoUtil.getUserDetails(userId,searchparam,SOLUTION3);
		LOGGER.info("The user Id is "+userId);
		return Response.status(SUCCESS).entity(responseFromMongo).build();
		
	}
	
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
