package org.rdswitchboard.tests.performance.dynamo.db;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;

public class Record {
	private String nodeId;
	private String text1;
	private String text2;
	private String text3;
	private String text4;
	private String text5;
	
	private static final Random random = new Random();

	public Record() {
		
	}
	
	public Record(int nodeId) {
		this.nodeId = Integer.toString(nodeId);
		this.text1 = randomText();
		this.text2 = randomText();
		this.text3 = randomText();
		this.text4 = randomText();
		this.text5 = randomText();
	}
	
	public static String randomText() {
		return new BigInteger(130, random).toString(32);
	}
	
	public String getNodeId() {
		return nodeId;
	}
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	public String getText1() {
		return text1;
	}
	public void setText1(String text1) {
		this.text1 = text1;
	}
	public String getText2() {
		return text2;
	}
	public void setText2(String text2) {
		this.text2 = text2;
	}
	public String getText3() {
		return text3;
	}
	public void setText3(String text3) {
		this.text3 = text3;
	}
	public String getText4() {
		return text4;
	}
	public void setText4(String text4) {
		this.text4 = text4;
	}
	public String getText5() {
		return text5;
	}
	public void setText5(String text5) {
		this.text5 = text5;
	}
	
	public void save(AmazonDynamoDB dynamo) {
	    Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
	    item.put("NodeID", new AttributeValue().withN(nodeId));
	    item.put("text1", new AttributeValue().withS(text1));
	    item.put("text2", new AttributeValue().withS(text2));
	    item.put("text3", new AttributeValue().withS(text3));
	    item.put("text4", new AttributeValue().withS(text4));
	    item.put("text5", new AttributeValue().withS(text5));
	    dynamo.putItem(
	    		new PutItemRequest()
	    		.withTableName("Test1M")
	    		.withItem(item));
	}

	public static Record load(AmazonDynamoDB dynamo, Integer id) {
		Map<String, AttributeValue> key = new HashMap<String, AttributeValue>();
		key.put("NodeID", new AttributeValue().withN(String.valueOf(id)));
	    GetItemResult result = dynamo.getItem(
	    		new GetItemRequest()
	    		.withTableName("Test1M")
	    		.withKey(key));
	    if ( result.getItem() == null )
	        return null;

	    Record record = new Record();
	    record.setNodeId(String.valueOf(id));
	    if ( result.getItem().get("text1") != null )
	    	record.setText1(result.getItem().get("text1").getS());
	    if ( result.getItem().get("text2") != null )
	    	record.setText1(result.getItem().get("text2").getS());
	    if ( result.getItem().get("text3") != null )
	    	record.setText1(result.getItem().get("text3").getS());
	    if ( result.getItem().get("text4") != null )
	    	record.setText1(result.getItem().get("text4").getS());
	    if ( result.getItem().get("text5") != null )
	    	record.setText1(result.getItem().get("text5").getS());
	    
	    return record;
	}

	@Override
	public String toString() {
		return "Record [nodeId=" + nodeId + "]";
	}
	
	
	
}
