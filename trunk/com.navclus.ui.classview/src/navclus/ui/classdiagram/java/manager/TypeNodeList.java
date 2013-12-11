/*******************************************************************************
Copyright (c) 2010, 2012 Seonah Lee, SA Lab, KAIST
All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v10.html *
Contributors:
Seonah Lee - initial implementation
*******************************************************************************/

package navclus.ui.classdiagram.java.manager;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IType;

public class TypeNodeList {

	private ArrayList<TypeNode> typenodes;
	private ArrayList<TypeNode> whitenodes;
	
	public TypeNodeList() {
		this.typenodes = new ArrayList<TypeNode>();
		this.whitenodes = new ArrayList<TypeNode>();
	}
	
	public ArrayList<TypeNode> getTypeNodes() {
		return typenodes;
	}

	public ArrayList<TypeNode> getWhiteNodes() {
		return whitenodes;
	}
	
	public int getSize() {
		return typenodes.size();
	}
	
	public int getWhiteSize() {
		return whitenodes.size();
	}
	
	public boolean contain(IType curType) {				
		for (TypeNode preNode: typenodes) {
			if (preNode.getType().getHandleIdentifier().equals(curType.getHandleIdentifier())) {
				return true;				
			}						
		}
		return false;
	}
	
	public boolean containWhite(IType curType) {				
		for (TypeNode preNode: whitenodes) {
			if (preNode.getType().getHandleIdentifier().equals(curType.getHandleIdentifier())) {
				return true;				
			}						
		}
		return false;
	}
	
	public TypeNode findNode(IType curType) {				
		for (TypeNode preNode: typenodes) {
			if (preNode.getType().getHandleIdentifier().equals(curType.getHandleIdentifier())) {
				return preNode;				
			}						
		}
		return null;
	}
	
	public TypeNode findWhiteNode(IType curType) {				
		for (TypeNode preNode: whitenodes) {
			if (preNode.getType().getHandleIdentifier().equals(curType.getHandleIdentifier())) {
				return preNode;				
			}						
		}
		return null;
	}

	public TypeNode addNode(IType curType) {
		TypeNode typeNode = new TypeNode(curType);		
		this.typenodes.add(typeNode);

		return typeNode;
	}
	
	public TypeNode addWhiteNode(IType curType) {
		TypeNode typeNode = new TypeNode(curType);		
		this.whitenodes.add(typeNode);

		return typeNode;
	}


	public boolean removeNode(TypeNode typeNode) {
		if (typeNode == null) return false;

		this.typenodes.remove(typeNode);
		return true;
	}
	
	public boolean removeWhiteNode(TypeNode typeNode) {
		if (typeNode == null) return false;

		this.whitenodes.remove(typeNode);
		return true;
	}

	public void dispose() {
		for (TypeNode typeNode: typenodes) {
			typeNode.clear();
		}
		this.typenodes = null; 
		this.whitenodes.clear(); 
		this.whitenodes = null;
	}
	
	public void clear() {
		this.typenodes.clear();
		this.whitenodes.clear();
	}
}
