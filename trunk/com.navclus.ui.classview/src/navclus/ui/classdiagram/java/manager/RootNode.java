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

import navclus.ui.classdiagram.classfigure.UMLNode;
import navclus.ui.classdiagram.file.manager.FileMapper;
import navclus.ui.classdiagram.file.manager.FileNodeList;

import org.eclipse.jdt.core.IType;
import org.eclipse.swt.SWT;
import org.eclipse.zest.core.widgets.GraphConnection;

import com.navclus.ui.classview.views.ClassView;

public class RootNode {

	private TypeNodeList typeNodeList;
	private NodeMapper nodeMapper;
	
	private FileNodeList fileNodeList;
	private FileMapper fileMapper;
	
	private ConnectionList structuralRelationList;


	boolean isDirty = false;

	public RootNode() {
		this.typeNodeList = new TypeNodeList();
		this.fileNodeList = new FileNodeList();
		this.structuralRelationList = new ConnectionList();
		this.nodeMapper = new NodeMapper();
		this.fileMapper = new FileMapper();
	}

	public ArrayList<TypeNode> getTypeNodes() {
		return typeNodeList.getTypeNodes();
	}
	
	public ArrayList<TypeNode> getWhiteNodes() {
		return typeNodeList.getWhiteNodes();
	}

	public boolean contain(IType curType) {
		if (typeNodeList.contain(curType))
			return true;
		else if (typeNodeList.containWhite(curType))
			return true;
		else
			return false;
	}

	public TypeNode findNode(IType curType) {
		return typeNodeList.findNode(curType);
	}

	public TypeNode addNode(IType curType) {
		return typeNodeList.addNode(curType);
	}
	
	public TypeNode addWhiteNode(IType curType) {
		return typeNodeList.addWhiteNode(curType);
	}

	public boolean removeNodewithChildren(IType curType) {
		TypeNode curNode = typeNodeList.findNode(curType);
		if (curNode == null)
			return false;

		// delete children
		for (IType childtype : curNode.embeddedTypes) {
			removeNode(childtype);
		}

		return removeNode(curType);
	}

	public boolean removeNode(IType curType) {
		TypeNode curNode = typeNodeList.findNode(curType);
		if (curNode == null)
			return false;

		removeStructuralRelations(curType);
		typeNodeList.removeNode(curNode);
		return nodeMapper.remove(curNode);
	}

	public boolean isDirty() {
		return isDirty;
	}

	public void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
	}

	public UMLNode getUMLNode(TypeNode node) {
		return nodeMapper.get(node);
	}

	public TypeNode getTypeNode(UMLNode node) {
		System.err.println("test:getTypeNode");
		return nodeMapper.get(node);
	}

	/**************************************************************************
	 * Very Important for drawing classes and files *
	 * ************************************************************************/
	public void drawGraphNodes() {
		if ((typeNodeList.getSize() == 0) && (fileNodeList.getSize() == 0))
			return;

		if (typeNodeList.getSize() > 0) {
			for (TypeNode node : typeNodeList.getTypeNodes()) { // it is modifiable
				nodeMapper.draw(node);
			}
		}
		
		if (typeNodeList.getWhiteSize() > 0) {
			for (TypeNode node : typeNodeList.getWhiteNodes()) { // it is modifiable
				nodeMapper.drawWhite(node);
			}
		}

		if (fileNodeList.getSize() > 0) {
			for (String node : fileNodeList.getFileNodes()) { // it is modifiable
				fileMapper.draw(node);
			}
		}
	}

	/**************************************************************************
	 * Structural Relationships *
	 * ************************************************************************/
	public void drawStructuralRelations() {
		List<ConnectionNode> connectionNodes = (List<ConnectionNode>) this
				.getStructuralRelations(); // salee - error
		for (ConnectionNode connectionNode : connectionNodes) {
			UMLNode sourceNode = this
					.getUMLNode(connectionNode.getSourceNode());
			UMLNode destinationNode = this.getUMLNode(connectionNode
					.getDestinationNode());
			if ((sourceNode == null) || (destinationNode == null))
				continue;

			// draw a connection
			GraphConnection curConnection = new GraphConnection(ClassView
					.getDefault().getG(), SWT.NONE, sourceNode, destinationNode);
			curConnection.setText(connectionNode.getTag());
			curConnection.setArrowTip(connectionNode.getArrowTip());
		}
	}

	public List<ConnectionNode> getStructuralRelations() {
		return structuralRelationList.getConnections();
	}

	public ConnectionNode addStructuralRelation(TypeNode node1, TypeNode node2,
			String tag) {
		return structuralRelationList.addConnection(node1, node2, tag);
	}

	public void removeStructuralRelations(IType type) { // doing
		structuralRelationList.removeConnection(type);
	}

	public void removeStructuralRelations(
			List<ConnectionNode> dummyconnectionparts) { // doing
		structuralRelationList.removeConnection(dummyconnectionparts);
	}

	public void updateStructuralRelations(IType type) { // doing
		structuralRelationList.updateConnection(type);
	}

	public boolean isStructurallyRelated(TypeNode node1, TypeNode node2) {
		return structuralRelationList.isConnected(node1, node2);
	}

	/**************************************************************************
	 * Both Relationships *
	 * ************************************************************************/

	public void clear() {
		if (ClassView.getDefault() == null)
			return;
		structuralRelationList.clear();
		
		typeNodeList.clear();
		nodeMapper.removeAll();
		nodeMapper.clear();
		
		fileNodeList.clear();
		fileMapper.removeAll();
		fileMapper.clear();
	}

	public void dispose() {
		
		structuralRelationList.dispose();
		
		typeNodeList.dispose();
		nodeMapper.dispose();
		
		fileNodeList.dispose();
		fileMapper.dispose();
	}

	public ArrayList<String> getFileNodes() {
		return fileNodeList.getFileNodes();
	}

	public boolean contain(String curFile) {
		if (fileNodeList.contain(curFile))
			return true;
		else
			return false;
	}

	public String findNode(String curFile) {
		return fileNodeList.findNode(curFile);
	}

	public String addNode(String curFile) {
		return fileNodeList.addNode(curFile);
	}

	public boolean removeNode(String curFile) {
		String curNode = fileNodeList.findNode(curFile);
		if (curNode == null)
			return false;

		fileNodeList.removeNode(curNode);
		return fileMapper.remove(curNode);
	}
	
	// public boolean removeNodewithChildren(String curFile) {
	// String curNode = fileNodeList.findNode(curFile);
	// if (curNode == null) return false;
	// return removeNode(curFile);
	// }
	//
	// public boolean removeNode(String curFile) {
	// String curNode = fileNodeList.findNode(curFile);
	// if (curNode == null) return false;
	//
	// fileNodeList.removeNode(curNode);
	// return fileMapper.remove(curNode);
	// }
}
