/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Ken Sueda - XML serialization
 *******************************************************************************/

package org.eclipse.navclus.monitor.usage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.navclus.commons.core.StatusHandler;
import org.eclipse.navclus.monitor.core.AbstractMonitorLog;
import org.eclipse.navclus.monitor.core.IInteractionEventListener;
import org.eclipse.navclus.monitor.core.InteractionEvent;
import org.eclipse.navclus.monitor.core.InteractionEvent.Kind;

/**
 * @author Mik Kersten TODO: use buffered output stream for better performance?
 */
public class InteractionEventLogger extends AbstractMonitorLog implements IInteractionEventListener {

	private int eventAccumulartor = 0;

	private final List<InteractionEvent> queue = new CopyOnWriteArrayList<InteractionEvent>();

//	private final InteractionEventObfuscator handleObfuscator = new InteractionEventObfuscator();

	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S z", Locale.ENGLISH);

	public InteractionEventLogger(File outputFile) {
		this.outputFile = outputFile;
	}

	public synchronized void interactionObserved(InteractionEvent event) {
		if (event.getKind().name().equals("COMMAND")) { // salee
			return;
		}
		if (event.getKind().name().equals("PREFERENCE")) { // salee
			return;
		}

//		 System.err.println("> " + event);
		if (UiUsageMonitorPlugin.getDefault() == null) {
			StatusHandler.log(new Status(IStatus.WARNING, UiUsageMonitorPlugin.ID_PLUGIN,
					"Attempted to log event before usage monitor start"));
		}
		try {
			if (started) {
				String xml = getXmlForEvent(event);
				RandomAccessFile f = new RandomAccessFile(outputFile, "rw");
				f.seek(f.length() - 10);
				xml = xml.replaceAll("\n", "\r\n");
				f.write(xml.getBytes());
				f.write("</user1>\r\n".getBytes());
			} else if (event != null) {
				queue.add(event);
			}
			eventAccumulartor++;
		} catch (Throwable t) {
			StatusHandler.log(new Status(IStatus.WARNING, UiUsageMonitorPlugin.ID_PLUGIN,
					"Could not log interaction event", t));
		}
	}

	@Override
	public void startMonitoring() {
		super.startMonitoring();
		for (InteractionEvent queuedEvent : queue) {
			interactionObserved(queuedEvent);
		}
		queue.clear();
	}

	@Override
	public void stopMonitoring() {
		super.stopMonitoring();
	}

	private String getXmlForEvent(InteractionEvent event) {
		return writeLegacyEvent(event);
	}

	/**
	 * @return true if successfully cleared
	 */
	public synchronized void clearInteractionHistory() throws IOException {
		this.clearInteractionHistory(true);
	}

	public synchronized void clearInteractionHistory(boolean startMonitoring) throws IOException {
		stopMonitoring();
		outputStream = new FileOutputStream(outputFile, false);
		outputStream.flush();
		outputStream.close();
		outputFile.delete();
		outputFile.createNewFile();
		if (startMonitoring) {
			startMonitoring();
		}
	}

	private static final String OPEN = "<";

	private static final String CLOSE = ">";

	private static final String SLASH = "/";

	private static final String ENDL = "\n";

	private static final String TAB = "\t";

	@Deprecated
	public String writeLegacyEvent(InteractionEvent e) {
		try {
			StringBuffer res = new StringBuffer();
			String tag = "interactionEvent";
			res.append(OPEN);
			res.append(tag);
			res.append(CLOSE);
			res.append(ENDL);

			openElement(res, "kind");
			formatContent(res, e.getKind());
			closeElement(res, "kind");

			openElement(res, "date");
			formatContent(res, e.getDate());
			closeElement(res, "date");

			openElement(res, "endDate");
			formatContent(res, e.getEndDate());
			closeElement(res, "endDate");

			openElement(res, "originId");
			formatContent(res, e.getOriginId());
			closeElement(res, "originId");

			openElement(res, "structureKind");
			formatContent(res, e.getStructureKind());
			closeElement(res, "structureKind");

			openElement(res, "structureHandle");
			formatContent(res, e.getStructureHandle());
			closeElement(res, "structureHandle");

			openElement(res, "navigation");
			formatContent(res, e.getNavigation());
			closeElement(res, "navigation");

			openElement(res, "delta");
			formatContent(res, e.getDelta());
			closeElement(res, "delta");

			openElement(res, "interestContribution");
			formatContent(res, e.getInterestContribution());
			closeElement(res, "interestContribution");

			res.append(OPEN);
			res.append(SLASH);
			res.append(tag);
			res.append(CLOSE);
			res.append(ENDL);
			return res.toString();
		} catch (Throwable t) {
			StatusHandler.log(new Status(IStatus.ERROR, UiUsageMonitorPlugin.ID_PLUGIN, "Could not write event", t));
			return "";
		}
	}

	private void formatContent(StringBuffer buffer, float interestContribution) {
		buffer.append(interestContribution);
	}

	@SuppressWarnings("deprecation")
	private void formatContent(StringBuffer buffer, String content) {
		if (content != null && content.length() > 0) {
			String xmlContent;
			xmlContent = org.eclipse.navclus.internal.commons.core.XmlStringConverter.convertToXmlString(content);
			xmlContent = xmlContent.replace("\n", "\n\t\t");
			buffer.append(xmlContent);
		}
	}

	private void formatContent(StringBuffer buffer, Kind kind) {
		buffer.append(kind.toString());
	}

	private void formatContent(StringBuffer buffer, Date date) {
		buffer.append(dateFormat.format(date));
	}

	private void openElement(StringBuffer buffer, String tag) {
		buffer.append(TAB);
		buffer.append(OPEN);
		buffer.append(tag);
		buffer.append(CLOSE);
	}

	private void closeElement(StringBuffer buffer, String tag) {
		buffer.append(OPEN);
		buffer.append(SLASH);
		buffer.append(tag);
		buffer.append(CLOSE);
		buffer.append(ENDL);
	}

}
