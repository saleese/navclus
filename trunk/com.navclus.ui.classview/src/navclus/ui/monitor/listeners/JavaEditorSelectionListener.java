/*******************************************************************************
Copyright (c) 2010, 2012 Seonah Lee, SA Lab, KAIST
All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v10.html *
Contributors:
Seonah Lee - initial implementation
 *******************************************************************************/

package navclus.ui.monitor.listeners;

import navclus.ui.classdiagram.jobs.JavaModification;
import navclus.ui.classdiagram.utils.JavaEditorUtil;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;

import com.navclus.ui.classview.views.ClassView;

public class JavaEditorSelectionListener implements ISelectionListener {

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		try {
			if (selection instanceof TextSelection) {
				ITextSelection selectedtext = (ITextSelection) selection;
				if (selectedtext.getStartLine() < 1)
					return;

				// do not work if the types of being deleted and created are the
				// same & the type will not be permanent.
				IJavaElement topElement = JavaEditorUtil.getJavaElement(part);
				if (topElement == null)
					return;

				modifyCurrentNode(selectedtext, topElement);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// called by the selectionChanged method ...
	private void modifyCurrentNode(ITextSelection selectedtext,
			IJavaElement topElement) {
		if (topElement == null)
			return;
		if (ClassView.getDefault().getRootModel() == null)
			return;

		JavaModification modifyingJob = new JavaModification(selectedtext,
				topElement, ClassView.getDefault().getRootModel());
		modifyingJob.setPriority(Job.INTERACTIVE);
		modifyingJob.schedule();
	}
}
