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

import java.io.File;

import org.eclipse.navclus.context.core.IContextStore;

/**
 * Notified of events where {@link IContextStore} is moved.
 * 
 * @author Mik Kersten
 */
public interface IContextStoreListener {

	/**
	 * @since 3.0
	 */
	public abstract void contextStoreMoved(File newDirectory);

}
