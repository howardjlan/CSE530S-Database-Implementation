package hw5;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class DBCollection {
	/**
	 * Constructs a collection for the given database
	 * with the given name. If that collection doesn't exist
	 * it will be created.
	 */
	DB database;
	String name;
	long count;
	
	public DBCollection(DB database, String name) {
		this.database = database;
		this.name = name;
		
		if(!(new File(database.f.getPath() + "/" + name + ".json").exists())) {
			// make new file
			File newJsonFile = new File(database.f.getPath() + "/" + name + ".json");
			try {
				newJsonFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			// getting count
			String coll = "";
			try {
				// !!not guaranteed that database will be named "data", don't hard code
				Scanner s = new Scanner(new File(database.f.getPath() + "/" + name + ".json"));
				while(s.hasNextLine()) {
					coll = coll + "\n" + s.nextLine();
				}
				s.close();
				count = coll.split("\n\t\n").length;
				if(coll.trim().isEmpty())
					count = 0;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	/**
	 * Returns a cursor for all of the documents in
	 * this collection.
	 */
	public DBCursor find() {
		// make query in the form of JsonObject
		JsonObject query = new JsonObject();
		query.addProperty("*", "*");
		
		// construct cursor
		DBCursor dbc = new DBCursor(this, query, null);
		return dbc;
	}
	
	/**
	 * Finds documents that match the given query parameters.
	 * 
	 * @param query relational select
	 * @return
	 */
	public DBCursor find(JsonObject query) {
		return new DBCursor(this, query, null);
	}
	
	/**
	 * Finds documents that match the given query parameters.
	 * 
	 * @param query relational select
	 * @param projection relational project
	 * @return
	 */
	public DBCursor find(JsonObject query, JsonObject projection) {
		return new DBCursor(this, query, projection);
	}
	
	/**
	 * Inserts documents into the collection
	 * Must create and set a proper id before insertion
	 * When this method is completed, the documents
	 * should be permanently stored on disk.
	 * @param documents
	 */
	public void insert(JsonObject... documents) {
		String uid = UUID.randomUUID().toString();
		
		String jStr = "";
		for(JsonObject j: documents) {
			// add "_id" field with the value being uid
			j.addProperty("_id", uid);
			// add "flag" field, with value 1 to show that the doc is active
			//j.addProperty("flag", 1);
			jStr = jStr + Document.toJsonString(j) + "\n\t\n";
		}
		// write to file
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter(database.f.getPath() + "/" + name + ".json", true));
			bw.append(jStr);
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		count = count + documents.length;
	}
	
	/**
	 * Locates one or more documents and replaces them
	 * with the update document.
	 * @param query relational select for documents to be updated
	 * @param update the document to be used for the update
	 * @param multi true if all matching documents should be updated
	 * 				false if only the first matching document should be updated
	 */
	public void update(JsonObject query, JsonObject update, boolean multi) {
		query.remove("_id");
		DBCursor dbc = this.find(query);	// all documents in this collection that match the query
		JsonObject result = null;
		while(dbc.hasNext()) {
			result = dbc.next();
			//result.remove("_id");
			//String _id = result.get("_id").getAsString();
			// save id
			//this.remove(result, multi);
			String coll = "";
			try {
				// !!not guaranteed that database will be named "data", don't hard code
				// read in all documents
				Scanner s = new Scanner(new File(database.f.getPath() + "/" + name + ".json"));
				while(s.hasNextLine()) {
					coll = coll + "\n" + s.nextLine();
				}
				s.close();
				
				// delete document(s)
				//String qStr = Document.toJsonString(query);
				String qStr = Document.toJsonString(result);
				if(multi) {
					coll = coll.replaceAll("\\"+ qStr, "");
				} else {
					coll = coll.replaceFirst("\\"+qStr, "");
					
				}
				coll = coll.trim();
				// write to file
				BufferedWriter bw;
				bw = new BufferedWriter(new FileWriter(database.f.getPath() + "/" + name + ".json"));
				bw.write(coll);
				bw.close();
				
				count = count - dbc.count();	// !!update count
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			//update.addProperty("_id", _id);
			this.insert(update);

			// iterate through result's keys
			//Entry<String, JsonElement> q = query.entrySet().iterator().next();
//			for(String key: result.keySet()) {
//				if(q.getKey().equals(key) && ) {
//					//result.remove("flag");
//					result.addProperty("flag", 0);
//					if(!multi) {
//						return;
//					}
//				}
//			}

		}
	}
	
	/**
	 * Removes one or more documents that match the given
	 * query parameters
	 * @param query relational select for documents to be removed
	 * @param multi true if all matching documents should be updated
	 * 				false if only the first matching document should be updated
	 */
	public void remove(JsonObject query, boolean multi) {
		query.remove("_id");
		DBCursor dbc = this.find(query);	// all documents in this collection that match the query
		//JsonObject result = null;
		String coll = "";
		try {
			// !!not guaranteed that database will be named "data", don't hard code
			// read in all documents
			Scanner s = new Scanner(new File(database.f.getPath() + "/" + name + ".json"));
			while(s.hasNextLine()) {
				coll = coll + "\n" + s.nextLine();
			}
			s.close();
			
			// delete document(s)
			//String qStr = Document.toJsonString(query);
			while(dbc.hasNext()) {
				String qStr = Document.toJsonString(dbc.next());
				if(multi) {
					coll = coll.replaceAll("\\"+ qStr, "");
				} else {
					coll = coll.replaceFirst("\\"+qStr, "");
				}
			}
			coll = coll.trim();
			
			// write to file
			BufferedWriter bw;
			bw = new BufferedWriter(new FileWriter(database.f.getPath() + "/" + name + ".json"));
			bw.write(coll);
			bw.close();
			
			count = count - dbc.count();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	/**
	 * Returns the number of documents in this collection
	 */
	public long count() {
		return count;
	}
	
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the ith document in the collection.
	 * Documents are separated by a line that contains only a single tab (\t)
	 * Use the parse function from the document class to create the document object
	 */
	public JsonObject getDocument(int i) {
		JsonParser parser = new JsonParser();
		String coll = "";
		try {
			// !!not guaranteed that database will be named "data", don't hard code
			Scanner s = new Scanner(new File(database.f.getPath() + "/" + name + ".json"));
			while(s.hasNextLine()) {
				coll = coll + "\n" + s.nextLine();
			}
			s.close();
			
			String doc = coll.split("\n\t\n")[i];
			//!!Document.parse(doc);
			JsonObject jso = (JsonObject) parser.parse(doc);
			return jso;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Drops this collection, removing all of the documents it contains from the DB
	 */
	public void drop() {
		//new File(database.f.getPath() + "/" + name + ".json").delete();
		try {
			Files.delete(Paths.get(database.f.getPath() + "/" + name + ".json"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
