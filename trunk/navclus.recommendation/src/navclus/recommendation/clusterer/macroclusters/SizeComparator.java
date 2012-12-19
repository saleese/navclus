/*******************************************************************************
Copyright (c) 2010, 2012 Seonah Lee, SA Lab, KAIST
All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v10.html *
Contributors:
Seonah Lee - initial implementation
*******************************************************************************/
package navclus.recommendation.clusterer.macroclusters;

import java.util.Comparator;

import navclus.recommendation.data.elements.ElementManager;


public class SizeComparator implements Comparator<ElementManager> {
	@Override
	public int compare(ElementManager o1, ElementManager o2) {
		if (o1.size() > o2.size() )
			return -1;
		else if (o1.size() > o2.size())
			return 0;
		else
			return 1;
	}
	
}
