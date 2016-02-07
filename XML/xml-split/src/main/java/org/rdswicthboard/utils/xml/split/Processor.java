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

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.configuration.Configuration;

public class Processor {

	private String inputFile;
	private String outputPrefix;
	private String inputEncoding;
	private String outputEncoding;
	private String rootNode;
	private boolean formatOutput;

	public Processor() {
		
	}
	
	public Processor(Configuration config) {
		// extract input file
		inputFile = config.getString(Properties.PROPERTY_INPUT_FILE);
		
		// extract output file
		outputPrefix = config.getString(Properties.PROPERTY_OUTPUT_PREFIX);
		
		// extract input encoding
		inputEncoding = config.getString(Properties.PROPERTY_INPUT_ENCODING);

		// extract output encoding
		outputEncoding = config.getString(Properties.PROPERTY_OUTPUT_ENCODING);
		
		// extract root node
		rootNode = config.getString(Properties.PROPERTY_ROOT_NODE);
		
		// ectract format output flag
		formatOutput = config.getBoolean(Properties.PROPERTY_FORMAT_OUTPUT);
	}
	
	public String getInputFile() {
		return inputFile;
	}

	public void setInputFile(String inputFile) {
		this.inputFile = inputFile;
	}

	public String getInputEncoding() {
		return inputEncoding;
	}

	public String getOutputPrefix() {
		return outputPrefix;
	}

	public void setOutputPrefix(String outputPrefix) {
		this.outputPrefix = outputPrefix;
	}

	public void setInputEncoding(String inputEncoding) {
		this.inputEncoding = inputEncoding;
	}

	public String getOutputEncoding() {
		return outputEncoding;
	}

	public void setOutputEncoding(String outputEncoding) {
		this.outputEncoding = outputEncoding;
	}

	public boolean isFormatOutput() {
		return formatOutput;
	}

	public void setFormatOutput(boolean formatOutput) {
		this.formatOutput = formatOutput;
	}
	
	@Override
	public String toString() {
		return "Processor [inputFile=" + inputFile + ", outputPrefix=" + outputPrefix + ", inputEncoding="
				+ inputEncoding + ", outputEncoding=" + outputEncoding + ", rootNode=" + rootNode + ", formatOutput="
				+ formatOutput + "]";
	}

	public int process() throws Exception {
		// test if source is an regular file and it is readable
		Path source = Paths.get(inputFile);
		if (!Files.isRegularFile(source))
			throw new Exception("The input file: " + source.toString() + " is not an regular file");
		if (!Files.isReadable(source))
			throw new Exception("The input file: " + source.toString() + " is not readable");
		
		// extrat parent path
		Path targetPrefix = Paths.get(outputPrefix);
		Path parent = targetPrefix.getParent();
		
		// try to create parent path if it exists
		if (null != parent)
			Files.createDirectories(parent);
		
		// create file template
		String template = targetPrefix.getFileName().toString() + "%d.xml";
		
		// generate expression
		String expression = String.format("<\\?\\s*xml(\\s+version=\"[\\d\\.]+\")?(\\s+encoding=\"[-\\w]+\")?(\\s+standalone=\"(yes|no)\")?\\s*\\?>\\s*<\\s*%s[^>]*>[\\s\\S]*?<\\s*\\/\\s*%s\\s*>", rootNode, rootNode); 
	
		// create pattern  
		Pattern pattern = Pattern.compile(expression);

		// read file into a string
		String content = new String(Files.readAllBytes(source), inputEncoding);
		
		// create matcher and apply it to the content
		Matcher matcher = pattern.matcher(content);
				
		// init file number
		int fileNumber = 0;
		
		// process all records
		while (matcher.find()) {
			String fileName = String.format(template, fileNumber++);
			
			Path path = null == parent ? Paths.get(fileName) : Paths.get(parent.toString(), fileName);
			
			Files.write(path, matcher.group()/*.replaceAll("\\?><", "?>\n<")*/.getBytes(outputEncoding));
		}
		
		return fileNumber;
	}
}
