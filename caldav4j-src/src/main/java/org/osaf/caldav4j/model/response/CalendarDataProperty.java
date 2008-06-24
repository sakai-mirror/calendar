/*
 * Copyright 2006 Open Source Applications Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.osaf.caldav4j.model.response;

import java.io.StringReader;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;

import org.apache.webdav.lib.BaseProperty;
import org.apache.webdav.lib.ResponseEntity;
import org.osaf.caldav4j.CalDAV4JException;
import org.w3c.dom.Element;

public class CalendarDataProperty extends BaseProperty {

	public static final String ELEMENT_CALENDAR_DATA = "calendar-data";

	private Calendar calendar = null;

	public CalendarDataProperty(ResponseEntity response, Element element) {
		super(response, element);
	}

	public Calendar getCalendar() throws CalDAV4JException {
		if (calendar != null) {
			return calendar;
		}

		String text = getElement().getTextContent();
		text.trim();
		CalendarBuilder builder = new CalendarBuilder();
		StringReader stringReader = new StringReader(text);
		try {
			calendar = builder.build(stringReader);
			return calendar;
		} catch (Exception e) {
			throw new CalDAV4JException("Problem building calendar", e);
		}
	}
}
