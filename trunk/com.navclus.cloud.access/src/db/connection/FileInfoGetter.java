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

	public void main(String[] args) throws ClientProtocolException, IOException {
		getFileInfo();
	}

	public JSONArray getFileInfo() throws ClientProtocolException, IOException {
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(
				"http://ec2-54-213-108-70.us-west-2.compute.amazonaws.com:3000/api/monitoring_files?project_name=" + BasicInfo.getProject());
		System.out.println("http://ec2-54-213-108-70.us-west-2.compute.amazonaws.com:3000/api/monitoring_files?project_name=" + BasicInfo.getProject());
		HttpResponse response = client.execute(request);
		BufferedReader rd = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));
		String line = "";
		while ((line = rd.readLine()) != null) {
			Object obj = JSONValue.parse(line);
			JSONArray array = (JSONArray) obj;
			
			response = null;
			request = null;
			client = null;
			return array;
		}
		
		response = null;
		request = null;
		client = null;
		return null;
	}
}