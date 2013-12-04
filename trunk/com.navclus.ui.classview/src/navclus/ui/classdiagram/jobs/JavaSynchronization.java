/*******************************************************************************
Copyright (c) 2010, 2012 Seonah Lee, SA Lab, KAIST
All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v10.html *
Contributors:
Seonah Lee - initial implementation
 *******************************************************************************/

package navclus.ui.classdiagram.jobs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

//import navclus.userinterface.classdiagram.PlugIn;

import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import navclus.ui.classdiagram.actions.RedrawAction;
import navclus.ui.classdiagram.java.analyzer.RootModel;
import navclus.ui.classdiagram.java.analyzer.TypeModel;
import navclus.ui.classdiagram.utils.JavaEditorUtil;
import navclus.ui.classdiagram.utils.TypeHistory;

public class JavaSynchronization extends Job {

	public static final String MY_FAMILY = "synchronizingJobFamily";

	public boolean belongsTo(Object family) {
		return family == MY_FAMILY;
	}

	IWorkbenchPage page;
	RootModel rootmodel;

	public JavaSynchronization(IWorkbenchPage page, RootModel rootmodel) {
		super("SynchronizingJob");
		this.page = page;
		this.rootmodel = rootmodel;
	}

	public IStatus run(IProgressMonitor monitor) {
		try {
			if (page == null)
				return Status.CANCEL_STATUS;

			IEditorReference[] editorreferences = page.getEditorReferences();
			for (IEditorReference editorreference : editorreferences) {
				IJavaElement element = JavaEditorUtil
						.getJavaElement(editorreference);

				if (element == null)
					continue;

				try {
					rootmodel.addJavaFile(element);
				} catch (JavaModelException e) {
					e.printStackTrace();
				}
			}

			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					(new RedrawAction()).run();
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
		return Status.OK_STATUS;
	}
	
}
