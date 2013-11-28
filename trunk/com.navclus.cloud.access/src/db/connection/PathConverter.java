package db.connection;

import info.connection.BasicInfo;

public class PathConverter {
	
	public String Client2Server(String path) {		
		String onlyFileName = path.substring(path.lastIndexOf('/') + 1);
		String keyName = "projects/" + BasicInfo.projectId + "/users/" + BasicInfo.userId + "/" + onlyFileName;
					
		return keyName;
	}
	
//	public String Server2Client(String keyName) {		
//		String monitoringDir = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString()
//				+ "/MonitoringData";
//		return path;
//	}	

}
