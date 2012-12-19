/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.monitor.usage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.core.InteractionContextManager;
import org.eclipse.mylyn.internal.monitor.ui.ActionExecutionMonitor;
import org.eclipse.mylyn.internal.monitor.ui.ActivityChangeMonitor;
import org.eclipse.mylyn.internal.monitor.ui.KeybindingCommandMonitor;
import org.eclipse.mylyn.internal.monitor.ui.MenuCommandMonitor;
import org.eclipse.mylyn.internal.monitor.ui.MonitorUiPlugin;
import org.eclipse.mylyn.internal.monitor.ui.PerspectiveChangeMonitor;
import org.eclipse.mylyn.internal.monitor.ui.PreferenceChangeMonitor;
import org.eclipse.mylyn.internal.monitor.ui.WindowChangeMonitor;
import org.eclipse.mylyn.monitor.core.IInteractionEventListener;
import org.eclipse.mylyn.monitor.ui.AbstractCommandMonitor;
import org.eclipse.mylyn.monitor.ui.IActionExecutionListener;
import org.eclipse.mylyn.monitor.ui.IMonitorLifecycleListener;
import org.eclipse.mylyn.monitor.ui.MonitorUi;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
//import org.eclipse.mylyn.commons.core.StatusHandler;

//import org.eclipse.update.internal.ui.security.Authentication;

/**
 * @author Mik Kersten
 * @author Shawn Minto
 */
public class UiUsageMonitorPlugin extends AbstractUIPlugin {

	public static final String PREF_USER_ID = "org.eclipse.mylyn.user.id";

	public static String VERSION = "1.0";

	public static String UPLOAD_FILE_LABEL = "USAGE";

	private static final long HOUR = 3600 * 1000;

	private static final long DAY = HOUR * 24;

	private static final long DELAY_ON_USER_REQUEST = 5 * DAY;

	public static final String DEFAULT_TITLE = "Mylyn Feedback";

	public static final String DEFAULT_DESCRIPTION = "Fill out the following form to help us improve Mylyn based on your input.\n";

	public static final long DEFAULT_DELAY_BETWEEN_TRANSMITS = 21 * 24 * HOUR;

	public static final String DEFAULT_ETHICS_FORM = "doc/study-ethics.html";

	public static final String DEFAULT_VERSION = "";

	public static final String DEFAULT_UPLOAD_SERVER = "http://mylyn.eclipse.org/monitor/upload";

	public static final String DEFAULT_UPLOAD_SERVLET_ID = "/GetUserIDServlet";

	public static final String DEFAULT_UPLOAD_SERVLET = "/MylarUsageUploadServlet";

	public static final String DEFAULT_ACCEPTED_URL_LIST = "";

	public static final String DEFAULT_CONTACT_CONSENT_FIELD = "false";

	public static final String ID_UI_PLUGIN = "org.eclipse.mylyn.ui";

	public static String MONITOR_LOG_NAME = "monitor-log";

	public static final String ID_PLUGIN = "org.eclipse.mylyn.monitor.usage";

	private InteractionEventLogger interactionLogger;

	private final String customizingPlugin = null;

	private PreferenceChangeMonitor preferenceMonitor;

	private PerspectiveChangeMonitor perspectiveMonitor;

	private ActivityChangeMonitor activityMonitor;

	private MenuCommandMonitor menuMonitor;

	private WindowChangeMonitor windowMonitor;

	private KeybindingCommandMonitor keybindingCommandMonitor;

	private static UiUsageMonitorPlugin plugin;

	private final List<IActionExecutionListener> actionExecutionListeners = new ArrayList<IActionExecutionListener>();

	private final List<AbstractCommandMonitor> commandMonitors = new ArrayList<AbstractCommandMonitor>();

	private ResourceBundle resourceBundle;

	private static Date lastTransmit = null;

//	private final Authentication uploadAuthentication = null;

	private static boolean performingUpload = false;

	private boolean questionnaireEnabled = true;

	private boolean backgroundEnabled = false;

//	private final StudyParameters studyParameters = new StudyParameters();

	private final ListenerList lifecycleListeners = new ListenerList();

