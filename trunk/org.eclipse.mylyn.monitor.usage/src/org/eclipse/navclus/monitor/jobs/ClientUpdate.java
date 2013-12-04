/*******************************************************************************
Copyright (c) 2010, 2012 Seonah Lee, SA Lab, KAIST
All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v10.html *
Contributors:
Seonah Lee - initial implementation
 *******************************************************************************/

package org.eclipse.navclus.monitor.jobs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import connection.ClientUpdater;

//import navclus.userinterface.classdiagram.PlugIn;

public class ClientUpdate extends Job {

	public static final String MY_FAMILY = "updatingJobFamily";

	@Override
	public boolean belongsTo(Object family) {
		return family == MY_FAMILY;
	}

	String monitoringDir;

	public ClientUpdate(String monitoringDir) {
		super("UpdatingClientJob");
		this.monitoringDir = monitoringDir;
	}

	@Override
	public IStatus run(IProgressMonitor monitor) {
		try {
			ClientUpdater.FileUpdate(monitoringDir);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Status.OK_STATUS;
	}
}
