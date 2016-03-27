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
		LOGGER.info("Please pass userid and user/{userid} path");
		return Response.status(200).entity("Please pass userid and user/{id} path").build();
		
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
			return MongoUtil.getInstance().createUser(userSignUp)==true?generateSuccessResponse():generateErrorResponse("Incorrect request");
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
			return MongoUtil.getInstance().performUserLogin(userLogin)==true?generateSuccessResponse():generateErrorResponse("Wrong credentials");
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
	@Path("/search/{userId}")
	public Response getUserdetails(@PathParam("userId") String userId)
	{
		String responseFromMongo = mongoUtil.getUserDetails(userId,SOLUTION1);
		LOGGER.info("The user Id is "+userId);
		return Response.status(SUCCESS).entity(responseFromMongo).build();
		
	}
	
	/**
	 * Get user details and links for chrome as it involves searching by tag
	 * @param userId
	 * @return
	 */
	@GET	
	@Path("/search/chrome/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserdetailsForChrome(@PathParam("userId") String userId)
	{
		String responseFromMongo = mongoUtil.getUserDetailsForChrome(userId,SOLUTION2);
		LOGGER.info("The user Id is "+userId);
		return Response.status(SUCCESS).entity(responseFromMongo).build();
		
	}
	
	/**
	 * get user details and links for desktop as it involves searching by search param where data extracted by tesseract
	 * @param userId
	 * @param searchparam
	 * @return
	 */
	@GET	
	@Path("/search/{userId}/{search}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserdetails(@PathParam("userId") String userId,@PathParam("search") String searchparam)
	{
		String responseFromMongo = mongoUtil.getUserDetails(userId,searchparam,SOLUTION3);
		LOGGER.info("The user Id is "+userId);
		return Response.status(SUCCESS).entity(responseFromMongo).build();
	}

	/**
	 * Cross search
	 * @param userId
	 * @param searchParam
	 * @return
	 */
	@GET	
	@Path("/crosssearch/{userId}/{search}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchAcrossUsers(@PathParam("userId") String userId,@PathParam("search") String searchParam)
	{
		String responseFromMongo = mongoUtil.searchAcrossUsers(userId,searchParam,SOLUTION3);
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
	@Path("/crosssearch/chrome/{userId}/{search}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchAcrossUsersForChrome(@PathParam("userId") String userId,@PathParam("search") String searchParam)
	{
		String responseFromMongo = mongoUtil.searchAcrossUsers(userId,searchParam,SOLUTION2);
		LOGGER.info("Get usage statistics");
		return Response.status(SUCCESS).entity(responseFromMongo).build();
	}
	
	
	/**
	 * get user satisfaction
	 * @param userId
	 * @param searchparam
	 * @return
	 * @throws JsonProcessingException 
	 */
	@GET	
	@Path("/usersatisfaction/{userId}/{rating}/{comments}/{solutionType}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserdetails(@PathParam("userId") String userId,@PathParam("rating") String rating, @PathParam("comments") String comments,@PathParam("solutionType") String solutionType) throws JsonProcessingException
	{
		LOGGER.info("The user Id is "+userId);
		return mongoUtil.updateTelemetryUserSatisfaction(userId,rating,comments,solutionType)==true?generateSuccessResponse():generateErrorResponse("Error updating");
	}
	
	/**
	 * @return
	 * @throws JsonProcessingException
	 */
	public static Response generateSuccessResponse() throws JsonProcessingException
	{
		
		ObjectMapper mapper = new ObjectMapper();
		ResponseStatus status = new ResponseStatus();
		status.setReason("Succesfully Signed-up!");
		status.setSuccess("Yes");
		return Response.status(200).entity(mapper.writeValueAsString(status)).build();
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
		return Response.status(ERROR).entity(mapper.writeValueAsString(status)).build();
	}
	
}
