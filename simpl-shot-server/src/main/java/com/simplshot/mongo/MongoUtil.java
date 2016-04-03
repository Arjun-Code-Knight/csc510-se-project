package com.simplshot.mongo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;
import com.simplshot.server.AppStart;
import com.simplshot.server.UserService;

public class MongoUtil {
	
	private static MongoUtil mongoUtil;
	private static boolean initilaized = false;
	public static String mongoHost;
	public static String mongoPort;
	public static String mongoDB;
	public static String mongoCollection;
	public static String mongoTelemtryCollection;
	public static final Logger LOGGER = Logger.getLogger(MongoUtil.class.getName());
	
	private MongoUtil()
	{
		if(!initilaized)
			initializeDatabase();
	}
	
	public static synchronized MongoUtil getInstance()
	{
		if(mongoUtil == null)
			 mongoUtil = new MongoUtil();
		return mongoUtil;
	}
	
	private void initializeDatabase()
	{
		MongoClient client = new MongoClient(mongoHost, Integer.parseInt(mongoPort));
		client.getDatabase(mongoDB);
		client.close();
		initilaized = true;
	}
	
	public boolean addLinkToUser(ObjectId _id, String emailId, String link, String ocrWords, String privateData,String solutionType){
		boolean status = false;
		MongoClient client = new MongoClient(mongoHost, Integer.parseInt(mongoPort));
		MongoDatabase database = client.getDatabase(mongoDB);
		Document insertUser = new Document();
		insertUser.put("_id", _id);
		insertUser.put("email", emailId);
		insertUser.put("url", link);
		insertUser.put("tags", ocrWords);
		insertUser.put("private", privateData);
		insertUser.put("solutionType", solutionType);
		LOGGER.info("Adding user to DB "+insertUser.toJson());
		try{
			database.getCollection(mongoCollection).insertOne(insertUser);
			status = true;
			LOGGER.info("Succesfully added link to user "+emailId);
		}catch(MongoException ex)
		{
			LOGGER.severe("Error adding link to User");
		}finally{
			client.close();
		}
		return status;
	}
	
	/**
	 * For Simple history
	 * @param userId
	 * @param solutionType
	 * @return
	 */
	public String getUserDetails(String emailId, String solutionType)
	{
		MongoClient client = new MongoClient(mongoHost, Integer.parseInt(mongoPort));
		Document queryUser = new Document();
		queryUser.put("email", emailId);
		if(solutionType.equalsIgnoreCase(UserService.SOLUTION1)||solutionType.equalsIgnoreCase(UserService.SOLUTION3))
			queryUser.put("solutionType","non-chrome");	
		else
			queryUser.put("solutionType","chrome");
		queryUser.put("url", new Document().append("$exists", true));
		MongoDatabase database = client.getDatabase(mongoDB);
		try{
			FindIterable<Document> cur = database.getCollection(mongoCollection).find(queryUser);
			Iterator<Document> iter = cur.iterator();
			List<Document> returnJson = new ArrayList<Document>();
			while(iter.hasNext()){
				Document tmp = iter.next();
				returnJson.add(new Document().append("email", tmp.get("email")).append("url", tmp.get("url")));
			}
			LOGGER.info("Found User with UserID details -- "+JSON.serialize(returnJson));
			updateTelemetry(emailId,solutionType);
			return JSON.serialize(returnJson);
		}catch(MongoException ex){;
			LOGGER.severe("Error Checking User");
		}finally{
			client.close();
		}
		return JSON.serialize(Collections.EMPTY_LIST);
	}
	
