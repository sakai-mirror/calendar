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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.PartStat;
import net.fortuna.ical4j.model.parameter.Role;
import net.fortuna.ical4j.model.parameter.Rsvp;
import net.fortuna.ical4j.model.property.Attach;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.DateProperty;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.XProperty;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpException;
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
import org.sakaiproject.authz.api.Member;
import org.sakaiproject.calendar.api.Calendar;
import org.sakaiproject.calendar.api.CalendarEdit;
import org.sakaiproject.calendar.api.CalendarEvent;
import org.sakaiproject.calendar.api.CalendarEventEdit;
import org.sakaiproject.calendar.api.RecurrenceRule;
import org.sakaiproject.calendar.impl.BaseCalendarService;
import org.sakaiproject.calendar.impl.GenericCalendarImporter;
import org.sakaiproject.calendar.impl.WeeklyRecurrenceRule;
import org.sakaiproject.calendar.impl.readers.IcalendarReader;
import org.sakaiproject.calendar.impl.readers.Reader;
import org.sakaiproject.calendar.impl.readers.Reader.ReaderImportCell;
import org.sakaiproject.calendar.impl.readers.Reader.ReaderImportRowHandler;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.content.api.ContentResourceEdit;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.ImportException;
import org.sakaiproject.exception.InconsistentException;
import org.sakaiproject.exception.OverQuotaException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.time.api.Time;
import org.sakaiproject.time.api.TimeBreakdown;
import org.sakaiproject.time.api.TimeRange;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.util.ResourceLoader;
import org.sakaiproject.util.StorageUser;
import org.sakaiproject.util.Validator;

/**
 * @author Zach A. Thomas <zach@aeroplanesoftware.com>
 *
 */
public class CalDAVCalendarService extends BaseCalendarService {
	
	private static Log M_log = LogFactory.getLog(CalDAVCalendarService.class);

	private String calDAVServerHost;
	private int calDAVServerPort;
	private String calDAVServerBasePath;
	private String defaultCalendarName;
	private String myWorkspaceTitle;
	private CalDAV4JMethodFactory methodFactory = new CalDAV4JMethodFactory();
	public static final long ONE_YEAR = 31536000000L;
	ResourceLoader rb = new ResourceLoader("calendarimpl", getSessionManager());
	static final DateFormat time24HourFormatter = new SimpleDateFormat("HH:mm");

	static final DateFormat time24HourFormatterWithSeconds = new SimpleDateFormat("HH:mm:ss");
	
	private TimeZoneRegistry timeZoneRegistry = TimeZoneRegistryFactory.getInstance().createRegistry();

	public static final long ONE_SECOND = 1000;
	
	public void init() {
		super.init();
		
		 try {
		   System.setProperty("ical4j.unfolding.relaxed", "true");
		   System.setProperty("ical4j.parsing.relaxed", "true");
		   System.setProperty("ical4j.compatibility.outlook", "true");
		 } catch (Throwable t) {
			 M_log.warn("init(): ", t);
		 }
	}
	

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
		
		private static final String ATTACHMENT_HEADER = "\n\nAttachments\n===========\n";
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
			//TODO what is this really supposed to do?
			return true;
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
			String siteName;
			String calendarCollectionPath;
			try {
				userEid = sitesOwner(calendar.getContext());
				http = createHttpClient(userEid);
				siteName = getSiteService().getSite(calendar.getContext()).getTitle();
				if (myWorkspaceTitle.equals(siteName)) siteName = defaultCalendarName;
				calendarCollectionPath = userEid + "/" + URLEncoder.encode(siteName,"UTF-8");
			} catch (UserNotDefinedException e1) {
				M_log.warn("CalDAVCalendarService::commitEvent() couldn't get an EID for userId '" + getSessionManager().getCurrentSessionUserId() + "'");
				return;
			} catch (IdUnusedException e) {
				return;
			} catch (UnsupportedEncodingException e) {
				return;
			}
			CalDAVCalendarCollection calendarCollection = getCalDAVCalendarCollection(calendarCollectionPath, http);
			net.fortuna.ical4j.model.Calendar iCalendar = null;
			VEvent ve = null;
			String timeZoneId = java.util.TimeZone.getDefault().getID();
			TimeZone tz = timeZoneRegistry.getTimeZone(timeZoneId);
			DtStart dtStart = new DtStart();
			dtStart.setDate(new DateTime(edit.getRange().firstTime().getTime()));
			try {
				dtStart = new DtStart(new DateTime(dtStart.getValue(), tz));
			} catch (ParseException e5) {
				// TODO Auto-generated catch block
				e5.printStackTrace();
			}
			DtEnd dtEnd = new DtEnd();
			dtEnd.setDate(new DateTime(edit.getRange().lastTime().getTime() + ONE_SECOND));
			try {
				dtEnd = new DtEnd(new DateTime(dtEnd.getValue(), tz));
			} catch (ParseException e5) {
				// TODO Auto-generated catch block
				e5.printStackTrace();
			}
			Summary summary = new Summary(edit.getDisplayName());
			Uid uid = new Uid(edit.getId());
			StringBuilder descText = new StringBuilder(edit.getDescription());
			
