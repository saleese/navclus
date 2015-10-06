/*******************************************************************************
Copyright (c) 2010, 2012 Seonah Lee, SA Lab, KAIST
All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v10.html *
Contributors:
Seonah Lee - initial implementation
 *******************************************************************************/

package navclus.userinterface.classdiagram.actions;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import navclus.userinterface.classdiagram.NavClusView;
import navclus.userinterface.classdiagram.java.manager.RootNode;
import navclus.userinterface.classdiagram.java.manager.TypeNode;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;


public class SaveAction extends Action implements IAction {

	public SaveAction() {
		super("", AS_RADIO_BUTTON);	
	}

	public void run() {	
		try {

			RootNode rootNode = NavClusView.getDefault().getRootNode();		
			if (rootNode == null)
				return;

			ArrayList<TypeNode> nodes = rootNode.getTypeNodes();

			FileWriter fieWriter = new FileWriter("test.uml");
			BufferedWriter bufferedWriter = new BufferedWriter(fieWriter);
			bufferedWriter.write("@start uml");
			bufferedWriter.newLine();
			
			for (int i = 0; i < nodes.size(); i++) {
				TypeNode node = nodes.get(i);
				bufferedWriter.write("class\t");
				bufferedWriter.write(node.getType().getElementName());
				bufferedWriter.newLine();
			}
			
			bufferedWriter.write("@end uml");
			bufferedWriter.close();
		}
		catch (IOException e) 
		{
			System.err.println(e);
		}

	}
}
