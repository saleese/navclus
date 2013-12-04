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


import navclus.ui.classdiagram.actions.RedrawAction;
import navclus.ui.classdiagram.java.analyzer.RootModel;
import navclus.ui.classdiagram.java.analyzer.TypeModel;

public class JavaAddition extends Job {

	public static final String MY_FAMILY = "addingJobFamily";

	public boolean belongsTo(Object family) {
		return family == MY_FAMILY;
	}

	IJavaElement javaElement;
	RootModel rootmodel;

	public JavaAddition(IJavaElement javaElement, RootModel rootmodel) {
		super("AddingJob");
		this.javaElement = javaElement;
		this.rootmodel = rootmodel;
	}

	public IStatus run(IProgressMonitor monitor) {
		try {
			if (javaElement instanceof ICompilationUnit) {
				rootmodel.openCU((ICompilationUnit) javaElement);
			} else if (javaElement instanceof IClassFile) {
				rootmodel.openClass((IClassFile) javaElement);
			} else if (javaElement instanceof IType) {
				rootmodel.createType((IType) javaElement);
			} else if (javaElement instanceof IMember) {
				IJavaElement type_element = javaElement
						.getAncestor(IJavaElement.TYPE);
				rootmodel.createType((IType) type_element);
			} else {
				// MessageDialog.openInformation(PlugIn.getDefaultShell(),
				// "Error",
				// "Cannot open this kind of Java Element:" + _element);
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
