package org.sakaiproject.calendar.impl.readers;

import junit.framework.TestCase;


//import org.hamcrest.MatcherAssert;
//import org.hamcrest.Matchers;
//import static org.hamcrest.CoreMatchers.*;
//import static org.hamcrest.CoreMatchers.is;


//import junit.framework.TestCase;

import org.sakaiproject.exception.ImportException;

//import static org.mockito.Mockito.*;

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
		assertEquals("get WEEKLY frequency","week",icalr.getFrequency());
	}

	// test result of null recurrence specification.
	public void testICallRecurrenceNull()  {
		ICalRecurrence icalr = null;
		try {
			icalr = new ICalRecurrence(null);
		} catch (ImportException e) {
			fail("Should handle null ICalRecurrence specification");
		}

		assertNull("frequency is null",icalr.getFrequency());
		assertNull("end date is null",icalr.getEND_TIME());
		assertNull("default interval is null",icalr.getINTERVAL());
		assertNull("default repeat/count is null",icalr.getREPEAT());

	}

	public void testRR1Rule() {
		String rr = "FREQ=WEEKLY;UNTIL=20111011T140000Z;BYDAY=TU,TH";
		ICalRecurrence icalr = parseRRuleString(rr);

		assertNotNull("should have generated an iCal recurrence object",icalr);
		assertNotNull("frequency",icalr.getFrequency());
		assertNotNull("until",icalr.getEND_TIME());
		assertEquals("20111011T140000Z",icalr.getEND_TIME().toString());
		//assertNull("default interval is null",icalr.getINTERVAL());
		assertEquals("default interval is 1",1,icalr.getINTERVAL().intValue());
		assertNull("default repeat/count is null",icalr.getREPEAT());


		//	fail("must test results of parsing string: ["+rr+"]");
	}

	//	why null pointer at icalreads 228?

	public void testRRRuleWeekly() {
		String rr = "FREQ=WEEKLY;";
		ICalRecurrence icalr = parseRRuleString(rr);

		assertEquals("frequency","week",icalr.getFrequency());
		assertNotNull("should have generated an iCal recurrence object",icalr);
		//assertEquals("20120622T173000Z",icalr.getEND_TIME().toString());
		assertNull("no end date",icalr.getEND_TIME());
		assertNull("if specify 'until' should not have count",icalr.getREPEAT());
		//assertNull("interval should be null",icalr.getINTERVAL());
		assertEquals("default interval is 1",1,icalr.getINTERVAL().intValue());
		assertTrue("rule should be valid",icalr.isValidateRRule());

	}

	public void testRRRuleWeeklyEndDate() {
		String rr = "FREQ=WEEKLY;UNTIL=20120622T173000Z;";
		ICalRecurrence icalr = parseRRuleString(rr);

		assertEquals("frequency","week",icalr.getFrequency());
		assertNotNull("should have generated an iCal recurrence object",icalr);
		assertEquals("20120622T173000Z",icalr.getEND_TIME().toString());
		assertNull("if specify 'until' should not have count",icalr.getREPEAT());
		//assertNull("interval should be null",icalr.getINTERVAL());
		assertEquals("default interval is 1",1,icalr.getINTERVAL().intValue());
		assertTrue("rule should be valid",icalr.isValidateRRule());

	}

	public void testRRWeeklyEndDateByDay1() {
		String rr = "FREQ=WEEKLY;UNTIL=20120622T173000Z;BYDAY=FR";
		ICalRecurrence icalr = parseRRuleString(rr);

		assertEquals("frequency","week",icalr.getFrequency());
		assertNotNull("should have generated an iCal recurrence object",icalr);
		assertEquals("20120622T173000Z",icalr.getEND_TIME().toString());
		assertNull("if specify 'until' should not have count",icalr.getREPEAT());
		//assertNull("interval should be null",icalr.getINTERVAL());
		assertEquals("default interval is 1",1,icalr.getINTERVAL().intValue());
		assertTrue("rule should be valid",icalr.isValidateRRule());

	}