	/**
	 * For Chrome
	 * @param userId
	 * @param solutionType
	 * @return
	 */
	public String getUserDetailsForChrome(String emailId, String solutionType)
	{
		MongoClient client = new MongoClient(mongoHost, Integer.parseInt(mongoPort));
		Document queryUser = new Document();
		queryUser.put("email", emailId);
		if(solutionType.equalsIgnoreCase(UserService.SOLUTION1)||solutionType.equalsIgnoreCase(UserService.SOLUTION3))
			queryUser.put("solutionType","non-chrome");	
		else
			queryUser.put("solutionType","chrome");
		queryUser.put("url", new Document().append("$exists", true));
		LOGGER.info("Request UserID details -- "+JSON.serialize(queryUser));
		MongoDatabase database = client.getDatabase(mongoDB);
		System.out.println(queryUser.toJson());
		try{
			FindIterable<Document> cur = database.getCollection(mongoCollection).find(queryUser);
			Iterator<Document> iter = cur.iterator();
			List<Document> returnJson = new ArrayList<Document>();
			while(iter.hasNext()){
				Document tmp = iter.next();
				returnJson.add(new Document().append("email", tmp.get("email")).append("url", tmp.get("url")).append("tags", tmp.get("tags")));
			}
			LOGGER.info("Found User with UserID details -- "+JSON.serialize(returnJson));
			updateTelemetry(emailId,solutionType);
			return JSON.serialize(returnJson);
		}catch(MongoException ex){;
			LOGGER.severe("Error Checking User");
		}finally{
			client.close();
		}
		return JSON.serialize(Collections.EMPTY_LIST);
	}
	
	/**
	 * For Desktop Application
	 * @param userId
	 * @param searchParam
	 * @param solutionType
	 * @return
	 */
	public String getUserDetails(String emailId,String searchParam, String solutionType)
	{
		MongoClient client = new MongoClient(mongoHost, Integer.parseInt(mongoPort));
		Document queryUser = new Document();
		queryUser.put("email", emailId);
		if(solutionType.equalsIgnoreCase(UserService.SOLUTION1)||solutionType.equalsIgnoreCase(UserService.SOLUTION3))
			queryUser.put("solutionType","non-chrome");	
		else
			queryUser.put("solutionType","chrome");
		queryUser.put("url", new Document().append("$exists", true));
		queryUser.put("tags", new Document().append("$regex", ".*"+searchParam+".*").append("$options", "i"));
		LOGGER.info("Request UserID details -- "+JSON.serialize(queryUser));
		MongoDatabase database = client.getDatabase(mongoDB);
		try{
			FindIterable<Document> cur = database.getCollection(mongoCollection).find(queryUser);
			Iterator<Document> iter = cur.iterator();
			List<Document> returnJson = new ArrayList<Document>();
			while(iter.hasNext()){
				Document tmp = iter.next();
				returnJson.add(new Document().append("email", tmp.get("email")).append("url", tmp.get("url")));
			}
			LOGGER.info("Found User with UserID details -- "+JSON.serialize(returnJson));
			updateTelemetry(emailId,solutionType);
			return JSON.serialize(returnJson);
		}catch(MongoException ex){;
			LOGGER.severe("Error Checking User");
		}finally{
			client.close();
		}
		return JSON.serialize(Collections.EMPTY_LIST);
	}
	
	/**
	 * Search across users
	 * @param userId
	 * @param searchParam
	 * @param solutionType
	 * @return
	 */
	public String searchAcrossUsers(String emailId,String searchParam,String solutionType)
	{
		MongoClient client = new MongoClient(mongoHost, Integer.parseInt(mongoPort));
		Document queryUser = new Document();
		queryUser.put("url", new Document().append("$exists", true));
		if(solutionType.equalsIgnoreCase(UserService.SOLUTION1)||solutionType.equalsIgnoreCase(UserService.SOLUTION3))
			queryUser.put("solutionType","non-chrome");	
		else
			queryUser.put("solutionType","chrome");
		queryUser.put("private", "false");
		queryUser.put("tags", new Document().append("$regex", ".*"+searchParam+".*").append("$options", "i"));
		LOGGER.info("Request UserID details -- "+JSON.serialize(queryUser));
		MongoDatabase database = client.getDatabase(mongoDB);
		try{
			FindIterable<Document> cur = database.getCollection(mongoCollection).find(queryUser);
			Iterator<Document> iter = cur.iterator();
			List<Document> returnJson = new ArrayList<Document>();
			while(iter.hasNext()){
				Document tmp = iter.next();
				returnJson.add(new Document().append("email", tmp.get("email")).append("url", tmp.get("url")));
			}
			LOGGER.info("Cross searching for tags -"+searchParam+" -->"+JSON.serialize(returnJson));
			updateCrossSearchTelemetry(emailId,"Cross-search",solutionType);
			return JSON.serialize(returnJson);
		}catch(MongoException ex){;
			LOGGER.severe("Error Checking User");
		}finally{
			client.close();
		}
		return JSON.serialize(Collections.EMPTY_LIST);
	}
	
