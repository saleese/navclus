/*******************************************************************************
Copyright (c) 2010, 2012 Seonah Lee, SA Lab, KAIST
All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v10.html *
Contributors:
Seonah Lee - initial implementation
 *******************************************************************************/

package navclus.ui.classdiagram.java.analyzer;

import java.util.ArrayList;

import com.navclus.ui.classview.Activator;

import navclus.ui.classdiagram.java.manager.ConnectionNode;
import navclus.ui.classdiagram.java.manager.RootNode;
import navclus.ui.classdiagram.java.manager.TypeNode;
//import navclus.ui.classdiagram.utils.FlagRedraw;
import navclus.ui.classdiagram.utils.TypeHistory;

import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;

public class RootModel {

	private RootNode rootNode;
	protected TypeModel typemodel;
	protected RelationModel relationmodel;
	protected RelationAnalyzer relationanalyzer;

	public RootModel(RootNode rootNode) {
		this.rootNode = rootNode;
		this.typemodel = new TypeModel(rootNode);
		this.relationanalyzer = new RelationAnalyzer(rootNode);
		this.relationmodel = new RelationModel(rootNode);
	}

	public RootNode getRootNode() {
		return rootNode;
	}

	public void setRootNode(RootNode rootNode) {
		this.rootNode = rootNode;
	}

	public boolean contain(IType curType) {
		return rootNode.contain(curType);
	}

	public void cleanUp() {
		rootNode.dispose();
	}

	public RelationAnalyzer getRelationModel() {
		return relationanalyzer;
	}

	public void setRelationModel(RelationAnalyzer relationmodel) {
		this.relationanalyzer = relationmodel;
	}

	public void addJavaFile(IJavaElement _element) throws JavaModelException {
		TypeHistory.setCurElement(_element);
		if (_element instanceof ICompilationUnit) {
			this.openCU((ICompilationUnit) _element);
		} else if (_element instanceof IClassFile) {
			this.openClass((IClassFile) _element);
		} else if (_element instanceof IType) {
			this.createType((IType) _element);
		} else if (_element instanceof IMember) {
			_element = _element.getAncestor(IJavaElement.TYPE);
			this.createType((IType) _element);
		} else {
			// MessageDialog.openInformation(Activator.getDefaultShell(),
			// "Error",
			// "Cannot open this kind of Java Element:" + _element);
		}
	}

	/**
	 * Opens a compilation unit and all the types in it. : not including the
	 * embedded class
	 */
	public void openCU(ICompilationUnit cu) throws JavaModelException {
		IType[] types = cu.getTypes();
		for (IType type : types) {
			createType(type);
			break;
		}
	}

	/**
	 * Loads a class into the editor.
	 */
	public void openClass(IClassFile classFile) throws JavaModelException {
		IType type = classFile.getType();
		createType(type);
	}

	public TypeNode createType(final IType curType) {
		if (curType == null)
			return null;

		// return null if the curNode exists in the list & find the position
		if (rootNode.contain(curType))
			return null;

		// add the node to list of the root part
		TypeNode typeNode;
		typeNode = rootNode.addNode(curType);

		// add the connections of the node to the list of the root part
		createConnections(typeNode);

		return typeNode;
	}

	public String addFile(String curFile) {
		if (curFile == null)
			return null;

		// return null if the curNode exists in the list & find the position
		if (rootNode.contain(curFile))
			return null;

		// add the node to list of the root part
		String fileNode = rootNode.addNode(curFile);
		return fileNode;
	}
	
	public void deleteFile(String curFile) {
		// if (
		rootNode.removeNode(curFile);
		// rootNode.updateDeleteView();
	}