//	public void testRRRuleWeeklyEndDateByDay3() {
//	//	String rr = "FREQ=WEEKLY;UNTIL=20111024T133000Z;BYDAY=MO,WE,FR";
//		String rr = "FREQ=WEEKLY;UNTIL=20120622T173000Z;BYDAY=MO,WE,FR";
//		ICalRecurrence icalr = parseRRuleString(rr);
//
//		assertEquals("frequency","MWF",icalr.getFrequency());
//		assertNotNull("should have generated an iCal recurrence object",icalr);
//		assertEquals("20120622T173000Z",icalr.getEND_TIME().toString());
//		assertNull("if specify 'until' should not have count",icalr.getREPEAT());
//		//assertNull("interval should be null",icalr.getINTERVAL());
//		assertEquals("default interval is 1",1,icalr.getINTERVAL().intValue());
//
//		assertTrue("rule should be valid",icalr.isValidateRRule());
//
//	}
//	
	
	public void testRRRuleMOWEFR() {
	//	String rr = "FREQ=WEEKLY;UNTIL=20111024T133000Z;BYDAY=MO,WE,FR";
		String rr = "FREQ=WEEKLY;UNTIL=20120622T173000Z;BYDAY=MO,WE,FR";
		ICalRecurrence icalr = parseRRuleString(rr);

		assertEquals("frequency","MWF",icalr.getFrequency());
		assertNotNull("should have generated an iCal recurrence object",icalr);
		assertEquals("20120622T173000Z",icalr.getEND_TIME().toString());
		assertNull("if specify 'until' should not have count",icalr.getREPEAT());
		assertEquals("default interval is 1",1,icalr.getINTERVAL().intValue());

		assertTrue("rule should be valid",icalr.isValidateRRule());

	}
	
	
	public void testRRRuleTTh() {

		String rr = "FREQ=WEEKLY;UNTIL=20120622T173000Z;BYDAY=TU,TH";
		ICalRecurrence icalr = parseRRuleString(rr);

		assertEquals("frequency","TTh",icalr.getFrequency());
		assertNotNull("should have generated an iCal recurrence object",icalr);
		assertEquals("20120622T173000Z",icalr.getEND_TIME().toString());
		assertNull("if specify 'until' should not have count",icalr.getREPEAT());
		assertEquals("default interval is 1",1,icalr.getINTERVAL().intValue());

		assertTrue("rule should be valid",icalr.isValidateRRule());

	}
	
	
	// based on google weekdays
//	public void testRRRuleWeekdays() {
//
//		String rr = "FREQ=WEEKLY;UNTIL=20111101T120000Z;BYDAY=MO,TU,WE,TH,FR;WKST=MO";
//		ICalRecurrence icalr = parseRRuleString(rr);
//
//		assertEquals("frequency","WEEKDAY",icalr.getFrequency());
//		assertNotNull("should have generated an iCal recurrence object",icalr);
//		assertEquals("20111101T120000Z",icalr.getEND_TIME().toString());
//		assertNull("if specify 'until' should not have count",icalr.getREPEAT());
//		assertNull("interval should be null",icalr.getINTERVAL());
//
//		assertTrue("rule should be valid",icalr.isValidateRRule());
//
//	}
	
	
	//	public void testRRRuleWeeklyEndDateByDay3() {
	//		String rr = "FREQ=WEEKLY;UNTIL=20111024T133000Z;BYDAY=MO,WE,FR";
	//		ICalRecurrence icalr = parseRRuleString(rr);
	//		
	//		assertEquals("frequency","WEEKLY",icalr.getFrequency());
	//		assertNotNull("should have generated an iCal recurrence object",icalr);
	//		assertEquals("20120622T173000Z",icalr.getEND_TIME().toString());
	//		assertNull("if specify 'until' should not have count",icalr.getREPEAT());
	//		assertNull("interval should be null",icalr.getINTERVAL());
	//		
	//		assertTrue("rule should be valid",icalr.isValidateRRule());
	//
	//	}
	//	

	//	public void testRRRuleWeeklyEndDateByDay3() {
	//		String rr = "FREQ=WEEKLY;UNTIL=20111024T133000Z;BYDAY=MO,WE,FR";
	//		ICalRecurrence icalr = parseRRuleString(rr);
	//		
	//		assertNotNull("should have generated an iCal recurrence object",icalr);
	//		//assertThat("should have proper date","20120622",icalr.getEND_TIME());
	//		assertEquals("20111024T133000Z",icalr.getEND_TIME().toString());
	//
	//		//org.hamcrest.MatcherAssert.assertThat("reason",icalr.getEND_TIME().toString(),startsWith("20120622"));
	//	//	assertThat(new String("HOWDY"),startsWith("HOW"));
	//		MatcherAssert.assertThat(icalr.getEND_TIME().toString(),Matchers.startsWith("20111024"));
	//	//	assertEquals(icalr.getEND_TIME(),startsWith("20120622"));
	//		
	//
	//
	////		fail("must test results of parsing string: ["+rr+"]");
	//	}
	//	
	private ICalRecurrence parseRRuleString(String rr1) {
		ICalRecurrence icalr = null;
		try {
			icalr = new ICalRecurrence(rr1);
		} catch (ImportException e) {
			fail("ical4j parsing failed for RRule: ["+rr1+"]");
		}
		return icalr;
	}

	public void testRRRuleDailyEndDate() {
		String rr = "FREQ=DAILY;UNTIL=20120622T173000Z;";
		ICalRecurrence icalr = parseRRuleString(rr);
	
		assertEquals("frequency","day",icalr.getFrequency());
		assertNotNull("should have generated an iCal recurrence object",icalr);
		assertEquals("20120622T173000Z",icalr.getEND_TIME().toString());
		assertNull("if specify 'until' should not have count",icalr.getREPEAT());
		//assertNull("interval should be null",icalr.getINTERVAL());
		assertEquals("default interval is 1",1,icalr.getINTERVAL().intValue());
		assertTrue("rule should be valid",icalr.isValidateRRule());
	
	}

	//	RRULE:FREQ=WEEKLY;UNTIL=20120622T173000Z;BYDAY=FR
}
