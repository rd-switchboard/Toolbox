/*******************************************************************************
 * Copyright (c) 2016 RD-Switchboard and others.
 *
 * This file is part of RD-Switchboard.
 *
 * RD-Switchboard is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * Contributors:
 *     Dima Kudriavcev - https://github.com/wizman777
 *******************************************************************************/

package org.rdswicthboard.utils.xml.split;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;

public class Properties {
	public static final String PROPERTY_INPUT_FILE = "input-file";
	public static final String PROPERTY_OUTPUT_PREFIX = "output-prefix";
	public static final String PROPERTY_CONFIG_FILE = "config-file";
	public static final String PROPERTY_INPUT_ENCODING = "input-encoding";
	public static final String PROPERTY_OUTPUT_ENCODING = "output-encoding";
	public static final String PROPERTY_ROOT_NODE = "root-node";
	public static final String PROPERTY_FORMAT_OUTPUT = "format-output";
	public static final String PROPERTY_HELP = "help";
	
	public static final String DEFAULT_OUTPUT_PREFIX = "out_";
	public static final String DEFAULT_ENCODING = StandardCharsets.UTF_8.toString();
	public static final String DEFAULT_FORMAT_OUTPUT = "no";
	
	public static Configuration fromArgs(String[] args) throws Exception {
		CommandLineParser parser = new DefaultParser();
		
		// create the Options
		Options options = new Options();
		options.addOption( "i", PROPERTY_INPUT_FILE, true, "input RDF file" );
		options.addOption( "P", PROPERTY_OUTPUT_PREFIX, true, "output files prefix (default is " + DEFAULT_OUTPUT_PREFIX +  "N.xml)" );
		options.addOption( "c", PROPERTY_CONFIG_FILE, true, "configuration file (optional)" );
		options.addOption( "I", PROPERTY_INPUT_ENCODING, true, "input file encoding (default is " + DEFAULT_ENCODING + ")" );
		options.addOption( "O", PROPERTY_OUTPUT_ENCODING, true, "output file encoding (default is " + DEFAULT_ENCODING + ")" );
		options.addOption( "r", PROPERTY_ROOT_NODE, true, "root node (with namespace if needed)" );
		options.addOption( "f", PROPERTY_FORMAT_OUTPUT, false, "format output encoding" );
		options.addOption( "h", PROPERTY_HELP, false, "print this message" );

		// parse the command line arguments
		CommandLine line = parser.parse( options, args );

		if (line.hasOption( PROPERTY_HELP )) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "java -jar oai-combine-[verion].jar [PARAMETERS]", options );
            
            System.exit(0);
		}
		
		// variables to store program properties
		CompositeConfiguration config = new CompositeConfiguration();
		config.setProperty( PROPERTY_INPUT_FILE, "publications.xml" );
		config.setProperty( PROPERTY_ROOT_NODE, "OAI-PMH" );
		config.setProperty( PROPERTY_OUTPUT_PREFIX, "nii/oai_" );
//		config.setProperty( PROPERTY_OUTPUT_PREFIX, DEFAULT_OUTPUT_PREFIX );
		config.setProperty( PROPERTY_INPUT_ENCODING, DEFAULT_ENCODING );
		config.setProperty( PROPERTY_OUTPUT_ENCODING, DEFAULT_ENCODING );
		config.setProperty( PROPERTY_FORMAT_OUTPUT, DEFAULT_FORMAT_OUTPUT );
		
		// check if arguments has input file properties 
		if (line.hasOption( PROPERTY_CONFIG_FILE )) {
			// if it does, load the specified configuration file
			Path defaultConfig = Paths.get(line.getOptionValue( PROPERTY_CONFIG_FILE ));
			if (Files.isRegularFile( defaultConfig ) && Files.isReadable( defaultConfig )) {
				config.addConfiguration(new PropertiesConfiguration( defaultConfig.toFile() ));
			} else
				throw new Exception("Invalid configuration file: " + defaultConfig.toString());
		} 
		
		// copy properties from the command line
		for (String property : new String[] { PROPERTY_INPUT_FILE,
											  PROPERTY_OUTPUT_PREFIX,
											  PROPERTY_INPUT_ENCODING,
											  PROPERTY_OUTPUT_ENCODING,
											  PROPERTY_ROOT_NODE }) {
			if (line.hasOption( property )) 
				config.setProperty( property, line.getOptionValue( property ));
		}
		
		// check if arguments has output encoding
		if (line.hasOption( PROPERTY_FORMAT_OUTPUT )) 
			config.setProperty( PROPERTY_FORMAT_OUTPUT, "yes");
					
		// the program has default output file, but input file must be presented
		if ( !config.containsKey( PROPERTY_INPUT_FILE ) )
			throw new Exception("Please specify input file");

		// the program has default output file, but input file must be presented
		if ( !config.containsKey( PROPERTY_ROOT_NODE ) )
			throw new Exception("Please specify root node");

		
		return config;
	}

}
