package hw5;

import java.io.File;

public class DB {
	/**
	 * Creates a database object with the given name.
	 * The name of the database will be used to locate
	 * where the collections for that database are stored.
	 * For example if my database is called "library",
	 * I would expect all collections for that database to
	 * be in a directory called "library".
	 * 
	 * If the given database does not exist, it should be
	 * created.
	 */
	public File f;	// directory
	public DB(String name) {
		//if the directory already exists?
		f = new File("testfiles/" + name);
		if(!f.exists())
		{
			new File("testfiles/" + name).mkdir();
		}
	}
	
	/**
	 * Retrieves the collection with the given name
	 * from this database. The collection should be in
	 * a single file in the directory for this database.
	 * 
	 * Note that it is not necessary to read any data from
	 * disk at this time. Those methods are in DBCollection.
	 */
	public DBCollection getCollection(String name) {
		DBCollection dbc = new DBCollection(this , name);
		return dbc;
	}
	
	/**
	 * Drops this database and all collections that it contains
	 */
	public void dropDatabase() {
		File[] all = f.listFiles();
		if(all != null) {
			for(File file: all) {
				file.delete();
			}
		}
		f.delete();
	}
	
	
}
