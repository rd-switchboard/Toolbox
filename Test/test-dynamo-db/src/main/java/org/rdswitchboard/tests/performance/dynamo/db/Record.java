package org.rdswitchboard.tests.performance.dynamo.db;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.KeyAttribute;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.DeleteItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;

@DynamoDBTable(tableName="Test1M")
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
	
	@DynamoDBHashKey(attributeName="NodeID")
	public String getNodeId() {
		return nodeId;
	}
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	
	@DynamoDBAttribute(attributeName="Text1")
	public String getText1() {
		return text1;
	}
	public void setText1(String text1) {
		this.text1 = text1;
	}
	
	@DynamoDBAttribute(attributeName="Text2")
	public String getText2() {
		return text2;
	}
	public void setText2(String text2) {
		this.text2 = text2;
	}
	
	@DynamoDBAttribute(attributeName="Text3")
	public String getText3() {
		return text3;
	}
	public void setText3(String text3) {
		this.text3 = text3;
	}
	
	@DynamoDBAttribute(attributeName="Text4")
	public String getText4() {
		return text4;
	}
	public void setText4(String text4) {
		this.text4 = text4;
	}
	
	@DynamoDBAttribute(attributeName="Text5")
	public String getText5() {
		return text5;
	}
	public void setText5(String text5) {
		this.text5 = text5;
	}

	/*
	public PrimaryKey getKey() {
		return new PrimaryKey()
				.addComponents(
						new KeyAttribute("NodeID", 
								new AttributeValue()
								.withS(nodeId)));
	}
	
	public Item getItem() {
		return new Item()
				.withPrimaryKey(getKey())
				.withString("text1", text1)
				.withString("text2", text2)
				.withString("text3", text3)
				.withString("text4", text4)
				.withString("text5", text5);
	}
	*/
	
	public void save(AmazonDynamoDB dynamo) {
	    Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
	    item.put("NodeID", new AttributeValue().withS(nodeId));
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
		key.put("NodeID", new AttributeValue().withS(String.valueOf(id)));
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
	
	public static void delete(AmazonDynamoDB dynamo, Integer id) {
		Map<String, AttributeValue> key = new HashMap<String, AttributeValue>();
		key.put("NodeID", new AttributeValue().withS(String.valueOf(id)));
	
		dynamo.deleteItem(
				new DeleteItemRequest()
				.withTableName("Test1M")
				.withKey(key));
	}

	@Override
	public String toString() {
		return "Record [nodeId=" + nodeId + "]";
	}
	
	
	
}
