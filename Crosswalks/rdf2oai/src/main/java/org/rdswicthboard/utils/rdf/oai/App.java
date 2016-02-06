package org.rdswicthboard.utils.rdf.oai;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class App {
	
	private static final String PROPERTIES_FILE = "rdf2oai.cfg";
	
	private static final String PROPERTY_INPUT_FILE = "input-file";
	private static final String PROPERTY_OUTPUT_FILE = "output-file";
	private static final String PROPERTY_CONFIG_FILE = "config-file";
	private static final String PROPERTY_INPUT_ENCODING = "input-encoding";
	private static final String PROPERTY_OUTPUT_ENCODING = "output-encoding";
	private static final String PROPERTY_SET_SPEC = "set-spec";
	private static final String PROPERTY_FORMAT_OUTPUT = "format-output";
	private static final String PROPERTY_HELP = "help";
	
	private static final String DEFAULT_OUTPUT_FILE = "output.xml";
	private static final String DEFAULT_ENCODING = StandardCharsets.UTF_8.toString();
	private static final String DEFAULT_SET_SPEC = "RDF";
	private static final String DEFAULT_FORMAT_OUTPUT = "no";
	
	private static final String TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	
	private static final String RDF_NAMESPACE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	private static final String RNF_NAMESPACE = "http://rns.nii.ac.jp/ns#";
	
	public static void main(String[] args) {
		// create the command line parser
		CommandLineParser parser = new DefaultParser();
		
		// create the Options
		Options options = new Options();
		options.addOption( "i", PROPERTY_INPUT_FILE, true, "input RDF file" );
		options.addOption( "o", PROPERTY_OUTPUT_FILE, true, "output OAI-PMH XML file (default is " + DEFAULT_OUTPUT_FILE +  ")" );
		options.addOption( "c", PROPERTY_CONFIG_FILE, true, "configuration file (" + PROPERTIES_FILE + ")" );
		options.addOption( "s", PROPERTY_SET_SPEC, true, "set spec value (default is " + DEFAULT_SET_SPEC + ")" );
		options.addOption( "I", PROPERTY_INPUT_ENCODING, true, "input file encoding (default is " + DEFAULT_ENCODING + ")" );
		options.addOption( "O", PROPERTY_OUTPUT_ENCODING, true, "output file encoding (default is " + DEFAULT_ENCODING + ")" );
		options.addOption( "f", PROPERTY_FORMAT_OUTPUT, false, "format output encoding" );
		options.addOption( "h", PROPERTY_HELP, false, "print this message" );

		try {
			// parse the command line arguments
			CommandLine line = parser.parse( options, args );

			if (line.hasOption( PROPERTY_HELP )) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp( "java -jar rdf2oai-[verion].jar [PARAMETERS] [INPUT FILE] [OUTPUT FILE]", options );
	            
	            System.exit(0);
			}
			
			// variables to store program properties
			CompositeConfiguration config = new CompositeConfiguration();
			config.setProperty( PROPERTY_INPUT_FILE, "researchers-small.rdf" );
			config.setProperty( PROPERTY_OUTPUT_FILE, DEFAULT_OUTPUT_FILE );
			config.setProperty( PROPERTY_INPUT_ENCODING, DEFAULT_ENCODING );
			config.setProperty( PROPERTY_OUTPUT_ENCODING, DEFAULT_ENCODING );
			config.setProperty( PROPERTY_SET_SPEC, DEFAULT_SET_SPEC );
			config.setProperty( PROPERTY_FORMAT_OUTPUT, DEFAULT_FORMAT_OUTPUT );
			
			// check if arguments has input file properties 
			if (line.hasOption( PROPERTY_CONFIG_FILE )) {
				// if it does, load the specified configuration file
				Path defaultConfig = Paths.get(line.getOptionValue( PROPERTY_CONFIG_FILE ));
				if (Files.isRegularFile( defaultConfig ) && Files.isReadable( defaultConfig )) {
					config.addConfiguration(new PropertiesConfiguration( defaultConfig.toFile() ));
				} else
					throw new Exception("Invalid configuration file: " + defaultConfig.toString());
			} else {
				// if it not, try to load default configurationfile
				Path defaultConfig = Paths.get(PROPERTIES_FILE);
				if (Files.isRegularFile(defaultConfig) && Files.isReadable(defaultConfig)) {
					config.addConfiguration(new PropertiesConfiguration(defaultConfig.toFile()));
				}
			}
			
			// check if arguments has input file 
			if (line.hasOption( PROPERTY_INPUT_FILE )) 
				config.setProperty( PROPERTY_INPUT_FILE, line.getOptionValue( PROPERTY_INPUT_FILE ));
			
			// check if arguments has output file
			if (line.hasOption( PROPERTY_OUTPUT_FILE )) 
				config.setProperty( PROPERTY_OUTPUT_FILE, line.getOptionValue( PROPERTY_OUTPUT_FILE ));

			// check if arguments has set spec name
			if (line.hasOption( PROPERTY_SET_SPEC )) 
				config.setProperty( PROPERTY_SET_SPEC, line.getOptionValue( PROPERTY_SET_SPEC ));
			
			// check if arguments has input encoding
			if (line.hasOption( PROPERTY_INPUT_ENCODING )) 
				config.setProperty( PROPERTY_INPUT_ENCODING, line.getOptionValue( PROPERTY_INPUT_ENCODING ));

			// check if arguments has output encoding
			if (line.hasOption( PROPERTY_OUTPUT_ENCODING )) 
				config.setProperty( PROPERTY_OUTPUT_ENCODING, line.getOptionValue( PROPERTY_OUTPUT_ENCODING ));

			// check if arguments has output encoding
			if (line.hasOption( PROPERTY_FORMAT_OUTPUT )) 
				config.setProperty( PROPERTY_FORMAT_OUTPUT, "yes");

			// check if arguments has input file without a key
			if (line.getArgs().length > 0) { 
				config.setProperty( PROPERTY_INPUT_FILE, line.getArgs()[0]);

				// check if arguments has output file without a key
				if (line.getArgs().length > 1) { 
					config.setProperty( PROPERTY_OUTPUT_FILE, line.getArgs()[1]);

					// check if there is too many arguments
					if (line.getArgs().length > 2) 
						throw new Exception("Too many arguments");					
				}
			}
			
			// The program has default output file, but input file must be presented
			if (!config.containsKey(PROPERTY_INPUT_FILE))
				throw new Exception("Please specify input file");
			
			// extract input file
			String inputFile = config.getString(PROPERTY_INPUT_FILE);
			
			// extract output file
			String outputFile = config.getString(PROPERTY_OUTPUT_FILE);
			
			// extract set spec
			String setSpecName = config.getString(PROPERTY_SET_SPEC);
			
			// extract encoding
			String inputEncoding = config.getString(PROPERTY_INPUT_ENCODING);
			String outputEncoding = config.getString(PROPERTY_OUTPUT_ENCODING);
			
			boolean formatOutput = config.getBoolean(PROPERTY_FORMAT_OUTPUT);
			
			// test if source is an regular file and it is readable
			Path source = Paths.get(inputFile);
			if (!Files.isRegularFile(source))
				throw new Exception("The input file: " + source.toString() + " is not an regular file");
			if (!Files.isReadable(source))
				throw new Exception("The input file: " + source.toString() + " is not readable");
			
			
			Path target = Paths.get(outputFile);
			if (Files.exists(target)) {
				if (!Files.isRegularFile(target))
					throw new Exception("The output file: " + target.toString() + " is not an regular file");
				if (!Files.isWritable(target))
					throw new Exception("The output file: " + target.toString() + " is not writable");
			}
			

			// create and setup document builder factory
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			
			// create new document builder
			DocumentBuilder builder = factory.newDocumentBuilder();
			       
			// create oai document
			Document oai = builder.newDocument();
			
			// set document version
			oai.setXmlVersion("1.0");
			oai.setXmlStandalone(true);
			
			// create root OAI-PMH element
			Element oaiPmh = oai.createElement("OAI-PMH");
			
			// set document namespaces
			oaiPmh.setAttribute("xmlns", "http://www.openarchives.org/OAI/2.0/");
			oaiPmh.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
			oaiPmh.setAttribute("xsi:schemaLocation", "http://www.openarchives.org/OAI/2.0/ http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd");
			
			// append root node
			oai.appendChild(oaiPmh);
	        
			// create responseDate element
	        Element responseDate = oai.createElement("responseDate");
	        
	        // create simple date format
	        DateFormat dateFormat = new SimpleDateFormat(TIME_FORMAT);
	        
	        // generate date
	        String date = dateFormat.format(new Date());
	        
	        // set current date and time
	        responseDate.setTextContent(date);
	        
	        oaiPmh.appendChild(responseDate);
			
			Element listRecords = oai.createElement("ListRecords");
			oaiPmh.appendChild(listRecords);
			
			// create xpath factory
			XPathFactory xPathfactory = XPathFactory.newInstance();
						
			// create namespace context
			NamespaceContext namespaceContext = new NamespaceContext() {
	            public String getNamespaceURI(String prefix) {
	                if (prefix.equals("rdf")) 
	                    return RDF_NAMESPACE;
	                else if (prefix.equals("rns")) 
	                	return RNF_NAMESPACE;
	                else
	                	return null;
	            }

	            @Override
	            public Iterator<?> getPrefixes(String val) {
	                throw new IllegalAccessError("Not implemented!");
	            }

	            @Override
	            public String getPrefix(String uri) {
	                throw new IllegalAccessError("Not implemented!");
	            }
	        };
	        
	        // create xpath object
	     	XPath xpath = xPathfactory.newXPath();
	     	// set namespace contex
	     	xpath.setNamespaceContext(namespaceContext);
			
	     	// create XPath expressions
			XPathExpression idExpr = xpath.compile("/rdf:RDF/rns:Researcher/@rdf:about");
			XPathExpression emptyExpr = xpath.compile("//text()[normalize-space(.) = '']");
			
			// read file into a sring
			String content = new String(Files.readAllBytes(source), inputEncoding);
			
			// split all records
			String[] records = content.split("<\\?xml\\s+version=\"[\\d\\.]+\".*\\?>\\s*");
			
			// process all records
			for (String rec : records) {
				if (!StringUtils.isBlank(rec)) {
					// convert string to input stream
					ByteArrayInputStream input =  new ByteArrayInputStream(("<?xml version=\"1.0\"?>" + rec).getBytes(StandardCharsets.UTF_8.toString()));
					
					// parse the xml document
					Document doc = builder.parse(input);
					
					// remove all spaces
					NodeList emptyNodes = (NodeList) emptyExpr.evaluate(doc, XPathConstants.NODESET);
					// Remove each empty text node from document.
					for (int i = 0; i < emptyNodes.getLength(); i++) {
					    Node emptyTextNode = emptyNodes.item(i);
					    emptyTextNode.getParentNode().removeChild(emptyTextNode);
					}
					
					// obtain researcher id
					String id = (String) idExpr.evaluate(doc, XPathConstants.STRING);
					if (StringUtils.isEmpty(id)) 
						throw new Exception("The record identifier can not be empty");
					
					// create record element
					Element record = oai.createElement("record");
					listRecords.appendChild(record);
					
					// create header element
					Element header = oai.createElement("header");
					record.appendChild(header);

					// create identifier element
					Element identifier = oai.createElement("identifier");
					identifier.setTextContent(id);
					header.appendChild(identifier);

					// create datestamp element
					Element datestamp = oai.createElement("datestamp");
					datestamp.setTextContent(date);
					header.appendChild(datestamp);

					// create set spec element if it exists
					if (!StringUtils.isEmpty(setSpecName)) {
						Element setSpec = oai.createElement("setSpec");
						setSpec.setTextContent(setSpecName);
						header.appendChild(setSpec);
					}
					
					// create metadata element
					Element metadata = oai.createElement("metadata");
					record.appendChild(metadata);
					
					// import the record
					metadata.appendChild(oai.importNode(doc.getDocumentElement(), true));					
				}
			}
			
			// create transformer factory
	        TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        
	        // create transformer
	        Transformer transformer = transformerFactory.newTransformer();
	        transformer.setOutputProperty(OutputKeys.ENCODING, outputEncoding);
	        
	        if (formatOutput) {
		        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
	        } else
	        	transformer.setOutputProperty(OutputKeys.INDENT, "no");
	        
	        // create dom source
	        DOMSource oaiSource = new DOMSource(oai);
	        
	        // create stream result
	        StreamResult result = new StreamResult(target.toFile());
	        
	        // stream xml to file
	        transformer.transform(oaiSource, result);
	        
	        // optional stream xml to console for testing
	        //StreamResult consoleResult = new StreamResult(System.out);
	        //transformer.transform(oaiSource, consoleResult);
			
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			
			//e.printStackTrace();
		    
            System.exit(1);
		}       
	}	
}
