package db.connection;

import info.connection.BasicInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONObject;

public class FileInfoPutter {

	public void putFileInfo(String fileName, String filePath) 
			throws ClientProtocolException, IOException {
		
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(
				"http://ec2-54-213-108-70.us-west-2.compute.amazonaws.com:3000/monitoring_files.json");
		
		System.out.println("Uploading new file info. to DB\n");

		JSONObject json = new JSONObject();
		json.put("user_id", BasicInfo.userId);
		json.put("project_id", BasicInfo.projectId);
		json.put("name", fileName);
		json.put("path", filePath);
		StringEntity input = new StringEntity(json.toString());
		input.setContentType("application/json");

		post.setEntity(input);
		HttpResponse response = client.execute(post);
		BufferedReader rd = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));
		String line = "";
		while ((line = rd.readLine()) != null) {
			System.out.println(line);
		}
	}

}