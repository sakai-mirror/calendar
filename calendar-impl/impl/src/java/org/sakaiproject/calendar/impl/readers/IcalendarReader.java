/**********************************************************************************
 * $URL$
 * $Id$
  ***********************************************************************************
 *
 * Copyright (c) 2007, 2008 The Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.calendar.impl.readers;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.DateProperty;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.util.CompatibilityHints;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.calendar.impl.GenericCalendarImporter;
import org.sakaiproject.exception.ImportException;
import org.sakaiproject.time.api.TimeBreakdown;
import org.sakaiproject.util.ResourceLoader;

/**
 * This class parses an import file from iCalendar.
 */
public class IcalendarReader extends Reader
{
	private ResourceLoader rb = new ResourceLoader("calendar");
	private static Log M_log = LogFactory.getLog(IcalendarReader.class);
	
	private ICalRecurrence localRrule;
	
	private static final String CONTACT_SECTION_HEADER = "Contacts";
	private static final String TODO_SECTION_HEADER = "Todos";
	private static final String EVENT_SECTION_HEADER = "Events";

	public static final String TITLE_HEADER = "Summary";	
	public static final String LOCATION_HEADER = "Location";
	public static final String DATE_HEADER = "Start Date";	
	public static final	String START_TIME_HEADER = "Start Time";
	public static final	String DURATION_HEADER = "Duration";	
	public static final	String ITEM_HEADER = "Type";	
	public static final	String DESCRIPTION_HEADER = "Description";
	public static final String FREQUENCY_HEADER="Frequency";
	
	//## These additional properies are used in the CSV importer.
	//END_TIME_PROPERTY_NAME
	//ENDS_PROPERTY_NAME
	//INTERVAL_PROPERTY_NAME
	//REPEAT_PROPERTY_NAME

	/**
	 * Default constructor 
	 * @throws ImportException 
	 */
	public IcalendarReader() throws ImportException
	{
		super();
	}
	
	// Some methods required for unit testing.
	
	// Allow creating instance with mock logger.
	public IcalendarReader(Log logger)
	{
		super();
		M_log = logger;
	}
	
	// Allow mock resource bundle
	public void setResourceBundle(ResourceLoader rb2) {
		this.rb = rb2;
	}
	

