package s3.connection;

import java.io.File;
import java.io.IOException;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

import db.connection.PathConverter;
import info.connection.BasicInfo;


public class FileUploader {
	
	AmazonS3 s3;
	String keyName;

	public String upload(String localPath)
			throws IOException {

		/*
		 * This credentials provider implementation loads your AWS credentials
		 * from a properties file at the root of your classpath.
		 * 
		 * Important: Be sure to fill in your AWS access credentials in the
		 * AwsCredentials.properties file before you try to run this sample.
		 * http://aws.amazon.com/security-credentials
		 */
		s3 = new AmazonS3Client(new ClasspathPropertiesFileCredentialsProvider());

		Region usWest2 = Region.getRegion(Regions.AP_NORTHEAST_1);
		s3.setRegion(usWest2);
		
		keyName = (new PathConverter()).Client2Server(localPath);

		System.out.println("===========================================");
		System.out.println("Getting Started with Amazon S3");
		System.out.println("===========================================\n");

		try {
			/*
			 * Upload an object to your bucket - You can easily upload a file to
			 * S3, or upload directly an InputStream if you know the length of
			 * the data in the stream. You can also specify your own metadata
			 * when uploading to S3, which allows you set a variety of options
			 * like content-type and content-encoding, plus additional metadata
			 * specific to your applications.
			 */
			System.out.println("Uploading a new object to S3 from a file\n");
			File file = new File(localPath);

			PutObjectRequest putObj = new PutObjectRequest(BasicInfo.bucketName, keyName, file);
			//making the object Public
            putObj.setCannedAcl(CannedAccessControlList.PublicRead);			
			s3.putObject(putObj);

			return keyName;
			
		} catch (AmazonServiceException ase) {
			System.out
					.println("Caught an AmazonServiceException, which means your request made it "
							+ "to Amazon S3, but was rejected with an error response for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
			return null;
		} catch (AmazonClientException ace) {
			System.out
					.println("Caught an AmazonClientException, which means the client encountered "
							+ "a serious internal problem while trying to communicate with S3, "
							+ "such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
			return null;
		}
	}
}
