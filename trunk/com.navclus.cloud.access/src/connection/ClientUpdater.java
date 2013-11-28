package connection;
import info.connection.BasicInfo;

import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import db.connection.FileInfoGetter;
import db.connection.FileInfoPutter;
import s3.connection.FileDownloader;
import s3.connection.FileUploader;

public class ClientUpdater {

	public static void main(String[] args) {
//		FileUpdate();
	}

	public static void FileUpdate(String monitoringDir) {
		try {
						
//			download file info. from DB
			JSONArray array = (new FileInfoGetter()).getFileInfo();
			
			for (Object o : array) {
				JSONObject fileInfo = (JSONObject) o;

				Long project_id = (Long) fileInfo.get("project_id");
				if (BasicInfo.projectId == project_id) {

					String name = (String) fileInfo.get("name");
					System.out.println("name: " + name);

					String path = (String) fileInfo.get("path");
					System.out.println("path:" + path);
					
//					donwload several files from s3
					(new FileDownloader()).download(path, monitoringDir + "/" +name);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
