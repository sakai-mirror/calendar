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
		assertEquals("interval is 1",new Integer(1),icalr.getINTERVAL());
		assertEquals("repeat is -1",new Integer(-1),icalr.getREPEAT());
		
	}
}
