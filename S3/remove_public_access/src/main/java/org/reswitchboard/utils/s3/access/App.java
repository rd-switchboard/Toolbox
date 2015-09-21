package org.reswitchboard.utils.s3.access;

import java.util.List;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.Grant;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.StringUtils;

public class App {

	public static void main(String[] args) {
		try {
	        if (args.length == 0 || StringUtils.isNullOrEmpty(args[0]))
	        	 throw new IllegalArgumentException("Bucket name can not be empty");
			
			String bucketName = args[0];
			String prefix = null;
			if (args.length > 1)
				prefix = args[1];	
			
			AmazonS3 s3client = new AmazonS3Client(new ProfileCredentialsProvider());        
	
			ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
			.withBucketName(bucketName);
			
			if (!StringUtils.isNullOrEmpty(prefix)) 
				listObjectsRequest.setPrefix(prefix);
	
			ObjectListing objectListing;
			
			do {
				objectListing = s3client.listObjects(listObjectsRequest);
				for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
					String key = objectSummary.getKey();
					System.out.println( " - " + key);
					
					for (int nAttempt = 1;; ++nAttempt) {
						try {
							
							AccessControlList acl = s3client.getObjectAcl(bucketName, key);
							List<Grant> grants = acl.getGrantsAsList();
							for (Grant grant : grants) {
							//	System.out.println( "      Grant: " + grant.toString());
								
								if (grant.getGrantee().equals(GroupGrantee.AllUsers)) {
									System.out.println( "      Revoking public access");

									acl.revokeAllPermissions(GroupGrantee.AllUsers);
									s3client.setObjectAcl(bucketName, key, acl);	
									
									break;
								}
							}

							break;
						} catch(Exception e) {
							System.out.println("Error: " + e.toString());
							
							if (nAttempt >= 10) {
								throw new Exception("Maximum number of invalid attempts has been reeched");
							}
						
							// double back-off delay
							Thread.sleep((long) (Math.pow(2, nAttempt) * 50));
						}						
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
