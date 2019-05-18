package db.mysql;

public class MySQLDBUtil {
	// constants can be used directly to connect mysql db
	// example: jdbc:mysql://localhost:3306/mylaiproject?user=root&password=root&autoReconnect=true&serverTimezone=UTC
	private static final String HOSTNAME = "localhost";
	private static final String PORT_NUM = "3306"; // this is my MAMP mysql port number
	public static final String DB_NAME = "mylaiproject";
	private static final String USERNAME = "root";  //default MAMP credential
	private static final String PASSWORD = "root";
	public static final String URL = "jdbc:mysql://"
									+ HOSTNAME + ":" + PORT_NUM + "/" + DB_NAME
									+ "?user=" + USERNAME + "&password=" + PASSWORD
									+ "&autoReconnect=true&serverTimezone=UTC";
}
