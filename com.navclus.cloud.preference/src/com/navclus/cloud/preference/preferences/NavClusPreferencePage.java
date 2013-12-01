package com.navclus.cloud.preference.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import com.navclus.cloud.preference.NavClusPrefPlugin;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

public class NavClusPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public NavClusPreferencePage() {
		super(GRID);
		setPreferenceStore(NavClusPrefPlugin.getDefault().getPreferenceStore());
		setDescription("Please, specify the e-mail and the project name, already registered to the NavClus cloud system:");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {
		addField(new StringFieldEditor(PreferenceConstants.P_STRING_EMAIL,
				"&e-mail:", getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.P_STRING_PROJECT,
				"&project:", getFieldEditorParent()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

}