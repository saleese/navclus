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

import navclus.ui.classdiagram.jobs.FileAddition;
import navclus.ui.classdiagram.jobs.FileDeletion;
import navclus.ui.classdiagram.jobs.JavaAddition;
import navclus.ui.classdiagram.jobs.JavaDeletion;
import navclus.ui.classdiagram.utils.JavaEditorUtil;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;

import com.navclus.ui.classview.views.ClassView;

public class JavaEditorPartListener2 implements IPartListener2 {

	public void partActivated(IWorkbenchPartReference partRef) {
	}

	public void partBroughtToTop(IWorkbenchPartReference partRef) {
	}

	public void partOpened(IWorkbenchPartReference partRef) {	
		final Object element = JavaEditorUtil.getFile(partRef);
		if (element == null)
			return;

		if (element instanceof IJavaElement) {
			IJavaElement javaElement = (IJavaElement) element;
			ClassView.getDefault().getSelectionKeeper().addSelection(javaElement);
			System.out.println("fileOpened: " + javaElement.getElementName());
			addJavaNode(javaElement);					
		}
		else if (element instanceof String) {
			String fileElement = (String) element;
			System.out.println("fileOpened: " + fileElement);
			addFileNode(fileElement);				
		}
	}

	public void partClosed(IWorkbenchPartReference partRef) {	
		final Object element = JavaEditorUtil.getFile(partRef);
		if (element == null)
			return;

		if (element instanceof IJavaElement) {
			IJavaElement javaElement = (IJavaElement) element;
			ClassView.getDefault().getSelectionKeeper().addSelection(javaElement);
			System.out.println("fileClosed: " + javaElement.getElementName());
			deleteJavaNode(javaElement);				
		}
		else if (element instanceof String) {
			String fileElement = (String) element;
			System.out.println("fileClosed: " + fileElement);
			deleteFileNode(fileElement);				
		}
	}

	public void partDeactivated(IWorkbenchPartReference partRef) {
	}

	public void partHidden(IWorkbenchPartReference partRef) {
	}

	public void partVisible(IWorkbenchPartReference partRef) {
	}

	public void partInputChanged(IWorkbenchPartReference partRef) {
	}

	// called by the partOpened method ...
	private void addJavaNode(IJavaElement curJavaElement) {
		if (curJavaElement == null)
			return;
		if (ClassView.getDefault().getRootModel() == null)
			return;

		boolean bCurrentExist = false;
		bCurrentExist = JavaEditorUtil.IsExistInTab(curJavaElement);

		// add the element if the node does not exist in the graph
		if (bCurrentExist == true) {
			JavaAddition addingJob = new JavaAddition(curJavaElement, ClassView
					.getDefault().getRootModel());
			addingJob.setPriority(Job.INTERACTIVE);
			addingJob.schedule();
		} else {
			System.err
					.println("<exception occurs - partOpened: addCurrentNodes>");
		}
	}
	
	// called by the partOpened method ...
	private void deleteJavaNode(IJavaElement curJavaElement) {
		if (curJavaElement == null)
			return;
		if (ClassView.getDefault().getRootModel() == null)
			return;

//		boolean bCurrentExist = false;
//		bCurrentExist = JavaEditorUtil.IsExistInTab(curJavaElement);

		// add the element if the node does not exist in the graph
//		if (bCurrentExist == true) {
			JavaDeletion deletingJob = new JavaDeletion(curJavaElement, ClassView
					.getDefault().getRootModel());
			deletingJob.setPriority(Job.INTERACTIVE);
			deletingJob.schedule();
//		} else {
//			System.err
//					.println("<exception occurs - partClosed: deleteCurrentNodes>");
//		}
	}
	
	private void addFileNode(String curFile) {
		if (curFile == null)
			return;
		if (ClassView.getDefault().getRootModel() == null)
			return;

//      needed when onTop function is called
//		boolean bCurrentExist = false;
//		bCurrentExist = JavaEditorUtil.IsExistInTab(curFile);

		// add the element if the node does not exist in the graph
//		if (bCurrentExist == true) {
			FileAddition addingJob = new FileAddition(curFile, ClassView
					.getDefault().getRootModel());
			addingJob.setPriority(Job.INTERACTIVE);
			addingJob.schedule();
//		} else {
//			System.err
//					.println("<exception occurs - partOpened: addCurrentNodes>");
//		}
	}
	
	private void deleteFileNode(String curFile) {
		if (curFile == null)
			return;
		if (ClassView.getDefault().getRootModel() == null)
			return;

//		boolean bCurrentExist = false;
//		bCurrentExist = JavaEditorUtil.IsExistInTab(curJavaElement);

		// add the element if the node does not exist in the graph
//		if (bCurrentExist == true) {
			FileDeletion deletingJob = new FileDeletion(curFile, ClassView
					.getDefault().getRootModel());
			deletingJob.setPriority(Job.INTERACTIVE);
			deletingJob.schedule();
//		} else {
//			System.err
//					.println("<exception occurs - partClosed: deleteCurrentNodes>");
//		}
	}

}