	public void createConnections(TypeNode curNode) {
		ArrayList<TypeNode> nodes = rootNode.getTypeNodes();

		for (TypeNode preNode : nodes) {
			if (preNode.getType().getHandleIdentifier()
					.equals(curNode.getType().getHandleIdentifier()))
				continue;

			try {
				draw_Relationships(preNode, curNode);
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
		}
	}

	// public void createNavigationalRelation(IType type1, IType type2) {
	// TypeNode node1 = rootNode.findNode(type1);
	// TypeNode node2 = rootNode.findNode(type2);
	//
	// ConnectionNode connection = rootNode.addNavigationalRelation(node1,
	// node2, "");
	//
	// // convert the order
	// connection.setArrowTip(3); // start_tip;
	//
	// // add related methods
	// // FlagRedraw.setSuper(true);
	// }

	/**
	 * Opens a compilation unit and all the types in it.
	 */
	public void closeCU(ICompilationUnit cu) throws JavaModelException {
		if (cu == null)
			return;
		if (cu.exists() == false)
			return;

		IType[] types = cu.getAllTypes();

		for (int i = 0; i < types.length; i++) {
			deleteTypewithChildren(types[i]);
		}
	}

	/**
	 * Loads a class into the editor.
	 */
	public void closeClass(IClassFile classFile) throws JavaModelException {
		IType type = classFile.getType();
		deleteTypewithChildren(type);
	}

	/**
	 * Opens a compilation unit and all the types in it.
	 */
	public void closeCUwoUpdate(ICompilationUnit cu) throws JavaModelException {
		IType[] types = cu.getAllTypes();

		for (int i = 0; i < types.length; i++) {
			deleteTypewithChildrenwoUpdate(types[i]);
		}
	}

	/**
	 * Loads a class into the editor.
	 */
	public void closeClasswoUpdate(IClassFile classFile)
			throws JavaModelException {
		IType type = classFile.getType();
		deleteTypewithChildrenwoUpdate(type);
	}

	public void deleteTypewithChildren(IType curType) {
		// if (
		rootNode.removeNodewithChildren(curType);
		// rootNode.updateDeleteView();
	}

	public void deleteTypewithChildrenwoUpdate(IType curType) {
		rootNode.removeNodewithChildren(curType);
	}

	// /////////////////////////from type model
	// //////////////////////// called by PatternPresenter
	public void addMember(IJavaElement locElement) {
		if (locElement == null)
			return;

		TypeNode locNode = getTypeNode(locElement);
		if (locNode == null)
			return;

		if (addMember(locNode, locElement)) {
			// rootNode.synchronizeNodesinView();
		}
		return;
	}

	// finding the node having itypeTop
	// if finding the node, add the javaelement methods
	private TypeNode getTypeNode(IJavaElement locElement) {
		IType locType = (IType) locElement.getAncestor(IJavaElement.TYPE);

		TypeNode locNode = rootNode.findNode(locType);
		if (locNode == null) {
			locNode = createType(locType);

			// drawing?
			// rootNode.synchronizeNodesinView();
		}

		return locNode;
	}

	private boolean addMember(TypeNode locNode, IJavaElement locElement) {
		if (locElement instanceof IMethod) {
			return locNode.addMethod((IMethod) locElement);
		} else if (locElement instanceof IField) {
			return locNode.addField((IField) locElement);
		}
		return false;
	}

	// null point exception!!!
	public void addElement(IJavaElement locElement, IJavaElement topElement) {
		if (locElement == null)
			return;
		if (topElement == null)
			return;

		IType topType = ((ITypeRoot) topElement).findPrimaryType();
		if (topType == null)
			return;
		if (topType.getHandleIdentifier().equals(
				locElement.getHandleIdentifier()))
			return;

		// finding the node having itypeTop
		// if finding the node, add the javaelement methods
		IType locType = (IType) locElement.getAncestor(IJavaElement.TYPE);
		TypeNode locNode = rootNode.findNode(locType);
		TypeNode topNode = rootNode.findNode(topType);
		if (topNode == null)
			return;
		if (locNode == null) {
			locNode = createType(locType);
			if (locNode == null)
				return;
		}

		if (!topType.getHandleIdentifier()
				.equals(locType.getHandleIdentifier())) {
			topNode.embeddedTypes.add(locType);
			TypeHistory.setPreType(locType);
		}

		if (locElement instanceof IMethod) {
			locNode.addMethod((IMethod) locElement); // if it exists, return
														// true
		} else if (locElement instanceof IField) {
			locNode.addField((IField) locElement); // if it exists, return true
		}
	}

	public void draw_Relationships(TypeNode preNode, TypeNode curNode)
			throws JavaModelException {
		try {

			// ----- Inheritance ----- //
			// class - extends: preNode --> curNode
			if (relationanalyzer.doesExtend(preNode, curNode)) {
				relationmodel.drawExtend(curNode, preNode); // Error! salee
				return;
			}
			if (relationanalyzer.doesExtend(curNode, preNode)) {
				relationmodel.drawExtend(preNode, curNode);
				return;
			}

			// interfaces - implements: preNode --> curNode
			if (relationanalyzer.doesImplement(preNode, curNode)) {
				relationmodel.drawImplement(curNode, preNode);
				return;
			}

			if (relationanalyzer.doesImplement(curNode, preNode)) {
				relationmodel.drawImplement(preNode, curNode);
				return;
			}

			// used: preNode --> curNode
			if (relationanalyzer.usedLocalMembers(preNode, curNode))
				relationmodel.drawUses(curNode, preNode);
			if (relationanalyzer.usedLocalMembers(curNode, preNode))
				relationmodel.drawUses(preNode, curNode);

		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}

	public void clearModel() {
		rootNode.clear();
	}

	// public void printNodes() {
	// rootNode.printNodes();
	// }

	// public void drawNodes() {
	// rootNode.drawGraphNodes();
	// }
}
