package connection;
import info.connection.BasicInfo;

import java.io.IOException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import db.connection.FileInfoPutter;
import s3.connection.FileUploader;

public class ServerUpdater {

	public void main(String[] args) {
		String fileName = "User01Task1.xml";
		FileUpdate(fileName);
	}

	public void FileUpdate(String localPath) {	
		try {
			if ((BasicInfo.getProject() == null)
					|| (BasicInfo.getProject().equals("")) 
					|| (BasicInfo.getUser() == null)
					|| (BasicInfo.getUser().equals(""))) {
				return;
			}
			
			// upload file info. to DB
			(new FileInfoPutter()).putFileInfo(localPath);
//			
//			System.out.println("file information is uploaded");
			
			// upload file to s3
			(new FileUploader()).upload(localPath);

			System.out.println("files are uploaded");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
