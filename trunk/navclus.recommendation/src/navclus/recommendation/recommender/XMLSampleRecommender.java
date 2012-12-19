/*******************************************************************************
Copyright (c) 2010, 2012 Seonah Lee, SA Lab, KAIST
All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v10.html *
Contributors:
Seonah Lee - initial implementation
*******************************************************************************/

package navclus.recommendation.recommender;

import java.util.Set;

import navclus.recommendation.clusterer.macroclusters.MacroClusterManager;
import navclus.recommendation.context.StringQueue;
import navclus.recommendation.data.elements.ElementManager;

import renewed.invertedindex.DocListNode;
import renewed.invertedindex.MacroClusterIndexer;
import renewed.invertedindex.SortedLinkedList;


public class XMLSampleRecommender {
	
	public ElementManager recommend(StringQueue contextQueue, MacroClusterManager macroClusterManager) {
		
		SortedLinkedList<DocListNode> answer = (new XMLSampleRecommender()).retrieve(contextQueue, macroClusterManager.getMacroClusterIndexer());		
		if (answer == null) return null;
		
		ElementManager recommendation = new ElementManager();
		recommendation = macroClusterManager.retrieve(answer);
		recommendation.sort();
		return recommendation;
	}

	public SortedLinkedList<DocListNode> retrieve(StringQueue contextQueue, MacroClusterIndexer macroClusterIndexer) {
		SortedLinkedList<DocListNode> answer = macroClusterIndexer.retrieve(contextQueue.getStringQueue());
		print(contextQueue);
//		print("retrieve answer", answer);
		return answer;
	}
	
	public void print(String title, SortedLinkedList<DocListNode> p) {
		if (p == null) return;
		
		System.out.print(title +": ");
		for (int i = 0; i < p.size(); i++) {	
			System.out.print(p.get(i) + ", ");	
		}
		System.out.println();
	}
	
	public void print(StringQueue contextQueue) {
		System.out.print("Query: ");
		for (String e: contextQueue.toSet()) {
			System.out.print(e + ", ");
		}
		System.out.println();
	}

}