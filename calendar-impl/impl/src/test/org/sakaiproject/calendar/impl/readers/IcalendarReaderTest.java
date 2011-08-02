package org.sakaiproject.calendar.impl.readers;

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

public class IcalendarReaderTest extends TestCase {

	private static Log mockLog = mock(Log.class);

	static String testSrcDir = "./src/test/org/sakaiproject/calendar/impl/readers";

	static String iCalHeader;
	static String iCalFooter;
	static String iRR1;
	static InputStream RR1_stream;

	static Reader iCalReader;

	protected void setUp() throws Exception {
		super.setUp();

		iCalReader = new IcalendarReader(mockLog);
		iCalHeader = readFileAsString(testSrcDir+"/"+"header.ics.data"); 
		iCalFooter = readFileAsString(testSrcDir+"/"+"footer.ics.data"); 
		iRR1 = readFileAsString(testSrcDir+"/"+"RR1.ics.data");
		RR1_stream = convertStringToStream(iCalHeader+iRR1+iCalFooter);

	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/********************
	 * File / string utilities.
	 ********************/
	
	static String readFileAsString(String filePath) throws java.io.IOException{
		byte[] buffer = new byte[(int) new File(filePath).length()];
		BufferedInputStream f = null;
		try {
			f = new BufferedInputStream(new FileInputStream(filePath));
			f.read(buffer);
		} finally {
			if (f != null) try { f.close(); } catch (IOException ignored) { }
		}
		return new String(buffer);
	}

	static InputStream convertStringToStream(String s) {

		InputStream is = null;

		try {
			is = new ByteArrayInputStream(s.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return is;
	}

	public String convertStreamToString(InputStream is) throws IOException {

		if (is != null) {
			Writer writer = new StringWriter();

			char[] buffer = new char[1024];
			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is, "UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} finally {
				is.close();
			}
			return writer.toString();
		} else {       
			return "";
		}
	}
	
	/********************
	 * Tests
	 ********************/

	public void testDummy() {
		assertTrue("dummy test",true);
	}

	public void testSetup() {
		assertNotNull("ical reader creation",iCalReader);
	}
	

	public void testImportWithNullHandlerExpectNotSupported() throws ImportException {
		// Add check that the proper error message is being passed.
		// specify mock resource loader
		((IcalendarReader) iCalReader).setResourceBundle(null);
		iCalReader.importStreamFromDelimitedFile(RR1_stream,null);
		//verify(mockLog).warn("IcalendarReader: Re-occuring events not supported: R: tues and thurs for  6 months");
		//verify(mockLog).warn(startsWith("IcalendarReader: Re-occuring events not supported:"));
		verify(mockLog).warn(startsWith("IcalendarReader: Re-occuring events "));
	}
	

//	public void testImportWithNullHandlerExpectNotSupported() throws ImportException {
//		// Add check that the proper error message is being passed.
//		// specify mock resource loader
//		((IcalendarReader) iCalReader).setResourceBundle(null);
//		iCalReader.importStreamFromDelimitedFile(RR1_stream,null);
//		//verify(mockLog).warn("IcalendarReader: Re-occuring events not supported: R: tues and thurs for  6 months");
//		verify(mockLog).warn(startsWith("IcalendarReader: Re-occuring events not supported:"));
//	}
	
}
