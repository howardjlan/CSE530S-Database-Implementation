package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import hw5.Document;

class DocumentTester {
	
	/*
	 * Things to consider testing:
	 * 
	 * Invalid JSON
	 * 
	 * Properly parses embedded documents (done)
	 * Properly parses arrays (done)
	 * Properly parses primitives (done)
	 * 
	 * Object to embedded document (done)
	 * Object to array (done)
	 * Object to primitive (done)
	 */

	
	@Test
	public void testJsonObjectToEmbeddedDocument() {
		// setup
		JsonObject jObject = Document.parse("{ name: \"cake\", recipe: { ingredients: [ eggs, flour, vanilla ], time: 60 } }");
		
		// test method
		String embeddedDoc = Document.toJsonString(jObject);
		assertTrue(embeddedDoc.equals("{\"name\":\"cake\",\"recipe\":{\"ingredients\":[\"eggs\",\"flour\",\"vanilla\"],\"time\":60}}"));
	}	
	
	@Test
	public void testJsonObjectToArray() {
		// setup
		JsonObject jArray = Document.parse("{ array: [\"one\", \"two\", \"three\"] }");
		
		// test method
		String array = Document.toJsonString(jArray);
		assertTrue(array.equals("{\"array\":[\"one\",\"two\",\"three\"]}"));
	}
	
	@Test
	public void testJsonObjectToPrimitive() {
		// setup
		JsonObject jPrim = Document.parse("{ \"key\":\"value\" }");
		
		// test method
		String prim = Document.toJsonString(jPrim);
		assertTrue(prim.equals("{\"key\":\"value\"}"));
	}
	
	@Test
	public void testParsePrimitive() {
		String json = "{ \"key\":\"value\" }";	//setup
		JsonObject results = Document.parse(json); //call method to be tested
		assertTrue(results.getAsJsonPrimitive("key").getAsString().equals("value")); //verify results
	}
	
	@Test
	public void testParseArray() {
		String json = "{ array: [\"one\", \"two\", \"three\"] }";
		JsonObject result = Document.parse(json);
		assertTrue(result.get("array").isJsonArray());
		assertTrue(result.get("array").getAsJsonArray().get(0).getAsString().equals("one"));
		assertTrue(result.get("array").getAsJsonArray().get(1).getAsString().equals("two"));
		assertTrue(result.get("array").getAsJsonArray().get(2).getAsString().equals("three"));
	}
	
	@Test
	public void testEmbeddedDocument() {
		String json = "{ name: \"cake\", recipe: { ingredients: [ eggs, flour, vanilla ], time: 60 } }";
		JsonObject result = Document.parse(json);
		assertTrue(result.get("recipe").isJsonObject());
		assertTrue(result.get("recipe").equals(Document.parse("{ ingredients: [ eggs, flour, vanilla ], time: 60 }")));
	}

}
