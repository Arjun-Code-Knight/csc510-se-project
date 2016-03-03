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
	
	public boolean addLinkToUser(ObjectId _id, String userId, String link, String ocrWords){
		boolean status = false;
		MongoClient client = new MongoClient(mongoHost, Integer.parseInt(mongoPort));
		MongoDatabase database = client.getDatabase(mongoDB);
		Document insertUser = new Document();
		insertUser.put("_id", _id);
		insertUser.put("name", userId);
		insertUser.put("url", link);
		insertUser.put("tags", ocrWords);
		LOGGER.info("Adding user to DB "+insertUser.toJson());
		try{
			database.getCollection(mongoCollection).insertOne(insertUser);
			status = true;
			LOGGER.info("Succesfully added link to user");
		}catch(MongoException ex)
		{
			LOGGER.severe("Error adding link to User");
		}finally{
			client.close();
		}
		return status;
	}
	
	
	public String getUserDetails(String userId, String solutionType)
	{
		MongoClient client = new MongoClient(mongoHost, Integer.parseInt(mongoPort));
		Document queryUser = new Document();
		queryUser.put("name", userId);
		MongoDatabase database = client.getDatabase(mongoDB);
		try{
			FindIterable<Document> cur = database.getCollection(mongoCollection).find(queryUser);
			Iterator<Document> iter = cur.iterator();
			List<Document> returnJson = new ArrayList<Document>();
			while(iter.hasNext()){
				Document tmp = iter.next();
				returnJson.add(new Document().append("name", tmp.get("name")).append("url", tmp.get("url")));
			}
			LOGGER.info("Found User with UserID details -- "+JSON.serialize(returnJson));
			updateTelemetry(userId,solutionType);
			return JSON.serialize(returnJson);
		}catch(MongoException ex){;
			LOGGER.severe("Error Checking User");
		}finally{
			client.close();
		}
		return JSON.serialize(Collections.EMPTY_LIST);
	}
	
	public String getUserDetailsForChrome(String userId, String solutionType)
	{
		MongoClient client = new MongoClient(mongoHost, Integer.parseInt(mongoPort));
		Document queryUser = new Document();
		queryUser.put("name", userId);
		MongoDatabase database = client.getDatabase(mongoDB);
		try{
			FindIterable<Document> cur = database.getCollection(mongoCollection).find(queryUser);
			Iterator<Document> iter = cur.iterator();
			List<Document> returnJson = new ArrayList<Document>();
			while(iter.hasNext()){
				Document tmp = iter.next();
				returnJson.add(new Document().append("name", tmp.get("name")).append("url", tmp.get("url")).append("tags", tmp.get("tags")));
			}
			LOGGER.info("Found User with UserID details -- "+JSON.serialize(returnJson));
			updateTelemetry(userId,solutionType);
			return JSON.serialize(returnJson);
		}catch(MongoException ex){;
			LOGGER.severe("Error Checking User");
		}finally{
			client.close();
		}
		return JSON.serialize(Collections.EMPTY_LIST);
	}
	
	public String getUserDetails(String userId,String searchParam, String solutionType)
	{
		MongoClient client = new MongoClient(mongoHost, Integer.parseInt(mongoPort));
		Document queryUser = new Document();
		queryUser.put("name", userId);
		queryUser.put("tags", new Document().append("$regex", ".*"+searchParam+".*").append("$options", "i"));
		MongoDatabase database = client.getDatabase(mongoDB);
		try{
			FindIterable<Document> cur = database.getCollection(mongoCollection).find(queryUser);
			Iterator<Document> iter = cur.iterator();
			List<Document> returnJson = new ArrayList<Document>();
			while(iter.hasNext()){
				Document tmp = iter.next();
				returnJson.add(new Document().append("name", tmp.get("name")).append("url", tmp.get("url")));
			}
			LOGGER.info("Found User with UserID details -- "+JSON.serialize(returnJson));
			updateTelemetry(userId,solutionType);
			return JSON.serialize(returnJson);
		}catch(MongoException ex){;
			LOGGER.severe("Error Checking User");
		}finally{
			client.close();
		}
		return JSON.serialize(Collections.EMPTY_LIST);
	}
	
	public boolean checkIfUserExists(String userId)
	{
		MongoClient client = new MongoClient(mongoHost, Integer.parseInt(mongoPort));
		Document queryUser = new Document();
		queryUser.put("name", userId);
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
	
	
	public boolean createUser(String userId)
	{
		boolean status = false;
		Document user = new Document();
		user.put("name", userId);
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
	
	public boolean updateTelemetry(String userId,String solutionType)
	{
		boolean status = false;
		MongoClient client = new MongoClient(mongoHost, Integer.parseInt(mongoPort));
		MongoDatabase database = client.getDatabase(mongoDB);
		try{
			Document insertStats = new Document();
			insertStats.put("name", userId);
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
				returnJson.add(new Document().append("name", tmp.get("name")).append("solutionType", tmp.get("solutionType")).
						append("time", tmp.getDate("time")));
			}
			LOGGER.info("Got Usage Statistics -- "+JSON.serialize(returnJson));
			return JSON.serialize(returnJson);
		}catch(MongoException ex){;
			LOGGER.severe("Error getting Statistics");
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
		MongoUtil.getInstance().dropDatabase();
		//MongoUtil.getInstance().createUser("Testuser");
		MongoUtil.getInstance().checkIfUserExists("Testuser");
		ObjectId _id = new ObjectId();
		MongoUtil.getInstance().addLinkToUser(_id,"TESTUSER1","http://localhost:1","tag1");
		_id = new ObjectId();
		//MongoUtil.getInstance().addLinkToUser(_id,"Testuser","http://localhost:2","tag2");
		_id = new ObjectId();
		//MongoUtil.getInstance().addLinkToUser(_id,"Testuser","http://localhost:3","tag3");
		MongoUtil.getInstance().getUserDetails("TESTUSER1","SOLUTION1");
		MongoUtil.getInstance().getUsageStatistics();
		//MongoUtil.getInstance().dropDatabase();
	}
	
}