	/**
	 * 
	 * @param userLogin
	 * @return
	 */
	public boolean performUserLogin(Login userLogin)
	{
		MongoClient client = new MongoClient(mongoHost, Integer.parseInt(mongoPort));
		Document queryUser = new Document();
		queryUser.put("email", userLogin.getEmail());
		queryUser.put("password", userLogin.getPassword());
		LOGGER.info("Request UserID details -- "+JSON.serialize(queryUser));
		MongoDatabase database = client.getDatabase(mongoDB);
		try{
			FindIterable<Document> cur = database.getCollection(mongoCollection).find(queryUser);
			Document response = cur.first();
			if(response != null){
				LOGGER.info("Checking User with UserID Exists"+response.toJson());
				return true;
			}
		}catch(MongoException ex)
		{
			LOGGER.severe("Error Checking User");
		}finally{
			client.close();
		}
		return false;
	}
	
	/**
	 * 
	 * @param getUserName
	 * @return
	 */
	public String getUserName(String emailId)
	{
		MongoClient client = new MongoClient(mongoHost, Integer.parseInt(mongoPort));
		Document queryUser = new Document();
		queryUser.put("email", emailId);
		LOGGER.info("Request UserID details -- "+JSON.serialize(queryUser));
		MongoDatabase database = client.getDatabase(mongoDB);
		try{
			FindIterable<Document> cur = database.getCollection(mongoCollection).find(queryUser);
			Document response = cur.first();
			if(response != null){
				LOGGER.info("Checking User with UserID Exists"+response.toJson());
				return response.getString("name");
			}
		}catch(MongoException ex)
		{
			LOGGER.severe("Error Checking User");
		}finally{
			client.close();
		}
		return "DEFAULT";
	}

	
	/**
	 * 
	 * @param check if user exists
	 * @return
	 */
	public boolean checkIfEmailExists(String email)
	{
		MongoClient client = new MongoClient(mongoHost, Integer.parseInt(mongoPort));
		Document queryUser = new Document();
		queryUser.put("email", email);
		MongoDatabase database = client.getDatabase(mongoDB);
		try{
			FindIterable<Document> cur = database.getCollection(mongoCollection).find(queryUser);
			Document response = cur.first();
			if(response != null){
				LOGGER.info("Checking User with UserID Exists"+response.toJson());
				return true;
			}
		}catch(MongoException ex)
		{
			LOGGER.severe("Error Checking User");
		}finally{
			client.close();
		}
		return false;
	}
	
	
	/**
	 * Sign up 
	 * @param signUpDetails
	 * @return
	 */
	public boolean createUser(UserSignup signUpDetails)
	{
		boolean status = false;
		Document user = new Document();
		user.put("name", signUpDetails.getUserName());
		user.put("password", signUpDetails.getPassword());
		user.put("age", signUpDetails.getAge());
		user.put("email", signUpDetails.getEmail());
		user.put("occupation", signUpDetails.getOccupation());
		user.put("sex", signUpDetails.getSex());
		if(checkIfEmailExists(signUpDetails.getEmail())) return false;
		LOGGER.info("Request UserID details -- "+JSON.serialize(user));
		MongoClient client = new MongoClient(mongoHost, Integer.parseInt(mongoPort));
		MongoDatabase database = client.getDatabase(mongoDB);
		try{
			database.getCollection(mongoCollection).insertOne(user);
			LOGGER.info("Insert succesfull for user "+user.toJson());
			status = true;
		}catch(MongoWriteException ex)
		{
			LOGGER.severe("Error Inserting record into the Database");
		}finally{
			client.close();
		}
		return status;
	}

	
	public boolean dropDatabase()
	{
		boolean status = false;
		MongoClient client = new MongoClient(mongoHost, Integer.parseInt(mongoPort));
		MongoDatabase database = client.getDatabase(mongoDB);
		try{
			database.drop();
			LOGGER.info("Database drop successful "+mongoDB);
			status = true;
		}catch(MongoException ex)
		{
			LOGGER.severe("Error dropping the database");
		}finally{
			client.close();
		}
		return status;
	}
	
