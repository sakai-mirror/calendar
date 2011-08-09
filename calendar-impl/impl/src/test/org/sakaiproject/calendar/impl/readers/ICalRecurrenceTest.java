package org.sakaiproject.calendar.impl.readers;

import junit.framework.TestCase;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.sakaiproject.calendar.impl.readers.Reader;
import org.sakaiproject.calendar.impl.readers.IcalendarReader;

import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import org.sakaiproject.exception.ImportException;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.is;
//import static org.junit.Assert.assertThat;
import static org.hamcrest.MatcherAssert.*;

//import static org.hamcrest.MatcherAssert.assertThat;


import junit.framework.TestCase;

import static org.mockito.Mockito.*;

/*
 * Test WEEKLY, DAILY etc
 * Test UNTIL
 * Test BYDAY 
 * Test many more
 */

public class ICalRecurrenceTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	// test constructor
	public void testICallRecurrenceConstructor() {
		ICalRecurrence icalr = null;
		try {
			 icalr = new ICalRecurrence("FREQ=WEEKLY");
		} catch (ImportException e) {
				fail("can not create ICallRecurrence object");
		}
		assertNotNull("ical constructor returns object",icalr);
		assertTrue("ical recurrence type is correct",ICalRecurrence.class.isInstance(icalr));
	}
	

	public void testICallRecurrenceConstructorBadRecur() {
		ICalRecurrence icalr = null;
		try {
			 icalr = new ICalRecurrence("FREQ=ABBA");
		} catch (ImportException e) {
			return;
		} catch (IllegalArgumentException e) {
			return;
		}
		
		fail("should not accept incorrect recurrence specification");
	}

	// test extraction of frequency
	public void testICallRecurrenceFrequencyWeekly() {
		ICalRecurrence icalr = null;
		try {
			 icalr = new ICalRecurrence("FREQ=WEEKLY");
		} catch (ImportException e) {
		}
		assertEquals("get WEEKLY frequency","WEEKLY",icalr.getFrequency());
	}
	
	// test result of null recurrence specification.
	public void testICallRecurrenceNull()  {
		ICalRecurrence icalr = null;
		try {
			icalr = new ICalRecurrence(null);
		} catch (ImportException e) {
			fail("Should handle null ICalRecurrence specification");
		}
		
		assertEquals("frequency is null","",icalr.getFrequency());
		assertNull("end date is null",icalr.getEND_TIME());
		assertNull("default interval is null",icalr.getINTERVAL());
		assertNull("default repeat/count is null",icalr.getREPEAT());
		
	}
	
	public void testRR1Rule() {
		String rr = "FREQ=WEEKLY;UNTIL=20111011T140000Z;BYDAY=TU,TH";
		ICalRecurrence icalr = parseRRuleString(rr);
		
		assertNotNull("should have generated an iCal recurrence object",icalr);
		
	//	fail("must test results of parsing string: ["+rr+"]");
	}

//	why null pointer at icalreads 228?
	
	public void testRR2Rule() {
		String rr = "FREQ=WEEKLY;UNTIL=20120622T173000Z;BYDAY=FR";
		ICalRecurrence icalr = parseRRuleString(rr);
		
		assertNotNull("should have generated an iCal recurrence object",icalr);
		//assertThat("should have proper date","20120622",icalr.getEND_TIME());
		assertEquals("20120622T173000Z",icalr.getEND_TIME().toString());
		assertNull("if specify 'until' should not have count",icalr.getREPEAT());
		assertNull("interval should be null",icalr.getINTERVAL());

		//		org.hamcrest.MatcherAssert.assertThat("reason",icalr.getEND_TIME().toString(),startsWith("20120622"));
	//	assertEquals(icalr.getEND_TIME(),startsWith("20120622"));
		


//		fail("must test results of parsing string: ["+rr+"]");
	}
	
	public void testRR3Rule() {
		String rr = "FREQ=WEEKLY;UNTIL=20111024T133000Z;BYDAY=MO,WE,FR";
		ICalRecurrence icalr = parseRRuleString(rr);
		
		assertNotNull("should have generated an iCal recurrence object",icalr);
		//assertThat("should have proper date","20120622",icalr.getEND_TIME());
		assertEquals("20111024T133000Z",icalr.getEND_TIME().toString());

		//		org.hamcrest.MatcherAssert.assertThat("reason",icalr.getEND_TIME().toString(),startsWith("20120622"));
	//	assertEquals(icalr.getEND_TIME(),startsWith("20120622"));
		


//		fail("must test results of parsing string: ["+rr+"]");
	}
	
	private ICalRecurrence parseRRuleString(String rr1) {
		ICalRecurrence icalr = null;
		try {
			 icalr = new ICalRecurrence(rr1);
		} catch (ImportException e) {
			fail("ical4j parsing failed for RRule: ["+rr1+"]");
		}
		return icalr;
	}
	
//	RRULE:FREQ=WEEKLY;UNTIL=20120622T173000Z;BYDAY=FR
}
