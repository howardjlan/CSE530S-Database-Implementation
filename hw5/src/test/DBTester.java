package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;

import hw5.DB;
import hw5.DBCollection;
import hw5.Document;

class DBTester {
	
	/**
	 * Things to consider testing:
	 * 
	 * Properly creates directory for new DB (done)
	 * Properly accesses existing directory for existing DB (done)
	 * Properly accesses collection (done)
	 * Properly drops a database (done)
	 * Special character handling?
	 */
	
	@Test
	public void testCreateDB() {
		DB hw5 = new DB("hw5"); //call method
		assertTrue(new File("testfiles/hw5").exists()); //verify results
	}
	
	@Test
	public void dropDatabase() {
		DB testdropdb = new DB("testdropdb");
		testdropdb.dropDatabase();
		assertFalse(new File("testfiles/testdropdb").exists());
	}

	@Test
	public void dropDatabaseWithContents() {
		DB testdropdb = new DB("testdropdb");
		DBCollection dbc = testdropdb.getCollection("sampleJSON");
		String json = "{\n \"key\":\"value\" \n}";	//setup
		JsonObject insertedDoc = Document.parse(json); 	//call method to be tested
		dbc.insert(insertedDoc);
		
		testdropdb.dropDatabase();
		assertFalse(new File("testfiles/testdropdb").exists());
	}
	
	@Test 
	public void testgetCollection()
	{
		DB testGetCollection = new DB("testGetCollection");
		DBCollection dbc = testGetCollection.getCollection("sampleJSON");
		String json = "{\n \"key\":\"value\" \n}";	//setup
		JsonObject insertedDoc = Document.parse(json); 	//call method to be tested
		dbc.insert(insertedDoc);
		DBCollection coll = testGetCollection.getCollection("sampleJSON");
		assertTrue(coll!= null);
		testGetCollection.dropDatabase();
		assertFalse(new File("testfiles/testGetCollection").exists());
		
	}
	
	@Test
	public void testAccessExistingDB() {
		DB existingDB = new DB("data");
		assertTrue(existingDB.f.exists());
	}
}
