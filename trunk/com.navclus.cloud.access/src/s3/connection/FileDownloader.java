package s3.connection;

/*
 * Copyright 2010-2013 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
import info.connection.BasicInfo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

/**
 * This sample demonstrates how to make basic requests to Amazon S3 using the
 * AWS SDK for Java.
 * <p>
 * <b>Prerequisites:</b> You must have a valid Amazon Web Services developer
 * account, and be signed up to use Amazon S3. For more information on Amazon
 * S3, see http://aws.amazon.com/s3.
 * <p>
 * <b>Important:</b> Be sure to fill in your AWS access credentials in the
 * AwsCredentials.properties file before you try to run this sample.
 * http://aws.amazon.com/security-credentials
 */
public class FileDownloader {

	static AmazonS3 s3;
	static String keyName;

	public void download(String fileName) throws IOException {
//		fileName = "User01Task1.xml";
		keyName = "projects/" + BasicInfo.projectId + "/users/" + BasicInfo.userId + "/" + fileName;

		s3 = new AmazonS3Client(
				new ClasspathPropertiesFileCredentialsProvider());

		GetObjectRequest request = new GetObjectRequest(BasicInfo.bucketName, keyName);
		S3Object object = s3.getObject(request);
		S3ObjectInputStream objectContent = object.getObjectContent();
		copy(objectContent, new FileOutputStream(fileName));
		
//        System.out.println("Downloading an object");
//        S3Object object = s3.getObject(new GetObjectRequest(bucketName, keyName));
//        System.out.println("Content-Type: "  + object.getObjectMetadata().getContentType());

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
