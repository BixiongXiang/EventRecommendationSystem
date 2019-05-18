package db;

import db.mysql.MySQLConnection;

/**
 * we build a connection with factory pattern, for we may change db in future
 * 
 * the build function are static, so can call directly
 * */
public class DBConnectionFactory {
	// This should change based on the pipeline.
	// This should change based on the pipeline.
	private static final String DEFAULT_DB = "mysql";  // may change to mongo db
	
	public static DBConnection getConnection(String db) {
		switch (db) {
		case "mysql":
			 return new MySQLConnection();
			//return null;
		case "mongodb":
			// return new MongoDBConnection();
			return null;
		default:
			throw new IllegalArgumentException("Invalid db:" + db);
		}

	}

	public static DBConnection getConnection() {
		return getConnection(DEFAULT_DB);
	}
}
