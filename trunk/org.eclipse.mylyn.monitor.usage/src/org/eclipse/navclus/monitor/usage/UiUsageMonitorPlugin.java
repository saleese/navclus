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

package org.eclipse.navclus.monitor.usage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.navclus.commons.core.StatusHandler;
import org.eclipse.navclus.internal.context.core.InteractionContextManager;
import org.eclipse.navclus.internal.monitor.ui.MonitorUiPlugin;
import org.eclipse.navclus.monitor.core.IInteractionEventListener;
import org.eclipse.navclus.monitor.jobs.ClientUpdate;
import org.eclipse.navclus.monitor.jobs.ServerUpdate;
import org.eclipse.navclus.monitor.ui.MonitorUi;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import connection.ClientUpdater;
import connection.ServerUpdater;

/**
 * @author Mik Kersten
 * @author Shawn Minto
 */
public class UiUsageMonitorPlugin extends AbstractUIPlugin {

	public static final String PREF_USER_ID = "org.eclipse.navclus.user.id";

	public static String VERSION = "1.0";

	public static String MONITOR_LOG_NAME = "monitor-log";

	public static final String ID_PLUGIN = "org.eclipse.navclus.monitor.usage";

	private InteractionEventLogger interactionLogger;

	private static UiUsageMonitorPlugin plugin;

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
				}
			});
		}
	}

	public UiUsageMonitorPlugin() {
		plugin = this;
	}

	@Override
	public void start(BundleContext context) throws Exception { //이클립스를 실행하면 이 부분을 수행
		SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		Calendar cal = Calendar.getInstance();
		MONITOR_LOG_NAME = "monitor-log " + simpleFormat.format(cal.getTime());

		super.start(context);

		final IWorkbench workbench = PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable() {
			public void run() {
				try {
					interactionLogger = new InteractionEventLogger(getMonitorLogFile());
					String monitoringDir = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString()
							+ "/MonitoringData";
					startMonitoring();
					(new ClientUpdater()).FileUpdate(monitoringDir);

//					updateClient(monitoringDir);

				} catch (Throwable t) {
					StatusHandler.log(new Status(IStatus.ERROR, UiUsageMonitorPlugin.ID_PLUGIN,
							"Monitor failed to start", t));
				}
			}
		});
	}

	public void startMonitoring() {
		interactionLogger.startMonitoring();
		MonitorUi.addInteractionListener(interactionLogger);
	}

	public void stopMonitoring() {

		for (IInteractionEventListener listener : MonitorUiPlugin.getDefault().getInteractionListeners()) {
			listener.stopMonitoring();
		}

		MonitorUi.removeInteractionListener(interactionLogger);
		interactionLogger.stopMonitoring();
	}

	@Override
	public void stop(BundleContext context) throws Exception { // 여기에서 스탑 전에 파일 업로드 할 것...
		// upload to a cloud system
		String fileName = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + "/MonitoringData/"
				+ MONITOR_LOG_NAME + InteractionContextManager.CONTEXT_FILE_EXTENSION_OLD;
		(new ServerUpdater()).FileUpdate(fileName);

		MonitorUiPlugin.getDefault().getSelectionMonitors().remove(selectionMonitor);
		stopMonitoring();
		super.stop(context); //이클립스 종료하면 이 부분 실행

//		updateServer(fileName);
		plugin = null;

	}

	/**
	 * Parallels TasksUiPlugin.getDefaultDataDirectory()
	 */
	public File getMonitorLogFile() { // 
		File rootDir = new File(ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + "/MonitoringData");
		if (!rootDir.exists()) {
			rootDir.mkdirs();
		}
		File file = new File(rootDir, MONITOR_LOG_NAME + InteractionContextManager.CONTEXT_FILE_EXTENSION_OLD); //이 부분을 수정하면 매일 다른 파일로 저장할 수 있다.
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

	/**
	 * Returns the shared instance.
	 */
	public static UiUsageMonitorPlugin getDefault() {
		return plugin;
	}

	public InteractionEventLogger getInteractionLogger() {
		return interactionLogger;
	}

	// called by the start method ...
	private void updateClient(String monitoringDir) {
		ClientUpdate updatingClientJob = new ClientUpdate(monitoringDir);
		updatingClientJob.setPriority(Job.INTERACTIVE);
		updatingClientJob.schedule();
	}

	// called by the start method ...
	private void updateServer(String fileName) {
		IWorkspace myWorkspace = ResourcesPlugin.getWorkspace();

		ServerUpdate updatingServerJob = new ServerUpdate(fileName);
		updatingServerJob.setPriority(Job.INTERACTIVE);
		updatingServerJob.setRule(myWorkspace.getRoot());
		updatingServerJob.schedule();
	}
}
