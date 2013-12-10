/*******************************************************************************
Copyright (c) 2010, 2012 Seonah Lee, SA Lab, KAIST
All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v10.html *
Contributors:
Seonah Lee - initial implementation
 *******************************************************************************/

package navclus.ui.classdiagram.classfigure;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

import com.navclus.ui.classview.Activator;

public class FileFigureCreator {

	Color classColor = new Color(null, 255, 255, 206);
	Font classFont = new Font(null, "Arial", 12, SWT.BOLD);

	public FileFigureCreator() {
		super();
	}

	public IFigure createClassFigure(String fileName) {
		if (fileName == null)
			return null;
		if (fileName == "")
			return null;

		// add class name
		Label classLabel = new Label(fileName, Activator.getDefault().getImage("file_obj"));
		classLabel.setFont(classFont);
		
		// create the figure
		ClassFigure classFigure = new ClassFigure(classLabel, classColor);
		classFigure.setSize(-1, -1);
		classFigure.setBackgroundColor(classColor);

		return classFigure;
	}
}