	public boolean updateTelemetry(String emailId,String solutionType)
	{
		boolean status = false;
		MongoClient client = new MongoClient(mongoHost, Integer.parseInt(mongoPort));
		MongoDatabase database = client.getDatabase(mongoDB);
		try{
			Document insertStats = new Document();
			insertStats.put("email", emailId);
			insertStats.put("solutionType", solutionType);
			insertStats.put("time", new Date());
			database.getCollection(mongoTelemtryCollection).insertOne(insertStats);
			status = true;
		}catch(MongoException ex){;
			LOGGER.severe("Error Updating Telemetry for the solution");
		}finally{
			client.close();
		}
		LOGGER.info("Updated telemtry for the user and solution Type");
		return status;
	}
	
	public boolean updateCrossSearchTelemetry(String emailId, String searchPram, String solutionType)
	{
		boolean status = false;
		MongoClient client = new MongoClient(mongoHost, Integer.parseInt(mongoPort));
		MongoDatabase database = client.getDatabase(mongoDB);
		try{
			Document insertStats = new Document();
			insertStats.put("email", emailId);
			insertStats.put("solutionType", solutionType);
			insertStats.put("cross-search-Param", searchPram);
			insertStats.put("time", new Date());
			database.getCollection(mongoTelemtryCollection).insertOne(insertStats);
			status = true;
		}catch(MongoException ex){;
			LOGGER.severe("Error Updating Telemetry for the solution");
		}finally{
			client.close();
		}
		LOGGER.info("Updated telemtry for the user and solution Type");
		return status;
	}
	
	public boolean UpdateTagsUsageTelemetry(String solutionType)
	{
		boolean status = false;
		MongoClient client = new MongoClient(mongoHost, Integer.parseInt(mongoPort));
		MongoDatabase database = client.getDatabase(mongoDB);
		try{
			Document insertStats = new Document();
			insertStats.put("solutionType", solutionType);
			insertStats.put("update-tags", "Tag-Update");
			insertStats.put("time", new Date());
			database.getCollection(mongoTelemtryCollection).insertOne(insertStats);
			status = true;
		}catch(MongoException ex){;
			LOGGER.severe("Error Updating Telemetry for the solution");
		}finally{
			client.close();
		}
		LOGGER.info("Updated telemtry for the user and solution Type");
		return status;
	}
	
