/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.navclus.monitor.ui;

import java.util.List;
import java.util.Set;

import org.eclipse.navclus.internal.monitor.ui.MonitorUiPlugin;
import org.eclipse.navclus.monitor.core.IInteractionEventListener;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * @author Steffen Pingel
 * @author Mik Kersten
 * @since 3.0
 */
public class MonitorUi {

	public static void addWindowPostSelectionListener(ISelectionListener listener) {
		MonitorUiPlugin.getDefault().addWindowPostSelectionListener(listener);
	}

	public static void removeWindowPostSelectionListener(ISelectionListener listener) {
		MonitorUiPlugin.getDefault().removeWindowPostSelectionListener(listener);
	}

	public static void addInteractionListener(IInteractionEventListener listener) {
		MonitorUiPlugin.getDefault().addInteractionListener(listener);
	}

	public static List<AbstractUserInteractionMonitor> getSelectionMonitors() {
		return MonitorUiPlugin.getDefault().getSelectionMonitors();
	}

	public static void removeInteractionListener(IInteractionEventListener listener) {
		MonitorUiPlugin.getDefault().removeInteractionListener(listener);
	}

	public static void addWindowPartListener(IPartListener listener) {
		MonitorUiPlugin.getDefault().addWindowPartListener(listener);
	}

	public static void removeWindowPartListener(IPartListener listener) {
		MonitorUiPlugin.getDefault().removeWindowPartListener(listener);
	}

}
