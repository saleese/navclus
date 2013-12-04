package connection;
import info.connection.BasicInfo;

import com.navclus.cloud.preference.*;

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
//			System.out.println(BasicInfo.getUser());
//			System.out.println(BasicInfo.getProject());
//			download file info. from DB
			JSONArray array = (new FileInfoGetter()).getFileInfo();
			
			for (Object o : array) {
				JSONObject fileInfo = (JSONObject) o;

				Long project_id = (Long) fileInfo.get("project_id");
				if (project_id == null) continue;
				
//				System.out.println(BasicInfo.projectId);
//				System.out.println(project_id);
				if (BasicInfo.projectId == project_id) {
//					System.out.println(BasicInfo.projectId);
//					System.out.println(project_id);

					String name = (String) fileInfo.get("name");
//					System.out.println("name: " + name);

					String path = (String) fileInfo.get("path");
//					System.out.println("path:" + path);
					
//					donwload several files from s3
					(new FileDownloader()).download(path, monitoringDir + "/" +name);
				}
			}
			System.out.println("files are updated");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
