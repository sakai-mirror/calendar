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

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import net.fortuna.ical4j.model.Date;
import java.util.List;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.webdav.lib.methods.DeleteMethod;
import org.osaf.caldav4j.CalDAV4JException;
import org.osaf.caldav4j.CalDAVCalendarCollection;
import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.methods.CalDAV4JMethodFactory;
import org.osaf.caldav4j.methods.HttpClient;
import org.osaf.caldav4j.methods.MkCalendarMethod;
import org.osaf.caldav4j.methods.PutMethod;
import org.sakaiproject.calendar.api.Calendar;
import org.sakaiproject.calendar.api.CalendarEdit;
import org.sakaiproject.calendar.api.CalendarEvent;
import org.sakaiproject.calendar.api.CalendarEventEdit;
import org.sakaiproject.calendar.impl.BaseCalendarService;
import org.sakaiproject.util.StorageUser;

/**
 * @author Zach A. Thomas <zach@aeroplanesoftware.com>
 *
 */
public class CalDAVCalendarService extends BaseCalendarService {

	private String calDAVServerHost;
	private int calDAVServerPort;
	private String calDAVServerBasePath;
	private CalDAV4JMethodFactory methodFactory = new CalDAV4JMethodFactory();
	

	public String getCalDAVServerHost() {
		return calDAVServerHost;
	}

	public void setCalDAVServerHost(String calDAVServerHost) {
		this.calDAVServerHost = calDAVServerHost;
	}

	public int getCalDAVServerPort() {
		return calDAVServerPort;
	}

