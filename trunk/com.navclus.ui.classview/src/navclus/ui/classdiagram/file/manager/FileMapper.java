/*******************************************************************************
Copyright (c) 2010, 2012 Seonah Lee, SA Lab, KAIST
All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v10.html *
Contributors:
Seonah Lee - initial implementation
*******************************************************************************/

/** 
 * The FileMapper class is needed to delete UML Nodes
 * according to File Closing.
 */

package navclus.ui.classdiagram.file.manager;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import navclus.ui.classdiagram.classfigure.FileFigureCreator;
import navclus.ui.classdiagram.classfigure.UMLNode;

import org.eclipse.jdt.core.IType;
import org.eclipse.swt.SWT;

import com.navclus.ui.classview.views.ClassView;


public class FileMapper {

	public HashMap<String, UMLNode> fileTable; 	// salee	
	
	public FileMapper() {
		this.fileTable = new LinkedHashMap<String, UMLNode>(); // salee	
	}

	public UMLNode get(String filename) {
		return fileTable.get(filename);		
	}
	
//	public TypeNode get(UMLNode umlnode) {
//		Set<TypeNode> typenodeset = nodetable.keySet();
//		
//		for (TypeNode typenode: typenodeset) {
//			UMLNode node = this.get(typenode);
//			if (node == null) continue;
//		
//			if (node.equals(umlnode)) {
//				System.err.println("test:TypeNode.get");
//				return typenode;
//			}
//		}
//		
//		return null;		
//	}
	
//	public boolean remove(TypeNode typenode) {
//		System.out.println("remove:" + typenode.getType().getElementName());
//		
//		UMLNode node = this.get(typenode);		
//		if (node == null) return false;
//		
//		nodetable.remove(typenode);
//		node.dispose();
//		return true;
//	}
//	
	public void removeAll() {		
		Set<String> typenodeset = fileTable.keySet();
		
		for (String fileNode: typenodeset) {
			UMLNode node = fileTable.get(fileNode);		
			if (node == null) continue;
			node.dispose();		
		}
		fileTable.clear();
	}
//		
//	public Point removeAtPosition(TypeNode typenode) {
//		UMLNode node = this.get(typenode);
//		Point curPoint = node.getLocation();		
//		if (node == null) return null;
//		
//		nodetable.remove(typenode);
//		node.dispose();
//		return curPoint;
//	}
	
	// @author salee 2012-11-08
	public UMLNode draw(String fileName) {
		UMLNode preGraphNode = this.get(fileName);
		if (preGraphNode == null) { // if it is the new class
			return this.create(fileName);
		}
		else { // if it is an old class
//			Point prePoint = preGraphNode.getLocation();
//			
//			IType preType = typenode.getType();
//			if (preType == null) return null;
//			
//			// setCustomFigure
//			IFigure classFigure = (new ClassFigureCreator()).createClassFigure(typenode);
//			if (classFigure == null) return null;
//			
//			preGraphNode.setUMLNode(ClassView.getDefault().getG(), classFigure);			
//			preGraphNode.setText((preType.getHandleIdentifier()));
//			preGraphNode.setLocation(prePoint.x, prePoint.y);
			return preGraphNode;
		}		
	}
	
	public UMLNode create(String fileName) {
		UMLNode curNode = new UMLNode(ClassView.getDefault().getG(), SWT.NONE, 
		 		  (new FileFigureCreator()).createClassFigure(fileName));					
		curNode.setText(fileName);
									
		fileTable.put(fileName, curNode);
		
		return curNode;
	}
	
	public boolean remove(String fileName) {	
		UMLNode node = fileTable.get(fileName);		
		if (node == null) return false;
		
		fileTable.remove(fileName);
		node.dispose();
		return true;
	}

//	public UMLNode create(TypeNode typenode, Point curPoint) {
//		UMLNode curNode;
//		
//		curNode = new UMLNode(ClassView.getDefault().getG(), SWT.NONE, 
//				(new ClassFigureCreator()).createClassFigure(typenode));					
//		curNode.setText((typenode.getType().getHandleIdentifier()));
//		curNode.setLocation(curPoint.x, curPoint.y);
//		
//		nodetable.put(typenode, curNode);
//		
//		return curNode;
//	}
	
	public void clear() {
		fileTable.clear();
	}
	
	public void dispose() {
		fileTable = null;
	}
}
