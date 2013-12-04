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

import org.eclipse.navclus.context.core.IInteractionElement;

/**
 * @author Mik Kersten
 */
public interface IRelationsListener {

	public void relationsChanged(IInteractionElement element);

}
