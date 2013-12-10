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
import org.json.simple.JSONValue;

public class FileInfoPutter {

	public void putFileInfo(String localPath) 
			throws ClientProtocolException, IOException {
		
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(
				"http://ec2-54-213-108-70.us-west-2.compute.amazonaws.com:3000/api/monitoring_files");
		
		System.out.println("Uploading new file info. to DB\n");

		String onlyFileName = localPath.substring(localPath.lastIndexOf('/') + 1);
		JSONObject json = new JSONObject();
		json.put("user_email", BasicInfo.getUser());
		json.put("project_name", BasicInfo.getProject());
		json.put("name", onlyFileName);
//		json.put("path", (new PathConverter()).Client2Server(localPath));
		StringEntity input = new StringEntity(json.toString());
		input.setContentType("application/json");

		post.setEntity(input);
		HttpResponse response = client.execute(post);
		BufferedReader rd = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));
		String line = "";
		while ((line = rd.readLine()) != null) {
			System.out.println(line);
			Object obj = JSONValue.parse(line);
			JSONObject jsonObject = (JSONObject) obj;
			
			BasicInfo.userId = (Long) jsonObject.get("user_id");			
		}
		rd = null;
		response = null;
		input = null;
		json = null;
		post = null;
		client = null;
	}

}