/*******************************************************************************
Copyright (c) 2010, 2012 Seonah Lee, SA Lab, KAIST
All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v10.html *
Contributors:
Seonah Lee - initial implementation
 *******************************************************************************/

package navclus.ui.classdiagram.jobs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.swt.widgets.Display;

//import navclus.userinterface.classdiagram.PlugIn;




import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

import com.navclus.ui.classview.views.ClassView;

import navclus.ui.classdiagram.actions.RedrawAction;
import navclus.ui.classdiagram.java.analyzer.RootModel;
import navclus.ui.classdiagram.java.analyzer.TypeModel;
import navclus.ui.classdiagram.utils.JavaEditorUtil;

public class JavaModification extends Job {

	public static final String MY_FAMILY = "modifyingJobFamily";

	public boolean belongsTo(Object family) {
		return family == MY_FAMILY;
	}

	ITextSelection selectedtext;
	IJavaElement locElement;
	IJavaElement topElement;
	RootModel rootmodel;

	public JavaModification(ITextSelection selectedtext,
			IJavaElement topElement,
			RootModel rootmodel) {
		super("modifyingJob");
		this.selectedtext = selectedtext;
		this.topElement = topElement;		
		this.rootmodel = rootmodel;
	}

	public IStatus run(IProgressMonitor monitor) {
		try {			
			IJavaElement locElement = ((ITypeRoot) topElement).getElementAt(selectedtext.getOffset());
			if (locElement == null) return Status.CANCEL_STATUS;
			
			System.out.println("selectionChanged: "	+ locElement.getElementName());
			rootmodel.addElement(locElement, topElement);
			ClassView.getDefault().getSelectionKeeper().addSelection(locElement);

			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					if (ClassView.getDefault().getG().getNodes().size() > 0) 
						(new RedrawAction()).run();
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
		return Status.OK_STATUS;
	}
}
