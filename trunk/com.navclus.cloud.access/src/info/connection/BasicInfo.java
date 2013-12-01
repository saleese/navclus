package info.connection;

import java.util.prefs.Preferences;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;

import com.navclus.cloud.preference.NavClusPrefPlugin;

public class BasicInfo {
		
	public static String bucketName = "monitoringdata";
	
	public static long projectId = 1;
	
	public static long userId = 1;
	
	public static String getUser() {
		String text = Platform.getPreferencesService().
				  getString("com.navclus.cloud.preference", "stringPreferenceEmail", "", null); 
		return text;
	}
	
	public static String getProject() {
		String text = Platform.getPreferencesService().
				  getString("com.navclus.cloud.preference", "stringPreferenceProject", "", null); 
		return text;
	}
	
}
