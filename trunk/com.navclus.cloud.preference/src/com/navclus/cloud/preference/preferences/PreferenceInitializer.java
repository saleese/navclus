package com.navclus.cloud.preference.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.navclus.cloud.preference.NavClusPrefPlugin;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = NavClusPrefPlugin.getDefault().getPreferenceStore();
//		store.setDefault(PreferenceConstants.P_STRING,
//				"Default value");
	}

}
