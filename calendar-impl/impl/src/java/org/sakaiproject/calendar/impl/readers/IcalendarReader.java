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

import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.fortuna.ical4j.data.CalendarBuilder;
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
	
	//## need to add these since they are used in the generic importer.
	//END_TIME_PROPERTY_NAME
	//ENDS_PROPERTY_NAME
	//INTERVAL_PROPERTY_NAME
	//REPEAT_PROPERTY_NAME
	
	// added for recurring events  For the moment just take the default ones below.
	/*
	public static final String ENDS_DEFAULT_COLUMN_HEADER = "End time default";
	public static final String FREQUENCY_DEFAULT_COLUMN_HEADER = "Frequency default";
	public static final String INTERVAL_DEFAULT_COLUMN_HEADER = "Interval";
	public static final String ITEM_TYPE_DEFAULT_COLUMN_HEADER = "Item Type";
	public static final String REPEAT_DEFAULT_COLUMN_HEADER = "Repeat Default";
	*/

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
	public void setResourceBundle(ResourceLoader rb) {
		this.rb = rb;
	}
	
//	public RRule getRRule() {
//		return rrule;
//	}
	
	// Class to represent an iCal recurrence rule.
//	public class RecurrenceRule {
//	
//		String rrule_text;
//		RRule rrule;
//		Recur recur;
//		
//		//String rrule_text;
//		//rrule_text;
//		//END_TIME_PROPERTY_NAME
//		//ENDS_PROPERTY_NAME
//		//INTERVAL_PROPERTY_NAME
//		//REPEAT_PROPERTY_NAME
//		
//		//rrule = new RRule(rrule_text);
//		
//		public RecurrenceRule(String rrule_text) throws ImportException {
//			this.rrule_text = rrule_text; 
//			try {
//				this.rrule = new RRule(rrule_text);
//				System.out.println("rrule_text: ["+rrule_text+"]");
//			} catch (ParseException e) {
//				M_log.warn("Parse exception for iCal recurrence rule: "+rrule_text);
//				throw new ImportException(e);
//			}
//			// The RRule has been successfully created, now use it.
//			System.out.println(rrule.toString());
//			System.out.println("getRecur:"+rrule.getRecur());
//			Recur recur = rrule.getRecur();
////			String f = recur.getFrequency();
////			System.out.println("frequency: "+f);
////			System.out.println("sequence")
//		}
//		
//		public String getEND_TIME() {
//			return null;
//		}
//		
//		public String getENDS() {
//			return null;
//		}
//		
//		public String getINTERVAL() {
//			return null;
//		}
//		
//		public String getREPEAT() {
//			return null;
//		}
//		
//	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.tool.calendar.ImportReader#importStreamFromDelimitedFile(java.io.InputStream, org.sakaiproject.tool.calendar.ImportReader.ReaderImportRowHandler)
	 */
	public void importStreamFromDelimitedFile(
			InputStream stream,
			ReaderImportRowHandler handler)
	throws ImportException//, IOException, ParserException
	{

		try {

			ColumnHeader columnDescriptionArray[] = null;
	//		String descriptionColumns[] = {"Summary","Description","Start Date","Start Time","Duration","Location"};
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

				//Move these to appropriate location if needed.
				String rrule_text;
	//			RRule rrule;
				if ( component.getProperty("RRULE") != null )
				{
//					columnHeaderMap.put(GenericCalendarImporter.ENDS_DEFAULT_COLUMN_HEADER, GenericCalendarImporter.ENDS_PROPERTY_NAME);
//					columnHeaderMap.put(GenericCalendarImporter.FREQUENCY_DEFAULT_COLUMN_HEADER, GenericCalendarImporter.FREQUENCY_PROPERTY_NAME);
//					columnHeaderMap.put(GenericCalendarImporter.INTERVAL_DEFAULT_COLUMN_HEADER, GenericCalendarImporter.INTERVAL_PROPERTY_NAME);
//					columnHeaderMap.put(GenericCalendarImporter.REPEAT_DEFAULT_COLUMN_HEADER, GenericCalendarImporter.REPEAT_PROPERTY_NAME);

					rrule_text = component.getProperty("RRULE").getValue();
					M_log.warn("IcalendarReader: Re-occuring events support under construction: " + rrule_text );

//
					// need test for invalid rrule text
					//rrule = new RRule(rrule_text);
					localRrule = new ICalRecurrence(rrule_text);					
					
				}	
				
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

				// Create array with values appropriate for the different properties.  The order
				// must reflect the order defined in the descriptionColumns array above
				// String descriptionColumns[] = {"Summary","Description","Start Date","Start Time","Duration","Location"}; as of 08/02/11
				// must add: 
//				columnHeaderMap.put(GenericCalendarImporter.ENDS_DEFAULT_COLUMN_HEADER, GenericCalendarImporter.ENDS_PROPERTY_NAME);
//				columnHeaderMap.put(GenericCalendarImporter.FREQUENCY_DEFAULT_COLUMN_HEADER, GenericCalendarImporter.FREQUENCY_PROPERTY_NAME);
//				columnHeaderMap.put(GenericCalendarImporter.INTERVAL_DEFAULT_COLUMN_HEADER, GenericCalendarImporter.INTERVAL_PROPERTY_NAME);
//				columnHeaderMap.put(GenericCalendarImporter.REPEAT_DEFAULT_COLUMN_HEADER, GenericCalendarImporter.REPEAT_PROPERTY_NAME);
//				{"Summary","Description","Start Date","Start Time","Duration","Location",
//					GenericCalendarImporter.FREQUENCY_DEFAULT_COLUMN_HEADER,
//					GenericCalendarImporter.INTERVAL_DEFAULT_COLUMN_HEADER,
//					GenericCalendarImporter.REPEAT_DEFAULT_COLUMN_HEADER
//					GenericCalendarImporter.ENDS_DEFAULT_COLUMN_HEADER
//					}
//				String columns[]	= 
//				{component.getProperty("SUMMARY").getValue(),
//						description,
//						DateFormat.getDateInstance(DateFormat.SHORT, rb.getLocale()).format(dtstartdate.getDate()),
//						DateFormat.getTimeInstance(DateFormat.SHORT, rb.getLocale()).format(dtstartdate.getDate()),
//						durationformat,
//						location,
//						localRrule.getFrequency(),
//						localRrule.getINTERVAL().toString(),
//						localRrule.getREPEAT().toString(),
//						localRrule.getEND_TIME().toString()
//				};

//				String columns[] = new String[10];
//				columns[0] = component.getProperty("SUMMARY").getValue();
//				columns[1] = description;
//				columns[2] = DateFormat.getDateInstance(DateFormat.SHORT, rb.getLocale()).format(dtstartdate.getDate());
//				columns[3] = DateFormat.getTimeInstance(DateFormat.SHORT, rb.getLocale()).format(dtstartdate.getDate());
//				columns[4] = durationformat;
//				columns[5] = location;
//				
//				if (localRrule != null) {
//					columns[6] = localRrule.getFrequency(); 
//					columns[7] = localRrule.getINTERVAL().toString();
//					columns[8] = localRrule.getREPEAT().toString();
//					columns[9] = localRrule.getEND_TIME().toString();
//				}
				
				//ArrayList al = new ArrayList({"ABBA"});
				ArrayList<String> al = new ArrayList<String>();
				al.add(component.getProperty("SUMMARY").getValue());
				al.add(description);
				al.add(DateFormat.getDateInstance(DateFormat.SHORT, rb.getLocale()).format(dtstartdate.getDate()));
				al.add(DateFormat.getTimeInstance(DateFormat.SHORT, rb.getLocale()).format(dtstartdate.getDate()));
				al.add(durationformat);
				al.add(location);
				
				// add the recurrence information if it is available.
				if (localRrule != null) {
					al.add(localRrule.getFrequency()); 
					al.add(localRrule.getINTERVAL().toString());
					al.add(localRrule.getREPEAT().toString());
					al.add(localRrule.getEND_TIME().toString());	
				}
				
				// create the string array.
				String columns[] = al.toArray(new String[al.size()]);
				
//						localRrule.getFrequency(),
//						localRrule.getINTERVAL().toString(),
//						localRrule.getREPEAT().toString(),
//					localRrule.getEND_TIME().toString()
//				};
				
				// Remove trailing/leading quotes from all columns.
				//trimLeadingTrailingQuotes(columns);

				handler.handleRow(
						processLine(
								columnDescriptionArray,
								lineNumber,
								columns));

				lineNumber++;

			} // end for

		}
		catch (Exception e)
		{
			M_log.warn(".importSteamFromDelimitedFile(): ", e);
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
		// Convert the date/time fields as they appear in the Outlook import to
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
		
	//	columnHeaderMap.put(ITEM_HEADER, GenericCalendarImporter.ITEM_TYPE_PROPERTY_NAME);
		// Not clear if we will need or can provide the item header

		
		//columnHeaderMap.put(ITEM_HEADER, GenericCalendarImporter.ITEM_TYPE_PROPERTY_NAME);
		//columnHeaderMap.put(GenericCalendarImporter.FREQUENCY_DEFAULT_COLUMN_HEADER);
		
		//csv values

/*		columnMap.put(GenericCalendarImporter.DATE_DEFAULT_COLUMN_HEADER, GenericCalendarImporter.DATE_PROPERTY_NAME);
		columnMap.put(GenericCalendarImporter.DESCRIPTION_DEFAULT_COLUMN_HEADER, GenericCalendarImporter.DESCRIPTION_PROPERTY_NAME);
		columnMap.put(GenericCalendarImporter.DURATION_DEFAULT_COLUMN_HEADER, GenericCalendarImporter.DURATION_PROPERTY_NAME);
		columnMap.put(GenericCalendarImporter.ENDS_DEFAULT_COLUMN_HEADER, GenericCalendarImporter.ENDS_PROPERTY_NAME);
		columnMap.put(GenericCalendarImporter.FREQUENCY_DEFAULT_COLUMN_HEADER, GenericCalendarImporter.FREQUENCY_PROPERTY_NAME);
		columnMap.put(GenericCalendarImporter.INTERVAL_DEFAULT_COLUMN_HEADER, GenericCalendarImporter.INTERVAL_PROPERTY_NAME);
		columnMap.put(GenericCalendarImporter.ITEM_TYPE_DEFAULT_COLUMN_HEADER, GenericCalendarImporter.ITEM_TYPE_PROPERTY_NAME);
		columnMap.put(GenericCalendarImporter.LOCATION_DEFAULT_COLUMN_HEADER, GenericCalendarImporter.LOCATION_PROPERTY_NAME);
		columnMap.put(GenericCalendarImporter.REPEAT_DEFAULT_COLUMN_HEADER, GenericCalendarImporter.REPEAT_PROPERTY_NAME);
		columnMap.put(GenericCalendarImporter.START_TIME_DEFAULT_COLUMN_HEADER, GenericCalendarImporter.START_TIME_PROPERTY_NAME);
		*/
		//columnMap.put(GenericCalendarImporter.TITLE_DEFAULT_COLUMN_HEADER, GenericCalendarImporter.TITLE_PROPERTY_NAME);



				
		return columnHeaderMap;
	}
}