	public boolean updateTelemetryUserSatisfaction(String emailId,String rating, String comments,String solutionType)
	{
		boolean status = false;
		MongoClient client = new MongoClient(mongoHost, Integer.parseInt(mongoPort));
		MongoDatabase database = client.getDatabase(mongoDB);
		try{
			Document insertStats = new Document();
			insertStats.put("email", emailId);
			insertStats.put("rating", rating);
			insertStats.put("comments", comments);
			insertStats.put("solutionType", solutionType);
			insertStats.put("time", new Date());
			database.getCollection(mongoTelemtryCollection).insertOne(insertStats);
			status = true;
		}catch(MongoException ex){;
			LOGGER.severe("Error Updating Telemetry for the solution");
		}finally{
			client.close();
		}
		LOGGER.info("Updated telemtry for the user and solution Type");
		return status;
	}
	
	
	public String getUsageStatistics()
	{
		MongoClient client = new MongoClient(mongoHost, Integer.parseInt(mongoPort));
		MongoDatabase database = client.getDatabase(mongoDB);
		try{
			FindIterable<Document> cur = database.getCollection(mongoTelemtryCollection).find();
			Iterator<Document> iter = cur.iterator();
			List<Document> returnJson = new ArrayList<Document>();
			while(iter.hasNext()){
				Document tmp = iter.next();
				returnJson.add(new Document().append("email", tmp.get("email")).append("solutionType", tmp.get("solutionType")).
						append("time", tmp.getDate("time")));
			}
			LOGGER.info("Got Solution Usage Statistics -- "+JSON.serialize(returnJson));
			return JSON.serialize(returnJson);
		}catch(MongoException ex){;
			LOGGER.severe("Error getting solution usage Statistics");
		}finally{
			client.close();
		}
		return JSON.serialize(Collections.EMPTY_LIST);
	}
	
	/*
	 * 
	 * Which solution, they use cross search more
	 * 
	 */
	public String getxStatistics()
	{
		MongoClient client = new MongoClient(mongoHost, Integer.parseInt(mongoPort));
		MongoDatabase database = client.getDatabase(mongoDB);
		Document queryUser = new Document();
		queryUser.put("cross-search-Param", "Cross-search");
		try{
			FindIterable<Document> cur = database.getCollection(mongoTelemtryCollection).find();
			Iterator<Document> iter = cur.iterator();
			List<Document> returnJson = new ArrayList<Document>();
			while(iter.hasNext()){
				Document tmp = iter.next();
				returnJson.add(new Document().append("email", tmp.get("email")).append("solutionType", tmp.get("solutionType")).
						append("time", tmp.getDate("time")));
			}
			LOGGER.info("Got cross Usage Statistics -- "+JSON.serialize(returnJson));
			return JSON.serialize(returnJson);
		}catch(MongoException ex){;
			LOGGER.severe("Error getting cross usage Statistics");
		}finally{
			client.close();
		}
		return JSON.serialize(Collections.EMPTY_LIST);
	}
	
	/*
	 * 
	 * tags usage telemetry
	 * 
	 */
	public String getTagsUsageTelemetry()
	{
		MongoClient client = new MongoClient(mongoHost, Integer.parseInt(mongoPort));
		MongoDatabase database = client.getDatabase(mongoDB);
		Document queryUser = new Document();
		queryUser.put("update-tags", "Tag-Update");
		try{
			FindIterable<Document> cur = database.getCollection(mongoTelemtryCollection).find();
			Iterator<Document> iter = cur.iterator();
			List<Document> returnJson = new ArrayList<Document>();
			while(iter.hasNext()){
				Document tmp = iter.next();
				returnJson.add(new Document().append("solutionType", tmp.get("solutionType")).
						append("time", tmp.getDate("time")));
			}
			LOGGER.info("Got Tags Usage Statistics -- "+JSON.serialize(returnJson));
			return JSON.serialize(returnJson);
		}catch(MongoException ex){;
			LOGGER.severe("Error getting tags Statistics");
		}finally{
			client.close();
		}
		return JSON.serialize(Collections.EMPTY_LIST);
	}
	
	/*
	 * 
	 * get users age telemetry
	 * 
	 */
	public String getAgeTelemetry()
	{
		MongoClient client = new MongoClient(mongoHost, Integer.parseInt(mongoPort));
		MongoDatabase database = client.getDatabase(mongoDB);
		Document queryUser = new Document();
		queryUser.put("age",  new Document().append("$exists", true));
		try{
			FindIterable<Document> cur = database.getCollection(mongoTelemtryCollection).find();
			Iterator<Document> iter = cur.iterator();
			List<Document> returnJson = new ArrayList<Document>();
			while(iter.hasNext()){
				Document tmp = iter.next();
				returnJson.add(new Document().append("email", tmp.get("email")).append("age", tmp.get("age")).
						append("time", tmp.getDate("time")));
			}
			LOGGER.info("Got Tags Usage Statistics -- "+JSON.serialize(returnJson));
			return JSON.serialize(returnJson);
		}catch(MongoException ex){;
			LOGGER.severe("Error getting tags Statistics");
		}finally{
			client.close();
		}
		return JSON.serialize(Collections.EMPTY_LIST);
	}
	
