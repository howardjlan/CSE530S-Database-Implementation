package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;

import hw5.DB;
import hw5.DBCollection;
import hw5.DBCursor;

class CursorTester {
	
	/**
	 * Things to consider testing:
	 * 
	 * hasNext (done)
	 * count (done)
	 * next (done)
	 */
	
	@Test
	public void testHasNext() {
		DB db = new DB("data");
		DBCollection test = db.getCollection("test");
		DBCursor results = test.find();
		assertTrue(results.hasNext());
	}
	
	@Test
	public void testCount() {
		DB db = new DB("data");
		DBCollection test = db.getCollection("test");
		DBCursor results = test.find();
		assertTrue(results.count() == 3);
	}
	
	@Test
	public void testNext() {
		DB db = new DB("data");
		DBCollection test = db.getCollection("test");
		DBCursor results = test.find();
		assertTrue(results.next() != null);
	}

}