	public void setCalDAVServerPort(int calDAVServerPort) {
		this.calDAVServerPort = calDAVServerPort;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.calendar.impl.BaseCalendarService#newStorage()
	 */
	@Override
	protected Storage newStorage() {
		return new CalDAVStorage(this);
	}
	
	protected class CalDAVStorage implements Storage 
	{
		
		/** The StorageUser to callback for new Resource and Edit objects. */
		protected StorageUser m_user = null;
		
		public CalDAVStorage(StorageUser user) {
			this.m_user = user;
		}

		public void cancelCalendar(CalendarEdit edit) {
			// TODO Auto-generated method stub
			
		}

		public void cancelEvent(Calendar calendar, CalendarEventEdit edit) {
			// TODO Auto-generated method stub
			
		}

		public boolean checkCalendar(String ref) {
			String sakaiUser = getSessionManager().getCurrentSessionUserId();
			String calendarCollectionPath = sakaiUser + "/" + ref;
			CalDAVCalendarCollection calendarCollection = getCalDAVCalendarCollection(calendarCollectionPath);
			
	        return calendarCollection != null;
		}

		public boolean checkEvent(Calendar calendar, String eventId) {
			// TODO Auto-generated method stub
			return false;
		}

		public void close() {
			// TODO Auto-generated method stub
			
		}

		public void commitCalendar(CalendarEdit edit) {
			// TODO Auto-generated method stub
			
		}

		public void commitEvent(Calendar calendar, CalendarEventEdit edit) {
			// TODO Auto-generated method stub
			
		}

		public CalendarEdit editCalendar(String ref) {
			String sakaiUser = getSessionManager().getCurrentSessionUserId();
			String calDAVPassword = getCalDAVPasswordForUser(sakaiUser);
			String calendarCollectionPath = sakaiUser + "/" + ref;
			CalDAVCalendarCollection calendarCollection = getCalDAVCalendarCollection(calendarCollectionPath);
			List<net.fortuna.ical4j.model.Calendar> calendars = null;
	        try {
	            // calendar = calendarCollection.getCalendarForEventUID(createHttpClient(sakaiUser, calDAVPassword), ref);
	            // calendar = calendarCollection.getCalendarByPath(createHttpClient(sakaiUser, calDAVPassword), "");
	             calendars = calendarCollection.getEventResources(createHttpClient(sakaiUser, calDAVPassword), new Date(0L), new Date());
	        } catch (CalDAV4JException ce) {
	            ce.printStackTrace();
	        }
			return makeSakaiCalendarForCalDAVCalendars(calendars);
		}

		private CalendarEdit makeSakaiCalendarForCalDAVCalendars(
				List<net.fortuna.ical4j.model.Calendar> calendars) {
			CalendarEdit cal = new BaseCalendarEdit(calendars.toString());
			return cal;
		}

		public CalendarEventEdit editEvent(Calendar calendar, String eventId) {
			// TODO Auto-generated method stub
			return null;
		}

		public Calendar getCalendar(String ref) {
			// TODO Auto-generated method stub
			return null;
		}

		public List getCalendars() {
			// TODO Auto-generated method stub
			return null;
		}

		public CalendarEvent getEvent(Calendar calendar, String eventId) {
			// TODO Auto-generated method stub
			return null;
		}

		public List getEvents(Calendar calendar) {
			// TODO Auto-generated method stub
			return null;
		}

		public List getEvents(Calendar calendar, long l, long m) {
			// TODO Auto-generated method stub
			return null;
		}

		public void open() {
			// TODO Auto-generated method stub
			
		}

		public CalendarEdit putCalendar(String ref) {
			// TODO Auto-generated method stub
			return null;
		}

		public CalendarEventEdit putEvent(Calendar calendar, String id) {
			return new BaseCalendarEventEdit(calendar, id);
		}

		public void removeCalendar(CalendarEdit calendar) {
			// TODO Auto-generated method stub
			
		}

		public void removeEvent(Calendar calendar, CalendarEventEdit edit) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	protected HttpClient createHttpClient(String username, String password){
        HttpClient http = new HttpClient();

        Credentials credentials = new UsernamePasswordCredentials(username, password);
        http.getState().setCredentials(AuthScope.ANY, credentials);
        http.getParams().setAuthenticationPreemptive(true);
        return http;
    }
	
	protected String getCalDAVPasswordForUser(String sakaiUser) {
		//TODO create a real authn lookup for CalDAV users
		return "password";
	}

	protected CalDAVCalendarCollection getCalDAVCalendarCollection(String collectionPath) {
		String fullPath = getCalDAVServerBasePath() + collectionPath;
        CalDAVCalendarCollection calendarCollection = new CalDAVCalendarCollection(
                fullPath, createHostConfiguration(), methodFactory,
                CalDAVConstants.PROC_ID_DEFAULT);
        return calendarCollection;
    }
	
	protected void put(String iCalendar, String path, HttpClient http) {
        PutMethod put = methodFactory.createPutMethod();
        try {
        	put.setRequestEntity(new StringRequestEntity(iCalendar, "text/calendar", "utf-8"));
        	put.setPath(path);
            http.executeMethod(createHostConfiguration(), put);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
	
	protected void mkdir(String path, HttpClient http){
        MkCalendarMethod mk = new MkCalendarMethod();
        mk.setPath(path);
        try {
        http.executeMethod(createHostConfiguration(), mk);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
	
	protected void del(String path, HttpClient http){
        DeleteMethod delete = new DeleteMethod();
        delete.setPath(path);
        try {
        	http.executeMethod(createHostConfiguration(), delete);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
	
	protected HostConfiguration createHostConfiguration(){
        HostConfiguration hostConfig = new HostConfiguration();
        hostConfig.setHost(getCalDAVServerHost(), getCalDAVServerPort());
        return hostConfig;
    }
	
	protected InputStream getResourceAsStreamForName(String resourceName) {
		ClassLoader currentThreadClassLoader
        = Thread.currentThread().getContextClassLoader();

       // Add the conf dir to the classpath
       // Chain the current thread classloader
       URLClassLoader urlClassLoader;
	try {
		urlClassLoader = new URLClassLoader(new URL[]{new File("/Users/zach/dev/caldav/sakai_2-5-x/calendar/caldav4j-src/src/test/resources/icalendar").toURL()}, currentThreadClassLoader);
		// Replace the thread classloader - assumes
	       // you have permissions to do so
	       Thread.currentThread().setContextClassLoader(urlClassLoader);
	} catch (MalformedURLException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}

       


	return Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
	}

	public String getCalDAVServerBasePath() {
		return calDAVServerBasePath;
	}

	public void setCalDAVServerBasePath(String calDAVServerBasePath) {
		this.calDAVServerBasePath = calDAVServerBasePath;
	}

}
