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

import org.apache.commons.configuration.Configuration;

public class App {

	public static void main(String[] args) {
		try {
			// extract program configuration
			Configuration config = Properties.fromArgs(args);
		
			// construct processor object
			Processor processor = new Processor(config);

			// process
			int recorded = processor.process();
			
			System.out.println("Recorded " + recorded + " files");
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			
			//e.printStackTrace();
		    
            System.exit(1);
		}       
	}

}
