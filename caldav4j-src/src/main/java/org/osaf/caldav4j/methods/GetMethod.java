/*
 * Copyright 2005 Open Source Applications Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osaf.caldav4j.methods;

import java.io.IOException;
import java.io.InputStream;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;

import org.apache.commons.httpclient.Header;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osaf.caldav4j.CalDAV4JProtocolException;


public class GetMethod extends org.apache.commons.httpclient.methods.GetMethod{
    private static final Log log = LogFactory.getLog(GetMethod.class);
    
    private CalendarBuilder calendarBuilder = null;
    
    public GetMethod (){
        super();
    }

    public CalendarBuilder getCalendarBuilder() {
        return calendarBuilder;
    }

    public void setCalendarBuilder(CalendarBuilder calendarBuilder) {
        this.calendarBuilder = calendarBuilder;
    }

    public Calendar getResponseBodyAsCalendar() throws IOException,
            ParserException, CalDAV4JProtocolException {
        Header header = getResponseHeader("Content-Type");
        String contentType = header.getValue();
        if (!contentType.startsWith("text/calendar")) {
            log.error("Content type must be \"text/calendar\" to parse as an " +
                    "icalendar resource. Type was: " + contentType);
            throw new CalDAV4JProtocolException(
                    "Content type must be \"text/calendar\" to parse as an " +
                    "icalendar resource");
        }
        InputStream stream = getResponseBodyAsStream();
        return calendarBuilder.build(stream);
    }
}