	private FileOutputStream outputStream;

	private SelectionMonitor selectionMonitor;

	public static class UiUsageMonitorStartup implements IStartup {

		public void earlyStartup() {
			final IWorkbench workbench = PlatformUI.getWorkbench();
			workbench.getDisplay().asyncExec(new Runnable() {
				public void run() {
					UiUsageMonitorPlugin.getDefault().selectionMonitor = new SelectionMonitor();
					MonitorUiPlugin.getDefault()
							.getSelectionMonitors()
							.add(UiUsageMonitorPlugin.getDefault().selectionMonitor);

//					UiUsageMonitorPlugin.getDefault().addMonitoredPreferences(
//							WorkbenchPlugin.getDefault().getPluginPreferences());
//					// MylarUsageMonitorPlugin.getDefault().addMonitoredPreferences(
//					// MylarUiPlugin.getDefault().getPluginPreferences());
//					UiUsageMonitorPlugin.getDefault().addMonitoredPreferences(
//							JavaPlugin.getDefault().getPluginPreferences());
//					UiUsageMonitorPlugin.getDefault().addMonitoredPreferences(
//							WorkbenchPlugin.getDefault().getPluginPreferences());
//					UiUsageMonitorPlugin.getDefault().addMonitoredPreferences(
//							EditorsPlugin.getDefault().getPluginPreferences());
//					UiUsageMonitorPlugin.getDefault().addMonitoredPreferences(
//							PDEPlugin.getDefault().getPluginPreferences());
				}
			});
		}
	}

	private final IWindowListener WINDOW_LISTENER = new IWindowListener() {
		public void windowActivated(IWorkbenchWindow window) {
		}

		public void windowDeactivated(IWorkbenchWindow window) {
		}

		public void windowClosed(IWorkbenchWindow window) {
			if (window.getShell() != null) {
				window.getShell().removeShellListener(SHELL_LISTENER);
			}
		}

		public void windowOpened(IWorkbenchWindow window) {
			if (window.getShell() != null && !PlatformUI.getWorkbench().isClosing()) {
				window.getShell().addShellListener(SHELL_LISTENER);
			}
		}
	};

	private final ShellListener SHELL_LISTENER = new ShellListener() {

		public void shellDeactivated(ShellEvent arg0) {
			if (!isPerformingUpload() && ContextCorePlugin.getDefault() != null) {
				for (IInteractionEventListener listener : MonitorUiPlugin.getDefault().getInteractionListeners()) {
					listener.stopMonitoring();
				}
			}
		}

		public void shellActivated(ShellEvent arg0) {
//			if (!MonitorUiPlugin.getDefault().suppressConfigurationWizards() && ContextCorePlugin.getDefault() != null) {
//				checkForStatisticsUpload();
//			}
			if (!isPerformingUpload() && ContextCorePlugin.getDefault() != null) {
				for (IInteractionEventListener listener : MonitorUiPlugin.getDefault().getInteractionListeners()) {
					listener.startMonitoring();
				}
			}
		}

		public void shellDeiconified(ShellEvent arg0) {
		}

		public void shellIconified(ShellEvent arg0) {
		}

		public void shellClosed(ShellEvent arg0) {
		}
	};

	private LogMoveUtility logMoveUtility;

	/**
	 * NOTE: this needs to be a separate class in order to avoid loading ..mylyn.context.core on eager startup
	 */
	private class LogMoveUtility {

//		private final IContextStoreListener DATA_DIR_MOVE_LISTENER = new IContextStoreListener() {
//
//			public void contextStoreMoved(File file) {
//				if (!isPerformingUpload()) {
//					for (IInteractionEventListener listener : MonitorUiPlugin.getDefault().getInteractionListeners()) {
//						listener.stopMonitoring();
//					}
//					interactionLogger.moveOutputFile(getMonitorLogFile().getAbsolutePath());
//					for (IInteractionEventListener listener : MonitorUiPlugin.getDefault().getInteractionListeners()) {
//						listener.startMonitoring();
//					}
//				}
//			}
//		};

		void start() {
//			ContextCore.getContextStore().addListener(DATA_DIR_MOVE_LISTENER);
		}

