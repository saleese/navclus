/*******************************************************************************
Copyright (c) 2010, 2012 Seonah Lee, SA Lab, KAIST
All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v10.html *
Contributors:
Seonah Lee - initial implementation
 *******************************************************************************/

package navclus.ui.monitor.patterns;

import java.util.LinkedList;

import navclus.ui.classdiagram.actions.ClearAction;
import navclus.ui.classdiagram.actions.RedrawAction;
import navclus.ui.classdiagram.java.analyzer.RootModel;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.widgets.Display;

import com.navclus.ui.classview.views.ClassView;

public class PatternPresenter {

	RootModel rootmodel;

	public PatternPresenter() {
		this.rootmodel = ClassView.getDefault().getRootModel(); // I change the 	
	}

	public void show(LinkedList<IJavaElement> selectedElements) throws JavaModelException {
		for (IJavaElement javaElement : selectedElements) {
			if (javaElement == null) continue;

			switch (javaElement.getElementType()) {
			case (IJavaElement.METHOD):
			case (IJavaElement.FIELD):
				// show the elements in a class figure
				rootmodel.addMember(javaElement);
				break;
			case (IJavaElement.TYPE):
				rootmodel.createType((IType) javaElement);
				break;
			case (IJavaElement.COMPILATION_UNIT):
				rootmodel.openCU((ICompilationUnit) javaElement);
				break;
			}
		}
//		rootmodel.drawNodes(); // 2013-12-10: this is repeated in RedrawAction()
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				(new RedrawAction()).run();
			}
		});
//		(new RedrawAction()).run(); //2013-12-10: add async
	}

	public void clear() {
		rootmodel.clearModel();
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				(new ClearAction()).run();
			}
		});
//		(new ClearAction()).run(); //2013-12-10: add async
	}
}
