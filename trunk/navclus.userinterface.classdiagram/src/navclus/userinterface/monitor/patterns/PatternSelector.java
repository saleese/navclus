/*******************************************************************************
Copyright (c) 2010, 2012 Seonah Lee, SA Lab, KAIST
All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v10.html *
Contributors:
Seonah Lee - initial implementation
*******************************************************************************/

package navclus.userinterface.monitor.patterns;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.LinkedList;

import navclus.recommendation.clusterer.Clusterer;
import navclus.recommendation.clusterer.macroclusters.MacroClusterManager;
import navclus.recommendation.context.StringQueue;
import navclus.recommendation.data.elements.Element;
import navclus.recommendation.data.elements.ElementManager;
import navclus.recommendation.recommender.XMLSampleRecommender;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;



/**
 * PatternSelect is a primary class which reads several interaction traces, creates patterns, print the navigation
 * patterns.
 * 
 * Error: I think this class takes a pipe-line architecture of transfering the pattern from XMLReader through
 * SegmentManager, FragmentManager
 * 
 * @author Seonah Lee, saleese@gmail.com
 */
public class PatternSelector {

	private static PatternSelector singleton;

	public static PatternSelector patternSelector() {
		if (singleton == null) {
			singleton = new PatternSelector();
		}
		return singleton;
	}

//	static String dataDirectory = "C:\\MonitoringData";
	
	private MacroClusterManager macroClusterManager;
	
	private PatternPresenter patternPresenter;

	public PatternPresenter getPatternPresenter() {
		return patternPresenter;
	}

	private final int threshold = 3;

	public void initiate() throws FileNotFoundException {
//		File rootDir = new File(ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + "/MonitoringData");
		
		patternPresenter = new PatternPresenter();
		macroClusterManager = (new Clusterer()).cluster(ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + "/MonitoringData");
	}

	private String setUpDataPath() {
		// TODO I did to flexibly change the path for an installation
		File dummy = new File("");
		return dummy.getAbsolutePath() + "\\workspace\\ca.ubc.cs.salee.monitor.usecontext\\data\\";
		
	}

	/**
	 * select the list of points of interest with the triggers
	 * 
	 * @see SelectionKeepter.addSelection() call this method!
	 */
	public void showRecommendations(LinkedList<IJavaElement> triggers) {
		try {
			ElementManager recommendation = new ElementManager();
			
			// ** show frequent lines here!!! **
			System.out.println("recommendation will start!");
			StringQueue contextQueue = convert2Queue(triggers);
			// making a recommendation
			recommendation = (new XMLSampleRecommender()).recommend(contextQueue, macroClusterManager);
			if ((recommendation == null) || (recommendation.size() <= 0)) return;
			
			recommendation.sort();
			recommendation.println(2, 5);			
			LinkedList<IJavaElement> javaList = convert2JavaList(recommendation, 2, 5);			
			if (javaList.size() <=  0) return;
			
			patternPresenter.show(javaList);
			
		} catch (Exception e) {
			System.err.println("Error in PatternSelector.showRecommendations():" + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public StringQueue convert2Queue(LinkedList<IJavaElement> triggers) {
		StringQueue contextQueue = new StringQueue(15); // default, we have to modify this! 
		
		for (IJavaElement element: triggers) {
			contextQueue.add(element.getHandleIdentifier());
		}		
		return contextQueue;
	}
	
	public LinkedList<IJavaElement> convert2JavaList(ElementManager recommendationList, int iThreshold, int iCount) {
		LinkedList<IJavaElement> javaList = new LinkedList<IJavaElement>();
		
		Iterator iterator = recommendationList.getVector().iterator();
		int cnt = 0;
		while (iterator.hasNext()) {
			Element node = (Element) iterator.next();
			if (node.getCount() >= iThreshold) {
				javaList.add(JavaCore.create(node.getName()));
				cnt++;
			}		
			if (cnt >= iCount)
				break;			
		}
		return javaList;
	}
	
}