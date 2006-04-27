/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006 The Sakai Foundation.
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

package org.sakaiproject.calendar.api;

import java.util.List;

import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.entity.api.EntityProducer;
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.time.api.Time;
import org.sakaiproject.time.api.TimeRange;

/**
* <p>CalendarService is the interface for the Calendar service.</p>
* <p>The service manages a set of calendars, each containing a set of events.</p>
*/
public interface CalendarService
	extends EntityProducer
{
	/** The type string for this application: should not change over time as it may be stored in various parts of persistent entities. */
	static final String APPLICATION_ID = "sakai:calendar";

	/** This string starts the references to resources in this service. */
	public static final String REFERENCE_ROOT = Entity.SEPARATOR + "calendar";

	/** Name for the event of adding a calendar. */
	public static final String EVENT_ADD_CALENDAR = "calendar.new";

	/** Name for the event of removing a calendar. */
	public static final String EVENT_REMOVE_CALENDAR = "calendar.delete";

	/** Security lock for importing events into a calendar. */
	public static final String EVENT_IMPORT_CALENDAR = "calendar.import";

	/** Security lock for adding to a calendar. */
	public static final String EVENT_READ_CALENDAR = "calendar.read";

	/** Security lock for removing or changing any events in a calendar. */
	public static final String EVENT_MODIFY_CALENDAR = "calendar.revise";

	/** The Reference type for a calendar. */
	public static final String REF_TYPE_CALENDAR = "calendar";

	/** The Reference type for a calendar pdf. */
	public static final String REF_TYPE_CALENDAR_PDF = "calpdf";

	/** The Reference type for an event. */
	public static final String REF_TYPE_EVENT = "event";

	/** Recurring event modification intention: no intention. */
	public static final int MOD_NA = 0;

	/** Recurring event modification intention: just this one. */
	public static final int MOD_THIS = 1;

	/** Recurring event modification intention: all. */
	public static final int MOD_ALL = 2;

	/** Recurring event modification intention: this and subsequent. */
	public static final int MOD_REST = 3;

	/** Recurring event modification intention: this and prior. */
	public static final int MOD_PRIOR = 4;

	/** Calendar Printing Views. */
	public static final int UNKNOWN_VIEW = -1;
	public static final int DAY_VIEW = 0;
	public static final int WEEK_VIEW = 2;
	public static final int MONTH_VIEW = 3;
	public static final int LIST_VIEW = 5;


	/**
	* Return a List of all the defined calendars.
	* @return a List of Calendar objects (may be empty)
	*/
	public List getCalendars();

	/**
	* check permissions for addCalendar().
	* @param ref A reference for the calendar.
	* @return true if the user is allowed to addCalendar(ref), false if not.
	*/
	public boolean allowAddCalendar(String ref);

	/**
	* Add a new calendar.
	* Must commitCalendar() to make official, or cancelCalendar() when done!
	* @param ref The new calendar reference.
	* @return The newly created calendar.
	* @exception IdUsedException if the id is not unique.
	* @exception IdInvalidException if the id is not made up of valid characters.
	* @exception PermissionException if the user does not have permission to add a calendar.
	*/
	public CalendarEdit addCalendar(String ref)
		throws IdUsedException, IdInvalidException, PermissionException;

	/**
	* check permissions for getCalendar().
	* @param ref The calendar reference.
	* @return true if the user is allowed to getCalendar(ref), false if not.
	*/
	public boolean allowGetCalendar(String ref);

	/**
	* Return a specific calendar.
	* @param ref The calendar reference.
	* @return the Calendar that has the specified name.
	* @exception IdUnusedException If this name is not defined for any calendar.
	* @exception PermissionException If the user does not have any permissions to the calendar.
	*/
	public Calendar getCalendar(String ref)
		throws IdUnusedException, PermissionException;

	/**
	* check permissions for importing calendar events
	* @param ref The calendar reference.
	* @return true if the user is allowed to import events, false if not.
	*/
	public boolean allowImportCalendar(String ref);

	/**
	* check permissions for editCalendar()
	* @param ref The calendar reference.
	* @return true if the user is allowed to update the calendar, false if not.
	*/
	public boolean allowUpdateCalendar(String ref);

	/**
	* Get a locked calendar object for editing.
	* Must commitCalendar() to make official, or cancelCalendar() or removeCalendar() when done!
	* @param ref The calendar reference.
	* @return A CalendarEdit object for editing.
	* @exception IdUnusedException if not found, or if not an CalendarEdit object
	* @exception PermissionException if the current user does not have permission to mess with this user.
	* @exception InUseException if the Calendar object is locked by someone else.
	*/
	public CalendarEdit editCalendar(String ref)
		throws IdUnusedException, PermissionException, InUseException;

	/**
	* Commit the changes made to a CalendarEdit object, and release the lock.
	* The CalendarEdit is disabled, and not to be used after this call.
	* @param edit The CalendarEdit object to commit.
	*/
	public void commitCalendar(CalendarEdit edit);

	/**
	* Cancel the changes made to a CalendarEdit object, and release the lock.
	* The CalendarEdit is disabled, and not to be used after this call.
	* @param edit The CalendarEdit object to commit.
	*/
	public void cancelCalendar(CalendarEdit edit);

	/**
	* Remove a calendar that is locked for edit.
	* @param edit The calendar to remove.
	* @exception PermissionException if the user does not have permission to remove a calendar.
	*/
	public void removeCalendar(CalendarEdit edit)
		throws PermissionException;

	/**
	* check permissions for removeCalendar().
	* @param ref The calendar reference.
	* @return true if the user is allowed to removeCalendar(calendarId), false if not.
	*/
	public boolean allowRemoveCalendar(String ref);

	/**
	* Access the internal reference which can be used to access the calendar from within the system.
	* @param context The context.
	* @param id The calendar id.
	* @return The the internal reference which can be used to access the calendar from within the system.
	*/
	public String calendarReference(String context, String id);

	/**
	* Access the internal reference which can be used to access the calendar-in-pdf format from within the system.
	* @param context The context.
	* @param id The calendar id.
	* @return The the internal reference which can be used to access the calendar-in-pdf format from within the system.
	*/
	public String calendarPdfReference(String context, String id, int scheduleType, List calendars, String timeRangeString,
			String userName, TimeRange dailyTimeRange);

	/**
	 * Access the internal reference which can be used to access the event from within the system.
	 * 
	 * @param context
	 *        The context.
	 * @param calendarlId
	 *        The channel id.
	 * @param id
	 *        The event id.
	 * @return The the internal reference which can be used to access the event from within the system.
	 */
	public String eventReference(String context, String calendarId, String id);

	/**
	* Takes several calendar References and merges their events from within a given time range.
	* @param references The List of calendar References.
	* @param range The time period to use to select events.
	* @return CalendarEventVector object with the union of all events from the list of calendars in the given time range.
	*/
	public CalendarEventVector getEvents(List references, TimeRange range);

	/**
	 * Construct a new recurrence rule who's frequency description matches the frequency parameter.
	 * @param frequency The frequency description of the desired rule.
	 * @return A new recurrence rule.
	 */
	RecurrenceRule newRecurrence(String frequency);

	/**
	 * Construct a new recurrence rule who's frequency description matches the frequency parameter.
	 * @param frequency The frequency description of the desired rule.
	 * @param interval The recurrence interval.
	 * @return A new recurrence rule.
	 */
	RecurrenceRule newRecurrence(String frequency, int interval);

	/**
	 * Construct a new recurrence rule who's frequency description matches the frequency parameter.
	 * @param frequency The frequency description of the desired rule.
	 * @param interval The recurrence interval.
	 * @param count The number of reecurrences limit.
	 * @return A new recurrence rule.
	 */
	RecurrenceRule newRecurrence(String frequency, int interval, int count);

	/**
	 * Construct a new recurrence rule who's frequency description matches the frequency parameter.
	 * @param frequency The frequency description of the desired rule.
	 * @param interval The recurrence interval.
	 * @param until The time after which recurrences stop.
	 * @return A new recurrence rule.
	 */
	RecurrenceRule newRecurrence(String frequency, int interval, Time until);

}	// CalendarService



