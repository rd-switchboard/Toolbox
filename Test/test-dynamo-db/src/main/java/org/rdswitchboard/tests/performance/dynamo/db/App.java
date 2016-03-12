package org.rdswitchboard.tests.performance.dynamo.db;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

public class App {
	
	private static final int TEST_ITEMS = 1000;

	public static void main(String[] args) {
		App app = new App();
		app._main(args);
	}
	
	private void _main(String[] args) {
	
		AmazonDynamoDB dynamo = new AmazonDynamoDBClient(new InstanceProfileCredentialsProvider());
		dynamo.setRegion(Region.getRegion(Regions.US_WEST_2));
		//DynamoDBMapper mapper = new DynamoDBMapper(dynamo);
		
		testBenchmark(TEST_ITEMS);
		testSaveDb(TEST_ITEMS, dynamo);
		testLoadDb(TEST_ITEMS, dynamo);
	}
	
	private void testBenchmark(int testN) {
		long startTime = System.currentTimeMillis();
		
		for (int n = 0; n < testN; ++n)
		{
			new Record(n);
		}
		
		long estimatedTime = System.currentTimeMillis() - startTime;		
		System.out.println("Created " + testN + " records over " + estimatedTime + " ms. That is " + (double) estimatedTime / (double) testN + " ms per record.");
	}
	
	private void testSaveDb(int testN, AmazonDynamoDB dynamo) {
		long startTime = System.currentTimeMillis();

		for (int n = 0; n < testN; ++n) 
		{
			Record r = new Record(n);
			r.save(dynamo);
		}
		
		long estimatedTime = System.currentTimeMillis() - startTime;		
		System.out.println("Saved " + testN + " records over " + estimatedTime + " ms. That is " + (double) estimatedTime / (double) testN + " ms per record.");
	}
	
	private void testLoadDb(int testN, AmazonDynamoDB dynamo) {
		long startTime = System.currentTimeMillis();
		
		for (int n = 0; n < testN; ++n) 
		{
			Record.load(dynamo, n);
		}

		long estimatedTime = System.currentTimeMillis() - startTime;		
		System.out.println("Loaded " + testN + " records over " + estimatedTime + " ms. That is " + (double) estimatedTime / (double) testN + " ms per record.");
	}

}