		void stop() {
//			ContextCore.getContextStore().removeListener(DATA_DIR_MOVE_LISTENER);
		}
	}

	public UiUsageMonitorPlugin() {
		plugin = this;

	}

	private void initDefaultPrefs() {
		getPreferenceStore().setDefault(MonitorPreferenceConstants.PREF_MONITORING_OBFUSCATE, true);

		if (!getPreferenceStore().contains(MonitorPreferenceConstants.PREF_MONITORING_INITIALLY_ENABLED)) {
			getPreferenceStore().setValue(MonitorPreferenceConstants.PREF_MONITORING_INITIALLY_ENABLED, true);
			getPreferenceStore().setValue(MonitorPreferenceConstants.PREF_MONITORING_ENABLED, true);
		}

		if (!getPreferenceStore().contains(
				MonitorPreferenceConstants.PREF_MONITORING_ENABLE_SUBMISSION_INITITALLY_ENABLED)) {
			getPreferenceStore().setValue(
					MonitorPreferenceConstants.PREF_MONITORING_ENABLE_SUBMISSION_INITITALLY_ENABLED, true);
			getPreferenceStore().setValue(MonitorPreferenceConstants.PREF_MONITORING_ENABLE_SUBMISSION, true);

		}

		getPreferenceStore().setValue(MonitorPreferenceConstants.PREF_MONITORING_STARTED, false);
	}

	@Override
	public void start(BundleContext context) throws Exception { //��Ŭ������ �����ϸ� �� �κ��� ����
		System.out.println("monitoring hello");
		SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		Calendar cal = Calendar.getInstance();
		MONITOR_LOG_NAME = "monitor-log " + simpleFormat.format(cal.getTime());
		super.start(context);
		initDefaultPrefs();
		final IWorkbench workbench = PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable() {
			public void run() {
				try {
					// ------- moved from synch start
//					new MonitorUsageExtensionPointReader().initExtensions();

					if (preferenceMonitor == null) {
						preferenceMonitor = new PreferenceChangeMonitor();
					}

					interactionLogger = new InteractionEventLogger(getMonitorLogFile());
					perspectiveMonitor = new PerspectiveChangeMonitor();
					activityMonitor = new ActivityChangeMonitor();
					windowMonitor = new WindowChangeMonitor();
					menuMonitor = new MenuCommandMonitor();
					keybindingCommandMonitor = new KeybindingCommandMonitor();

					// browserMonitor = new BrowserMonitor();
					// setAcceptedUrlMatchList(studyParameters.getAcceptedUrlList());

//					studyParameters.setServletUrl(DEFAULT_UPLOAD_SERVER + DEFAULT_UPLOAD_SERVLET);
					// ------- moved from synch start

					if (getPreferenceStore().getBoolean(MonitorPreferenceConstants.PREF_MONITORING_ENABLED)) {
						startMonitoring();
					}

					if (plugin.getPreferenceStore().contains(MonitorPreferenceConstants.PREF_PREVIOUS_TRANSMIT_DATE)) {
						lastTransmit = new Date(plugin.getPreferenceStore().getLong(
								MonitorPreferenceConstants.PREF_PREVIOUS_TRANSMIT_DATE));
					} else {
						lastTransmit = new Date();
						plugin.getPreferenceStore().setValue(MonitorPreferenceConstants.PREF_PREVIOUS_TRANSMIT_DATE,
								lastTransmit.getTime());
					}
				} catch (Throwable t) {
					StatusHandler.log(new Status(IStatus.ERROR, UiUsageMonitorPlugin.ID_PLUGIN,
							"Monitor failed to start", t));
				}
			}
		});
	}

	public void startMonitoring() {
		if (getPreferenceStore().contains(MonitorPreferenceConstants.PREF_MONITORING_STARTED)) {
			return;
		}
		interactionLogger.startMonitoring();
		for (IInteractionEventListener listener : MonitorUiPlugin.getDefault().getInteractionListeners()) {
			listener.startMonitoring();
		}

		IWorkbench workbench = PlatformUI.getWorkbench();
		MonitorUi.addInteractionListener(interactionLogger);
		getCommandMonitors().add(keybindingCommandMonitor);

		getActionExecutionListeners().add(new ActionExecutionMonitor());
		workbench.addWindowListener(WINDOW_LISTENER);
		for (IWorkbenchWindow w : MonitorUiPlugin.getDefault().getMonitoredWindows()) {
			if (w.getShell() != null) {
				w.getShell().addShellListener(SHELL_LISTENER);
			}
		}

		if (logMoveUtility == null) {
			logMoveUtility = new LogMoveUtility();
		}
		logMoveUtility.start();

		MonitorUiPlugin.getDefault().addWindowPerspectiveListener(perspectiveMonitor); //��Ŭ���� ���� page ��ȯ ��, �̺�Ʈ �߻� �ڵ�
		workbench.getActivitySupport().getActivityManager().addActivityManagerListener(activityMonitor);
		workbench.getDisplay().addFilter(SWT.Selection, menuMonitor); //�̰����� ���� �̺�Ʈ�� �ι� �߻��ȴ�.
		workbench.addWindowListener(windowMonitor); //â ��ȯ ��, �̺�Ʈ �߻� �ڵ�

		// installBrowserMonitor(workbench);

		for (Object listener : lifecycleListeners.getListeners()) {
			((IMonitorLifecycleListener) listener).startMonitoring();
		}

		if (!MonitorUiPlugin.getDefault().suppressConfigurationWizards()) {
			checkForFirstMonitorUse();
		}
		getPreferenceStore().setValue(MonitorPreferenceConstants.PREF_MONITORING_STARTED, true);
	}

