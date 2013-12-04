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

import navclus.ui.classdiagram.actions.RedrawAction;
import navclus.ui.classdiagram.jobs.JavaAddition;
import navclus.ui.classdiagram.jobs.JavaDeletion;
import navclus.ui.classdiagram.utils.JavaEditorUtil;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;
//import org.eclipse.mylyn.monitor.core.InteractionEvent;






import com.navclus.ui.classview.views.ClassView;

public class JavaEditorPartListener2 implements IPartListener2 {

	public void partActivated(IWorkbenchPartReference partRef) {
	}

	public void partBroughtToTop(IWorkbenchPartReference partRef) {
	}

	public void partOpened(IWorkbenchPartReference partRef) {
		final IJavaElement javaelement = JavaEditorUtil.getJavaElement(partRef);
		if (javaelement == null)
			return;

		ClassView.getDefault().getSelectionKeeper().addSelection(javaelement);
		System.out.println("fileChanged: " + javaelement.getElementName());
		addCurrentNode(javaelement);

		// InteractionEvent interactionEvent
		// = new InteractionEvent(
		// InteractionEvent.Kind.PREFERENCE, // kind
		// "null", // structureKind
		// javaelement.getHandleIdentifier().hashCode() + " ", // handle
		// "null", //viewer.ID, // originId
		// "null", // navigatedRelation
		// "classview: open_class: " + "null", // viewer.countGraphNodes(), //
		// delta
		// 1f);
		//
		// MonitorUiPlugin.getDefault().notifyInteractionObserved(interactionEvent);
	}

	public void partClosed(IWorkbenchPartReference partRef) {
		IJavaElement javaelement = JavaEditorUtil.getJavaElement(partRef);
		if (javaelement == null)
			return;
		
		deleteCurrentNode(javaelement);
//		try {
//			ClassView.getDefault().getRootModel()
//					.closeCU((ICompilationUnit) javaelement);
//
//			Display.getDefault().asyncExec(new Runnable() {
//				@Override
//				public void run() {
//					if (ClassView.getDefault().getG().getNodes().size() > 0)
//						(new RedrawAction()).run();
//				}
//			});
//		} catch (JavaModelException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		// InteractionEvent interactionEvent
		// = new InteractionEvent(
		// InteractionEvent.Kind.PREFERENCE, // kind
		// "null", // structureKind
		// javaelement.getHandleIdentifier().hashCode() + " ", // handle
		// viewer.ID, // originId
		// "null", // navigatedRelation
		// "classview ; close_class ; " + viewer.countGraphNodes(), // delta
		// 1f);
		//
		// MonitorUiPlugin.getDefault().notifyInteractionObserved(interactionEvent);
	}

	public void partDeactivated(IWorkbenchPartReference partRef) {
	}

	public void partHidden(IWorkbenchPartReference partRef) {
	}

	public void partVisible(IWorkbenchPartReference partRef) {
	}

	public void partInputChanged(IWorkbenchPartReference partRef) {
		// IJavaElement javaelement = javaeditorutil.getJavaElement(partRef);
		// if (javaelement == null) return;
		//
		// removePreviousNode();
		// addCurrentNode(javaelement);
	}

	// called by the partOpened method ...
	private void addCurrentNode(IJavaElement curJavaElement) {
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
	private void deleteCurrentNode(IJavaElement curJavaElement) {
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

}
