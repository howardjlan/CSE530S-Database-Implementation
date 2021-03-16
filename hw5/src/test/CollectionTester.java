package test;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.junit.jupiter.api.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import hw5.DB;
import hw5.DBCollection;
import hw5.DBCursor;
import hw5.Document;

class CollectionTester {
	
	/**
	 * Things to consider testing
	 * 
	 * Queries:
	 * 	Find all (done)
	 * 	Find with relational select
	 * 		Conditional operators
	 * 		Embedded documents
	 * 		Arrays
	 * 	Find with relational project (done)
	 * 
	 * Inserts (done)
	 * Updates (done)
	 * Deletes (done)
	 * 
	 * getDocument (done)
	 * drop (done)
	 */

	
	@Test
	public void testSelectIN() {
		DB db = new DB("data");
		DBCollection test = db.getCollection("test");
		JsonObject query = new JsonObject();
		String q1 = "{ array: { $in: [ one, four ] } }";
		query = Document.parse(q1);
		DBCursor dbc = test.find(query);
		JsonObject result = null;
		assertTrue(dbc.count() == 1);
		if(dbc.hasNext()) {
			result = dbc.next();
			assertTrue(result.equals(test.getDocument(2)));
		} else {
			assertTrue("EQArrayOfDocuments: the query returned no results", false);
		}
	}
	
	@Test
	public void testSelectProjectEQ() {
		DB db = new DB("data");
		DBCollection test = db.getCollection("EmbeddedWithArray");
		JsonObject query = new JsonObject();
		DBCursor dbc = null;
		String q1 = "{ \"recipe.ingredients\" : { $eq: [ walnut, eggs, flour ] } }";
		query = Document.parse(q1);
		JsonObject projection = Document.parse("{ name: 1 }");
		dbc = test.find(query, projection);
		JsonObject result = null;
		assertTrue(dbc.count() == 2);
		if(dbc.hasNext()) {
			result = dbc.next();
			assertTrue(result.equals(Document.parse("{ name: cookie }")));
			result = dbc.next();
			assertTrue(result.equals(Document.parse("{ name: pie }")));
		} else {
			assertTrue("EQArrayOfDocuments: the query returned no results", false);
		}
	}
	
	@Test
	public void testSelectLTEProjectArrayOfDocuments() {
		DB db = new DB("data");
		DBCollection test = db.getCollection("ArrayOfDocuments");
		JsonObject query = new JsonObject();
		DBCursor dbc = null;
		String q1 = "{ instock.qty: { $lte: 6 } }";
		query = Document.parse(q1);
		JsonObject projection = Document.parse("{ instock: 1 }");
		dbc = test.find(query, projection);
		JsonObject result = null;
		assertTrue(dbc.count() == 2);
		if(dbc.hasNext()) {
			result = dbc.next();
			assertTrue(result.equals(Document.parse("{instock: [ { warehouse: \"C\", qty: 5 } ]}")));
			result = dbc.next();
			assertTrue(result.equals(Document.parse("{instock: [ { warehouse: \"A\", qty: 30 }, { warehouse: \"B\", qty: 5 } ]}")));
		} else {
			assertTrue("EQArrayOfDocuments: the query returned no results", false);
		}
	}
	
	@Test
	public void testSelectEQArrayOfDocuments() {
		DB db = new DB("data");
		DBCollection test = db.getCollection("ArrayOfDocuments");
		JsonObject query = new JsonObject();
		DBCursor dbc = null;
		String q1 = "{ instock.qty: { $eq: 5 } }";
		query = Document.parse(q1);
		dbc = test.find(query);
		JsonObject result = null;
		assertTrue(dbc.count() == 2);
		if(dbc.hasNext()) {
			result = dbc.next();
			assertTrue(result.equals(test.getDocument(1)));
			result = dbc.next();
			assertTrue(result.equals(test.getDocument(3)));
		} else {
			assertTrue("EQArrayOfDocuments: the query returned no results", false);
		}
	}
	
	@Test
	public void testSelectGTEArrayOfDocuments() {
		DB db = new DB("data");
		DBCollection test = db.getCollection("ArrayOfDocuments");
		JsonObject query = new JsonObject();
		DBCursor dbc = null;
		String q1 = "{ instock.qty: { $gte: 40 } }";
		query = Document.parse(q1);
		dbc = test.find(query);
		JsonObject result = null;
		assertTrue(dbc.count() == 2);
		
		if(dbc.hasNext()) {
			result = dbc.next();
			assertTrue(result.equals(test.getDocument(2)));
			result = dbc.next();
			assertTrue(result.equals(test.getDocument(4)));
		} else {
			assertTrue("GTEArrayOfDocuments: the query returned no results", false);
		}
	}
	