//	public void addMonitoredPreferences(Preferences preferences) {
//		if (preferenceMonitor == null) {
//			preferenceMonitor = new PreferenceChangeMonitor();
//		}
//		preferences.addPropertyChangeListener(preferenceMonitor);
//	}
//
//	public void removeMonitoredPreferences(Preferences preferences) {
//		if (preferenceMonitor != null) {
//			preferences.removePropertyChangeListener(preferenceMonitor);
//		} else {
//			StatusHandler.log(new Status(IStatus.WARNING, UiUsageMonitorPlugin.ID_PLUGIN,
//					"UI Usage Monitor not started", new Exception()));
//		}
//	}

	public boolean isObfuscationEnabled() {
		return UiUsageMonitorPlugin.getPrefs().getBoolean(MonitorPreferenceConstants.PREF_MONITORING_OBFUSCATE);
	}

	public void stopMonitoring() {
		if (!getPreferenceStore().contains(MonitorPreferenceConstants.PREF_MONITORING_STARTED)) {
			return;
		}

		for (Object listener : lifecycleListeners.getListeners()) {
			((IMonitorLifecycleListener) listener).stopMonitoring();
		}

		for (IInteractionEventListener listener : MonitorUiPlugin.getDefault().getInteractionListeners()) {
			listener.stopMonitoring();
		}

		IWorkbench workbench = PlatformUI.getWorkbench();
		MonitorUi.removeInteractionListener(interactionLogger);

		getCommandMonitors().remove(keybindingCommandMonitor);
		getActionExecutionListeners().remove(new ActionExecutionMonitor());

		workbench.removeWindowListener(WINDOW_LISTENER);
		for (IWorkbenchWindow w : MonitorUiPlugin.getDefault().getMonitoredWindows()) {
			if (w.getShell() != null) {
				w.getShell().removeShellListener(SHELL_LISTENER);
			}
		}
		logMoveUtility.stop();
		// ContextCore.getPluginPreferences().removePropertyChangeListener(DATA_DIR_MOVE_LISTENER);

		MonitorUiPlugin.getDefault().removeWindowPerspectiveListener(perspectiveMonitor);
		workbench.getActivitySupport().getActivityManager().removeActivityManagerListener(activityMonitor);
		workbench.getDisplay().removeFilter(SWT.Selection, menuMonitor);
		workbench.removeWindowListener(windowMonitor);

		// uninstallBrowserMonitor(workbench);
		interactionLogger.stopMonitoring();

		getPreferenceStore().setValue(MonitorPreferenceConstants.PREF_MONITORING_STARTED, false);
	}

	public void addMonitoringLifecycleListener(IMonitorLifecycleListener listener) {
		lifecycleListeners.add(listener);
		if (isMonitoringEnabled()) {
			listener.startMonitoring();
		}
	}

	public void removeMonitoringLifecycleListener(IMonitorLifecycleListener listener) {
		lifecycleListeners.remove(listener);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
//		outputStream.write("</user1>\r\n".getBytes());
//		outputStream.close();
		super.stop(context); //��Ŭ���� �����ϸ� �� �κ� ����
		plugin = null;
		resourceBundle = null;
		MonitorUiPlugin.getDefault().getSelectionMonitors().remove(selectionMonitor);
	}

	public void actionObserved(IAction action, String info) {
		for (IActionExecutionListener listener : actionExecutionListeners) {
			listener.actionObserved(action);
		}
	}

	public List<IActionExecutionListener> getActionExecutionListeners() {
		return actionExecutionListeners;
	}

	public List<AbstractCommandMonitor> getCommandMonitors() {
		return commandMonitors;
	}

	/**
	 * Parallels TasksUiPlugin.getDefaultDataDirectory()
	 */
	public File getMonitorLogFile() {
		File rootDir = new File(ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + "/MonitoringData");
		if (!rootDir.exists()) {
			rootDir.mkdirs();
		}
		File file = new File(rootDir, MONITOR_LOG_NAME + InteractionContextManager.CONTEXT_FILE_EXTENSION_OLD); //�� �κ��� �����ϸ� ���� �ٸ� ���Ϸ� ������ �� �ִ�.
		if (!file.exists() || !file.canWrite()) {
			try {
				file.createNewFile();
				outputStream = new FileOutputStream(file, true);
				outputStream.write("<user1>\r\n".getBytes());
				outputStream.write("</user1>\r\n".getBytes());
				outputStream.close();
			} catch (IOException e) {
				StatusHandler.log(new Status(IStatus.ERROR, UiUsageMonitorPlugin.ID_PLUGIN,
						"Could not create monitor file", e));
			}
		}
		return file;
	}

	private long getUserPromptDelay() {
		return DELAY_ON_USER_REQUEST / DAY;
	}

