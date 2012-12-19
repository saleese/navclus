/*******************************************************************************
Copyright (c) 2010, 2012 Seonah Lee, SA Lab, KAIST
All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v10.html *
Contributors:
Seonah Lee - initial implementation
 *******************************************************************************/
package navclus.recommendation.clusterer;

import java.io.File;
import java.io.FilenameFilter;

import navclus.recommendation.clusterer.macroclusters.MacroClusterManager;
import navclus.recommendation.clusterer.microclusters.MicroClusterManager;
import navclus.recommendation.clusterer.microclusters.MicroVector;
import navclus.recommendation.reader.SampleXMLSegmenter;

public class Clusterer {

	public MacroClusterManager cluster(String dataDirectory) {
		try {
			File file = new File(dataDirectory); 
			String[] list = file.list(new FilenameFilter() 
			{ 
				@Override 
				public boolean accept(File dir, String name)  
				{ 
					return name.endsWith(".xml"); 
				} 
			}); 

			MicroClusterManager microClusterManager = new MicroClusterManager();	
			MacroClusterManager macroClusterManager = new MacroClusterManager();
			MicroVector microVector = new MicroVector();

			if (list == null) return null;
			for (int i = 0; i < (list.length - 1); i++) { // salee, 2012-09-12			
				microClusterManager = (new SampleXMLSegmenter(microClusterManager)).scanfile(dataDirectory, list[i]);
				microClusterManager.insert2segment("done"); // ... 각 파일 끝나는 정보...
			}

			// Group micro-clusters...
			microVector.createClosestPair(microClusterManager, 2);
			microVector.sort();
			microVector.group();	

			// create macro-clusters		
			macroClusterManager.create(microClusterManager, microVector);
			System.out.println("clustering is done!");
			
			return macroClusterManager;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}	
	}	
}
