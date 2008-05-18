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

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.DateProperty;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Summary;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.webdav.lib.methods.DeleteMethod;
import org.osaf.caldav4j.CalDAV4JException;
import org.osaf.caldav4j.CalDAVCalendarCollection;
import org.osaf.caldav4j.methods.CalDAV4JMethodFactory;
import org.osaf.caldav4j.methods.HttpClient;
import org.osaf.caldav4j.methods.MkCalendarMethod;
import org.osaf.caldav4j.methods.PutMethod;
import org.osaf.caldav4j.util.ICalendarUtils;
import org.sakaiproject.calendar.api.Calendar;
import org.sakaiproject.calendar.api.CalendarEdit;
import org.sakaiproject.calendar.api.CalendarEvent;
import org.sakaiproject.calendar.api.CalendarEventEdit;
import org.sakaiproject.calendar.api.RecurrenceRule;
import org.sakaiproject.calendar.impl.BaseCalendarService;
import org.sakaiproject.calendar.impl.GenericCalendarImporter;
import org.sakaiproject.calendar.impl.readers.IcalendarReader;
import org.sakaiproject.calendar.impl.readers.Reader;
import org.sakaiproject.calendar.impl.readers.Reader.ReaderImportCell;
import org.sakaiproject.calendar.impl.readers.Reader.ReaderImportRowHandler;
import org.sakaiproject.exception.ImportException;
import org.sakaiproject.time.api.Time;
import org.sakaiproject.time.api.TimeBreakdown;
import org.sakaiproject.time.api.TimeRange;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.util.ResourceLoader;
import org.sakaiproject.util.StorageUser;

/**
 * @author Zach A. Thomas <zach@aeroplanesoftware.com>
 *
 */
public class CalDAVCalendarService extends BaseCalendarService {
	
	private static Log M_log = LogFactory.getLog(CalDAVCalendarService.class);

	private String calDAVServerHost;
	private int calDAVServerPort;
	private String calDAVServerBasePath;
	private CalDAV4JMethodFactory methodFactory = new CalDAV4JMethodFactory();
	public static final long ONE_YEAR = 31536000000L;
	ResourceLoader rb = new ResourceLoader("calendarimpl", getSessionManager());
	static final DateFormat time24HourFormatter = new SimpleDateFormat("HH:mm");

	static final DateFormat time24HourFormatterWithSeconds = new SimpleDateFormat("HH:mm:ss");
	

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
			String userEid;
			HttpClient http;
			try {
				http = createHttpClient();
				userEid = getUserDirectoryService().getUserEid(getSessionManager().getCurrentSessionUserId());
			} catch (UserNotDefinedException e1) {
				M_log.warn("CalDAVCalendarService::commitEvent() couldn't get an EID for userId '" + getSessionManager().getCurrentSessionUserId() + "'");
				return;
			}
			String calendarCollectionPath = userEid + "/" + calendar.getId();
			CalDAVCalendarCollection calendarCollection = getCalDAVCalendarCollection(calendarCollectionPath);

