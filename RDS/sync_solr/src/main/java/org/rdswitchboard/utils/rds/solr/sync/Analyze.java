/* This file is part of RD-Switchboard.
 * RD-Switchboard is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>. 
 *
 * Author: https://github.com/wizman777
 */

package org.rdswitchboard.utils.rds.solr.sync;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Analyze {
	private final int total;
	private final int chunkSize;
	private final int numChunk;
	
	@JsonCreator
	public Analyze(
			@JsonProperty("total") int total, 
			@JsonProperty("chunkSize") int chunkSize, 
			@JsonProperty("numChunk") int numChunk) {
		this.total = total;
		this.chunkSize = chunkSize;
		this.numChunk = numChunk;
	}

	public int getTotal() {
		return total;
	}

	public int getChunkSize() {
		return chunkSize;
	}

	public int getNumChunk() {
		return numChunk;
	}

	@Override
	public String toString() {
		return "Analyze [total=" + total + ", chunkSize=" + chunkSize
				+ ", numChunk=" + numChunk + "]";
	}
}
