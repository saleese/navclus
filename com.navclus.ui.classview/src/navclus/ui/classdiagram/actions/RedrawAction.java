/*******************************************************************************
Copyright (c) 2010, 2012 Seonah Lee, SA Lab, KAIST
All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v10.html *
Contributors:
Seonah Lee - initial implementation
*******************************************************************************/

package navclus.ui.classdiagram.actions;

import navclus.ui.classdiagram.java.manager.RootNode;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;

import com.navclus.ui.classview.views.ClassView;


public class RedrawAction extends Action implements IAction {
	
	public RedrawAction() {
		super("", AS_RADIO_BUTTON);	
	}

	public void run() {	
		
		RootNode rootNode = ClassView.getDefault().getRootNode();		
		if (rootNode == null)
			return;
				
		// draw nodes
		rootNode.drawGraphNodes();
		
		// remove all connections
		ClassView.getDefault().getG().removeAllGraphConnections();		
		rootNode.drawStructuralRelations();
						
		ClassView.getDefault().getG().applyLayout();					
	}
}
