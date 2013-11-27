package db.connection;

import info.connection.BasicInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

public class FileInfoGetter {

	public static void main(String[] args) throws ClientProtocolException,
			IOException {
		getFileInfo();
	}

	public static void getFileInfo() throws ClientProtocolException,
			IOException {
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(
				"http://ec2-54-213-108-70.us-west-2.compute.amazonaws.com:3000/monitoring_files.json");
		HttpResponse response = client.execute(request);
		BufferedReader rd = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));
		String line = "";
		while ((line = rd.readLine()) != null) {
			// System.out.println(line);

			Object obj = JSONValue.parse(line);
			JSONArray array = (JSONArray) obj;

			for (Object o : array) {
				JSONObject fileInfo = (JSONObject) o;

				Long project_id = (Long) fileInfo.get("project_id");
				if (BasicInfo.projectId == project_id) {

					Long user_id = (Long) fileInfo.get("user_id");
					System.out.println(user_id);

					String name = (String) fileInfo.get("name");
					System.out.println(name);

					String path = (String) fileInfo.get("path");
					System.out.println(path);

					String url = (String) fileInfo.get("url");
					System.out.println(url);
				}
			}
		}
	}
}