package org.rdswitchboard.api.limits;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

public class App {
	private static final String PROPERTIES_FILE = "properties/check_limits.properties";
	private static final String DEF_HOST = "localhost";
	private static final String DEF_DB = "dbs_graph";
		
	public static void main(String[] args) {
		try {
			// read properties file
			String propertiesFile = PROPERTIES_FILE;
			
			if (args.length != 0 && args[0] != null && args[0].isEmpty())
				propertiesFile = args[0];
			
        	Properties properties = new Properties();
        	try (InputStream in = new FileInputStream(propertiesFile)) {
            	    properties.load(in);
        	}

			String mHost = properties.getProperty("host", DEF_HOST);
			String mUser = properties.getProperty("user");
			String mPassword = properties.getProperty("password");
			String mDatabase = properties.getProperty("database", DEF_DB);

			System.out.println("Loading JDBC driver");
			// The newInstance() call is a work around for some
	        // broken Java implementations
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			
			System.out.println("Connecting to the database");
			// Obtain the mysql connection
			try (Connection conn = DriverManager.getConnection("jdbc:mysql://"+mHost+"/"+mDatabase+"?user="+mUser+"&password="+mPassword)) {
				System.out.println("Creating a statment");
				// create mysql statment
	            try (Statement stmt1 = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) { 
	            	System.out.println("Querying all data sources");
					// load all datasets objects
	            	try (ResultSet ds = stmt1.executeQuery("SELECT user_id, level, limit FROM users")) {
						while (ds.next()) {
							Integer userId = ds.getInt("user_id");
							Integer level = ds.getInt("level");
							Integer limit = ds.getInt("limit");
							System.out.println("Processing user with id: " + userId);
							
							if (level != 2) {
								Integer newLevel = 1;
								if (limit > 0) {
									System.out.println("Querying ussage");
									try (Statement stmt2 = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
										try (ResultSet ds2 = stmt2.executeQuery("SELECT COUNT(*) AS `cnt` FROM logs WHERE `user_id`='"+userId+"' AND NOW() - `time` <= '86400'")) {
											if (ds2.next()) {
												Integer count = ds2.getInt("cnt");
												if (count > limit)
													newLevel = 3;	
											}
										}
									}
								}
								
								if (level != newLevel) {
									ds.updateInt("level", newLevel);
									ds.updateRow();
								}
							}
							
						}
					}
	            } 
			}
			
		} catch (Exception e) {
			e.printStackTrace();
	    }
	}
}