//	public void userCancelSubmitFeedback(Date currentTime, boolean wait3Hours) {
//		if (wait3Hours) {
//			lastTransmit.setTime(currentTime.getTime() + DELAY_ON_USER_REQUEST
//					- studyParameters.getTransmitPromptPeriod());
//			plugin.getPreferenceStore().setValue(MonitorPreferenceConstants.PREF_PREVIOUS_TRANSMIT_DATE,
//					lastTransmit.getTime());
//		} else {
//			long day = HOUR * 24;
//			lastTransmit.setTime(currentTime.getTime() + day - studyParameters.getTransmitPromptPeriod());
//			plugin.getPreferenceStore().setValue(MonitorPreferenceConstants.PREF_PREVIOUS_TRANSMIT_DATE,
//					lastTransmit.getTime());
//		}
//	}

	/**
	 * Returns the shared instance.
	 */
	public static UiUsageMonitorPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle, or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = UiUsageMonitorPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		try {
			if (resourceBundle == null) {
				resourceBundle = ResourceBundle.getBundle("org.eclipse.mylyn.monitor.ui.MonitorPluginResources");
			}
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
		return resourceBundle;
	}

	// TODO: remove
	private void checkForFirstMonitorUse() {

	}

	public void incrementObservedEvents(int increment) {
		int numEvents = getPreferenceStore().getInt(MonitorPreferenceConstants.PREF_NUM_USER_EVENTS);
		numEvents += increment;
		getPreferenceStore().setValue(MonitorPreferenceConstants.PREF_NUM_USER_EVENTS, numEvents);
	}

