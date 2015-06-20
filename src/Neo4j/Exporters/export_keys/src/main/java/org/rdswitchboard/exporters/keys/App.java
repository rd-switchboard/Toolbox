package org.rdswitchboard.exporters.keys;

import java.io.IOException;

import org.rdswitchboard.utils.aggrigation.AggrigationUtils;

public class App {
	private static final String SOURCE_NEO4J_FOLDER = "neo4j";	
	//private static final String FIRST_LABEL = "RDA";	
	//private static final String SECOND_LABEL = "ORCID";	
	//private static final String FIRST_PROPERTY = "rda_url";	
	//private static final String SECOND_PROPERTY = "orcid_url";	
	//private static final int MIN_RELATIONSHIPS = 1;	
	private static final int MAX_RELATIONSHIPS = 4;	
	private static final String OUTPUT_FOLDER = "keys";	
	private static final int PAGINATION = 0;	
	
	private static final String[] LABELS = new String[] { 
		AggrigationUtils.LABEL_FIGSHARE,
		AggrigationUtils.LABEL_DRYAD, 
		AggrigationUtils.LABEL_RDA, 
		AggrigationUtils.LABEL_ORCID
	};
	
	private static final String[] PROPERTIES = new String[] {
		AggrigationUtils.PROPERTY_FIGSHARE_URL,
		AggrigationUtils.PROPERTY_KEY, 
		AggrigationUtils.PROPERTY_RDA_URL, 
		AggrigationUtils.PROPERTY_ORCID_URL
	};
	
	private static String getArgument(String[] args, int id) {
		return args.length > id && !args[id].isEmpty() ? args[id] : null;
	}
		
	
	private static String getArgument(String[] args, int id, String defValue) {
		String value = getArgument(args, id);
		return null == value ? defValue : value;
	}
	
	private static int getIntArgument(String[] args, int id, int defValue) {
		String value = getArgument(args, id);
		return null == value ? defValue : Integer.parseInt(value);
	}		
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String neo4jFolder = getArgument(args, 0, SOURCE_NEO4J_FOLDER);
		String outputFolder = getArgument(args, 1, OUTPUT_FOLDER);
		int maxRels = getIntArgument(args, 2, MAX_RELATIONSHIPS);
		int pagination = getIntArgument(args, 3, PAGINATION);
		
		try {
			Exporter expoter = new Exporter(neo4jFolder, outputFolder);
			
			int length = LABELS.length;
			for (int i = 0; i < length - 1; ++i)
				for (int j = i+1; j < length; ++j)
					expoter.process(LABELS[i], PROPERTIES[i], LABELS[j], PROPERTIES[j], maxRels, pagination);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
	}
}
