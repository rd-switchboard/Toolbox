package org.rdswitchboard.exporters.local;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.rdswitchboard.utils.graph.GraphNode;
import org.rdswitchboard.utils.graph.GraphRelationship;
import org.rdswitchboard.utils.graph.GraphSchema;
import org.rdswitchboard.utils.graph.GraphUtils;
import org.rdswitchboard.utils.neo4j.local.Neo4jUtils;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.tooling.GlobalGraphOperations;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Exporter {
	private static final int MAX_COMMANDS = 1024;

	private GraphDatabaseService graphDb;
	private GlobalGraphOperations global;
	
	private final File schemaFolder;
	private final File nodesFolder;
	private final File relationshipsFolder;
	
	private int nodeCounter;
	private int relationshipCounter;

	private int nodeFileCounter;
	private int relationshipFileCounter;

	private List<GraphSchema> graphSchemas;
	private List<GraphNode> graphNodes;
	private List<GraphRelationship> graphRelationships;
	
	private static final ObjectMapper mapper = new ObjectMapper();

	public Exporter(String dbFolder, final String outputFolder) {
		System.out.println("Source Neo4j folder: " + dbFolder);
		System.out.println("Target folder: " + outputFolder);
	
		graphDb = Neo4jUtils.getReadOnlyGraphDb(dbFolder);
		global = Neo4jUtils.getGlobalOperations(graphDb);
		
		// Set output folder
		File folder = new File(outputFolder);

		schemaFolder = GraphUtils.getSchemaFolder(folder);
		nodesFolder = GraphUtils.getNodeFolder(folder);
		relationshipsFolder = GraphUtils.getRelationshipFolder(folder);
		
		schemaFolder.mkdirs();
		nodesFolder.mkdirs();
		relationshipsFolder.mkdirs();
	}	
	
	public void process() throws JsonGenerationException, JsonMappingException, IOException {
		nodeCounter = relationshipCounter = nodeFileCounter = relationshipFileCounter = 0;

		long beginTime = System.currentTimeMillis();
		
		try ( Transaction tx = graphDb.beginTx() ) {
			Iterable<Node> nodes = global.getAllNodes();
			
			for (Node node : nodes) {
				exportNode(node);
			
				if (null != graphNodes && graphNodes.size() >= MAX_COMMANDS)
					saveNodes();
				
				if (null != graphRelationships && graphRelationships.size() >= MAX_COMMANDS)
					saveRelationships();
			}
		}

		if (null != graphNodes)
			saveNodes();
		
		if (null != graphRelationships)
			saveRelationships();
		
		if (null != graphSchemas)
			saveSchema();
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(String.format("Done. Exported %d nodes and %d relationships over %d ms. Average %f ms per node", 
				nodeCounter, relationshipCounter, endTime - beginTime, (float)(endTime - beginTime) / (float)nodeCounter));
	}
	
	private void exportNode(Node node) {
	//	System.out.println("Node: " + node.getId());
			
		GraphNode graphNode = GraphUtils.createNode(node);
		if (null == graphNodes)
			graphNodes = new ArrayList<GraphNode>();
		graphNodes.add(graphNode);
		++nodeCounter;

		Iterable<Relationship> relationships = node.getRelationships(Direction.OUTGOING);
		if (null != relationships && relationships.iterator().hasNext()) {
			GraphConnection start = GraphUtils.createConnection(node);
			graphSchema.addIndex(start);

			for (Relationship relationship : relationships)
				exportRelationship(start, relationship);
		}
	}
	
	
	private void exportRelationship(GraphConnection start, Relationship relationship)  {
	//	System.out.println("Relationship: " + relationship.getId());

		GraphConnection end = GraphUtils.createConnection(relationship.getEndNode());
		graphSchema.addIndex(end);
		
		GraphRelationship graphRelationship = GraphUtils.createRelationship(start, end, relationship);
		
		if (null == graphRelationships)
			graphRelationships = new ArrayList<GraphRelationship>();
		graphRelationships.add(graphRelationship);		
		++relationshipCounter;		
	}
	
	private void saveSchema() throws JsonGenerationException, JsonMappingException, IOException {
		mapper.writeValue(new File(schemaFolder, GraphUtils.GRAPH_SCHEMA), graphSchemas);
		graphSchemas = null;
	}
	
	private void saveNodes() throws JsonGenerationException, JsonMappingException, IOException {
		String fileName = Long.toString(nodeFileCounter) + GraphUtils.GRAPH_EXTENSION;
		mapper.writeValue(new File(nodesFolder, fileName), graphNodes);
		graphNodes = null;
		++nodeFileCounter;
	}
	
	private void saveRelationships() throws JsonGenerationException, JsonMappingException, IOException {
		String fileName = Long.toString(relationshipFileCounter) + GraphUtils.GRAPH_EXTENSION;
		mapper.writeValue(new File(relationshipsFolder, fileName), graphRelationships);
		graphRelationships = null;
		++relationshipFileCounter;
	}
}
