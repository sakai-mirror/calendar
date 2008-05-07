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

import java.util.ArrayList;
import java.util.List;

import org.sakaiproject.calendar.api.CalendarEdit;
import org.sakaiproject.calendar.api.CalendarEvent;
import org.sakaiproject.calendar.api.CalendarEventVector;
import org.sakaiproject.calendar.api.CalendarService;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.time.api.Time;
import org.sakaiproject.time.api.TimeRange;
import org.sakaiproject.time.api.TimeService;
import org.sakaiproject.time.impl.BasicTimeService;
import org.sakaiproject.time.impl.MyTime;

import junit.framework.TestCase;

public class CalDAVCalendarServiceTest extends TestCase {
	
	private static final long AN_HOUR = 3600000;
	private final List<Reference> NO_ATTACHMENTS = null;
	private TimeService timeService = new BasicTimeService();
	
	public void testCanInstantiateCalDAVCalendarService() {
		CalendarService calDAVCalendarService = createCalDAVCalendarService();
		assertNotNull(calDAVCalendarService);
		assertTrue(calDAVCalendarService instanceof CalDAVCalendarService);
	}
	
	public void testCanAddNewEventToExistingCalendar() throws IdUnusedException, PermissionException, InUseException {
		CalendarService calDavCalendarService = createCalDAVCalendarService();
		CalendarEdit cal = calDavCalendarService.editCalendar("test-calendar");
		Time eventStart = new MyTime(System.currentTimeMillis() + AN_HOUR);
		Time eventEnd = new MyTime(System.currentTimeMillis() + AN_HOUR + AN_HOUR);
		TimeRange eventTimeRange = timeService.newTimeRange(eventStart, eventEnd);
		CalendarEvent event = cal.addEvent(eventTimeRange, "My Big Event", "My big event is happening!", "party", "Home", NO_ATTACHMENTS);
		List<String> eventRefs = new ArrayList<String>();
		eventRefs.add(event.getReference());
		CalendarEventVector events = calDavCalendarService.getEvents(eventRefs, eventTimeRange);
		assertNotNull(events);
		assertTrue("events vector should not be empty", events.size() > 0);
	}
	
	private CalDAVCalendarService createCalDAVCalendarService() {
		SakaiStubFacade sakaiStub = new SakaiStubFacade();
		CalDAVCalendarService calDavCalendarService = new CalDAVCalendarService();
		((CalDAVCalendarService)calDavCalendarService).setEntityManager(sakaiStub);
		((CalDAVCalendarService)calDavCalendarService).setFunctionManager(sakaiStub);
		((CalDAVCalendarService)calDavCalendarService).setThreadLocalManager(sakaiStub);
		((CalDAVCalendarService)calDavCalendarService).setSecurityService(sakaiStub);
		((CalDAVCalendarService)calDavCalendarService).setSessionManager(sakaiStub);
		((CalDAVCalendarService)calDavCalendarService).setTimeService(sakaiStub);
		((CalDAVCalendarService)calDavCalendarService).setAliasService(sakaiStub);
		((CalDAVCalendarService)calDavCalendarService).setAuthzGroupService(sakaiStub);
		((CalDAVCalendarService)calDavCalendarService).setEventTrackingService(sakaiStub);
		((CalDAVCalendarService)calDavCalendarService).setUserDirectoryService(sakaiStub);
		((CalDAVCalendarService)calDavCalendarService).setToolManager(sakaiStub);
		((CalDAVCalendarService)calDavCalendarService).setContentHostingService(sakaiStub);
		((CalDAVCalendarService)calDavCalendarService).setIdManager(new org.sakaiproject.id.impl.UuidV4IdComponent());
		((CalDAVCalendarService)calDavCalendarService).setCalDAVServerBasePath("/chandler/dav/");
		((CalDAVCalendarService)calDavCalendarService).setCalDAVServerHost("localhost");
		((CalDAVCalendarService)calDavCalendarService).setCalDAVServerPort(8080);
		((CalDAVCalendarService)calDavCalendarService).init();
		return calDavCalendarService;
	}

}
