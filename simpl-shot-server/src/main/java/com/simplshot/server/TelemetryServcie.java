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
	 * count of which solution is widely used
	 *
	 */
	@GET	
	@Path("/getprivateusagestats/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getprivateStatistics()
	{
		String responseFromMongo = mongoUtil.getUsageStatistics();
		LOGGER.info("Get usage statistics");
		return Response.status(SUCCESS).entity(responseFromMongo).build();
	}
	
	/*
	 * 
	 * Get usage statistics
	 * count of which solution is widely used
	 *
	 */
	@GET	
	@Path("/getxusagestats/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUsageStatistics()
	{
		String responseFromMongo = mongoUtil.getXStatistics();
		LOGGER.info("Get usage statistics");
		return Response.status(SUCCESS).entity(responseFromMongo).build();
	}
	
	/*
	 * 
	 * Get usage statistics
	 * 
	 */
	@GET	
	@Path("/gettagsusagestats/")/*Manually tagging*/
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTagsUsageStats()
	{
		String responseFromMongo = mongoUtil.getTagsUsageTelemetry();
		LOGGER.info("Get tags usage statistics");
		return Response.status(SUCCESS).entity(responseFromMongo).build();
	}

	/*
	 * 
	 * Get usage statistics
	 * 
	 */
	@GET	
	@Path("/getsearchstats/")/*which solution is being used for cross search- pivate search vs Xsearch*/ 
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCrossSearchUsage()
	{
		String responseFromMongo = mongoUtil.getsearchStatistics();
		LOGGER.info("Get usage statistics");
		return Response.status(SUCCESS).entity(responseFromMongo).build();
	}
	
	/* 
	 * Get usage statistics on occupation
	 * 
	 */
	@GET	
	@Path("/getagestats/")/* average age of the suer base*/ 
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAgeUsageStats()
	{
		String responseFromMongo = mongoUtil.getAgeTelemetry();
		LOGGER.info("Get tags usage statistics");
		return Response.status(SUCCESS).entity(responseFromMongo).build();
	}


	/* 
	 * Get usage statistics on age
	 * 
	 */
	@GET	
	@Path("/getoccupationstats/")/*group and give count*/
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOccupationStats()
	{
		String responseFromMongo = mongoUtil.getOccupationTelemetry();
		LOGGER.info("Get tags usage statistics");
		return Response.status(SUCCESS).entity(responseFromMongo).build();
	}

	
	/* 
	 * Get usage statistics on age
	 * 
	 */
	@GET	
	@Path("/getusersatisfcationstats/")/* ratings*/
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserSatisfactionStats()
	{
		String responseFromMongo = mongoUtil.getUserSatisfactionSurvey();
		LOGGER.info("Get tags usage statistics");
		return Response.status(SUCCESS).entity(responseFromMongo).build();
	}
	
	
	/* 
	 * Get usage statistics on age
	 * 
	 */
	@GET	
	@Path("/getusersatisfcationstats/mostlikedsolution/")/* ratings*/
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserSatisfactionStatsmostliked()
	{
		String responseFromMongo = mongoUtil.getUserSatisfactionSurveyLikeSoution();
		LOGGER.info("Get tags usage statistics");
		return Response.status(SUCCESS).entity(responseFromMongo).build();
	}
	
	
	/* 
	 * Get usage statistics on age
	 * 
	 */
	@GET	
	@Path("/getusersatisfcationstats/mostlikedsolutionwithavgage/")/* ratings*/
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserSatisfactionStatswithLikeSoltuionAvgAge()
	{
		String responseFromMongo = mongoUtil.getUserSatisfactionSurveywithAvgAge();
		LOGGER.info("Get tags usage statistics");
		return Response.status(SUCCESS).entity(responseFromMongo).build();
	}
	
	/* 
	 * Get usage statistics on occupation
	 * 
	 */
	@GET	
	@Path("/getusersatisfcationstats/mostlikedsolutionwithoccupation/")/* ratings*/
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserSatisfactionStatswithLikeSoltuionwithOccupation()
	{
		String responseFromMongo = mongoUtil.getUserSatisfactionSurveywithOccupation();
		LOGGER.info("Get tags usage statistics");
		return Response.status(SUCCESS).entity(responseFromMongo).build();
	}
	
	
}