	/*
	 * 
	 * get occupation telemetry
	 * 
	 */
	public String getOccupationTelemetry()
	{
		MongoClient client = new MongoClient(mongoHost, Integer.parseInt(mongoPort));
		MongoDatabase database = client.getDatabase(mongoDB);
		Document queryUser = new Document();
		queryUser.put("occupation",  new Document().append("$exists", true));
		try{
			FindIterable<Document> cur = database.getCollection(mongoTelemtryCollection).find();
			Iterator<Document> iter = cur.iterator();
			List<Document> returnJson = new ArrayList<Document>();
			while(iter.hasNext()){
				Document tmp = iter.next();
				returnJson.add(new Document().append("email", tmp.get("email")).append("occupation", tmp.get("occupation")).
						append("time", tmp.getDate("time")));
			}
			LOGGER.info("Got Tags Usage Statistics -- "+JSON.serialize(returnJson));
			return JSON.serialize(returnJson);
		}catch(MongoException ex){;
			LOGGER.severe("Error getting tags Statistics");
		}finally{
			client.close();
		}
		return JSON.serialize(Collections.EMPTY_LIST);
	}
	
	/*
	 * 
	 * get satisfaction telemetry
	 * 
	 */
	public String getUserSatisfactionSurvey()
	{
		MongoClient client = new MongoClient(mongoHost, Integer.parseInt(mongoPort));
		MongoDatabase database = client.getDatabase(mongoDB);
		Document queryUser = new Document();
		queryUser.put("rating",  new Document().append("$exists", true));
		try{
			FindIterable<Document> cur = database.getCollection(mongoTelemtryCollection).find();
			Iterator<Document> iter = cur.iterator();
			List<Document> returnJson = new ArrayList<Document>();
			while(iter.hasNext()){
				Document tmp = iter.next();
				returnJson.add(new Document().append("email", tmp.get("email")).append("rating", tmp.get("rating")).append("comments", tmp.get("comments")).append("solutionType", tmp.get("solutionType")).
						append("time", tmp.getDate("time")));
			}
			LOGGER.info("Got satisfaction Usage Statistics -- "+JSON.serialize(returnJson));
			return JSON.serialize(returnJson);
		}catch(MongoException ex){;
			LOGGER.severe("Error getting satisfaction Statistics");
		}finally{
			client.close();
		}
		return JSON.serialize(Collections.EMPTY_LIST);
	}
	
	
	
	public boolean updateTagsForUser(String fileUrl, String newTag)
	{
		boolean status = false;
		MongoClient client = new MongoClient(mongoHost, Integer.parseInt(mongoPort));
		Document queryImage = new Document();
		queryImage.put("url", fileUrl);
		MongoDatabase database = client.getDatabase(mongoDB);
		try{
			FindIterable<Document> cur = database.getCollection(mongoCollection).find(queryImage);
			Iterator<Document> iter = cur.iterator();
			Document updateJson = new Document();
			while(iter.hasNext()){
				Document tmp = iter.next();
				String currTags = tmp.getString("tags");
				currTags = currTags == null?"":currTags; 
				updateJson.append("$set", new Document().append("tags",currTags+"|"+newTag));
			}
			database.getCollection(mongoCollection).updateOne(queryImage, updateJson);
			status = true;
			/***
			 * Check if update is done
			 */
			cur = database.getCollection(mongoCollection).find(queryImage);
			iter = cur.iterator();
			while(iter.hasNext()){
				LOGGER.fine("Updated data "+JSON.serialize(iter.next()));
			}
			LOGGER.info("Found User with UserID details -- "+JSON.serialize(updateJson));
		}catch(MongoException ex){;
			LOGGER.severe("Error Checking User");
		}finally{
			client.close();
		}
		return status;
	}
	
