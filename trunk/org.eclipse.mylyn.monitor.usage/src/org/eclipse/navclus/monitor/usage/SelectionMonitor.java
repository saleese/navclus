/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.navclus.monitor.usage;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.actions.SelectionConverter;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.navclus.context.core.ContextCore;
import org.eclipse.navclus.context.core.IInteractionElement;
import org.eclipse.navclus.internal.monitor.ui.MonitorUiPlugin;
import org.eclipse.navclus.monitor.core.InteractionEvent;
import org.eclipse.navclus.monitor.ui.AbstractUserInteractionMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.EditorPart;

/**
 * Limited to Java selections.
 * 
 * @author Mik Kersten
 */
public class SelectionMonitor extends AbstractUserInteractionMonitor {

	private static final String STRUCTURE_KIND_JAVA = "java";

	private static final String ID_JAVA_UNKNOWN = "(non-source element)";

	public static final String SELECTION_DEFAULT = "selected";

	public static final String SELECTION_NEW = "new";

	public static final String SELECTION_DECAYED = "decayed";

	public static final String SELECTION_PREDICTED = "predicted";

	private static final Object ID_JAVA_UNKNOW_OLD = "(non-existing element)";

	private IJavaElement lastSelectedElement = null;

	// private InteractionEventObfuscator obfuscator = new InteractionEventObfuscator();

	@Override
	protected void handleWorkbenchPartSelection(IWorkbenchPart part, ISelection selection, boolean contributeToContext) {
		// ignored, since not using context monitoring facilities
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {

		String structureKind = InteractionEvent.ID_UNKNOWN;
		String elementHandle = InteractionEvent.ID_UNKNOWN;

		// salee: interactionKind = selection
		InteractionEvent.Kind interactionKind = InteractionEvent.Kind.SELECTION;

		if (selection instanceof StructuredSelection) {
			StructuredSelection structuredSelection = (StructuredSelection) selection;
			Object selectedObject = structuredSelection.getFirstElement();
			if (selectedObject == null) {
				return;
			}
			if (selectedObject instanceof IJavaElement) {
				IJavaElement javaElement = (IJavaElement) selectedObject;
				structureKind = STRUCTURE_KIND_JAVA;
				elementHandle = javaElement.getHandleIdentifier();
				// obfuscatedElementHandle =
				// obfuscateJavaElementHandle(javaElement);
				lastSelectedElement = javaElement;

			} else {
				structureKind = InteractionEvent.ID_UNKNOWN + ": " + selectedObject.getClass();
				if (selectedObject instanceof IAdaptable) {
					IResource resource = (IResource) ((IAdaptable) selectedObject).getAdapter(IResource.class);
					if (resource != null) {
						elementHandle = getHandleIdentifier(resource.getFullPath());
					}
				}
			}
		} else {
			if (selection instanceof TextSelection && part instanceof JavaEditor) {
				TextSelection textSelection = (TextSelection) selection;
				IJavaElement javaElement;
				try {
					javaElement = SelectionConverter.resolveEnclosingElement((JavaEditor) part, textSelection);
					if (javaElement != null) {
						structureKind = STRUCTURE_KIND_JAVA;
						// obfuscatedElementHandle =
						// obfuscateJavaElementHandle(javaElement);
						elementHandle = javaElement.getHandleIdentifier();
						if (javaElement.equals(lastSelectedElement)) {
							interactionKind = InteractionEvent.Kind.EDIT;
						}
						lastSelectedElement = javaElement;
					}
				} catch (JavaModelException e) {
					// ignore unresolved elements
				}
			} else if (part instanceof EditorPart) {
				EditorPart editorPart = (EditorPart) part;
				IEditorInput input = editorPart.getEditorInput();
				if (input instanceof IPathEditorInput) {
					structureKind = "file";
					elementHandle = getHandleIdentifier(((IPathEditorInput) input).getPath());
					// obfuscatedElementHandle =
					// obfuscator.obfuscateResourcePath(((IPathEditorInput)
					// input).getPath());
				}
			}
		}
		IInteractionElement node = ContextCore.getContextManager().getElement(elementHandle);
		String delta = "";
		float selectionFactor = ContextCore.getCommonContextScaling().get(InteractionEvent.Kind.SELECTION);

		if (node != null) {
			if (node.getInterest().getEncodedValue() <= selectionFactor
					&& node.getInterest().getValue() > selectionFactor) {
				delta = SELECTION_PREDICTED;
			} else if (node.getInterest().getEncodedValue() < selectionFactor
					&& node.getInterest().getDecayValue() > selectionFactor) {
				delta = SELECTION_DECAYED;
			} else if (node.getInterest().getValue() == selectionFactor
					&& node.getInterest().getDecayValue() < selectionFactor) {
				delta = SELECTION_NEW;
			} else {
				delta = SELECTION_DEFAULT;
			}
		}

		InteractionEvent event = new InteractionEvent(interactionKind, structureKind, elementHandle, part.getSite().getId(), "null", delta, 0);
		MonitorUiPlugin.getDefault().notifyInteractionObserved(event);
	}

	// NOTE: duplicated from ResourceStructureBridge
	private String getHandleIdentifier(IPath path) {
		if (path != null) {
			return path.toPortableString();
		} else {
			return null;
		}
	}

	/**
	 * Some events do not have a valid handle, e.g. hande is null or ?
	 */
	public static boolean isValidStructureHandle(InteractionEvent event) {
		String handle = event.getStructureHandle();
		return handle != null && !handle.trim().equals("") && !handle.equals(SelectionMonitor.ID_JAVA_UNKNOWN)
				&& !handle.equals(SelectionMonitor.ID_JAVA_UNKNOW_OLD) && event.isValidStructureHandle();
	}
}
