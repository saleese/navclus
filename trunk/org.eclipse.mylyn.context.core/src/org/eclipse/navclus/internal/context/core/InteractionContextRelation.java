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

package org.eclipse.navclus.internal.context.core;

import org.eclipse.navclus.context.core.IDegreeOfInterest;
import org.eclipse.navclus.context.core.IInteractionElement;
import org.eclipse.navclus.context.core.IInteractionRelation;

/**
 * TODO: make immutable?
 * 
 * @author Mik Kersten
 */
public class InteractionContextRelation implements IInteractionRelation {

	private final DegreeOfInterest interest;

	private final String structureKind;

	private final String relationshipHandle;

	private final IInteractionElement source;

	private final IInteractionElement target;

	public InteractionContextRelation(String kind, String edgeKind, IInteractionElement source,
			IInteractionElement target, InteractionContext context) {
		interest = new DegreeOfInterest(context, context.getScaling());
		this.structureKind = kind;
		this.relationshipHandle = edgeKind;
		this.target = target;
		this.source = source;
	}

	public IInteractionElement getTarget() {
		return target;
	}

	public IDegreeOfInterest getInterest() {
		return interest;
	}

	@Override
	public String toString() {
		return "(rel: " + relationshipHandle + ", source: " + source.getHandleIdentifier() + ", target: " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				+ target.getHandleIdentifier() + ")"; //$NON-NLS-1$
	}

	public String getLabel() {
		return toString();
	}

	public String getRelationshipHandle() {
		return relationshipHandle;
	}

	public String getContentType() {
		return structureKind;
	}

	public IInteractionElement getSource() {
		return source;
	}
}