	/* (non-Javadoc)
	 * @see org.sakaiproject.tool.calendar.ImportReader#importStreamFromDelimitedFile(java.io.InputStream, org.sakaiproject.tool.calendar.ImportReader.ReaderImportRowHandler)
	 */
	public void importStreamFromDelimitedFile(
			InputStream stream,
			ReaderImportRowHandler handler)
	throws ImportException
	{

		try {

			ColumnHeader columnDescriptionArray[] = null;
			// If change columns remember to check the column header definitions above and defaultColumnMap below.
			String descriptionColumns[] = {"Summary","Description","Start Date","Start Time","Duration","Location",
				GenericCalendarImporter.FREQUENCY_DEFAULT_COLUMN_HEADER,
				GenericCalendarImporter.INTERVAL_DEFAULT_COLUMN_HEADER,
				GenericCalendarImporter.REPEAT_DEFAULT_COLUMN_HEADER,
				GenericCalendarImporter.ENDS_DEFAULT_COLUMN_HEADER
				};

			int lineNumber = 1;
			String durationformat ="";
			String requireValues = "";

			// column map stuff
			trimLeadingTrailingQuotes(descriptionColumns);
			columnDescriptionArray = buildColumnDescriptionArray(descriptionColumns);

			// enable "relaxed parsing"; read file using LF instead of CRLF
			CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING, true); 
			CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, true); 
			CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_OUTLOOK_COMPATIBILITY, true); 
			CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION, true); 

			CalendarBuilder builder = new CalendarBuilder();
			net.fortuna.ical4j.model.Calendar calendar = builder.build(stream);

			for (Iterator i = calendar.getComponents("VEVENT").iterator(); i.hasNext();)
			{
				Component component = (Component) i.next();

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
					M_log.warn("IcalendarReader: SUMMARY is required; event not imported");
					continue;
				}
				String summary = component.getProperty("SUMMARY").getValue();
				
				if (dtstartdate == null || dtenddate == null )
				{
					M_log.warn("IcalendarReader: DTSTART/DTEND required: " + summary );
					continue;
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
				
				String rrule_text;
				if ( component.getProperty("RRULE") != null )
				{
					rrule_text = component.getProperty("RRULE").getValue();
					M_log.info("IcalendarReader: recurring event text: " + rrule_text );
					localRrule = new ICalRecurrence(rrule_text);										
				}	

				// Build the array of values corresponding to the description columns above.
				ArrayList<String> al = new ArrayList<String>();
				al.add(component.getProperty("SUMMARY").getValue());
				al.add(description);
				al.add(DateFormat.getDateInstance(DateFormat.SHORT, rb.getLocale()).format(dtstartdate.getDate()));
				al.add(DateFormat.getTimeInstance(DateFormat.SHORT, rb.getLocale()).format(dtstartdate.getDate()));
				al.add(durationformat);
				al.add(location);
				
				al.add(localRrule.getFrequency()); 
				al.add(localRrule.getINTERVAL() != null ? localRrule.getINTERVAL().toString() : "");
				al.add(localRrule.getREPEAT() != null ? localRrule.getREPEAT().toString() : "");
				al.add(localRrule.getEND_TIME() != null
						? DateFormat.getDateInstance(DateFormat.SHORT, rb.getLocale()).format(localRrule.getEND_TIME())
								: "");

				// create the string array.
				String columns[] = al.toArray(new String[al.size()]);
				
				handler.handleRow(
						processLine(
								columnDescriptionArray,
								lineNumber,
								columns));

				lineNumber++;

			} // end for

		}
		catch (NullPointerException e)
		{
			M_log.warn(".importStreamFromDelimitedFile(): ", e);
		}
		catch (IOException e)
		{
			M_log.warn(".importStreamFromDelimitedFile(): ", e);
		}
		catch (ParserException e)
		{
			M_log.warn(".importStreamFromDelimitedFile(): ", e);
		}

	} // end importStreamFromDelimitedFile

	/* (non-Javadoc)
	 * @see org.sakaiproject.tool.calendar.schedimportreaders.Reader#filterEvents(java.util.List, java.lang.String[])
	 */
	public List filterEvents(List events, String[] customFieldNames) throws ImportException
	{
		Iterator it = events.iterator();
		int lineNumber = 1;
		
		//
		// Convert the date/time fields as they appear in the import to
		// be a synthesized start/end timerange.
		//
		while ( it.hasNext() )
		{
			Map eventProperties = (Map)it.next();

			Date startTime = (Date) eventProperties.get(GenericCalendarImporter.START_TIME_PROPERTY_NAME);
			TimeBreakdown startTimeBreakdown = null;
			
			if ( startTime != null )
			{
				// if the source time zone were known, this would be
				// a good place to set it: startCal.setTimeZone()
				GregorianCalendar startCal = new GregorianCalendar();
				startCal.setTimeInMillis( startTime.getTime() );
				startTimeBreakdown = 
						  getTimeService().newTimeBreakdown( 0, 0, 0, 
							  startCal.get(Calendar.HOUR_OF_DAY),
							  startCal.get(Calendar.MINUTE),
							  startCal.get(Calendar.SECOND),
								0 );
			}
			else
			{
				Integer line = Integer.valueOf(lineNumber);
				String msg = (String)rb.getFormattedMessage("err_no_stime_on", 
																		  new Object[]{line});
				throw new ImportException( msg );
			}
			
			Integer durationInMinutes = (Integer)eventProperties.get(GenericCalendarImporter.DURATION_PROPERTY_NAME);

			if ( durationInMinutes == null )
			{
				Integer line = Integer.valueOf(lineNumber);
				String msg = (String)rb.getFormattedMessage("err_no_dtime_on", 
																		  new Object[]{line});
				throw new ImportException( msg );
			}
			
			Date endTime =
				new Date(
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
							  endCal.get(Calendar.HOUR_OF_DAY),
							  endCal.get(Calendar.MINUTE),
							  endCal.get(Calendar.SECOND),
							  0 );
			}

			Date startDate = (Date) eventProperties.get(GenericCalendarImporter.DATE_PROPERTY_NAME);
			
			// if the source time zone were known, this would be
			// a good place to set it: startCal.setTimeZone()
			GregorianCalendar startCal = new GregorianCalendar();
			if ( startDate != null )
				startCal.setTimeInMillis( startDate.getTime() );
				
			startTimeBreakdown.setYear( startCal.get(Calendar.YEAR) );
			startTimeBreakdown.setMonth( startCal.get(Calendar.MONTH)+1 );
			startTimeBreakdown.setDay( startCal.get(Calendar.DAY_OF_MONTH) );
				
			endTimeBreakdown.setYear( startCal.get(Calendar.YEAR) );
			endTimeBreakdown.setMonth( startCal.get(Calendar.MONTH)+1 );
			endTimeBreakdown.setDay( startCal.get(Calendar.DAY_OF_MONTH) );
			
			eventProperties.put(
				GenericCalendarImporter.ACTUAL_TIMERANGE,
				getTimeService().newTimeRange(
						  getTimeService().newTimeLocal(startTimeBreakdown),
						  getTimeService().newTimeLocal(endTimeBreakdown),
					true,
					false));
					
			lineNumber++;
		}
		
		return events;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.tool.calendar.schedimportreaders.Reader#getDefaultColumnMap()
	 */
	public Map getDefaultColumnMap()
	{
		Map columnHeaderMap = new HashMap();

		// orginal ical
		columnHeaderMap.put(DATE_HEADER, GenericCalendarImporter.DATE_PROPERTY_NAME);
		columnHeaderMap.put(DESCRIPTION_HEADER, GenericCalendarImporter.DESCRIPTION_PROPERTY_NAME);
		columnHeaderMap.put(DURATION_HEADER, GenericCalendarImporter.DURATION_PROPERTY_NAME);
		columnHeaderMap.put(LOCATION_HEADER, GenericCalendarImporter.LOCATION_PROPERTY_NAME);
		columnHeaderMap.put(START_TIME_HEADER, GenericCalendarImporter.START_TIME_PROPERTY_NAME);
		columnHeaderMap.put(TITLE_HEADER, GenericCalendarImporter.TITLE_PROPERTY_NAME);

		// Added for recurring events.  Can replace with an iCal specific override defined above if necessary.

		columnHeaderMap.put(GenericCalendarImporter.FREQUENCY_DEFAULT_COLUMN_HEADER, GenericCalendarImporter.FREQUENCY_PROPERTY_NAME);
		columnHeaderMap.put(GenericCalendarImporter.INTERVAL_DEFAULT_COLUMN_HEADER, GenericCalendarImporter.INTERVAL_PROPERTY_NAME);
		columnHeaderMap.put(GenericCalendarImporter.REPEAT_DEFAULT_COLUMN_HEADER, GenericCalendarImporter.REPEAT_PROPERTY_NAME);
		columnHeaderMap.put(GenericCalendarImporter.ENDS_DEFAULT_COLUMN_HEADER, GenericCalendarImporter.ENDS_PROPERTY_NAME);
				
		return columnHeaderMap;
	}
}
