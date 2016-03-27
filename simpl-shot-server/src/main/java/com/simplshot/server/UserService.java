package com.simplshot.server;

import java.io.IOException;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.util.JSON;
import com.simplshot.mongo.Login;
import com.simplshot.mongo.MongoUtil;
import com.simplshot.mongo.ResponseStatus;
import com.simplshot.mongo.UserSignup;

@Path("user")
public class UserService {

	private static final int ERROR = 500;
	private static final int SUCCESS = 200;
	private static final String SOLUTION1 = "SOLUTION1-DESKTOPWITHOUTSERACH";//just history
	private static final String SOLUTION2 = "SOLUTION2-CHROME";//chrome use specific tags
	private static final String SOLUTION3 = "SOLUTION3-DESKTOPWITHSEARCH";// tesseract
	private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());
	private MongoUtil mongoUtil = MongoUtil.getInstance();
	
	/**
	 * 
	 * @return
	 */
	@GET	
	@Produces(MediaType.TEXT_HTML)
	public Response getUserdetails()
	{
		LOGGER.info("Please pass userid and user/{emailId} path");
		return Response.status(200).entity("Please pass emailId and user/{emailId} path").build();
		
	}
	
	/**
	 * 
	 * @param signUpRequest
	 * @return
	 * @throws JsonProcessingException
	 */
	@POST
	@Path("/signup")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response userSignUp(String signUpRequest) throws JsonProcessingException
	{
		ObjectMapper mapper = new ObjectMapper();
		System.out.println("Json request "+signUpRequest);
		try {
			UserSignup userSignUp = mapper.readValue(signUpRequest, UserSignup.class);
			return MongoUtil.getInstance().createUser(userSignUp)==true?generateSuccessResponse(userSignUp.getUserName()):generateErrorResponse("Email id exists");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return generateErrorResponse("Incorrect request");
		}
	}
	
	/**
	 * 
	 * Login Request
	 * @param loginRequest
	 * @return
	 * @throws JsonProcessingException
	 */
	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response userLogin(String loginRequest) throws JsonProcessingException
	{
		ObjectMapper mapper = new ObjectMapper();
		System.out.println("Json request "+loginRequest);
		try {
			Login userLogin = mapper.readValue(loginRequest, Login.class);
			return MongoUtil.getInstance().performUserLogin(userLogin)==true?generateSuccessResponse(MongoUtil.getInstance().getUserName(userLogin.getEmail())):generateErrorResponse("Wrong credentials");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return generateErrorResponse("Incorrect request");
		}
	}
	
	/**
	**Get user details and links for desktop as there is only history
	*/
	@GET	
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/search/{emailId}")
	public Response getUserdetails(@PathParam("emailId") String emailId)
	{
		String responseFromMongo = mongoUtil.getUserDetails(emailId,SOLUTION1);
		LOGGER.info("The emailId is "+emailId);
		return Response.status(SUCCESS).entity(responseFromMongo).build();
		
	}
	
	/**
	 * Get user details and links for chrome as it involves searching by tag
	 * @param userId
	 * @return
	 */
	@GET	
	@Path("/search/chrome/{emailId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserdetailsForChrome(@PathParam("emailId") String emailId)
	{
		String responseFromMongo = mongoUtil.getUserDetailsForChrome(emailId,SOLUTION2);
		LOGGER.info("The user Id is "+emailId);
		return Response.status(SUCCESS).entity(responseFromMongo).build();
		
	}
	
	/**
	 * get user details and links for desktop as it involves searching by search param where data extracted by tesseract
	 * @param userId
	 * @param searchparam
	 * @return
	 */
	@GET	
	@Path("/search/{emailId}/{search}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserdetails(@PathParam("emailId") String emailId,@PathParam("search") String searchparam)
	{
		String responseFromMongo = mongoUtil.getUserDetails(emailId,searchparam,SOLUTION3);
		LOGGER.info("The emailId is "+emailId);
		return Response.status(SUCCESS).entity(responseFromMongo).build();
	}
	
	/**
	 * get user details and links for desktop as it involves searching by search param where data extracted by tesseract
	 * @param userId
	 * @param searchparam
	 * @return
	 */
	@GET	
	@Path("/search/chrome/{emailId}/{search}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserdetailsForChromeWithParam(@PathParam("emailId") String emailId,@PathParam("search") String searchparam)
	{
		String responseFromMongo = mongoUtil.getUserDetails(emailId,searchparam,SOLUTION2);
		LOGGER.info("The emailId is "+emailId);
		return Response.status(SUCCESS).entity(responseFromMongo).build();
	}
	

	/**
	 * Cross search
	 * @param userId
	 * @param searchParam
	 * @return
	 */
	@GET	
	@Path("/crosssearch/{emailId}/{search}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchAcrossUsers(@PathParam("emailId") String emailId,@PathParam("search") String searchParam)
	{
		String responseFromMongo = mongoUtil.searchAcrossUsers(emailId,searchParam,SOLUTION3);
		LOGGER.info("Get usage statistics");
		return Response.status(SUCCESS).entity(responseFromMongo).build();
	}
	
	/**
	 * Cross search
	 * @param userId
	 * @param searchParam
	 * @return
	 */
	@GET	
	@Path("/crosssearch/chrome/{emailId}/{search}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchAcrossUsersForChrome(@PathParam("emailId") String emailId,@PathParam("search") String searchParam)
	{
		String responseFromMongo = mongoUtil.searchAcrossUsers(emailId,searchParam,SOLUTION2);
		LOGGER.info("Get usage statistics");
		return Response.status(SUCCESS).entity(responseFromMongo).build();
	}
	
	
	/**
	 * get user satisfaction
	 * @param emailId
	 * @param searchparam
	 * @return
	 * @throws JsonProcessingException 
	 */
	@GET	
	@Path("/usersatisfaction/{emailId}/{rating}/{comments}/{solutionType}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserdetails(@PathParam("emailId") String emailId,@PathParam("rating") String rating, @PathParam("comments") String comments,@PathParam("solutionType") String solutionType) throws JsonProcessingException
	{
		LOGGER.info("The emailId is "+emailId);
		return mongoUtil.updateTelemetryUserSatisfaction(emailId,rating,comments,solutionType)==true?generateSuccessResponse(MongoUtil.getInstance().getUserName(emailId)):generateErrorResponse("Error updating");
	}
	
	/**
	 * @return
	 * @throws JsonProcessingException
	 */
	public static Response generateSuccessResponse(String userName) throws JsonProcessingException
	{
		
		ObjectMapper mapper = new ObjectMapper();
		ResponseStatus status = new ResponseStatus();
		status.setReason("Succesfully Logged-in!");
		status.setSuccess("Yes");
		status.setUser(userName);
		LOGGER.info("Response --"+JSON.serialize(status.toString()));
		return Response.status(SUCCESS).entity(mapper.writeValueAsString(status)).build();
	}
	
	/**
	 * @param reason
	 * @return
	 * @throws JsonProcessingException
	 */
	public static Response generateErrorResponse(String reason) throws JsonProcessingException
	{
		
		ObjectMapper mapper = new ObjectMapper();
		ResponseStatus status = new ResponseStatus();
		status.setReason(reason);
		status.setSuccess("No");
		LOGGER.info("Response --"+JSON.serialize(status.toString()));
		return Response.status(SUCCESS).entity(mapper.writeValueAsString(status)).build();
	}
	
}