//	public void configureProxy(HttpClient httpClient, String uploadScript) {
//		WebUtil.configureHttpClient(httpClient, null);
//		HostConfiguration hostConfiguration = WebUtil.createHostConfiguration(httpClient, new WebLocation(uploadScript,
//				uploadAuthentication.getUser(), uploadAuthentication.getPassword()), null);
//		httpClient.setHostConfiguration(hostConfiguration);
//	}

	public static IPreferenceStore getPrefs() {
		return getDefault().getPreferenceStore();
	}

	public static boolean isPerformingUpload() {
		return performingUpload;
	}

	public static void setPerformingUpload(boolean performingUpload) {
		UiUsageMonitorPlugin.performingUpload = performingUpload;
	}

	public InteractionEventLogger getInteractionLogger() {
		return interactionLogger;
	}

	public boolean isQuestionnaireEnabled() {
		return questionnaireEnabled;
	}

	public void setQuestionnaireEnabled(boolean questionnaireEnabled) {
		this.questionnaireEnabled = questionnaireEnabled;
	}

//	class MonitorUsageExtensionPointReader {
//
//		public static final String EXTENSION_ID_STUDY = "org.eclipse.mylyn.monitor.ui.study";
//
//		public static final String ELEMENT_SCRIPTS = "scripts";
//
//		public static final String ELEMENT_SCRIPTS_VERSION = "version";
//
//		public static final String ELEMENT_SCRIPTS_SERVER_URL = "url";
//
//		public static final String ELEMENT_SCRIPTS_UPLOAD_USAGE = "upload";
//
//		public static final String ELEMENT_SCRIPTS_GET_USER_ID = "userId";
//
//		public static final String ELEMENT_SCRIPTS_UPLOAD_QUESTIONNAIRE = "questionnaire";
//
//		public static final String ELEMENT_UI = "ui";
//
//		public static final String ELEMENT_UI_TITLE = "title";
//
//		public static final String ELEMENT_UI_DESCRIPTION = "description";
//
//		public static final String ELEMENT_UI_UPLOAD_PROMPT = "daysBetweenUpload";
//
//		public static final String ELEMENT_UI_QUESTIONNAIRE_PAGE = "questionnairePage";
//
//		public static final String ELEMENT_UI_BACKGROUND_PAGE = "backgroundPage";
//
//		public static final String ELEMENT_UI_CONSENT_FORM = "consentForm";
//
//		public static final String ELEMENT_UI_CONTACT_CONSENT_FIELD = "useContactField";
//
//		public static final String ELEMENT_MONITORS = "monitors";
//
//		public static final String ELEMENT_MONITORS_BROWSER_URL = "browserUrlFilter";
//
//		private boolean extensionsRead = false;
//
//		// private MonitorUsageExtensionPointReader thisReader = new
//		// MonitorUsageExtensionPointReader();
//
//		public void initExtensions() {
//			try {
//				if (!extensionsRead) {
//					IExtensionRegistry registry = Platform.getExtensionRegistry();
//					IExtensionPoint extensionPoint = registry.getExtensionPoint(EXTENSION_ID_STUDY);
//					if (extensionPoint != null) {
//						IExtension[] extensions = extensionPoint.getExtensions();
//						for (IExtension extension : extensions) {
//							IConfigurationElement[] elements = extension.getConfigurationElements();
//							for (IConfigurationElement element : elements) {
//								if (element.getName().compareTo(ELEMENT_SCRIPTS) == 0) {
//									readScripts(element);
//								} else if (element.getName().compareTo(ELEMENT_UI) == 0) {
//									readForms(element);
//								} else if (element.getName().compareTo(ELEMENT_MONITORS) == 0) {
//									readMonitors(element);
//								}
//							}
//							customizingPlugin = extension.getContributor().getName();
//						}
//						extensionsRead = true;
//					}
//				}
//			} catch (Throwable t) {
//				StatusHandler.log(new Status(IStatus.ERROR, UiUsageMonitorPlugin.ID_PLUGIN,
//						"Could not read monitor extension", t));
//			}
//		}
//
//		private void readScripts(IConfigurationElement element) {
//			studyParameters.setVersion(element.getAttribute(ELEMENT_SCRIPTS_VERSION));
//		}
//
//		private void readForms(IConfigurationElement element) throws CoreException {
//			studyParameters.setTitle(element.getAttribute(ELEMENT_UI_TITLE));
//			studyParameters.setDescription(element.getAttribute(ELEMENT_UI_DESCRIPTION));
//			if (element.getAttribute(ELEMENT_UI_UPLOAD_PROMPT) != null) {
//				Integer uploadInt = new Integer(element.getAttribute(ELEMENT_UI_UPLOAD_PROMPT));
//				studyParameters.setTransmitPromptPeriod(HOUR * 24 * uploadInt);
//			}
//			studyParameters.setUseContactField(element.getAttribute(ELEMENT_UI_CONTACT_CONSENT_FIELD));
//
//			try {
//				if (element.getAttribute(ELEMENT_UI_QUESTIONNAIRE_PAGE) != null) {
//					Object questionnaireObject = element.createExecutableExtension(ELEMENT_UI_QUESTIONNAIRE_PAGE);
//					if (questionnaireObject instanceof AbstractStudyQuestionnairePage) {
//						AbstractStudyQuestionnairePage page = (AbstractStudyQuestionnairePage) questionnaireObject;
//						studyParameters.setQuestionnairePage(page);
//					}
//				} else {
//					UiUsageMonitorPlugin.getDefault().setQuestionnaireEnabled(false);
//				}
//			} catch (Throwable e) {
//				StatusHandler.log(new Status(IStatus.ERROR, UiUsageMonitorPlugin.ID_PLUGIN,
//						"Could not load questionaire", e));
//				UiUsageMonitorPlugin.getDefault().setQuestionnaireEnabled(false);
//			}
//
//			try {
//				if (element.getAttribute(ELEMENT_UI_BACKGROUND_PAGE) != null) {
//					Object backgroundObject = element.createExecutableExtension(ELEMENT_UI_BACKGROUND_PAGE);
//					if (backgroundObject instanceof AbstractStudyBackgroundPage) {
//						AbstractStudyBackgroundPage page = (AbstractStudyBackgroundPage) backgroundObject;
//						studyParameters.setBackgroundPage(page);
//						UiUsageMonitorPlugin.getDefault().setBackgroundEnabled(true);
//					}
//				} else {
//					UiUsageMonitorPlugin.getDefault().setBackgroundEnabled(false);
//				}
//			} catch (Throwable e) {
//				StatusHandler.log(new Status(IStatus.ERROR, UiUsageMonitorPlugin.ID_PLUGIN,
//						"Could not load background page", e));
//				UiUsageMonitorPlugin.getDefault().setBackgroundEnabled(false);
//			}
//
//			studyParameters.setFormsConsent("/" + element.getAttribute(ELEMENT_UI_CONSENT_FORM));
//
//		}
//
//		private void readMonitors(IConfigurationElement element) throws CoreException {
//			// TODO: This should parse a list of filters but right now it takes
//			// the
//			// entire string as a single filter.
//			// ArrayList<String> urlList = new ArrayList<String>();
//			String urlList = element.getAttribute(ELEMENT_MONITORS_BROWSER_URL);
//			studyParameters.setAcceptedUrlList(urlList);
//		}
//	}
//
//	public StudyParameters getStudyParameters() {
//		return studyParameters;
//	}

	public String getCustomizingPlugin() {
		return customizingPlugin;
	}

	public boolean isMonitoringEnabled() {
		return getPreferenceStore().getBoolean(MonitorPreferenceConstants.PREF_MONITORING_ENABLED);
	}

	public String getCustomizedByMessage() {
		String customizedBy = UiUsageMonitorPlugin.getDefault().getCustomizingPlugin();
		String message = "NOTE: You have previously downloaded the Mylyn monitor and a user study plug-in with id: "
				+ customizedBy + "\n" + "If you are not familiar with this plug-in do not upload data.";
		return message;
	}

	public boolean isBackgroundEnabled() {
		return backgroundEnabled;
	}

	public void setBackgroundEnabled(boolean backgroundEnabled) {
		this.backgroundEnabled = backgroundEnabled;
	}

//	public String getExtensionVersion() {
//		return studyParameters.getVersion();
//	}
//
//	public boolean usingContactField() {
//		if (studyParameters.getUseContactField().equals("true")) {
//			return true;
//		} else {
//			return false;
//		}
//	}
}
