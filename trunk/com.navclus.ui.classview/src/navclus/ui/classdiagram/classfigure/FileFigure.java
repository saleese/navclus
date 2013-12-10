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

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.swt.graphics.Color;

public class FileFigure extends Figure {

	String filename;

	public FileFigure(Label name, Color classColor) {
		ToolbarLayout layout = new ToolbarLayout();
		setLayoutManager(layout);
		setBorder(new LineBorder(ColorConstants.black, 1));
		setOpaque(true);

		add(name);	
		setName(name.getText());
		setBackgroundColor(classColor);
	}

	public String getName() {
		return filename;
	}

	public void setName(String name) {
		this.filename = name;
	}
}