	        try {
				net.fortuna.ical4j.model.Calendar iCalendar = calendarCollection.getCalendarForEventUID(
				        http, edit.getId());

				VEvent ve = ICalendarUtils.getFirstEvent(iCalendar);
				// TODO update all the event properties from the CalendarEventEdit
				ICalendarUtils.addOrReplaceProperty(ve, new Summary(edit.getDisplayName()));
				ICalendarUtils.addOrReplaceProperty(ve, new Description(edit.getDescription()));
				calendarCollection.updateMasterEvent(http, ve, null);
			} catch (CalDAV4JException e) {
				M_log.error("CalDAVCalendarService::commitEvent() '" + e.getMessage() + "'");
				return;
			}
			
		}

		public CalendarEdit editCalendar(String ref) {
			String sakaiUser = getSessionManager().getCurrentSessionUserId();
			String calDAVPassword = getCalDAVPasswordForUser(sakaiUser);
			String calendarCollectionPath = sakaiUser + "/" + ref;
			CalDAVCalendarCollection calendarCollection = getCalDAVCalendarCollection(calendarCollectionPath);
			List<net.fortuna.ical4j.model.Calendar> calendars = null;
	        try {
	             calendars = calendarCollection.getEventResources(createHttpClient(), new Date(0L), new Date());
	        } catch (CalDAV4JException ce) {
	        	M_log.error("CalDAVCalendarService::editCalendar() '" + ce.getMessage() + "'");
	        	return null;
	        } catch (UserNotDefinedException e) {
	        	M_log.warn("CalDAVCalendarService::editCalendar() couldn't get an EID for userId '" + getSessionManager().getCurrentSessionUserId() + "'");
	        	return null;
			}
			return makeSakaiCalendarForCalDAVCalendars(calendars, ref);
		}

		private CalendarEdit makeSakaiCalendarForCalDAVCalendars(
				List<net.fortuna.ical4j.model.Calendar> calendars, String ref) {
			CalendarEdit cal = new BaseCalendarEdit(ref);
			return cal;
		}

		protected BaseCalendarEventEdit makeCalendarEventForCalDAVvEvent(Calendar cal, VEvent component) {
			final Calendar calendar = cal;
			final String eventUid = component.getUid().getValue();
			
			final List<BaseCalendarEventEdit> eventList = new ArrayList<BaseCalendarEventEdit>();
			String durationformat ="";
			int lineNumber = 1;
			ColumnHeader columnDescriptionArray[] = null;
			String descriptionColumns[] = {"Summary","Description","Start Date","Start Time","Duration","Location"};
			// column map stuff
			trimLeadingTrailingQuotes(descriptionColumns);
			columnDescriptionArray = buildColumnDescriptionArray(descriptionColumns);
			ReaderImportRowHandler handler = new Reader.ReaderImportRowHandler()
			{
				// This is the callback that is called for each row.
				public void handleRow(Iterator columnIterator) throws ImportException
				{
					final Map<String,Object> eventProperties = new HashMap<String,Object>();

					// Add all the properties to the map
					while (columnIterator.hasNext())
					{
						Reader.ReaderImportCell column = (Reader.ReaderImportCell) columnIterator.next();

						String value = column.getCellValue().trim();
						Object mapCellValue = null;

						// First handle any empy columns.
						if (value.length() == 0)
						{
							mapCellValue = null;
						}
						else
						{
							if (GenericCalendarImporter.FREQUENCY_PROPERTY_NAME.equals(column.getPropertyName()))
							{
								mapCellValue = column.getCellValue();
							}
							else if (GenericCalendarImporter.END_TIME_PROPERTY_NAME.equals(column.getPropertyName())
									|| GenericCalendarImporter.START_TIME_PROPERTY_NAME.equals(column.getPropertyName()))
							{
								boolean success = false;

								try
								{
									mapCellValue = GenericCalendarImporter.TIME_FORMATTER.parse(value);
									success = true;
								}

								catch (ParseException e)
								{
									// Try another format
								}

								if (!success)
								{
									try
									{
										mapCellValue = GenericCalendarImporter.TIME_FORMATTER_WITH_SECONDS.parse(value);
										success = true;
									}

									catch (ParseException e)
									{
										// Try another format
									}
								}

								if (!success)
								{
									try
									{
										mapCellValue = time24HourFormatter.parse(value);
										success = true;
									}

									catch (ParseException e)
									{
										// Try another format
									}
								}

								if (!success)
								{
									try
									{
										mapCellValue = time24HourFormatterWithSeconds.parse(value);
										success = true;
									}

									catch (ParseException e)
									{
										// Give up, we've run out of possible formats.
			                   String msg = (String)rb.getFormattedMessage(
			                                           "err_time", 
			                                           new Object[]{new Integer(column.getLineNumber()),
			                                                        column.getColumnHeader()});
			                   throw new ImportException( msg );
									}
								}
							}
							else if (GenericCalendarImporter.DURATION_PROPERTY_NAME.equals(column.getPropertyName()))
							{
			             String timeFormatErrorString = (String)rb.getFormattedMessage(
			                                           "err_time", 
			                                           new Object[]{new Integer(column.getLineNumber()),
			                                                        column.getColumnHeader()});

								String parts[] = value.split(":");

								if (parts.length == 1)
								{
									// Convert to minutes to get into one property field.
									try
									{
										mapCellValue = new Integer(Integer.parseInt(parts[0]));
									}
									catch (NumberFormatException ex)
									{
										throw new ImportException(timeFormatErrorString);
									}
								}
								else if (parts.length == 2)
								{
									// Convert to hours:minutes to get into one property field.
									try
									{
										mapCellValue = new Integer(Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]));
									}
									catch (NumberFormatException ex)
									{
										throw new ImportException(timeFormatErrorString);
									}
								}
								else
								{
									// Not a legal format of mm or hh:mm
									throw new ImportException(timeFormatErrorString);
								}
							}
							else if (GenericCalendarImporter.DATE_PROPERTY_NAME.equals(column.getPropertyName())
									|| GenericCalendarImporter.ENDS_PROPERTY_NAME.equals(column.getPropertyName()))
							{
			             DateFormat df = DateFormat.getDateInstance( DateFormat.SHORT, rb.getLocale() );
			             df.setLenient(false);
								try
								{
									mapCellValue = df.parse(value);
								}
								catch (ParseException e)
								{
			                String msg = (String)rb.getFormattedMessage("err_date", 
			                                                            new Object[]{new Integer(column.getLineNumber()),
			                                                                         column.getColumnHeader()});
			                throw new ImportException( msg );
								}
							}
							else if (GenericCalendarImporter.INTERVAL_PROPERTY_NAME.equals(column.getPropertyName())
									|| GenericCalendarImporter.REPEAT_PROPERTY_NAME.equals(column.getPropertyName()))
							{
								try
								{
									mapCellValue = new Integer(column.getCellValue());
								}
								catch (NumberFormatException ex)
								{
			                String msg = (String)rb.getFormattedMessage("err_interval", 
			                                                            new Object[]{new Integer(column.getLineNumber()),
			                                                                         column.getColumnHeader()});
			                throw new ImportException( msg );
								}
							}
							else
							{
								// Just a string...
								mapCellValue = column.getCellValue();
							}
						}

						// Store in the map for later reference.
						eventProperties.put(column.getPropertyName(), mapCellValue);
					}
					
					java.util.Date startTime = (java.util.Date) eventProperties.get(GenericCalendarImporter.START_TIME_PROPERTY_NAME);
					TimeBreakdown startTimeBreakdown = null;
					
					if ( startTime != null )
					{
						// if the source time zone were known, this would be
						// a good place to set it: startCal.setTimeZone()
						GregorianCalendar startCal = new GregorianCalendar();
						startCal.setTimeInMillis( startTime.getTime() );
						startTimeBreakdown = 
								  getTimeService().newTimeBreakdown( 0, 0, 0, 
									  startCal.get(java.util.Calendar.HOUR_OF_DAY),
									  startCal.get(java.util.Calendar.MINUTE),
									  startCal.get(java.util.Calendar.SECOND),
										0 );
					}
					else
					{
						Integer line = new Integer(1);
						String msg = (String)rb.getFormattedMessage("err_no_stime_on", 
																				  new Object[]{line});
						throw new ImportException( msg );
					}
					
					Integer durationInMinutes = (Integer)eventProperties.get(GenericCalendarImporter.DURATION_PROPERTY_NAME);

					if ( durationInMinutes == null )
					{
						Integer line = new Integer(1);
						String msg = (String)rb.getFormattedMessage("err_no_dtime_on", 
																				  new Object[]{line});
						throw new ImportException( msg );
					}
					
					java.util.Date endTime =
						new java.util.Date(
							startTime.getTime() + (durationInMinutes.longValue() * 60 * 1000) );
							
					TimeBreakdown endTimeBreakdown = null;

					if ( endTime != null )
					{
						// if the source time zone were known, this would be
						// a good place to set it: endCal.setTimeZone()
						GregorianCalendar endCal = new GregorianCalendar();
						endCal.setTimeInMillis( endTime.getTime() );
						endTimeBreakdown = 
								  getTimeService().newTimeBreakdown( 0, 0, 0, 
									  endCal.get(java.util.Calendar.HOUR_OF_DAY),
									  endCal.get(java.util.Calendar.MINUTE),
									  endCal.get(java.util.Calendar.SECOND),
									  0 );
					}

					java.util.Date startDate = (java.util.Date) eventProperties.get(GenericCalendarImporter.DATE_PROPERTY_NAME);
					
					// if the source time zone were known, this would be
					// a good place to set it: startCal.setTimeZone()
					GregorianCalendar startCal = new GregorianCalendar();
					if ( startDate != null )
						startCal.setTimeInMillis( startDate.getTime() );
						
					startTimeBreakdown.setYear( startCal.get(java.util.Calendar.YEAR) );
					startTimeBreakdown.setMonth( startCal.get(java.util.Calendar.MONTH)+1 );
					startTimeBreakdown.setDay( startCal.get(java.util.Calendar.DAY_OF_MONTH) );
						
					endTimeBreakdown.setYear( startCal.get(java.util.Calendar.YEAR) );
					endTimeBreakdown.setMonth( startCal.get(java.util.Calendar.MONTH)+1 );
					endTimeBreakdown.setDay( startCal.get(java.util.Calendar.DAY_OF_MONTH) );
					
					eventProperties.put(
						GenericCalendarImporter.ACTUAL_TIMERANGE,
						getTimeService().newTimeRange(
								  getTimeService().newTimeLocal(startTimeBreakdown),
								  getTimeService().newTimeLocal(endTimeBreakdown),
							true,
							false));
					RecurrenceRule recurrenceRule = null;
					//PrototypeEvent prototypeEvent = new GenericCalendarImporter().new PrototypeEvent();
					BaseCalendarEventEdit baseCalendarEvent = (BaseCalendarEventEdit)newResourceEdit(calendar, eventUid, null);

					baseCalendarEvent.setDescription((String) eventProperties.get(GenericCalendarImporter.DESCRIPTION_PROPERTY_NAME));
					baseCalendarEvent.setDisplayName((String) eventProperties.get(GenericCalendarImporter.TITLE_PROPERTY_NAME));
					baseCalendarEvent.setLocation((String) eventProperties.get(GenericCalendarImporter.LOCATION_PROPERTY_NAME));
					baseCalendarEvent.setType((String) eventProperties.get(GenericCalendarImporter.ITEM_TYPE_PROPERTY_NAME));

					if (baseCalendarEvent.getType() == null || baseCalendarEvent.getType().length() == 0)
					{
						baseCalendarEvent.setType("Activity");
					}

					// The time range has been calculated in the reader, based on
					// whatever time fields are available in the particular import format.
					// This range has been placed in the ACTUAL_TIMERANGE property.

					TimeRange timeRange = (TimeRange) eventProperties.get(GenericCalendarImporter.ACTUAL_TIMERANGE);

					if (timeRange == null)
					{
		            String msg = (String)rb.getFormattedMessage("err_notime", 
		                                                        new Object[]{new Integer(1)});
		            throw new ImportException( msg );
					}

					// The start/end times were calculated during the import process.
					baseCalendarEvent.setRange(timeRange);

					// Do custom fields, if any.
//					if (customFieldPropertyNames != null)
//					{
//						for (int i = 0; i < customFieldPropertyNames.length; i++)
//						{
//							prototypeEvent.setField(customFieldPropertyNames[i], (String) eventProperties.get(customFieldPropertyNames[i]));
//						}
//					}

					// See if this is a recurring event
					String frequencyString = (String) eventProperties.get(GenericCalendarImporter.FREQUENCY_PROPERTY_NAME);

					if (frequencyString != null)
					{
						Integer interval = (Integer) eventProperties.get(GenericCalendarImporter.INTERVAL_PROPERTY_NAME);
						Integer count = (Integer) eventProperties.get(GenericCalendarImporter.REPEAT_PROPERTY_NAME);
						Date until = (Date) eventProperties.get(GenericCalendarImporter.ENDS_PROPERTY_NAME);

						if (count != null && until != null)
						{
		               String msg = (String)rb.getFormattedMessage("err_datebad", 
		                                                           new Object[]{new Integer(1)});
		               throw new ImportException( msg );
						}

						if (interval == null && count == null && until == null)
						{
							recurrenceRule = newRecurrence(frequencyString);
						}
						else if (until == null && interval != null && count != null)
						{
							recurrenceRule = newRecurrence(frequencyString, interval.intValue(), count.intValue());
						}
						else if (until == null && interval != null && count == null)
						{
							recurrenceRule = newRecurrence(frequencyString, interval.intValue());
						}
						else if (until != null && interval != null && count == null)
						{
							Time untilTime = getTimeService().newTime(until.getTime());

							recurrenceRule = newRecurrence(frequencyString, interval.intValue(), untilTime);
						}

						// See if we were able to successfully create a recurrence rule.
						if (recurrenceRule == null)
						{
		               String msg = (String)rb.getFormattedMessage("err_freqbad", 
		                                                           new Object[]{new Integer(1)});
		               throw new ImportException( msg );
						}

						baseCalendarEvent.setRecurrenceRule(recurrenceRule);
					}
					//baseCalendarEvent.setLineNumber(1);
					eventList.add(baseCalendarEvent);
				}
			};
			// Find event duration
			DateProperty dtstartdate;
			DateProperty dtenddate;
			if(component instanceof VEvent){
				VEvent vEvent = (VEvent) component;
				dtstartdate = vEvent.getStartDate();
				dtenddate = vEvent.getEndDate(true);
			}else{
				dtstartdate = (DateProperty) component.getProperty("DTSTART");
				dtenddate =  (DateProperty) component.getProperty("DTEND");
			}
		
		if ( component.getProperty("SUMMARY") == null )
		{
			throw new RuntimeException();
		}
		String summary = component.getProperty("SUMMARY").getValue();
		
		if ( component.getProperty("RRULE") != null )
		{
			throw new RuntimeException("IcalendarReader: Re-occuring events not supported: " + summary );
		}
		else if (dtstartdate == null || dtenddate == null )
		{
			throw new RuntimeException("IcalendarReader: DTSTART/DTEND required: " + summary );
		}
		
			int durationsecs = (int) ((dtenddate.getDate().getTime() - dtstartdate.getDate().getTime()) / 1000);
			int durationminutes = (durationsecs/60) % 60;
			int durationhours = (durationsecs/(60*60)) % 24;
		
			// Put duration in proper format (hh:mm or mm) if less than 1 hour
			if (durationminutes < 10)
			{
				durationformat = "0"+durationminutes;
			}
			else
			{
				durationformat = ""+durationminutes;
			}

			if (durationhours != 0)
			{
				durationformat = durationhours+":"+durationformat;
			}
			
			String description = "";
			if ( component.getProperty("DESCRIPTION") != null )
				description = component.getProperty("DESCRIPTION").getValue();
		   
		String location = "";
			if (component.getProperty("LOCATION") != null)
		   location = component.getProperty("LOCATION").getValue();
		   
			String columns[]	= 
					{summary,
					 description,
					 DateFormat.getDateInstance(DateFormat.SHORT, rb.getLocale()).format(dtstartdate.getDate()),
					 DateFormat.getTimeInstance(DateFormat.SHORT, rb.getLocale()).format(dtstartdate.getDate()),
					 durationformat,
					 location};
		
			try {
				handler.handleRow(
					processLine(
						columnDescriptionArray,
						lineNumber,
						columns));
			} catch (ImportException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
			lineNumber++;
			return eventList.get(0);
		}
		
		/**
		 * Split a line into a list of CSVReaderImportCell objects.
		 * @param columnDescriptionArray
		 * @param lineNumber
		 * @param columns
		 */
		protected Iterator<ReaderImportCell> processLine(
			ColumnHeader[] columnDescriptionArray,
			int lineNumber,
			String[] columns)
		{
			List<ReaderImportCell> list = new ArrayList<ReaderImportCell>();

			for (int i = 0; i < columns.length; i++)
			{
				if ( i >= columnDescriptionArray.length )
				{
					continue;
				}
				else
				{
					list.add(
						new ReaderImportCell(
							lineNumber,
							i,
							columns[i],
							columnDescriptionArray[i].getColumnProperty(),
							columnDescriptionArray[i].getColumnHeader()));
				}
			}

			return list.iterator();
		}

		public CalendarEventEdit editEvent(Calendar calendar, String eventId) {
			HttpClient http;
			String userEid;
			try {
				userEid = getUserDirectoryService().getUserEid(getSessionManager().getCurrentSessionUserId());
				http = createHttpClient();
			} catch (UserNotDefinedException e1) {
				return null;
			}
			String calendarCollectionPath = userEid + "/" + calendar.getId();
			CalDAVCalendarCollection calendarCollection = getCalDAVCalendarCollection(calendarCollectionPath);
			net.fortuna.ical4j.model.Calendar iCalendar = null;
			try {
				iCalendar = calendarCollection.getCalendarForEventUID(http, eventId);
			} catch (CalDAV4JException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			return makeCalendarEventForCalDAVvEvent(calendar, ICalendarUtils.getFirstEvent(iCalendar));
		}

		public Calendar getCalendar(String ref) {
			return new BaseCalendarEdit(ref);
		}

		public List getCalendars() {
			try {
				HttpClient http = createHttpClient();
			} catch (UserNotDefinedException e) {
				return new ArrayList();
			}
			String calendarCollectionPath = "";
			CalDAVCalendarCollection calendarCollection = getCalDAVCalendarCollection(calendarCollectionPath);
			// TODO how do we get all the calendars for the whole system?
			return new ArrayList();
		}

		public CalendarEvent getEvent(Calendar calendar, String eventId) {
			return editEvent(calendar, eventId);			
		}

		public List getEvents(Calendar calendar) {
			// ask for events in the time range from the "beginning of time" to a year from now
			return getEvents(calendar, 0L, new java.util.Date().getTime() + ONE_YEAR);
		}

		public List getEvents(Calendar calendar, long l, long m) {
			List<CalendarEvent> events = new ArrayList<CalendarEvent>();
			String userEid;
			HttpClient http;
			try {
				http = createHttpClient();
				userEid = getUserDirectoryService().getUserEid(getSessionManager().getCurrentSessionUserId());
			} catch (UserNotDefinedException e1) {
				return events;
			}
			String calendarCollectionPath = userEid + "/" + calendar.getId();
			CalDAVCalendarCollection calendarCollection = getCalDAVCalendarCollection(calendarCollectionPath);
			DateTime startDate = new DateTime(l);
			startDate.setUtc(true);
			DateTime endDate = new DateTime(m);
			endDate.setUtc(true);
			List<net.fortuna.ical4j.model.Calendar> iCalendars = null;
			try {
				iCalendars = calendarCollection.getEventResources(http, startDate, endDate);
			} catch (CalDAV4JException e) {
				// something went wrong, so we'll return an empty list
				return events;
			}
			for (net.fortuna.ical4j.model.Calendar iCalendar : iCalendars) {
				List<Component> calComponents = iCalendar.getComponents(Component.VEVENT);
				for (Component component : calComponents) {
					events.add(makeCalendarEventForCalDAVvEvent(calendar, (VEvent)component));
				}
			}
			return events;
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
	
	protected HttpClient createHttpClient() throws UserNotDefinedException{
		String username = getUserDirectoryService().getUserEid(getSessionManager().getCurrentSessionUserId());
		String calDAVPassword = getCalDAVPasswordForUser(username);
        HttpClient http = new HttpClient();

        Credentials credentials = new UsernamePasswordCredentials(username, calDAVPassword);
        http.getState().setCredentials(null, null, credentials);
        http.getState().setAuthenticationPreemptive(true);
        return http;
    }
	
	protected String getCalDAVPasswordForUser(String sakaiUser) {
		//TODO create a real authn lookup for CalDAV users
		return CalDAVConstants.TEST_PASSWORD;
	}

	protected CalDAVCalendarCollection getCalDAVCalendarCollection(String collectionPath) {
		String fullPath = getCalDAVServerBasePath() + collectionPath;
        CalDAVCalendarCollection calendarCollection = new CalDAVCalendarCollection(
                fullPath, createHostConfiguration(), methodFactory,
                org.osaf.caldav4j.CalDAVConstants.PROC_ID_DEFAULT);
        return calendarCollection;
    }
	
	protected void put(String iCalendar, String path, HttpClient http) {
        PutMethod put = methodFactory.createPutMethod();
        try {
        	put.setRequestBody(iCalendar);
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
	
	/**
	 * Remove leading/trailing quotes
	 * @param columnsReadFromFile
	 */
	protected void trimLeadingTrailingQuotes(String[] columnsReadFromFile)
	{
		for (int i = 0; i < columnsReadFromFile.length; i++)
		{
			String regex2 = "(?:\")*([^\"]+)(?:\")*";
			columnsReadFromFile[i] =
				columnsReadFromFile[i].trim().replaceAll(regex2, "$1");
		}
	}
	
	/**
	 * Create meta-information from the first line of the "file" (actually stream)
	 * that contains the names of the columns.
	 * @param columns
	 */
	protected ColumnHeader[] buildColumnDescriptionArray(String[] columns)
	
	{
		ColumnHeader[] columnDescriptionArray;
		columnDescriptionArray = new ColumnHeader[columns.length];

		for (int i = 0; i < columns.length; i++)
		{
			columnDescriptionArray[i] =
				new ColumnHeader(
					columns[i],
					(String) columnHeaderMap().get(columns[i]));
		}
		return columnDescriptionArray;
	}
	
	private Map<String,String> columnHeaderMap()
	{
		Map<String,String> columnHeaderMap = new HashMap<String,String>();

		columnHeaderMap.put(IcalendarReader.TITLE_HEADER, GenericCalendarImporter.TITLE_PROPERTY_NAME);
		columnHeaderMap.put(IcalendarReader.DESCRIPTION_HEADER, GenericCalendarImporter.DESCRIPTION_PROPERTY_NAME);
		columnHeaderMap.put(IcalendarReader.DATE_HEADER, GenericCalendarImporter.DATE_PROPERTY_NAME);
		columnHeaderMap.put(IcalendarReader.START_TIME_HEADER, GenericCalendarImporter.START_TIME_PROPERTY_NAME);
		columnHeaderMap.put(IcalendarReader.DURATION_HEADER, GenericCalendarImporter.DURATION_PROPERTY_NAME);
		//columnHeaderMap.put(ITEM_HEADER, GenericCalendarImporter.ITEM_TYPE_PROPERTY_NAME);
		columnHeaderMap.put(IcalendarReader.LOCATION_HEADER, GenericCalendarImporter.LOCATION_PROPERTY_NAME);
				
		return columnHeaderMap;
	}
	
	/**
	 * Contains header information such as the text label used for the
	 * header and the calendar event property with which it is associated.
	 **/
	public class ColumnHeader
	{
		private String columnProperty;
		private String columnHeader;

		/**
		 * Default constructor 
		 */
		public ColumnHeader()
		{
			super();
		}

		/**
		 * Construct a ColumnHeader with a specified text label used in the import
		 * file and the Calendar Event property that is associated with it.
		 * @param columnHeader
		 * @param columnProperty
		 */
		public ColumnHeader(String columnHeader, String columnProperty)
		{
			this.columnHeader = columnHeader;
			this.columnProperty = columnProperty;
		}

		/**
		 * Gets the column header as it appears in the import file.
		 */
		public String getColumnHeader()
		{
			return columnHeader;
		}

		/**
		 * Gets the calendar event property name associated with this header.
		 */
		public String getColumnProperty()
		{
			return columnProperty;
		}

	}

}
