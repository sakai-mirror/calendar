/************************************************************************************
 *
 * Copyright (c) 2012 The Sakai Foundation.
 * @author Leidse Onderwijsinstellingen
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

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.sakaiproject.calendar.api.RecurrenceRule;
import org.sakaiproject.calendar.api.CalendarEvent.EventAccess;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.time.api.TimeRange;

/**
 * @author Tania Tritean, ISDC
 */
public class CalendarEventEntity implements Serializable {

	/**
	 * 
	 */
	private static final long			serialVersionUID	= 1L;

	/** The effective time range. */
	protected TimeRange					range				= null;

	/**
	 * The base time range: for non-recurring events, this matches m_range, but for recurring events, it is always the
	 * range of the initial event in the sequence (transient).
	 */
	protected TimeRange					baseRange			= null;

	/** The recurrence rule (single rule). */
	protected RecurrenceRule			singleRule			= null;

	/** The exclusion recurrence rule. */
	protected RecurrenceRule			exclusionRule		= null;

	/** The properties. */
	protected ResourcePropertiesEdit	properties			= null;

	/** The event id. */
	protected String					id					= null;

	/** The attachments - dereferencer objects. */
	protected List						attachments			= null;

	/** The event code for this edit. */
	protected String					event				= null;

	/** Active flag. */
	protected boolean					active				= false;

	/** The Collection of groups (authorization group id strings). */
	protected Collection				groups				= new Vector();

	/** The message access. */
	protected EventAccess				access				= EventAccess.SITE;

	public void CalendarEventEntity() {
		
	}
	/**
	 * @return the range
	 */
	public TimeRange getRange() {
		return range;
	}
	
	/**
	 * @param range
	 *            the range to set
	 */
	public void setRange(TimeRange range) {
		this.range = range;
	}

	/**
	 * @return the baseRange
	 */
	public TimeRange getBaseRange() {
		return baseRange;
	}

	/**
	 * @param baseRange
	 *            the baseRange to set
	 */
	public void setBaseRange(TimeRange baseRange) {
		this.baseRange = baseRange;
	}

	/**
	 * @return the singleRule
	 */
	public RecurrenceRule getSingleRule() {
		return singleRule;
	}

	/**
	 * @param singleRule
	 *            the singleRule to set
	 */
	public void setSingleRule(RecurrenceRule singleRule) {
		this.singleRule = singleRule;
	}

	/**
	 * @return the exclusionRule
	 */
	public RecurrenceRule getExclusionRule() {
		return exclusionRule;
	}

	/**
	 * @param exclusionRule
	 *            the exclusionRule to set
	 */
	public void setExclusionRule(RecurrenceRule exclusionRule) {
		this.exclusionRule = exclusionRule;
	}

	/**
	 * @return the properties
	 */
	public ResourcePropertiesEdit getProperties() {
		return properties;
	}

	/**
	 * @param properties
	 *            the properties to set
	 */
	public void setProperties(ResourcePropertiesEdit properties) {
		this.properties = properties;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the attachments
	 */
	public List getAttachments() {
		return attachments;
	}

	/**
	 * @param attachments
	 *            the attachments to set
	 */
	public void setAttachments(List attachments) {
		this.attachments = attachments;
	}

	/**
	 * @return the event
	 */
	public String getEvent() {
		return event;
	}

	/**
	 * @param event
	 *            the event to set
	 */
	public void setEvent(String event) {
		this.event = event;
	}

	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active
	 *            the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * @return the groups
	 */
	public Collection getGroups() {
		return groups;
	}

	/**
	 * @param groups
	 *            the groups to set
	 */
	public void setGroups(Collection groups) {
		this.groups = groups;
	}

	/**
	 * @return the access
	 */
	public EventAccess getAccess() {
		return access;
	}

	/**
	 * @param access
	 *            the access to set
	 */
	public void setAccess(EventAccess access) {
		this.access = access;
	}

	/**
	 * Access the display name property (cover for PROP_DISPLAY_NAME).
	 * 
	 * @return The event's display name property.
	 */
	public String getDisplayName() {
		String name = null;
		if (properties != null) {
			name = properties.getPropertyFormatted(ResourceProperties.PROP_DISPLAY_NAME);
		}
		return name;

	} // getDisplayName

	/**
	 * Access the type (cover for PROP_CALENDAR_TYPE).
	 * 
	 * @return The event's type property.
	 */
	public String getType() {
		String type = null;
		if (properties != null) {
			type = properties.getPropertyFormatted(ResourceProperties.PROP_CALENDAR_TYPE);
		}
		return type;

	} // getType
	/**
	 * @return the startTime
	 */
	public long getStartTime() {
		if (range != null) {
			return range.firstTime().getTime();
		}
		return -1;
	}
	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(long startTime) {
		if (range != null) {
			range.firstTime().setTime(startTime);
		}
	}
	/**
	 * @return the endTime
	 */
	public long getEndTime() {
		if (range != null) {
			return range.lastTime().getTime();
		}
		return -1;
	}
	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(long endTime) {
		if (range != null) {
			range.lastTime().setTime(endTime);
		}
	}
}