	@Test
	public void testSelectEQEmbeddedWithArray() {
		DB db = new DB("data");
		DBCollection test = db.getCollection("EmbeddedWithArray");
		JsonObject query = new JsonObject();
		DBCursor dbc = null;
		String q1 = "{ \"recipe.ingredients\" : { $eq: [ walnut, eggs, flour ] } }";
		query = Document.parse(q1);
		dbc = test.find(query);
		JsonObject result = null;
		assertTrue(dbc.count() == 2);
		if(dbc.hasNext()) {
			result = dbc.next();
			assertTrue(result.equals(test.getDocument(1)));
			result = dbc.next();
			assertTrue(result.equals(test.getDocument(2)));
		} else {
			assertTrue("EQArrayOfDocuments: the query returned no results", false);
		}
	}
	
	@Test
	public void testSelectQTEmbeddedWithArray() {
		DB db = new DB("data");
		DBCollection test = db.getCollection("EmbeddedWithArray");
		JsonObject query = new JsonObject();
		DBCursor dbc = null;
		String q1 = "{ \"recipe.time\" : { $gt: 59 } }";
		query = Document.parse(q1);
		dbc = test.find(query);
		JsonObject result = null;
		assertTrue(dbc.count() == 3);
		if(dbc.hasNext()) {
			result = dbc.next();
			assertTrue(result.equals(test.getDocument(0)));
			result = dbc.next();
			assertTrue(result.equals(test.getDocument(2)));
			result = dbc.next();
			assertTrue(result.equals(test.getDocument(3)));
		} else {
			assertTrue("EQArrayOfDocuments: the query returned no results", false);
		}
	}
	
	@Test
	public void testSelectNEEmbeddedWithArray() {
		DB db = new DB("data");
		DBCollection test = db.getCollection("EmbeddedWithArray");
		JsonObject query = new JsonObject();
		DBCursor dbc = null;
		String q1 = "{ \"recipe.time\" : { $ne: 50 } }";
		query = Document.parse(q1);
		dbc = test.find(query);
		JsonObject result = null;
		assertTrue(dbc.count() == 3);
		if(dbc.hasNext()) {
			result = dbc.next();
			assertTrue(result.equals(test.getDocument(0)));
			result = dbc.next();
			assertTrue(result.equals(test.getDocument(2)));
			result = dbc.next();
			assertTrue(result.equals(test.getDocument(3)));
		} else {
			assertTrue("EQArrayOfDocuments: the query returned no results", false);
		}
	}
	
	@Test
	public void testSelectQTEEmbeddedWithArray() {
		DB db = new DB("data");
		DBCollection test = db.getCollection("EmbeddedWithArray");
		JsonObject query = new JsonObject();
		DBCursor dbc = null;
		String q1 = "{ \"recipe.time\" : { $gte: 240 } }";
		query = Document.parse(q1);
		dbc = test.find(query);
		JsonObject result = null;
		assertTrue(dbc.count() == 1);
		if(dbc.hasNext()) {
			result = dbc.next();
			assertTrue(result.equals(test.getDocument(3)));
		} else {
			assertTrue("EQArrayOfDocuments: the query returned no results", false);
		}
	}
	
	@Test
	public void testSelectLTEmbeddedWithArray() {
		DB db = new DB("data");
		DBCollection test = db.getCollection("EmbeddedWithArray");
		JsonObject query = new JsonObject();
		DBCursor dbc = null;
		String q1 = "{ \"recipe.time\" : { $lt: 51 } }";
		query = Document.parse(q1);
		dbc = test.find(query);
		JsonObject result = null;
		assertTrue(dbc.count() == 2);

		if(dbc.hasNext()) {
			result = dbc.next();
			assertTrue(result.equals(test.getDocument(1)));
			result = dbc.next();
			assertTrue(result.equals(test.getDocument(4)));
		} else {
			assertTrue("EQArrayOfDocuments: the query returned no results", false);
		}
	}
	
	@Test
	public void testSelectLTEEmbeddedWithArray() {
		DB db = new DB("data");
		DBCollection test = db.getCollection("EmbeddedWithArray");
		JsonObject query = new JsonObject();
		DBCursor dbc = null;
		String q1 = "{ \"recipe.time\" : { $lte: 60 } }";
		query = Document.parse(q1);
		dbc = test.find(query);
		JsonObject result = null;
		assertTrue(dbc.count() == 4);
		if(dbc.hasNext()) {
			result = dbc.next();
			assertTrue(result.equals(test.getDocument(0)));
			result = dbc.next();
			assertTrue(result.equals(test.getDocument(1)));
			result = dbc.next();
			assertTrue(result.equals(test.getDocument(2)));
			result = dbc.next();
			assertTrue(result.equals(test.getDocument(4)));
		} else {
			assertTrue("EQArrayOfDocuments: the query returned no results", false);
		}
	}
	
	
	@Test
	public void testSelectGTEmbeddedWithArray() {
		DB db = new DB("data");
		DBCollection test = db.getCollection("EmbeddedWithArray");
		JsonObject query = new JsonObject();
		DBCursor dbc = null;
		String q1 = "{ \"recipe.servings\" : { $gt: 40 } }";
		query = Document.parse(q1);
		dbc = test.find(query);
		JsonObject result = null;
		assertTrue(dbc.count() == 1);
		if(dbc.hasNext()) {
			result = dbc.next();
			assertTrue(result.equals(test.getDocument(4)));
		} else {
			assertTrue("EQArrayOfDocuments: the query returned no results", false);
		}
	}
	
