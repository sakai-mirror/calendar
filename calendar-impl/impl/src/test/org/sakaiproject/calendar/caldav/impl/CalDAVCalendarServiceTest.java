/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006, 2007, 2008 The Sakai Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 * 
 *      http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 *
 **********************************************************************************/
package org.sakaiproject.calendar.caldav.impl;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.sakaiproject.calendar.api.CalendarEvent;
import org.sakaiproject.calendar.api.CalendarService;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.time.api.TimeService;
import org.sakaiproject.time.impl.BasicTimeService;

public class CalDAVCalendarServiceTest extends CalDAVBaseTest {
	
	private static final long AN_HOUR = 3600000;
	private final List<Reference> NO_ATTACHMENTS = null;
	private TimeService timeService = new BasicTimeService();
	
	public void setUp() throws Exception {
		super.setUp();
		
		InputStream sampleCalendar = getResourceAsStreamForName("Daily_NY_5pm.ics");
		HttpClient http = createHttpClient("test", "password");
		mkdir("/chandler/dav/test/unit-test", http);
		put(sampleCalendar, "/chandler/dav/test/unit-test/Daily_NY_5pm.ics", http);
		
		sampleCalendar = getResourceAsStreamForName("All_Day_NY_JAN1.ics");
		put(sampleCalendar, "/chandler/dav/test/unit-test/All_Day_NY_JAN1.ics", http);
		
		sampleCalendar = getResourceAsStreamForName("Normal_Pacific_1pm.ics");
		put(sampleCalendar, "/chandler/dav/test/unit-test/Normal_Pacific_1pm.ics", http);
		
		sampleCalendar = getResourceAsStreamForName("singleEvent.ics");
		put(sampleCalendar, "/chandler/dav/test/unit-test/singleEvent.ics", http);
		
	}
	
	public void tearDown() throws Exception {
		super.tearDown();
		
		HttpClient http = createHttpClient("test", "password");
		del("/chandler/dav/test/unit-test", http);
	}
	
	public void testCanInstantiateCalDAVCalendarService() {
		CalendarService calDAVCalendarService = createCalDAVCalendarService();
		assertNotNull(calDAVCalendarService);
		assertTrue(calDAVCalendarService instanceof CalDAVCalendarService);
	}
	
//	public void testCanAddNewEventToExistingCalendar() throws IdUnusedException, PermissionException, InUseException {
//		CalendarService calDavCalendarService = createCalDAVCalendarService();
//		CalendarEdit cal = calDavCalendarService.editCalendar("test-calendar");
//		Time eventStart = new MyTime(System.currentTimeMillis() + AN_HOUR);
//		Time eventEnd = new MyTime(System.currentTimeMillis() + AN_HOUR + AN_HOUR);
//		TimeRange eventTimeRange = timeService.newTimeRange(eventStart, eventEnd);
//		CalendarEvent event = cal.addEvent(eventTimeRange, "My Big Event", "My big event is happening!", "party", "Home", NO_ATTACHMENTS);
//		List<String> eventRefs = new ArrayList<String>();
//		eventRefs.add(event.getReference());
//		CalendarEventVector events = calDavCalendarService.getEvents(eventRefs, eventTimeRange);
//		assertNotNull(events);
//		assertTrue("events vector should not be empty", events.size() > 0);
//	}
	
	public void testCanReadExistingEvent() throws Exception {
		CalendarService calDAVCalendarService = createCalDAVCalendarService();
		CalendarEvent event = calDAVCalendarService.getCalendar("unit-test").getEvent("0F94FE7B-8E01-4B27-835E-CD1431FD6475");
		assertNotNull("did not receive the event we requested.", event);
		assertEquals("Normal_Pacific_1pm", event.getDisplayName());
	}

}
