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

package org.rdswitchboard.utils.s3.find;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.StringUtils;

public class App {
	public static void main(String[] args) {
		try {
	        if (args.length != 2)
	        	 throw new IllegalArgumentException("Bucket name and search string can not be empty");
	        
	        String buckey = args[0];
	        String search = args[1];
	        String prefix = null;
	        int pos = buckey.indexOf('/');
	        if (pos > 0) {
	        	prefix = buckey.substring(pos + 1);
	        	buckey = buckey.substring(0, pos);
	        }
	        
	        AmazonS3 s3client = new AmazonS3Client(new InstanceProfileCredentialsProvider());
	        
//			AmazonS3 s3client = new AmazonS3Client(new ProfileCredentialsProvider());        
	
			ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
				.withBucketName(buckey);
			
			if (!StringUtils.isNullOrEmpty(prefix)) 
				listObjectsRequest.setPrefix(prefix);
	
			ObjectListing objectListing;
			
			do {
				objectListing = s3client.listObjects(listObjectsRequest);
				for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
					String key = objectSummary.getKey();
					System.out.println( " - " + key);
					
			        S3Object object = s3client.getObject(new GetObjectRequest(buckey, key));
			        String str = IOUtils.toString(object.getObjectContent());
			        if (str.contains(search)) {
			        	System.out.println( "Found!");
			        	
			        	FileUtils.writeStringToFile(new File("s3/" + key), str);
			        }
				}
				listObjectsRequest.setMarker(objectListing.getNextMarker());
			} while (objectListing.isTruncated());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
