package connection;
import java.io.IOException;

import db.connection.FileInfoPutter;
import s3.connection.FileUploader;

public class ClientUpdater {

	public static void main(String[] args) {
		String fileName = "User01Task1.xml";
		FileUpdate(fileName);
	}

	public static void FileUpdate(String fileName) {
//		try {
//			// upload file to s3
//			String filePath = (new FileUploader()).upload(fileName);
//
//			// upload file info. to DB
//			(new FileInfoPutter()).putFileInfo(fileName, filePath);
//
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}