			Location location = new Location(edit.getLocation());
			List<Reference> attachments = edit.getAttachments();
			XProperty eventType = new XProperty(EVENTTYPE, edit.getType());
			//TODO try to add URLS to the body of the event for attachments
			if (attachments != null && attachments.size() > 0) {
				descText.append(ATTACHMENT_HEADER);
				for (Reference attachmentRef : attachments) {
					descText.append("\n" + attachmentRef.getUrl());
				}
			}
			Description desc = new Description(descText.toString());
	        try {
				iCalendar = calendarCollection.getCalendarByPath(http, edit.getId() + ".ics");
				ve = ICalendarUtils.getFirstEvent(iCalendar);
				// for some reason Zimbra is giving us an event even if it doesn't match the UID we asked for
				if(!edit.getId().equals(ve.getUid().getValue())) throw new CalDAV4JException("Received an event not matching the requested UID.");
	        } catch (CalDAV4JException e) {
				// didn't find existing event, so create a new one
	        	ve = new VEvent();
	        	ICalendarUtils.addOrReplaceProperty(ve, summary);
				ICalendarUtils.addOrReplaceProperty(ve, dtStart);
				ICalendarUtils.addOrReplaceProperty(ve, dtEnd);
				ICalendarUtils.addOrReplaceProperty(ve, uid);
				ICalendarUtils.addOrReplaceProperty(ve, desc);
				ICalendarUtils.addOrReplaceProperty(ve, location);
				ICalendarUtils.addOrReplaceProperty(ve, eventType);
				try {
					calendarCollection.addEvent(http, ve, tz.getVTimeZone());
					return;
				} catch (CalDAV4JException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	        }

				 
	        // TODO update all the event properties from the CalendarEventEdit
	        ICalendarUtils.addOrReplaceProperty(ve, summary);
	        ICalendarUtils.addOrReplaceProperty(ve, dtStart);
	        ICalendarUtils.addOrReplaceProperty(ve,dtEnd);
	        ICalendarUtils.addOrReplaceProperty(ve, uid);
	        ICalendarUtils.addOrReplaceProperty(ve, desc);
	        ICalendarUtils.addOrReplaceProperty(ve, location);
	        ICalendarUtils.addOrReplaceProperty(ve, eventType);
	        try {
	        	del(getCalDAVServerBasePath() + calendarCollectionPath + "/" + edit.getId() + ".ics", http);
	        	calendarCollection.addEvent(http, ve, tz.getVTimeZone());
	        	//calendarCollection.updateMasterEventAtPath(http, ve, getCalDAVServerBasePath() + calendarCollectionPath + "/" + edit.getId() + ".ics", null);
	        	//calendarCollection.updateMasterEvent(http, ve, null);
	        	return;
	        } catch (CalDAV4JException e2) {
	        	// TODO Auto-generated catch block
	        	e2.printStackTrace();
	        } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		public CalendarEdit editCalendar(String ref) {
			
			List<net.fortuna.ical4j.model.Calendar> calendars = null;
	        try {
	        	String sakaiUser = sitesOwner(getToolManager().getCurrentPlacement().getContext());
	        	HttpClient http = createHttpClient(sakaiUser);
	        	String calendarCollectionPath = sakaiUser + "/" + ref;
	        	CalDAVCalendarCollection calendarCollection = getCalDAVCalendarCollection(calendarCollectionPath, http);
	        	calendars = calendarCollection.getEventResources(http, new Date(0L), new Date());
	        } catch (CalDAV4JException ce) {
	        	M_log.error("CalDAVCalendarService::editCalendar() '" + ce.getMessage() + "'");
	        	return null;
	        } catch (UserNotDefinedException e) {
	        	M_log.warn("CalDAVCalendarService::editCalendar() couldn't get an EID for userId '" + getSessionManager().getCurrentSessionUserId() + "'");
	        	return null;
			} catch (IdUnusedException e) {
				M_log.warn("CalDAVCalendarService::editCalendar() couldn't get the Site for context id '" + getToolManager().getCurrentPlacement().getContext() + "'");
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
			String descriptionColumns[] = {"Summary","Description","Start Date","Start Time","Duration","Location","Recurrence"};
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
							else if ("Recurrence".equals(column.getPropertyName())) {
								try {
									mapCellValue = new Recur(value);
								} catch (ParseException e) {
									String msg = (String)rb.getFormattedMessage("err_freqbad", 
                                            new Object[]{new Integer(column.getLineNumber()),
                                                         column.getColumnHeader()});
									throw new ImportException( msg );
								}
							}
							else if (GenericCalendarImporter.DATE_PROPERTY_NAME.equals(column.getPropertyName())
									|| GenericCalendarImporter.UNTIL_PROPERTY_NAME.equals(column.getPropertyName()))
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
					String baseDescription = (String) eventProperties.get(GenericCalendarImporter.DESCRIPTION_PROPERTY_NAME);
					if (baseDescription == null) baseDescription = "";
					String[] descriptionSplit = baseDescription.split(ATTACHMENT_HEADER+"\n");
					if (descriptionSplit.length > 1) {
						String[] attachmentLines = descriptionSplit[1].split("\n");
						for (int i = 0;i < attachmentLines.length; i++) {
							try {
								ContentResourceEdit attachment = contentHostingService.addAttachmentResource(attachmentLines[i]);
								attachment.setContent(attachmentLines[i].getBytes());
								attachment.setContentType(ResourceProperties.TYPE_URL);
								contentHostingService.commitResource(attachment);
								baseCalendarEvent.addAttachment(m_entityManager.newReference(attachment.getReference()));
							} catch (IdInvalidException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (InconsistentException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IdUsedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (PermissionException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (ServerOverloadException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (OverQuotaException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}

					baseCalendarEvent.setDescription(descriptionSplit[0]);
					baseCalendarEvent.setDisplayName((String) eventProperties.get(GenericCalendarImporter.TITLE_PROPERTY_NAME));
					baseCalendarEvent.setLocation((String) eventProperties.get(GenericCalendarImporter.LOCATION_PROPERTY_NAME));
					baseCalendarEvent.setType((String) eventProperties.get(GenericCalendarImporter.ITEM_TYPE_PROPERTY_NAME));
//					RecurrenceRule rule = null;
//					if (Recur.WEEKLY.equals(eventProperties.get(GenericCalendarImporter.FREQUENCY_PROPERTY_NAME))) {
//						rule = new WeeklyRecurrenceRule((Integer)eventProperties.get(GenericCalendarImporter.INTERVAL_PROPERTY_NAME));
//					}
//					
//					if (rule != null) baseCalendarEvent.setRecurrenceRule(rule);

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
					Recur recurrence = (Recur) eventProperties.get("Recurrence");

					if (recurrence != null)
					{
						recurrenceRule = newRecurrence(recurrence);

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
		
//		if ( component.getProperty("RRULE") != null )
//		{
//			throw new RuntimeException("IcalendarReader: Re-occuring events not supported: " + summary );
//		}
		if (dtstartdate == null || dtenddate == null )
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
			
		String recurrence = "";
		if (component.getProperty(Property.RRULE) != null) {
			recurrence = ((RRule)component.getProperty(Property.RRULE)).getValue();
		}
		   
			String columns[]	= 
					{summary,
					 description,
					 DateFormat.getDateInstance(DateFormat.SHORT, rb.getLocale()).format(dtstartdate.getDate()),
					 DateFormat.getTimeInstance(DateFormat.SHORT, rb.getLocale()).format(dtstartdate.getDate()),
					 durationformat,
					 location,
					 recurrence};
		
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
			String siteName;
			String calendarCollectionPath;
			try {
				// we get the name of the site's creator
				userEid = sitesOwner(calendar.getContext());
				http = createHttpClient(userEid);
				siteName = getSiteService().getSite(calendar.getContext()).getTitle();
				if (myWorkspaceTitle.equals(siteName)) siteName = defaultCalendarName;
				calendarCollectionPath = userEid + "/" + URLEncoder.encode(siteName,"UTF-8");
			} catch (UserNotDefinedException e1) {
				return null;
			} catch (IdUnusedException e) {
				return null;
			} catch (UnsupportedEncodingException e) {
				return null;
			}
			CalDAVCalendarCollection calendarCollection = getCalDAVCalendarCollection(calendarCollectionPath, http);
			net.fortuna.ical4j.model.Calendar iCalendar = null;
			try {
				iCalendar = calendarCollection.getCalendarByPath(http, eventId + ".ics");
				String iCalEventId = ICalendarUtils.getFirstEvent(iCalendar).getUid().getValue();
				if(!eventId.equals(iCalEventId)) throw new CalDAV4JException(
						"Error getting calendar for event UID. Expected '" + eventId + "', received '" + iCalEventId + "'");
			} catch (CalDAV4JException e) {
				M_log.error(e.getMessage());
				return null;
			}
			return makeCalendarEventForCalDAVvEvent(calendar, ICalendarUtils.getFirstEvent(iCalendar));
		}

		private String sitesOwner(String siteId) throws IdUnusedException {
			return getSiteService().getSite(siteId).getCreatedBy().getEid();
		}

		public Calendar getCalendar(String ref) {
			return new BaseCalendarEdit(ref);
		}

		public List<Calendar> getCalendars() {
			// TODO how do we get all the calendars for the whole system?
			return new ArrayList<Calendar>();
		}

		public CalendarEvent getEvent(Calendar calendar, String eventId) {
			return editEvent(calendar, eventId);			
		}

		public List<CalendarEvent> getEvents(Calendar calendar) {
			// ask for events in the time range from the "beginning of time" to a year from now
			return getEvents(calendar, 0L, new java.util.Date().getTime() + ONE_YEAR);
		}

		public List<CalendarEvent> getEvents(Calendar calendar, long l, long m) {
			List<CalendarEvent> events = new ArrayList<CalendarEvent>();
			String userEid;
			String siteName;
			String calendarCollectionPath;
			HttpClient http;
			try {
				userEid = sitesOwner(calendar.getContext());
				http = createHttpClient(userEid);
				siteName = getSiteService().getSite(calendar.getContext()).getTitle();
				if (myWorkspaceTitle.equals(siteName)) siteName = defaultCalendarName;
				calendarCollectionPath = userEid + "/" + URLEncoder.encode(siteName,"UTF-8");
			} catch (UserNotDefinedException e1) {
				return events;
			} catch (IdUnusedException e) {
				return events;
			} catch (UnsupportedEncodingException e) {
				return events;
			}
			
			CalDAVCalendarCollection calendarCollection = getCalDAVCalendarCollection(calendarCollectionPath, http);
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

		public CalendarEventEdit putEvent(Calendar calendar, String eventUid) {
			return (BaseCalendarEventEdit)newResourceEdit(calendar, eventUid, null);
		}

		public void removeCalendar(CalendarEdit calendar) {
			// TODO Auto-generated method stub
			
		}

		public void removeEvent(Calendar calendar, CalendarEventEdit edit) {
			String userEid;
			HttpClient http;
			String siteName;
			try {
				userEid = sitesOwner(calendar.getContext());
				http = createHttpClient(userEid);
				siteName = getSiteService().getSite(calendar.getContext()).getTitle();
				if (myWorkspaceTitle.equals(siteName)) siteName = defaultCalendarName;
			} catch (UserNotDefinedException e1) {
				return;
			} catch (IdUnusedException e) {
				return;
			}
			String calendarCollectionPath = userEid + "/" + siteName;
			CalDAVCalendarCollection calendarCollection = getCalDAVCalendarCollection(calendarCollectionPath, http);
			try {
				calendarCollection.deleteEvent(http, edit.getId());
			} catch (CalDAV4JException e) {
				M_log.error("CalDAVCalendarService failed to delete event with id '" + edit.getId() + "'");
			}
			
		}
		
	}
	
	protected HttpClient createHttpClient(String username) throws UserNotDefinedException{
		String calDAVPassword = getCalDAVPasswordForUser(username);
        HttpClient http = new HttpClient();

        Credentials credentials = new UsernamePasswordCredentials(username, calDAVPassword);
        http.getState().setCredentials(null, null, credentials);
        http.getState().setAuthenticationPreemptive(true);
        return http;
    }
	
	protected String getCalDAVPasswordForUser(String sakaiUser) {
		//TODO create a real authn lookup for CalDAV users
		String rv = CalDAVConstants.TEST_PASSWORDS.get(sakaiUser);
		return rv == null ? "password" : rv;
	}

	protected CalDAVCalendarCollection getCalDAVCalendarCollection(String collectionPath, HttpClient httpClient) {
		String fullPath = getCalDAVServerBasePath() + collectionPath;
        CalDAVCalendarCollection calendarCollection = new CalDAVCalendarCollection(
                fullPath, createHostConfiguration(), methodFactory,
                org.osaf.caldav4j.CalDAVConstants.PROC_ID_DEFAULT);
        try {
			calendarCollection.getTicketsIDs(httpClient, "");
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CalDAV4JException e) {
			// if the Calendar collection doesn't exist, we can create it
			mkdir(fullPath, httpClient);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		columnHeaderMap.put("Recurrence", "Recurrence");
				
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

	public String getDefaultCalendarName() {
		return defaultCalendarName;
	}

	public void setDefaultCalendarName(String defaultCalendarName) {
		this.defaultCalendarName = defaultCalendarName;
	}

	public String getMyWorkspaceTitle() {
		return myWorkspaceTitle;
	}

	public void setMyWorkspaceTitle(String myWorkspaceTitle) {
		this.myWorkspaceTitle = myWorkspaceTitle;
	}

}
