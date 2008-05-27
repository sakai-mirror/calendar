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

import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Summary;

import org.apache.commons.httpclient.HttpClient;
import org.osaf.caldav4j.CalDAVCalendarCollection;
import org.osaf.caldav4j.util.ICalendarUtils;
import org.sakaiproject.calendar.api.Calendar;
import org.sakaiproject.calendar.api.CalendarEvent;
import org.sakaiproject.calendar.api.CalendarEventEdit;
import org.sakaiproject.calendar.api.CalendarService;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.time.api.Time;
import org.sakaiproject.time.api.TimeRange;
import org.sakaiproject.time.impl.BasicTimeService;
import org.sakaiproject.time.impl.MyTime;

public class CalDAVCalendarServiceTest extends CalDAVBaseTest {
	
	private static final long AN_HOUR = 3600000;
	private final List<Reference> NO_ATTACHMENTS = null;
	private BasicTimeService timeService = new BasicTimeService();
	
	public void setUp() throws Exception {
		super.setUp();
		
		InputStream sampleCalendar = getResourceAsStreamForName("Daily_NY_5pm.ics");
		HttpClient http = createHttpClient(CalDAVConstants.TEST_USER_NAME, CalDAVConstants.TEST_PASSWORD);
		CalDAVUtility.mkdir(CalDAVConstants.SERVER_BASE_PATH + CalDAVConstants.TEST_USER_NAME + "/" + CalDAVConstants.TEST_COLLECTION, http);
		CalDAVUtility.put(sampleCalendar, CalDAVConstants.SERVER_BASE_PATH + CalDAVConstants.TEST_USER_NAME + "/" + CalDAVConstants.TEST_COLLECTION + "/DE916949-731D-4DAE-BA93-48A38B2B2030.ics", http);
		
		sampleCalendar = getResourceAsStreamForName("All_Day_NY_JAN1.ics");
		CalDAVUtility.put(sampleCalendar, CalDAVConstants.SERVER_BASE_PATH + CalDAVConstants.TEST_USER_NAME + "/" + CalDAVConstants.TEST_COLLECTION + "/C68DADAD-37CE-44F7-8A37-52E1D02E29CA.ics", http);
		
		sampleCalendar = getResourceAsStreamForName("Normal_Pacific_1pm.ics");
		CalDAVUtility.put(sampleCalendar, CalDAVConstants.SERVER_BASE_PATH + CalDAVConstants.TEST_USER_NAME + "/" + CalDAVConstants.TEST_COLLECTION + "/0F94FE7B-8E01-4B27-835E-CD1431FD6475.ics", http);
		
		sampleCalendar = getResourceAsStreamForName("singleEvent.ics");
		CalDAVUtility.put(sampleCalendar, CalDAVConstants.SERVER_BASE_PATH + CalDAVConstants.TEST_USER_NAME + "/" + CalDAVConstants.TEST_COLLECTION + "/66be2585-327b-4cc1-93a7-d0e6de648183.ics", http);
		
	}
	
	public void tearDown() throws Exception {
		super.tearDown();
		
		HttpClient http = createHttpClient(CalDAVConstants.TEST_USER_NAME, CalDAVConstants.TEST_PASSWORD);
		CalDAVUtility.del(CalDAVConstants.SERVER_BASE_PATH + CalDAVConstants.TEST_USER_NAME + "/" + CalDAVConstants.TEST_COLLECTION, http);
		CalDAVUtility.del(CalDAVConstants.SERVER_BASE_PATH + CalDAVConstants.TEST_USER_NAME + "/foo", http);
	}
	
	public void testCanInstantiateCalDAVCalendarService() {
		assertNotNull(calDAVCalendarService);
		assertTrue(calDAVCalendarService instanceof CalDAVCalendarService);
	}
	
	public void testCanAddNewEventToExistingCalendar() throws Exception {
		Calendar cal = calDAVCalendarService.getCalendar(CalDAVConstants.TEST_COLLECTION);
		Time eventStart = new MyTime(timeService, System.currentTimeMillis() + AN_HOUR);
		Time eventEnd = new MyTime(timeService, System.currentTimeMillis() + AN_HOUR + AN_HOUR);
		TimeRange eventTimeRange = timeService.newTimeRange(eventStart, eventEnd);
		CalendarEvent event = cal.addEvent(eventTimeRange, "My Big Event", "My big event is happening!", "party", "Home", NO_ATTACHMENTS);
		CalendarEventEdit eventEdit = cal.getEditEvent(event.getId(), CalendarService.EVENT_MODIFY_CALENDAR);
		assertNotNull("unable to retrieve the event we just added", eventEdit);
	}
	
	public void testCanReadExistingEvent() throws Exception {
		CalendarEvent event = calDAVCalendarService.getCalendar(CalDAVConstants.TEST_COLLECTION).getEvent("0F94FE7B-8E01-4B27-835E-CD1431FD6475");
		assertNotNull("did not receive the event we requested.", event);
		assertEquals("Test Event", event.getDisplayName());
	}
	
	public void testUpdateEventDisplayName() throws Exception {
		Calendar cal = calDAVCalendarService.getCalendar(CalDAVConstants.TEST_COLLECTION);
		CalendarEventEdit eventEdit = cal.getEditEvent("0F94FE7B-8E01-4B27-835E-CD1431FD6475", CalendarService.EVENT_MODIFY_CALENDAR);
		if ("Hello".equals(eventEdit.getDisplayName())) fail("The event description should not be 'Hello' until after I update it.");
        eventEdit.setDisplayName("Hello");
        cal.commitEvent(eventEdit);
        
        CalendarEvent eventRead = cal.getEvent("0F94FE7B-8E01-4B27-835E-CD1431FD6475");
        assertEquals("Hello", eventRead.getDisplayName());
    }
	
	public void testCanCreateNonExistantCollection() throws Exception {
		// precondition for this test: collection "foo" should not exist.
		Calendar cal = calDAVCalendarService.getCalendar("foo");
		Time eventStart = new MyTime(timeService, System.currentTimeMillis() + AN_HOUR);
		Time eventEnd = new MyTime(timeService, System.currentTimeMillis() + AN_HOUR + AN_HOUR);
		TimeRange eventTimeRange = timeService.newTimeRange(eventStart, eventEnd);
		CalendarEvent event = cal.addEvent(eventTimeRange, "Out of Thin Air", "This calendar collection never existed before.", "Activity", "Home", NO_ATTACHMENTS);
		assertNotNull(event);
	}

}
