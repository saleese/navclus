/*******************************************************************************
Copyright (c) 2010, 2012 Seonah Lee, SA Lab, KAIST
All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v10.html *
Contributors:
Seonah Lee - initial implementation
*******************************************************************************/
package navclus.recommendation.clusterer.microclusters;

import java.util.Comparator;

public class MicroComparator implements Comparator<MicroPair> {

	@Override
	public int compare(MicroPair arg0, MicroPair arg1) {
		// TODO Auto-generated method stub
		return (arg0).getSimilarity() > (arg1).getSimilarity() ? 0:1;
	}

}