	@Test
	public void testSelectEQ() {
		DB db = new DB("data");
		DBCollection test = db.getCollection("test");
		JsonObject query = new JsonObject();
		DBCursor dbc = null;
		
		String q1 = "{ key: { $eq: value } }";
		query = Document.parse(q1);
		dbc = test.find(query);
		JsonObject result = null;
		if(dbc.hasNext())
				result = dbc.next();
		assertTrue(result.keySet().size() == 1);
		assertTrue(result.keySet().iterator().next().equals("key"));
		assertTrue(result.getAsJsonPrimitive(result.keySet().iterator().next()).getAsString().equals("value"));
		
		String q2 = "{ embedded: { $eq: { key2: value2 } } }";
		query = Document.parse(q2);
		dbc = test.find(query);
		if(dbc.hasNext())
			result = dbc.next();
		assertTrue(result.keySet().size() == 1);
		assertTrue(result.keySet().iterator().next().equals("embedded"));
		assertTrue(result.getAsJsonObject("embedded").equals(Document.parse("{ key2: value2 }")));
		
		String q3 = "{ array : { $eq: [one, two, three]  } }";
		query = Document.parse(q3);
		dbc = test.find(query);
		if(dbc.hasNext())
			result = dbc.next();
		assertTrue(result.keySet().size() == 1);
		assertTrue(result.keySet().iterator().next().equals("array"));
		JsonParser parser = new JsonParser();
		JsonArray ja = (JsonArray) parser.parse("[\"one\", \"two\", \"three\"]");
		assertTrue(result.getAsJsonArray("array").equals(ja));
	}
	
	
	@Test
	public void testGetDocument() {
		DB db = new DB("data");
		DBCollection test = db.getCollection("test");
		JsonObject primitive = test.getDocument(0);
		assertTrue(primitive.getAsJsonPrimitive("key").getAsString().equals("value"));
	}
	
	// Amy's method
	@Test
	public void testFindAll() {
		DB db = new DB("data");
		DBCollection test = db.getCollection("test");
		DBCursor allItr = test.find();
		
		// first document
		JsonObject all = allItr.next();
		assertTrue(all.getAsJsonPrimitive("key").getAsString().equals("value"));
		all = allItr.next();
		
		
		// second document
		JsonObject key2value2 = new JsonObject();
		key2value2.addProperty("key2", "value2");
		assertTrue(all.getAsJsonObject("embedded").equals(key2value2));
		all = allItr.next();
		
		// third document
		JsonArray onetwothree = new JsonArray(3);
		onetwothree.add("one");
		onetwothree.add("two");
		onetwothree.add("three");
		assertTrue(all.getAsJsonArray("array").equals(onetwothree));
		
		assertFalse(allItr.hasNext());//no more documents
	}
	
	@Test
	public void testInsert() {
		DB db = new DB("data");
		DBCollection test = db.getCollection("testInsert");
		
		String json = "{\n \"key\":\"value\" \n}";	//setup
		JsonObject insertedDoc = Document.parse(json); 	//call method to be tested
		
		test.insert(insertedDoc);
		
		// verify
		JsonObject results = test.getDocument(0);
		assertTrue(results.getAsJsonPrimitive("key").getAsString().equals("value")); //verify results
		//!! invalid test, need to check if the last doc is correct instead of index 0
	}
	
	@Test
	public void testUpdate() {
		DB db = new DB("data");
		DBCollection test = db.getCollection("testUpdate");
		String json = "{\n \"key\":\"value\" \n}";	//setup
		JsonObject insertedDoc = Document.parse(json); 
		test.insert(insertedDoc);
		
		JsonObject q1 = Document.parse("{ key: { $eq: value } }");
		JsonObject update = Document.parse("{\n \"key\":\"update\" \n}");
		test.update(q1, update, false);
		
		assertTrue(test.getDocument(0).equals(update));
		test.drop();
	}
	
	@Test
	public void testRemove() {
		DB db = new DB("data");
		DBCollection test = db.getCollection("testDelete");
		String json = "{\n \"key\":\"value\" \n}";	//setup
		JsonObject insertedDoc = Document.parse(json); 	
		test.insert(insertedDoc);
		
		String q1 = "{ key: { $eq: value } }";
		JsonObject query = Document.parse(q1);
		test.remove(query, false);
		// verify
		assertTrue(test.getDocument(0) == null);
		//assertTrue(results.getAsJsonPrimitive("key").getAsString().equals("value")); //verify results
	}
	
	@Test
	public void testDropDBColl() throws IOException {
		DB db = new DB("data");
		File newf = new File("testfiles/data/testDropDBColl.json");
		   newf.createNewFile();
		DBCollection test = db.getCollection("testDropDBColl");
		String json = "{\n \"key\":\"value\" \n}"; //setup
		JsonObject insertedDoc = Document.parse(json);
		test.insert(insertedDoc);
		test.drop();
		assertTrue(!newf.exists());
	}

}
