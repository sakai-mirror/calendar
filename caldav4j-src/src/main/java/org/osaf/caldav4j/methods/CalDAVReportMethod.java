/*
 * Copyright 2006 Open Source Applications Foundation
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

import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.webdav.lib.methods.DepthSupport;
import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.DOMValidationException;
import org.osaf.caldav4j.model.request.CalDAVReportRequest;
import org.osaf.caldav4j.util.XMLUtils;
import org.w3c.dom.Document;

public class CalDAVReportMethod extends CalDAVXMLResponseMethodBase implements DepthSupport {
    private static final Log log = LogFactory
        .getLog(CalDAVReportMethod.class);
    
    private CalDAVReportRequest reportRequest;
    
    private int depth = DEPTH_1;
    
    public CalDAVReportMethod() {

    }

    public CalDAVReportMethod(String path, CalDAVReportRequest reportRequest) {
        this.reportRequest = reportRequest;
        setPath(path);
    }

    /**
     * Depth setter.
     *
     * @param depth New depth value
     */
    public void setDepth(int depth) {
        this.depth = depth;
    }

    /**
     * Depth getter.
     *
     * @return int depth value
     */
    public int getDepth() {
        return depth;
    }
    
    public String getName() {
        return CalDAVConstants.METHOD_REPORT;
    }

    public CalDAVReportRequest getReportRequest() {
        return reportRequest;
    }

    public void setReportRequest(CalDAVReportRequest reportRequest) {
        this.reportRequest = reportRequest;
    }
    
    
    /**
     * Generate additional headers needed by the request.
     *
     * @param state State token
     * @param conn The connection being used to make the request.
     */
    public void addRequestHeaders(HttpState state, HttpConnection conn)
    throws IOException, HttpException {

        super.addRequestHeaders(state, conn);

        switch (depth) {
        case DEPTH_0:
            super.setRequestHeader("Depth", "0");
            break;
        case DEPTH_1:
            super.setRequestHeader("Depth", "1");
            break;
        case DEPTH_INFINITY:
            super.setRequestHeader("Depth", "infinity");
            break;
        }

        addRequestHeader(CalDAVConstants.HEADER_CONTENT_TYPE, CalDAVConstants.TEXT_XML_CONTENT_TYPE);

    }

    /**
     * Generates a request body from the calendar query.
     */
    protected String generateRequestBody() {
        Document doc = null;
        try {
            doc = reportRequest.createNewDocument(XMLUtils
                    .getDOMImplementation());
        } catch (DOMValidationException domve) {
            log.error("Error trying to create DOM from CalDAVReportRequest: ", domve);
            throw new RuntimeException(domve);
        }
        return XMLUtils.toPrettyXML(doc);
    }
}