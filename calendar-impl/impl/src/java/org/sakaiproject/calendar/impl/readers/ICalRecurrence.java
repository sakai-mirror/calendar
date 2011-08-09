package org.sakaiproject.calendar.impl.readers;

import java.text.ParseException;
import java.util.Date;

import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.property.DateProperty;
import net.fortuna.ical4j.model.property.RRule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.exception.ImportException;
import org.sakaiproject.time.api.Time;

public class ICalRecurrence  
{
	private static Log M_log = LogFactory.getLog(ICalRecurrence.class);
	private String rrule_text = "";
	private RRule rrule = null;
	private Recur recur = null;
	//private Time
	//private interval

	public ICalRecurrence(String rrule_text) throws ImportException {
		
		if (rrule_text == null) {
			return;	
		}
		
		try {
			this.rrule_text = rrule_text;
			this.rrule = new RRule(rrule_text);
			System.out.println("rrule_text: ["+rrule_text+"]");
		} catch (ParseException e) {
			M_log.warn("Parse exception for iCal recurrence rule: "+rrule_text);
			throw new ImportException(e);
		}
		
		// The RRule has been successfully created, now use it.
		System.out.println(rrule.toString());
		System.out.println("getRecur:"+rrule.getRecur());
		recur = rrule.getRecur();
		
		// Make sure the rule makes sense.
		isValidateRRule();
		//	String f = recur.getFrequency();
		//	System.out.println("frequency: "+f);
		//	System.out.println("sequence")
	}

	// Get declared event frequency, e.g weekly, monthly, daily
	public String getFrequency() {
		if (recur == null) {
			return "";
		}
		return recur.getFrequency();
	}
	
	// No instances past this time are created.
	public Date getEND_TIME() {
		if (recur == null) {
			return null;
		}
		Date d = recur.getUntil();
		return d;
//		return null;
	}

//	public String getENDS() {
//		return null;
//	}

	// How frequently should the event be scheduled?
	// Every other year?  Every week?
	// Default to scheduling every possible time.
	public Integer getINTERVAL() {
		if (recur == null) {
			return null;
		}
		
		Integer i = recur.getInterval();
		if (i.equals(-1)) {
			return null;
		}
		return i;
		
//		return recur.getInterval();
//		return 1;
	}

	// Count of how many times this event should be repeated.
//	// same as count?  This many occurrences. 0 means no limit.
	public Integer getREPEAT() {
		if (recur == null) {
			return null;
		}
		Integer c = recur.getCount();
		if (c.equals(-1)) {
			return null;
		}
		return c;
	}
	
	/*
	 * Take a recurrence and validate the settings to ensure
	 * they make sense.  Currently this will only log the problem 
	 * and return a flag indicating whether or not the the rule is valid.
	 * Calling code can determine what to do with the invalid recurrence.
	 * The order of the tests is important.  These tests are also
	 * applied in GenericCalendarImporter.
	 */
	
	public Boolean isValidateRRule() {
		
		Boolean valid = Boolean.valueOf(true);
		
		M_log.warn("in isValidateRRule");

		
		// Can specify no modifiers
		if (getEND_TIME() == null && getREPEAT() == null && getINTERVAL() == null) {
			valid = Boolean.valueOf(true);
		}
	
		// can't specify both end time and repeat
		if (valid && getEND_TIME() != null && getREPEAT() != null) {
			M_log.warn("iCal recurrence specifies both ending time and repeat count: "+rrule_text);
			valid = Boolean.valueOf(false);
		}
		
		// must have an interval
		if (valid && getINTERVAL() == null) {
			M_log.warn("iCal recurrence specifies ending time or repeat but not interval: "+rrule_text);
			valid = Boolean.valueOf(false);
		}
		
		if (valid && getINTERVAL() == null && getREPEAT() == null && getEND_TIME() != null) {
			valid = Boolean.valueOf(false);
		}

		if (!valid) {
			M_log.warn("iCal recurrence was not valid: ["+rrule_text+"]");
		}
		
		return valid;

	}

}
