/*******************************************************************************
Copyright (c) 2010, 2012 Seonah Lee, SA Lab, KAIST
All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v10.html *
Contributors:
Seonah Lee - initial implementation
*******************************************************************************/

package navclus.ui.classdiagram.file.manager;

import java.util.ArrayList;

public class FileNodeList {

	private ArrayList<String> filenodes = null;
	private ArrayList<String> nodetrashes = null;
	
	public FileNodeList() {
		this.filenodes = new ArrayList<String>();
		this.nodetrashes = new ArrayList<String>();
	}
	
	public ArrayList<String> getFileNodes() {
		return filenodes;
	}

	public ArrayList<String> getTrashNodes() {
		return nodetrashes;
	}
	
	public int getSize() {
		return filenodes.size();
	}
	
	public boolean contain(String curFile) {				
		for (String preNode: filenodes) {
			if (preNode.equals(curFile)) {
				return true;				
			}						
		}
		return false;
	}
	
	public String findNode(String curFile) {
		for (String preNode: filenodes) {
			if (preNode.equals(curFile)) {
				return preNode;				
			}						
		}
		return null;
	}

	public String addNode(String curFile) {
		String fileNode = curFile;		
		this.filenodes.add(fileNode);

		return fileNode;
	}

	public String addNode(int index, String curFile) {
		String fileNode = new String(curFile);		
		this.filenodes.add(index, fileNode);

		return fileNode;
	}
	
	public boolean removeNode(String fileNode) {
		if (fileNode == null) return false;
		
		this.filenodes.remove(fileNode);
		
		this.nodetrashes.add(fileNode);
		return true;
	}

	public void dispose() {
		filenodes = null;
		nodetrashes = null;
	}
	
	public void clear() {
		filenodes.clear();
		nodetrashes.clear();
	}
}
