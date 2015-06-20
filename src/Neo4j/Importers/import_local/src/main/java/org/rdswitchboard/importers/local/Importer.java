package org.rdswitchboard.importers.local;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.rdswitchboard.utils.graph.GraphIndex;
import org.rdswitchboard.utils.graph.GraphNode;
import org.rdswitchboard.utils.graph.GraphRelationship;
import org.rdswitchboard.utils.graph.GraphSchema;
import org.rdswitchboard.utils.graph.GraphUtils;
import org.rdswitchboard.utils.neo4j.local.Neo4jUtils;
import org.rdswitchboard.utils.aggrigation.AggrigationUtils;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Importer {
	private Map<String, Index<Node>> indexes = new HashMap<String, Index<Node>>();

	private GraphDatabaseService graphDb;
	
	private static final ObjectMapper mapper = new ObjectMapper(); 
	
	private File schemaFolder;
	private File nodesFolder;
	private File relationshipsFolder;
	
	private int nodeCounter;
	private int relationshipCounter;
		
	public Importer(final String neo4jFolder, final String imputFolder) throws Exception {
		System.out.println("Source folder: " + imputFolder);
		System.out.println("Target Neo4j folder: " + neo4jFolder);
		
		graphDb = Neo4jUtils.getGraphDb( neo4jFolder );
		
		// Set input folder
		File folder = new File(imputFolder);
		schemaFolder = GraphUtils.getSchemaFolder(folder);
		if (!schemaFolder.isDirectory())
			throw new Exception("Invalid schema path: " + schemaFolder.getPath());
		
		nodesFolder = GraphUtils.getNodeFolder(folder);
		if (!nodesFolder.isDirectory())
			throw new Exception("Invalid nodes path: " + nodesFolder.getPath());
		
		relationshipsFolder = GraphUtils.getRelationshipFolder(folder);
		if (!relationshipsFolder.isDirectory())
			throw new Exception("Invalid relationships path: " + relationshipsFolder.getPath());
	}
		
	public void process() throws Exception {
		importSchema();
		importNodes();
		importRelationships();
	}	
	
	private void importSchema() throws Exception {
		System.out.println("Creating constrants and obtain indexes");

		long beginTime = System.currentTimeMillis();
		
		GraphSchema[] graphSchema = mapper.readValue(new File(schemaFolder, GraphUtils.GRAPH_SCHEMA), GraphSchema[].class);
		
		// First make sure the constant exists
		for (GraphSchema schema : graphSchema)
			try ( Transaction tx = graphDb.beginTx() ) 
			{
				if (schema.isUnique())
					Neo4jUtils.createConstrant(graphDb, schema.getLabel(), schema.getKey());
				else 
					Neo4jUtils.createIndex(graphDb, schema.getLabel(), schema.getKey());
			
				tx.success();
			}
			
/*		// Next obtain an index
		for (String index : graphSchema.getIndexes())
			try ( Transaction tx = graphDb.beginTx() ) 
			{
				obtainIndex(index);
			}*/
		
		long endTime = System.currentTimeMillis();
		System.out.println(String.format("Done. Imported %d indexes over %d ms. Average %f ms per index", 
				graphSchema.length, endTime - beginTime, (float)(endTime - beginTime) / (float)graphSchema.length));

	}
	
	private void importNodes() throws Exception {
		System.out.println("Importing nodes");
		
		nodeCounter = 0;
		long beginTime = System.currentTimeMillis();
		
		File[] nodeFiles = nodesFolder.listFiles();
		for (File nodeFile : nodeFiles) 
			if (!nodeFile.isDirectory()) { 
				System.out.println("Processing node: " + nodeFile.getName());

				GraphNode[] graphNodes = mapper.readValue(nodeFile, GraphNode[].class);
				try ( Transaction tx = graphDb.beginTx() ) 
				{
					for (GraphNode graphNode : graphNodes) 
						importNode(graphNode);		
					
					tx.success();
				}
			}
		
		long endTime = System.currentTimeMillis();
		System.out.println(String.format("Done. Imported %d nodes over %d ms. Average %f ms per node", 
				nodeCounter, endTime - beginTime, (float)(endTime - beginTime) / (float)nodeCounter));
	}
	
	private void importRelationships() throws Exception {
		System.out.println("Importing relationsips");
		
		relationshipCounter = 0;
		long beginTime = System.currentTimeMillis();
		
		File[] relationshipFiles = relationshipsFolder.listFiles();
		for (File relationshipFile : relationshipFiles) 
			if (!relationshipFile.isDirectory()) { 
				System.out.println("Processing relationship: " + relationshipFile.getName());
				
				GraphRelationship[] graphRelationships = mapper.readValue(relationshipFile, GraphRelationship[].class);
				
				try ( Transaction tx = graphDb.beginTx() ) 
				{		
					for (GraphRelationship graphRelationship : graphRelationships) 
						importRelationship(graphRelationship);
					
					tx.success();
				}
			}
		
		long endTime = System.currentTimeMillis();
		System.out.println(String.format("Done. Imported %d relationships over %d ms. Average %f ms per relationship", 
				relationshipCounter, endTime - beginTime, (float)(endTime - beginTime) / (float)relationshipCounter));
	}	
	
	private void importNode(GraphNode graphNode) throws Exception {
		if (null == graphNode.getIndexes() || graphNode.getIndexes().size() != 1)
			throw new Exception("Only node with one legacy index are supported");
		GraphIndex graphIndex = graphNode.getIndexes().get(0);
		Index<Node> index = getIndex(graphIndex.getIndex());
		Node node = (Node) index.get(graphIndex.getKey(), graphIndex.getValue()).getSingle();
		if (null == node) {
			node = graphDb.createNode();
				 
			++nodeCounter; 
				
			if (null != graphNode.getProperties())
				for (Entry<String, Object> entry : graphNode.getProperties().entrySet()) {  
					Object value = entry.getValue();
					if (null != value) {
						if (value instanceof Collection<?>) {
							String[] arr = ((Collection<?>) value).toArray(new String[((Collection<?>) value).size()]);
							if (arr.length == 1)
								node.setProperty(entry.getKey(), arr[0]);
							else if (arr.length > 1)
								node.setProperty(entry.getKey(), arr);
						}
						else
							node.setProperty(entry.getKey(), value);
					}
				}
			
			if (null != graphNode.getLabels())
				for (String label : graphNode.getLabels())
					node.addLabel(DynamicLabel.label(label));
			
			index.add(node, graphIndex.getKey(), graphIndex.getValue());
		}
	}
	
	private void importRelationship(GraphRelationship graphRelationship) throws Exception {
		Node nodeStart = findNode(graphRelationship.getStart());
		if (null == nodeStart) 
			throw new Exception("Error in graph, unable to find start node " + graphRelationship.getStart().getIndex() + " with key: " + graphRelationship.getStart().getKey());
		Node nodeEnd = findNode(graphRelationship.getEnd());
		if (null == nodeEnd)
			throw new Exception("Error in graph, unable to find end node " + graphRelationship.getEnd().getIndex() + " with key: " + graphRelationship.getEnd().getKey());

		RelationshipType relationship = DynamicRelationshipType.withName(graphRelationship.getRelationship());
		
		Iterable<Relationship> rels = nodeStart.getRelationships(relationship, Direction.OUTGOING);
		for (Relationship rel : rels) 
			if (rel.getEndNode().getId() == nodeEnd.getId())
				return;
		
		Relationship rel = nodeStart.createRelationshipTo(nodeEnd, relationship);
			
		++relationshipCounter;
			
		if (null != graphRelationship.getProperties())
			for (Entry<String, Object> entry : graphRelationship.getProperties().entrySet())
				rel.setProperty(entry.getKey(), entry.getValue());
	}
	
	private Index<Node> getIndex(String label) {
		if ( indexes.containsKey(label) ) 
			return indexes.get( label );
		
		Index<Node> index = graphDb.index().forNodes( label );
		indexes.put(label, index);
		return index;
	}
	
	/*
	private String getCorrectLabel(String label) throws Exception {
		label = label.toLowerCase();
		for (String l : labels)
			if (l.toLowerCase().equals(label))
				return l;
		
		return null;
	}
*/
	private Node findNode(GraphIndex index) {
		return getIndex(index.getIndex())
				.get(index.getKey(), index.getValue())
				.getSingle();
		
	/*	ResourceIterable<Node> nodes = graphDb.findNodesByLabelAndProperty(
				DynamicLabel.label(connection.getType()), 
				AggrigationUtils.PROPERTY_KEY, 
				connection.getKey());
		
		try (ResourceIterator<Node> noded = nodes.iterator()) {
			if (noded.hasNext())
				return noded.next();
			else
				return null;
		}*/
	}
}
