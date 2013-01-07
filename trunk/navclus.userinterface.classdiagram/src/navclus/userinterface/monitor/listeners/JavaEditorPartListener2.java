/*******************************************************************************
Copyright (c) 2010, 2012 Seonah Lee, SA Lab, KAIST
All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v10.html *
Contributors:
Seonah Lee - initial implementation
*******************************************************************************/

package navclus.userinterface.monitor.listeners;

import navclus.userinterface.classdiagram.NavClusView;
import navclus.userinterface.classdiagram.actions.JavaAddition;
import navclus.userinterface.classdiagram.actions.RedrawAction;
import navclus.userinterface.classdiagram.utils.JavaEditorUtil;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;
//import org.eclipse.mylyn.monitor.core.InteractionEvent;

public class JavaEditorPartListener2 implements IPartListener2 {


	public void partActivated(IWorkbenchPartReference partRef) {
	}

	public void partBroughtToTop(IWorkbenchPartReference partRef) {				
	}

	public void partOpened(IWorkbenchPartReference partRef) {	
		final IJavaElement javaelement = JavaEditorUtil.getJavaElement(partRef);
		if (javaelement == null) return;		
		else {
			NavClusView.getDefault().getSelectionKeeper().addSelection(javaelement);
			addCurrentNode(javaelement);
		}

//				InteractionEvent interactionEvent 
//				= new InteractionEvent(
//						InteractionEvent.Kind.PREFERENCE, // kind
//						"null",    // structureKind
//						javaelement.getHandleIdentifier().hashCode() + " ", // handle
//						"null", //viewer.ID, // originId
//						"null",	   // navigatedRelation
//						"classview: open_class: " + "null", // viewer.countGraphNodes(), // delta
//						1f);
//						
//				MonitorUiPlugin.getDefault().notifyInteractionObserved(interactionEvent);		
	}	

	public void partClosed(IWorkbenchPartReference partRef) {
		IJavaElement javaelement = JavaEditorUtil.getJavaElement(partRef);
		if (javaelement == null) return;
		try {			
			NavClusView.getDefault().getRootModel().closeCU((ICompilationUnit) javaelement);
			
			if (NavClusView.getDefault().getG().getNodes().size() > 0)
				(new RedrawAction()).run();
			
		} catch (JavaModelException e) {
			e.printStackTrace();
		}

		//		InteractionEvent interactionEvent 
		//		= new InteractionEvent(
		//				InteractionEvent.Kind.PREFERENCE, // kind
		//				"null",    // structureKind
		//				javaelement.getHandleIdentifier().hashCode() + " ", // handle
		//				viewer.ID, // originId
		//				"null",	   // navigatedRelation
		//				"classview ; close_class ; " + viewer.countGraphNodes(), // delta
		//				1f);
		//				
		//		MonitorUiPlugin.getDefault().notifyInteractionObserved(interactionEvent);
	}
	public void partDeactivated(IWorkbenchPartReference partRef) {
	}
	public void partHidden(IWorkbenchPartReference partRef) {
	}
	public void partVisible(IWorkbenchPartReference partRef) {
	}

	public void partInputChanged(IWorkbenchPartReference partRef) {
		//		IJavaElement javaelement = javaeditorutil.getJavaElement(partRef);
		//		if (javaelement == null) return;
		//
		//		removePreviousNode();
		//		addCurrentNode(javaelement);					
	}

	// called by the partOpened method ... 
	private boolean addCurrentNode(IJavaElement curJavaElement) {
		if (curJavaElement == null) return true;

		boolean bCurrentExist = false;
		bCurrentExist = JavaEditorUtil.IsExistInTab(curJavaElement);

		// add the element if the node does not exist in the graph
		if (bCurrentExist == true) {
			JavaAddition addingJob = new JavaAddition(curJavaElement, NavClusView.getDefault().getRootModel());
			addingJob.setPriority(Job.INTERACTIVE);
			addingJob.schedule();
			return bCurrentExist;
		}
		else { 
			System.err.println("<exception occurs - partActivated:addCurrentNodes>");
			return bCurrentExist;
		}
	}

}
