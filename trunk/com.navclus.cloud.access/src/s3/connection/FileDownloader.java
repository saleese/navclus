package s3.connection;

import info.connection.BasicInfo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

public class FileDownloader {

	static AmazonS3 s3;

	public void download(String keyName, String localPath) throws IOException {		
		if (keyName == null) return;
		if (localPath == null) return;
		
		s3 = new AmazonS3Client(
				new ClasspathPropertiesFileCredentialsProvider());

		GetObjectRequest request = new GetObjectRequest(BasicInfo.bucketName, keyName);
		S3Object object;
		try {
			object = s3.getObject(request);
			S3ObjectInputStream objectContent = object.getObjectContent();
			copy(objectContent, new FileOutputStream(localPath));
			
//	        System.out.println("Downloading an object");
//	        S3Object object = s3.getObject(new GetObjectRequest(bucketName, keyName));
//	        System.out.println("Content-Type: "  + object.getObjectMetadata().getContentType());
			
		} catch (AmazonS3Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		} 
	}

	/**
	 * The default buffer size to use.
	 */
	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

	public static int copy(InputStream input, OutputStream output)
			throws IOException {
		long count = copyLarge(input, output);
		if (count > Integer.MAX_VALUE) {
			return -1;
		}
		return (int) count;
	}

	public static long copyLarge(InputStream input, OutputStream output)
			throws IOException {
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		long count = 0;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

}