	public boolean deleteTagsForUser(String fileUrl, String deleteTag)
	{
		boolean status = false;
		MongoClient client = new MongoClient(mongoHost, Integer.parseInt(mongoPort));
		Document queryImage = new Document();
		queryImage.put("url", fileUrl);
		MongoDatabase database = client.getDatabase(mongoDB);
		try{
			FindIterable<Document> cur = database.getCollection(mongoCollection).find(queryImage);
			Iterator<Document> iter = cur.iterator();
			Document updateJson = new Document();
			while(iter.hasNext()){
				Document tmp = iter.next();
				String currTags = tmp.getString("tags");
				currTags = currTags == null?"":currTags.replace(deleteTag, ""); 
				updateJson.append("$set", new Document().append("tags",currTags));
			}
			database.getCollection(mongoCollection).updateOne(queryImage, updateJson);
			status = true;
			LOGGER.info("Found User with UserID details -- "+JSON.serialize(updateJson));
		}catch(MongoException ex){;
			LOGGER.severe("Error Checking User");
		}finally{
			client.close();
		}
		return status;
	}
	
	public static void main(String[] args) {
		AppStart.loadProperties();
		//MongoUtil.getInstance().dropDatabase();
		if(MongoUtil.getInstance().checkIfEmailExists("sample9@gmail.com"))
			System.out.println("Emaail exists");
		for(int i = 4 ; i < 100; i++){
			UserSignup newuser = new UserSignup();
			newuser.setAge(25);
			newuser.setEmail("sample"+i+"@gmail.com");
			newuser.setOccupation("Engineer");
			newuser.setPassword("1234");
			newuser.setSex("M");
			newuser.setUserName("User"+i);
		//MongoUtil.getInstance().createUser(newuser);
		}
		ObjectId _id = new ObjectId();
		MongoUtil.getInstance().addLinkToUser(_id,"TESTUSER1","http://localhost:1","tag1","YES","chrome");
		MongoUtil.getInstance().getAllUsers();
		//MongoUtil.getInstance().checkIfUserExists("Testuser");
		//ObjectId _id = new ObjectId();
		//MongoUtil.getInstance().addLinkToUser(_id,"Testuser","http://localhost:2","tag2");
		//_id = new ObjectId();
		//MongoUtil.getInstance().addLinkToUser(_id,"Testuser","http://localhost:3","tag3");
		//MongoUtil.getInstance().getUserDetails("TESTUSER1","SOLUTION1");
		//MongoUtil.getInstance().getUsageStatistics();
		//MongoUtil.getInstance().dropDatabase();
	}
	
	/**
	 * 
	 * @param getUserName
	 * @return
	 */
	public String getAllUsers()
	{
		MongoClient client = new MongoClient(mongoHost, Integer.parseInt(mongoPort));
		Document queryUser = new Document();
		queryUser.put("name", new Document().append("$exists", true));
		MongoDatabase database = client.getDatabase(mongoDB);
		try{
			FindIterable<Document> cur = database.getCollection(mongoCollection).find(queryUser);
			Iterator<Document> iter = cur.iterator();
			List<Document> returnJson = new ArrayList<Document>();
			while(iter.hasNext()){
				Document tmp = iter.next();
				returnJson.add(new Document().append("name", tmp.get("name")).append("email", tmp.get("email")));
			}
			LOGGER.info("Found User with UserID details -- "+JSON.serialize(returnJson));
			return JSON.serialize(returnJson);
		}catch(MongoException ex)
		{
			LOGGER.severe("Error Checking User");
		}finally{
			client.close();
		}
		return JSON.serialize(Collections.EMPTY_LIST);
	}	
	
}

