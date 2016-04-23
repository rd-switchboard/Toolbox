/* This file is part of RD-Switchboard.
 * RD-Switchboard is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>. 
 *
 * Author: https://github.com/wizman777
 */

package org.rdswitchboard.utils.rds.records.clean;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class App {
	private static final String PROPERTIES_FILE = "properties/clean_ands_records.properties";	
	
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

			String mHost = properties.getProperty("host", "localhost");
			System.out.println("Host: " + mHost);
			String mUser = properties.getProperty("user");
			System.out.println("User: " + mUser);
			String mPassword = properties.getProperty("password");

			URL url = new URL(properties.getProperty("base_url") + "/registry_object/delete/");

			Cookie cookie = new Cookie("PHPSESSID", properties.getProperty("session"));

			String inputFile = properties.getProperty("input", "rds_keys.csv");

			int maxObjects = Integer.parseInt(properties.getProperty("max_objects", "256"));
			
			System.out.println("Loading index");
			Set<String> index = new HashSet<String>();
			
			// load data
			try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
			    String line;
			    while ((line = br.readLine()) != null) {
			    	index.add(line);
			    }
			}
			
			System.out.println("Loading JDBC driver");
			// The newInstance() call is a work around for some
	        // broken Java implementations
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			
			int deletedRows = 0;
			
			long beginTime = System.currentTimeMillis();
			
			Client client = Client.create();

			System.out.println("Connecting to the database");
			// Obtain the mysql connection
			try (Connection conn = DriverManager.getConnection("jdbc:mysql://"+mHost+"/dbs_registry?user="+mUser+"&password="+mPassword)) {
				System.out.println("Creating a statment");
				// create mysql statment
	            try (Statement stmt1 = conn.createStatement()) { //java.sql.ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
	            	System.out.println("Querying all data sources");
					// load all datasets objects
	            	try (ResultSet ds = stmt1.executeQuery("SELECT data_source_id FROM data_sources")) {
						while (ds.next()) {
							Integer sourceId = ds.getInt("data_source_id");
							System.out.println("Processing data source with id " + sourceId);
							
							List<Integer> arrIds = new ArrayList<Integer>();
							int counter = 0;
							
							try (Statement stmt2 = conn.createStatement()) {
								try (ResultSet rs = stmt2.executeQuery("SELECT registry_object_id, `key` FROM registry_objects WHERE data_source_id=" + sourceId + " AND class='collection'")) {
									 while (rs.next()) { 
										 ++counter;
										 if (!index.contains(rs.getString("key"))) 
											 arrIds.add(rs.getInt("registry_object_id"));
									 }
								}
							}
							System.out.println("Found " + counter + " records");
							
							if (!arrIds.isEmpty()) {
								System.out.println("Deleting " + arrIds.size() + " records");
								int steps = arrIds.size() / maxObjects + 1;
								for (int step = 0; step < steps; ++step) {
									
									StringBuilder sb = new StringBuilder();
									sb.append("data_source_id="+sourceId + "&select_all=false");
									addParam(sb, "filters[sort][updated]", "desc");
									addParam(sb, "filters[filter][status]", "PUBLISHED");
									
									int from = step * maxObjects;
									int to = Math.min(arrIds.size(), (step + 1) * maxObjects);
									for (int i = from; i < to; ++i) 
										addParam(sb, "affected_ids[]", arrIds.get(i).toString());
									
									System.out.println("Erasing " + (to - from) + " objects");
	
									String d = sb.toString();
								//	System.out.println(d);
											
									WebResource webResource = client.resource(url.toString());
									ClientResponse response = webResource
											 	.header("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:38.0) Gecko/20100101 Firefox/38.0")
											 	.accept( MediaType.APPLICATION_JSON, "*/*" )
											 	.acceptLanguage( "en-US", "en" )
											 	.type( MediaType.APPLICATION_FORM_URLENCODED )
				                                .cookie(cookie)
									 			.post(ClientResponse.class, d);
									 
									if (response.getStatus() != 200) {
										throw new RuntimeException("Failed : HTTP error code : "
												+ response.getStatus());
									}

									String output = response.getEntity(String.class);
								 
									System.out.println(output);
								 
									deletedRows += to - from;
								}
							}
						}
					}
	            } 
			}
            	
			long endTime = System.currentTimeMillis();
			
			System.out.println(String.format("Done. Deleted %d records. Spent %d ms", 
					deletedRows, endTime - beginTime));
			
		} catch (Exception e) {
			e.printStackTrace();
	    }
	}
	
	public static void addParam(StringBuilder sb, String param, String value) throws UnsupportedEncodingException {
		sb.append("&");
		sb.append(URLEncoder.encode(param, "UTF-8"));
		sb.append("=");
		sb.append(value);
	}
}
