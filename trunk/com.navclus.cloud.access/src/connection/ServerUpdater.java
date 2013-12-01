package connection;
import info.connection.BasicInfo;

import java.io.IOException;

import db.connection.FileInfoPutter;
import s3.connection.FileUploader;

public class ServerUpdater {

	public static void main(String[] args) {
		String fileName = "User01Task1.xml";
		FileUpdate(fileName);
	}

	public static void FileUpdate(String localPath) {	
		try {
			// upload file to s3
			(new FileUploader()).upload(localPath);

			// upload file info. to DB
			(new FileInfoPutter()).putFileInfo(localPath);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
