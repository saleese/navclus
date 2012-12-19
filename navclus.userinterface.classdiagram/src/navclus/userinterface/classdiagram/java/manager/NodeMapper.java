/*******************************************************************************
Copyright (c) 2010, 2012 Seonah Lee, SA Lab, KAIST
All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v10.html *
Contributors:
Seonah Lee - initial implementation
*******************************************************************************/

package navclus.userinterface.classdiagram.java.manager;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import navclus.userinterface.classdiagram.NavClusView;
import navclus.userinterface.classdiagram.classfigure.ClassFigureCreator;
import navclus.userinterface.classdiagram.classfigure.UMLNode;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jdt.core.IType;
import org.eclipse.swt.SWT;


public class NodeMapper {

	public HashMap<TypeNode, UMLNode> nodetable; 	// salee
	
	
	public NodeMapper() {
		this.nodetable = new LinkedHashMap<TypeNode, UMLNode>(); // salee	
	}

	public UMLNode get(TypeNode typenode) {
		return nodetable.get(typenode);		
	}
	
	public TypeNode get(UMLNode umlnode) {
		Set<TypeNode> typenodeset = nodetable.keySet();
		
		for (TypeNode typenode: typenodeset) {
			UMLNode node = this.get(typenode);
			if (node == null) continue;
		
			if (node.equals(umlnode)) {
				System.err.println("test:TypeNode.get");
				return typenode;
			}
		}
		
		return null;		
	}
	
	public boolean remove(TypeNode typenode) {
		System.out.println("remove:" + typenode.getType().getElementName());
		
		UMLNode node = this.get(typenode);		
		if (node == null) return false;
		
		nodetable.remove(typenode);
		node.dispose();
		return true;
	}
	
	public void removeAll() {		
		Set<TypeNode> typenodeset = nodetable.keySet();
		
		for (TypeNode typenode: typenodeset) {
			UMLNode node = this.get(typenode);		
			if (node == null) continue;
			node.dispose();		
		}
		nodetable.clear();
	}
		
	public Point removeAtPosition(TypeNode typenode) {
		UMLNode node = this.get(typenode);
		Point curPoint = node.getLocation();		
		if (node == null) return null;
		
		nodetable.remove(typenode);
		node.dispose();
		return curPoint;
	}
	
	// @author salee 2012-11-08
	public UMLNode draw(TypeNode typenode) {
		UMLNode preGraphNode = this.get(typenode);
		if (preGraphNode == null) { // if it is the new class
			return this.create(typenode);
		}
		else { // if it is an old class
			Point prePoint = preGraphNode.getLocation();
			
			IType preType = typenode.getType();
			if (preType == null) return null;
			
			// setCustomFigure
			IFigure classFigure = (new ClassFigureCreator()).createClassFigure(typenode);
			if (classFigure == null) return null;
			
			preGraphNode.setUMLNode(NavClusView.getDefault().getG(), classFigure);			
			preGraphNode.setText((preType.getHandleIdentifier()));
			preGraphNode.setLocation(prePoint.x, prePoint.y);
			return preGraphNode;
		}		
	}
	
	public UMLNode create(TypeNode typenode) {	
		UMLNode curNode;		
		curNode = new UMLNode(NavClusView.getDefault().getG(), SWT.NONE, 
		 		  (new ClassFigureCreator()).createClassFigure(typenode));					
		curNode.setText((typenode.getType().getHandleIdentifier()));
									
		nodetable.put(typenode, curNode);
		
		return curNode;
	}

	public UMLNode create(TypeNode typenode, Point curPoint) {
		UMLNode curNode;
		
		curNode = new UMLNode(NavClusView.getDefault().getG(), SWT.NONE, 
		 		  (new ClassFigureCreator()).createClassFigure(typenode));					
		curNode.setText((typenode.getType().getHandleIdentifier()));
		curNode.setLocation(curPoint.x, curPoint.y);
		
		nodetable.put(typenode, curNode);
		
		return curNode;
	}
	
	public void clear() {
		nodetable.clear();
	}
}
