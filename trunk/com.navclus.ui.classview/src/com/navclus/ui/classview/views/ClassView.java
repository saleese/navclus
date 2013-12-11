package com.navclus.ui.classview.views;

import navclus.ui.classdiagram.actions.ClearAction;
import navclus.ui.classdiagram.java.analyzer.RootModel;
import navclus.ui.classdiagram.java.manager.RootNode;
import navclus.ui.classdiagram.jobs.JavaAddition;
import navclus.ui.classdiagram.jobs.JavaSynchronization;
import navclus.ui.classdiagram.listeners.GraphMouseListener;
import navclus.ui.classdiagram.utils.JavaEditorUtil;
import navclus.ui.monitor.listeners.JavaEditorPartListener2;
import navclus.ui.monitor.listeners.JavaEditorSelectionListener;
import navclus.ui.monitor.patterns.PatternSelector;
import navclus.ui.monitor.selections.SelectionKeeper;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.*;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.action.*;
//import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.HorizontalTreeLayoutAlgorithm;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class ClassView extends ViewPart {
	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "com.navclus.ui.classview.views.ClassView";
	public final static int DRAW_NAVIGATIONAL_RELATIONSHIP = 10;
	public final static int DRAW_STRUCTURAL_RELATIONSHIP = 20;

	private Graph g = null;
	private RootModel rootmodel = null;

	JavaEditorPartListener2 javaeditorpartlistner2;
	JavaEditorSelectionListener javaeditorselectionlistener;

	private PatternSelector patternSelector;
	private SelectionKeeper selectionKeeper;

	// private TableViewer viewer;
	private Action action1;
	private Action action2;
	private Action doubleClickAction;

	private static ClassView viewer;

	/**
	 * The constructor.
	 */
	public ClassView() {
		viewer = this;
	}

	/**
	 * Returns the shared instance.
	 */
	public static ClassView getDefault() {
		if (viewer == null)
			viewer = new ClassView();

		return viewer;
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {

		final IWorkbench workbench = PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable() {
			public void run() {
				try {
					javaeditorpartlistner2 = new JavaEditorPartListener2();
					workbench.getActiveWorkbenchWindow().getPartService()
							.addPartListener(javaeditorpartlistner2);

					javaeditorselectionlistener = new JavaEditorSelectionListener();
					workbench
							.getActiveWorkbenchWindow()
							.getSelectionService()
							.addPostSelectionListener(
									javaeditorselectionlistener);

				} catch (Throwable t) {
					// StatusHandler.log(new Status(IStatus.ERROR,
					// UiUsageMonitorPlugin.ID_PLUGIN,
					// "Monitor failed to start", t));
				}
			}
		});

		// Create the help context id for the viewer's control
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(),
		// "com.navclus.userinterface.classview.viewer");

		g = new Graph(parent, SWT.NONE);

		g.setLayoutAlgorithm(new HorizontalTreeLayoutAlgorithm(
				LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);

		// g.setLayoutAlgorithm(new
		// CompositeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING,
		// new LayoutAlgorithm[] {
		// new
		// HorizontalTreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING),
		// new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING) }),
		// true);

		g.setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);
		
		// Error: we should stop the listener before closing this program
		g.getLightweightSystem().getRootFigure().addMouseListener(new GraphMouseListener());

		rootmodel = new RootModel(new RootNode());

		makeActions();
		// hookContextMenu();
		// hookDoubleClickAction();
		contributeToActionBars();
		
		synchronizeNodes();

		this.patternSelector = new PatternSelector();
		this.selectionKeeper = new SelectionKeeper();
		try { // note: I will move this to a job thread, later
			this.patternSelector.initiate();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				ClassView.this.fillContextMenu(manager);
			}
		});
		// Menu menu = menuMgr.createContextMenu(viewer.getControl());
		// viewer.getControl().setMenu(menu);
		// getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(action1);
		manager.add(new Separator());
		manager.add(action2);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(action1);
		manager.add(action2);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(action1);
		manager.add(action2);
	}

	private void makeActions() {
		action1 = new Action() {
			public void run() {
				g.applyLayout();
				// showMessage("Action 1 executed");
			}
		};
		action1.setText("Apply a Layout");
		action1.setToolTipText("Apply a Layout");
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_TOOL_NEW_WIZARD));

		action2 = new ClearAction();
		action2.setText("Clear");
		action2.setToolTipText("Clear");
		action2.setImageDescriptor(ImageDescriptor.createFromImage(new Image(
				Display.getDefault(), this.getClass().getResourceAsStream(
						"clear.gif"))));
		// action2.setChecked(true); -- looks to be selected

		doubleClickAction = new Action() {
			public void run() {
				// ISelection selection = viewer.getSelection();
				// Object obj =
				// ((IStructuredSelection)selection).getFirstElement();
//				 showMessage("Double-click detected on "+obj.toString());
			}
		};
	}

	private void hookDoubleClickAction() {
		// viewer.addDoubleClickListener(new IDoubleClickListener() {
		// public void doubleClick(DoubleClickEvent event) {
		// doubleClickAction.run();
		// }
		// });
	}

	private void showMessage(String message) {
		// MessageDialog.openInformation(
		// viewer.getControl().getShell(),
		// "Class View",
		// message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		// viewer.getControl().setFocus();
	}

	public RootModel getRootModel() {
		return rootmodel;
	}

	public RootNode getRootNode() {
		if (rootmodel == null)
			return null;

		return rootmodel.getRootNode();
	}

	public Graph getG() {
		return g;
	}

	public PatternSelector getPatternSelector() {
		return patternSelector;
	}

	public SelectionKeeper getSelectionKeeper() {
		return selectionKeeper;
	}

	// called by the partOpened method ...
	private void synchronizeNodes() {
		if (ClassView.getDefault().getRootModel() == null)
			return;

		// add the element if the node does not exist in the graph
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		JavaSynchronization synchronizingJob = new JavaSynchronization(page, ClassView.getDefault().getRootModel());
		synchronizingJob.setPriority(Job.INTERACTIVE);
		synchronizingJob.schedule();
	}

	@Override
	public void dispose() {
		final IWorkbench workbench = PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable() {
			public void run() {
				try {
					workbench.getActiveWorkbenchWindow().getPartService()
							.removePartListener(javaeditorpartlistner2);
					workbench
							.getActiveWorkbenchWindow()
							.getSelectionService()
							.removePostSelectionListener(
									javaeditorselectionlistener);
				} catch (Throwable t) {
					// StatusHandler.log(new Status(IStatus.ERROR,
					// UiUsageMonitorPlugin.ID_PLUGIN,
					// "Monitor failed to start", t));
				}
			}
		});

		// Error: we should stop the listener before closing this program
		g.getLightweightSystem().getRootFigure().removeMouseListener(new GraphMouseListener());
		this.rootmodel.cleanUp();
		this.rootmodel = null;
		this.patternSelector = null;
		this.selectionKeeper = null;

		super.dispose();
	}
}