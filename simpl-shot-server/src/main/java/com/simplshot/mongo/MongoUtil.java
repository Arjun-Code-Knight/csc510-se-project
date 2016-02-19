package com.simplshot.mongo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.bson.Document;

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
	
	public boolean addLinkToUser(String userId, String link){
		boolean status = false;
		MongoClient client = new MongoClient(mongoHost, Integer.parseInt(mongoPort));
		MongoDatabase database = client.getDatabase(mongoDB);
		Document insertUser = new Document();
		insertUser.put("name", userId);
		insertUser.put("url", link);
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
	
	
	public String getUserDetails(String userId)
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
				returnJson.add(iter.next());
			}
			LOGGER.info("Found User with UserID details -- "+JSON.serialize(returnJson));
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
	
	public static void main(String[] args) {
		AppStart.loadProperties();
		MongoUtil.getInstance().dropDatabase();
		//MongoUtil.getInstance().createUser("Testuser");
		MongoUtil.getInstance().checkIfUserExists("Testuser");
		MongoUtil.getInstance().addLinkToUser("Testuser","http://localhost:1");
		MongoUtil.getInstance().addLinkToUser("Testuser","http://localhost:2");
		MongoUtil.getInstance().addLinkToUser("Testuser","http://localhost:3");
		//MongoUtil.getInstance().getUserDetails("Testuser");
		//MongoUtil.getInstance().dropDatabase();
	}
	
}
