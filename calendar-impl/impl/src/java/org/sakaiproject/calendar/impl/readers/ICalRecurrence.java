package org.sakaiproject.calendar.impl.readers;

import java.text.ParseException;

import net.fortuna.ical4j.model.Recur;
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
	public Time getEND_TIME() {
		if (recur == null) {
			return null;
		}
		return (Time) recur.getUntil();
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
			return 1;
		}
		return recur.getInterval();
//		return 1;
	}

	// Count of how many times this event should be repeated.
//	// same as count?  This many occurrences. 0 means no limit.
	public Integer getREPEAT() {
		if (recur == null) {
			return -1;
		}
		return -1;
	}

}
