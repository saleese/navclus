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

	private ArrayList<TypeNode> typenodes = null;
	private ArrayList<TypeNode> nodetrashes = null;
	
	public TypeNodeList() {
		this.typenodes = new ArrayList<TypeNode>();
		this.nodetrashes = new ArrayList<TypeNode>();
	}
	
	public ArrayList<TypeNode> getTypeNodes() {
		return typenodes;
	}

	public ArrayList<TypeNode> getTrashNodes() {
		return nodetrashes;
	}
	
	public int getSize() {
		return typenodes.size();
	}
	
	public boolean contain(IType curType) {				
		for (TypeNode preNode: typenodes) {
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

	public TypeNode addNode(IType curType) {
		TypeNode typeNode = new TypeNode(curType);		
		this.typenodes.add(typeNode);

		return typeNode;
	}

	public TypeNode addNode(int index, IType curType) {
		TypeNode typeNode = new TypeNode(curType);		
		this.typenodes.add(index, typeNode);

		return typeNode;
	}
	
	public boolean removeNode(TypeNode typeNode) {
		if (typeNode == null) return false;

		this.typenodes.remove(typeNode);
		this.nodetrashes.add(typeNode);
		return true;
	}

	public void dispose() {
		for (TypeNode typeNode: typenodes) {
			typeNode.clear();
		}
		this.typenodes = null; 
		this.nodetrashes.clear(); 
		this.nodetrashes = null;
	}
	
	public void clear() {
		this.typenodes.clear();
		this.nodetrashes.clear();
	}
}
