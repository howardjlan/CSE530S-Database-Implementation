package hw5;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class DBCursor implements Iterator<JsonObject>{
	// ?? what kind of iterator? arraylist? or iterator?
	Iterator<JsonObject> itr;
	DBCollection c;
	long size;
	
	public DBCursor(DBCollection collection, JsonObject query, JsonObject fields) {
		
		this.c = collection;
		ArrayList<JsonObject> c1 = new ArrayList<JsonObject>();
		Entry<String, JsonElement> q = query.entrySet().iterator().next(); // works only if query has just one expression
		if(q.getKey().equals("*")) {
			// select all
			for(int i = 0; i < collection.count(); i++) {
				c1.add(collection.getDocument(i));
			}
		}
		else {
			// just query relational select
			String operator = "";
			JsonElement operand = null;
			JsonElement j = q.getValue();
			while(j.isJsonObject()) {// while value is a document
				String str = j.getAsJsonObject().keySet().iterator().next();
				if(str.contains("$")) {// if the document's key has '$' in it
					operator = str;
					operand = j.getAsJsonObject().get(str);
					break;	// zoomed in close enough, so break
				}
				j = j.getAsJsonObject().get(str);
				// zoom in to next document within the query
			}
			String qKey = q.getKey();	// make String field
			String f1 = qKey;
			String f2 = "";
			boolean dot = false;
			if(qKey.contains(".")) {	// if field contains a dot, as in "f1.f2",
										// then f1 is the key of a document and f2 is a field within
				f1 = qKey.substring(0, qKey.indexOf("."));// check the type of the chars before the dot in String field: array or jsonobject
				f2 = qKey.substring(qKey.indexOf(".")+1, qKey.length());// save chars after the dot in variable f2
				dot = true;
			}
			
			for(int i = 0; i < collection.count(); i++) {	// iterate through collection
				JsonObject doc = collection.getDocument(i);
				for(Entry<String, JsonElement> e: doc.entrySet()) {	// iterate through entries
					if(e.getKey().equals(f1)) {
						if(dot) {
							if(e.getValue().isJsonArray()) {	// LEVEL 1 OF NESTING, array
								// iterate through array to find field f2
								JsonArray ja = e.getValue().getAsJsonArray();
								for(int k = 0; k < ja.size(); k++) {
									if(ja.get(k).isJsonObject()) {
										// LEVEL 2 OF NESTING, array of documents
										JsonObject jo = ja.get(k).getAsJsonObject();
										for(String gah: jo.keySet()) {
											// correct key
											if(gah.equals(f2)) {
												if(operator.equals("$eq") && jo.get(gah).equals(operand)) {
													if(!c1.contains(doc))
														c1.add(doc);
													break;
												}
												else if(operator.equals("$gt") && jo.get(gah).getAsInt() > operand.getAsInt()) {
													if(!c1.contains(doc))
														c1.add(doc);
													break;
												}
												else if(operator.equals("$gte") && jo.get(gah).getAsInt() >= operand.getAsInt()) {
													if(!c1.contains(doc))
														c1.add(doc);
													break;
												}
												else if(operator.equals("$lt") && jo.get(gah).getAsInt() < operand.getAsInt()) {
													if(!c1.contains(doc))
														c1.add(doc);
													break;
												}
												else if(operator.equals("$lte") && jo.get(gah).getAsInt() <= operand.getAsInt()) {
													if(!c1.contains(doc))
														c1.add(doc);
													break;
												}
												else if(operator.equals("$ne") && jo.get(gah).getAsInt() != operand.getAsInt()) {
													if(!c1.contains(doc))
														c1.add(doc);
													break;
												}
												
											}
										}
									}
									else if(ja.get(k).isJsonArray()) {
										// LEVEL 2 OF NESTING, array of arrays
										JsonArray jaa = ja.get(k).getAsJsonArray();
										for(int z = 0; z < jaa.size(); z++) {
											if(jaa.get(z).equals(operand)) {
												c1.add(doc);
											}
										}
									}
								}
							}
							else if(e.getValue().isJsonObject()) {	// LEVEL 1 OF NESTING, document
								JsonObject jo = e.getValue().getAsJsonObject();
								// iterate through jo to find array
								for(String ugh: jo.keySet()) {
									// LEVEL 2 OF NESTING, embedded document with array
									if(ugh.equals(f2) && jo.get(ugh).isJsonArray()) {
										JsonArray jaa = jo.get(ugh).getAsJsonArray();
										// iterate over array's contents
										Iterator<JsonElement> haaa = jaa.iterator();
										while(haaa.hasNext()) {
											JsonElement ae = haaa.next();
											if(operator.equals("$eq") && jaa.equals(operand.getAsJsonArray()) ) {
												if(!c1.contains(doc))
													c1.add(doc);
											}
											else if(operator.equals("$gt") && ae.getAsInt() > operand.getAsInt()) {
												if(!c1.contains(doc))
													c1.add(doc);
											}
											else if(operator.equals("$gte") && ae.getAsInt() >= operand.getAsInt()) {
												if(!c1.contains(doc))
													c1.add(doc);
											}
											else if(operator.equals("$lt") && ae.getAsInt() < operand.getAsInt()) {
												if(!c1.contains(doc))
													c1.add(doc);
											}
											else if(operator.equals("$lte") && ae.getAsInt() <= operand.getAsInt()) {
												if(!c1.contains(doc))
													c1.add(doc);
											}
											else if(operator.equals("$ne") && ae.getAsInt() != operand.getAsInt()) {
												if(!c1.contains(doc))
													c1.add(doc);
											}
											else {
												continue;
											}
										}
									}
									else if(ugh.equals(f2) && jo.get(ugh).isJsonPrimitive()) {
										int compare = jo.get(ugh).getAsInt();
										if(operator.equals("$eq") && compare == operand.getAsInt()) {
											if(!c1.contains(doc))
												c1.add(doc);
										}
										else if(operator.equals("$gt") && compare > operand.getAsInt()) {
											if(!c1.contains(doc))
												c1.add(doc);
										}
										else if(operator.equals("$gte") && compare >= operand.getAsInt()) {
											if(!c1.contains(doc))
												c1.add(doc);
										}
										else if(operator.equals("$lt") && compare < operand.getAsInt()) {
											if(!c1.contains(doc))
												c1.add(doc);
										}
										else if(operator.equals("$lte") && compare <= operand.getAsInt()) {
											if(!c1.contains(doc))
												c1.add(doc);
										}
										else if(operator.equals("$ne") && compare != operand.getAsInt()) {
											if(!c1.contains(doc))
												c1.add(doc);
										}
										else {
											continue;
										}
									}
								}
							}
						}
						else if(operator.equals("$eq") && e.getValue().equals(operand))  {
							if(!c1.contains(doc))
								c1.add(doc);
						}
						else if(operator.equals("$gt") && e.getValue().getAsInt() > operand.getAsInt()) {
							// I want e to be greater than operand
							if(!c1.contains(doc))
								c1.add(doc);
						}
						else if(operator.equals("$gte") && e.getValue().getAsInt() >= operand.getAsInt()) {
							if(!c1.contains(doc))
								c1.add(doc);
						}
						else if(operator.equals("$lt") && e.getValue().getAsInt() < operand.getAsInt()) {
							if(!c1.contains(doc))
								c1.add(doc);
						}
						else if(operator.equals("$lte") && e.getValue().getAsInt() <= operand.getAsInt()) {
							if(!c1.contains(doc))
								c1.add(doc);
						}
						else if(operator.equals("$ne") && e.getValue().getAsInt() != operand.getAsInt()) {
							if(!c1.contains(doc))
								c1.add(doc);
						}
						else if(operator.equals("$nin") && e.getValue().getAsString().compareTo(operand.getAsString()) <= 0) {
							// iterate through e.getvalue and see if query's array contains it
							for(int k = 0; k < e.getValue().getAsJsonArray().size(); k++) {
								if(!j.getAsJsonObject().get(operator).getAsJsonArray().contains(e.getValue().getAsJsonArray().get(k))) {
									c1.add(doc);
								}
							}
						}
						else if(operator.equals("$in")) {
							// iterate through e.getvalue and see if query's array contains it
							for(int k = 0; k < e.getValue().getAsJsonArray().size(); k++) {
								if(j.getAsJsonObject().get(operator).getAsJsonArray().contains(e.getValue().getAsJsonArray().get(k))) {
									c1.add(doc);
								}
							}
						}
					}
				}
			}
		}
		if(fields != null) {
			// select and project
			// extract keys(fields) that have value 1
			ArrayList<String> project = new ArrayList<String>();
			for(Entry<String, JsonElement> e: fields.entrySet()) {
				if(e.getValue().getAsInt() == 1) {
					project.add(e.getKey());
				}
			}
			// iterate through documents in c1
			for(JsonObject d: c1) {
				// iterate through fields in the doc
				for(String field: d.keySet()) {
					// if the field is not in project, then remove(String name)
					if(!project.contains(field)) {
						d.remove(field);
						break;
					}
				}
			}
		}
		
		size = c1.size();
		itr = c1.iterator();
	}
	
	public JsonElement queryOperandExtractor(JsonObject query, String operator) {
		Entry<String, JsonElement> q = query.entrySet().iterator().next();
		JsonElement operand = q.getValue().getAsJsonObject().get(operator);
		return operand;
	}
	
	/**
	 * Returns true if there are more documents to be seen
	 */
	public boolean hasNext() {
		return itr.hasNext();
	}

	/**
	 * Returns the next document
	 */
	public JsonObject next() {
		return itr.next();
	}
	
	/**
	 * Returns the total number of documents
	 */
	public long count() {
		return size;
	}

}
