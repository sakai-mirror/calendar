/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/calendar/trunk/calendar-tool/tool/src/java/org/s
akaiproject/calendar/tool/CalendarAction.java $
 * $Id: CalendarAction.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
 ***********************************************************************************
 *
 * Copyright (c) 2012 The Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.opensource.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/

/*
 * Modified from code with permission granted by Leidse Onderwijsinstellingen. 
 */

package org.sakaiproject.calendar.entityprovider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sakaiproject.calendar.api.SakaiProxy;
import org.sakaiproject.calendar.api.CalendarEventEntity;

import org.sakaiproject.entitybroker.entityprovider.capabilities.AutoRegisterEntityProvider;
import org.sakaiproject.entitybroker.entityprovider.capabilities.Describeable;
import org.sakaiproject.entitybroker.entityprovider.capabilities.RESTful;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.calendar.api.Calendar;
import org.sakaiproject.calendar.api.CalendarEventEdit;
import org.sakaiproject.calendar.api.CalendarEventVector;
import org.sakaiproject.calendar.api.CalendarService;
import org.sakaiproject.calendar.api.ExternalCalendarSubscriptionService;
import org.sakaiproject.calendar.impl.BaseCalendarService;
import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.entitybroker.EntityReference;
import org.sakaiproject.entitybroker.entityprovider.extension.Formats;
import org.sakaiproject.entitybroker.entityprovider.search.Search;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.util.MergedList;
import org.sakaiproject.util.MergedListEntryProviderBase;
import org.sakaiproject.util.MergedListEntryProviderFixedListWrapper;
import org.sakaiproject.util.StringUtil;

/**
 * The sakai entity used to access calendar events.
 * 
 * @author Tania Tritean, ISDC!
 */
public class CalendarEventEntityProviderImpl implements AutoRegisterEntityProvider, RESTful, Describeable {

	String ENTITY_PREFIX	= "calendar-event";
	
	private static Log M_log = LogFactory.getLog(CalendarEventEntityProviderImpl.class);

	/**
	 * mergedCalendarReferences property.
	 */
	private static final String								MERGED_CALENDARS_PROP			= "mergedCalendarReferences";

	/**
	 * The sakai schedule tool id.
	 */
	private static final String								SAKAI_SCHEDULE_TOOL_ID			= "sakai.schedule";

	/**
	 * The sakai summary calendar tool id.
	 */
	private static final String								SAKAI_SUMMARY_CALENDAR_TOOL_ID	= "sakai.summary.calendar";

	/**
	 * Date format.
	 */
	private static final String								DATE_FORMAT_DD_MM				= "dd-MM";

	/**
	 * Sakai facade.
	 */
	private transient SakaiProxy							sakaiProxy;

	/**
	 * Calendar service.
	 */
	private transient CalendarService						calendarService;

	/**
	 * External calendar service.
	 */
	private transient ExternalCalendarSubscriptionService	externalCalendarSubscriptionService;

	/**
	 * Checks if an entity exists.
	 * 
	 * @param id
	 *            id
	 * @return if entity exists
	 */
	public boolean entityExists(final String id) {
		return false;
	}

	/**
	 * Gets a collection of entities
	 * 
	 * @param ref
	 *            reference
	 * @param search
	 *            search criteria Restriction siteId supported.
	 * @return collection of entities
	 */
	public List<?> getEntities(final EntityReference ref, final Search search) {
		if (this.sakaiProxy.getCurrentUserId() == null) {
			throw new IllegalStateException("No user logged in!");
		}
		return this.readCurrentUserEvents();
	}

	/**
	 * Reads the current user events.
	 * 
	 * @return events
	 */
	private CalendarEventVector readCurrentUserEvents() {
		final CalendarEventVector events = this.calendarService.getEvents(this.getCalendarReferences(),
				this.sakaiProxy.getICalTimeRange());

		CalendarEventVector results = new CalendarEventVector();

		final String directToolEventUrl = this.createDirectTooEventUrl();

		for (final Object object : events) {
			final CalendarEventEdit entity = (CalendarEventEdit) object;

			CalendarEventEntity result = convertToEntityWrapper(directToolEventUrl, entity);
			results.add(result);

		}
		return results;
	}

	/**
	 * Convertor.
	 * 
	 * @param directToolEventUrl
	 *            url
	 * @param entity
	 *            entity
	 * @return converted value
	 */
	private CalendarEventEntity convertToEntityWrapper(final String directToolEventUrl, final CalendarEventEdit entity) {
		CalendarEventEntity result = new CalendarEventEntity();
		result.setAccess(entity.getAccess());
		result.setActive(entity.isActiveEdit());
		result.setAttachments(entity.getAttachments());
		// result.setBaseRange(entity.getRange());
		// result.setEvent(entity.get)
		result.setExclusionRule(entity.getExclusionRule());
		result.setGroups(entity.getGroups());
		String id = entity.getId();
		if (entity.getRecurrenceRule() != null) {
			String[] parts = entity.getId().split("!");
			if (parts != null) {
				id = parts[parts.length - 1];
			}
		}
		result.setId(id);
		result.setProperties(entity.getPropertiesEdit());
		result.setRange(entity.getRange());
		result.setSingleRule(entity.getRecurrenceRule());

		result.getProperties().addProperty(
				"eventStartDate",
				DateFormatUtils.format(new Date(entity.getRange().firstTime().getTime()),
						CalendarEventEntityProviderImpl.DATE_FORMAT_DD_MM));
		result.getProperties().addProperty("eventDirectUrl", directToolEventUrl + entity.getReference());
		return result;
	}

	/**
	 * Creates direct too url and also appends the parameters to access the event which are: 'action=doDescription',
	 * 'panel=Main' and the parameter for event 'eventReference' is only appended, the value mast be added.
	 * 
	 * @return direct url
	 */
	private String createDirectTooEventUrl() {
		final StringBuilder url = new StringBuilder(this.sakaiProxy.getPortalUrl());
		final Site site = this.sakaiProxy.getSite(this.sakaiProxy.getCurrentSiteId());
		final ToolConfiguration fromTool = site
				.getToolForCommonId(CalendarEventEntityProviderImpl.SAKAI_SCHEDULE_TOOL_ID);
		url.append("/directtool/");
		url.append(fromTool.getId());
		url.append("?panel=Main&sakai_action=doDescription&eventReference=");
		return url.toString().replace("/sakai-entitybroker-direct", this.sakaiProxy.getPortalPath());
	}

	/**
	 * Gets the entity event.
	 * 
	 * @param ref
	 *            reference
	 * @return entity
	 */
	public Object getEntity(final EntityReference ref) {
		if (this.sakaiProxy.getCurrentUserId() == null) {
			throw new IllegalStateException("No user logged in!");
		}
		final String id = ref.getId();
		final String[] idParts = id.split(":");
		if (idParts.length != 2) {
			throw new IllegalArgumentException("Id is not ok.");
		}
		final Reference reference = this.sakaiProxy.createReference(idParts[1]);
		reference.set(CalendarService.APPLICATION_ID,
				CalendarService.REF_TYPE_EVENT, idParts[1],
				SiteService.MAIN_CONTAINER, idParts[0]);
		
		final Entity entity = this.calendarService.getEntity(reference);
		/*
		entity.getProperties() .addProperty( "urlInPOrtal", this.sakaiProxy
								.getUrlForTool( CalendarEventEntityProviderImpl.SAKAI_SUMMARY_CALENDAR_TOOL_ID,
										null, null, idParts[0]));
		*/								
		return convertToEntityWrapper(null, (CalendarEventEdit) entity);
	}


	public String createEntity(final EntityReference ref, final Object entity, final Map<String, Object> params) {
		return null;
	}

	public void deleteEntity(final EntityReference ref, final Map<String, Object> params) {

	}

	/**
	 * @return formats
	 */
	public String[] getHandledInputFormats() {
		return new String[] {Formats.JSON };
	}

	/**
	 * @return formats
	 */
	public String[] getHandledOutputFormats() {
		return new String[] {Formats.JSON, Formats.XML, Formats.HTML};
	}

	public Object getSampleEntity() {
		// TODO Auto-generated method stub
		return new CalendarEventEntity();
	}

	/**
	 * TODO: Note this update is FAR from complete, it currently is only able to update dates (based
	 * on long time values)
	 * That was the requirement for the current project. 
	 * More work is needed on this still
	 */
	public void updateEntity(final EntityReference ref, final Object entity, final Map<String, Object> params) {
		// TODO Auto-generated method stub
		if (this.sakaiProxy.getCurrentUserId() == null) {
			throw new IllegalStateException("No user logged in!");
		}
		
		final String id = ref.getId();
		final String[] idParts = id.split(":");
		if (idParts.length != 2) {
			throw new IllegalArgumentException("Id is not ok.");
		}
        if (entity.getClass().isAssignableFrom(CalendarEventEntity.class)) {
            CalendarEventEntity cal  = (CalendarEventEntity) entity;
    		final Reference reference = this.sakaiProxy.createReference(idParts[1]);
    		reference.set(CalendarService.APPLICATION_ID,
    				CalendarService.REF_TYPE_EVENT, idParts[1],
    				SiteService.MAIN_CONTAINER, idParts[0]);
    		
    	    Calendar calendarObj = null;
			CalendarEventEdit edit = null;
    	    try {
    	    	String calId = this.calendarService.calendarReference(reference.getContext(), reference.getContainer());
				calendarObj = this.calendarService.getCalendar(calId);
				edit = calendarObj.getEditEvent(idParts[1], org.sakaiproject.calendar.api.CalendarService.EVENT_MODIFY_CALENDAR);
				//Update everything!
				if (cal.getRange() != null) {
					//Limit the duration to prevent errors, you can't set more than 24 hours in UI anyway
					long maxDuration = 1000*60*60*24;
					M_log.debug(cal.getRange().duration());
					if (cal.getRange().duration() < maxDuration) {
						edit.setRange(cal.getRange());
					}
					else { 
						throw new IllegalStateException("Duration greater than maximum allowed!");
					}
				}
				calendarObj.commitEvent(edit, cal.getIntention());
			} catch (IdUnusedException e) {
				// TODO Auto-generated catch block
				M_log.debug(e);
				throw new IllegalStateException(e.getMessage(), e);
			} catch (PermissionException e) {
				// TODO Auto-generated catch block
				M_log.debug(e);
				throw new IllegalStateException(e.getMessage(), e);
			} catch (InUseException e) {
				// TODO Auto-generated catch block
				M_log.debug(e);
				throw new IllegalStateException(e.getMessage(), e);
			}
    	    finally {
    	    	//Cleanup
    	    	if (edit != null && edit.isActiveEdit()) {
    	    		calendarObj.cancelEvent(edit);
    	    	}
    	    }
        }

	}

	/**
	 * Collects the references for calendar.
	 * 
	 * @return references for calendar.
	 */
	private List getCalendarReferences() {
		// get merged calendars channel refs
		String initMergeList = null;
		final ToolConfiguration tc = this.sakaiProxy.getToolByCommonIdInCurrentSite(SakaiProxy.SCHEDULE_TOOL_ID);
		if (tc != null) {
			initMergeList = tc.getPlacementConfig().getProperty(CalendarEventEntityProviderImpl.MERGED_CALENDARS_PROP);
		}

		// load all calendar channels (either primary or merged calendars)
		final String primaryCalendarReference = this.calendarService.calendarReference(
				this.sakaiProxy.getCurrentSiteId(), SiteService.MAIN_CONTAINER);
		final MergedList mergedCalendarList = this.loadChannels(primaryCalendarReference, initMergeList, null);

		// add external calendar subscriptions
		final List referenceList = mergedCalendarList.getReferenceList();
		final Set subscriptionRefList = getCalendarSubscriptionChannelsForChannels(primaryCalendarReference,
				referenceList);
		referenceList.addAll(subscriptionRefList);

		return referenceList;
	}

	/**
	 * Method without is my workspace check.
	 * 
	 * @param primaryCalendarReference
	 *            reference
	 * @param channels
	 *            channels
	 * @return channels
	 */
	public Set<String> getCalendarSubscriptionChannelsForChannels(String primaryCalendarReference,
			Collection<Object> channels) {
		Set<String> subscriptionChannels = new HashSet<String>();
		Set<String> subscriptionUrlsAdded = new HashSet<String>();

		for (Object channel : channels) {
			Set<String> channelSubscriptions = externalCalendarSubscriptionService
					.getCalendarSubscriptionChannelsForChannel((String) channel);
			for (String channelSub : channelSubscriptions) {
				Reference ref = sakaiProxy.createReference(channelSub);
				if (!subscriptionUrlsAdded.contains(ref.getId())) {
					subscriptionChannels.add(channelSub);
					subscriptionUrlsAdded.add(ref.getId());
				}
			}
		}
		return subscriptionChannels;
	}

	/**
	 ** loadChannels -- load specified primaryCalendarReference or merged calendars if initMergeList is defined
	 **/
	private MergedList loadChannels(final String primaryCalendarReference, final String initMergeList,
			MergedList.EntryProvider entryProvider) {
		final MergedList mergedCalendarList = new MergedList();
		String[] channelArray = null;
		final boolean isOnWorkspaceTab = this.sakaiProxy.isUserSite(this.sakaiProxy.getCurrentSiteId());

		// Figure out the list of channel references that we'll be using.
		// MyWorkspace is special: if not superuser, and not otherwise defined,
		// get all channels
		if (isOnWorkspaceTab && !this.sakaiProxy.isSuperUser() && (initMergeList == null)) {
			channelArray = mergedCalendarList.getAllPermittedChannels(new CalendarChannelReferenceMaker());
		} else {
			channelArray = mergedCalendarList.getChannelReferenceArrayFromDelimitedString(primaryCalendarReference,
					initMergeList);
		}
		if (entryProvider == null) {
			entryProvider = new MergedListEntryProviderFixedListWrapper(new EntryProvider(), primaryCalendarReference,
					channelArray, new CalendarReferenceToChannelConverter());
		}
		mergedCalendarList.loadChannelsFromDelimitedString(isOnWorkspaceTab, false, entryProvider,
				StringUtil.trimToZero(this.sakaiProxy.getCurrentSessionUserId()), channelArray,
				this.sakaiProxy.isSuperUser(), this.sakaiProxy.getCurrentSiteId());

		return mergedCalendarList;
	}

	/**
	 * @return prefix
	 */
	public String getEntityPrefix() {
		return ENTITY_PREFIX;
	}

	/**
	 * @param sakaiProxy
	 *            the sakaiProxy to set
	 */
	public void setSakaiProxy(final SakaiProxy sakaiProxy) {
		this.sakaiProxy = sakaiProxy;
	}

	/**
	 * @param calendarService
	 *            the calendarService to set
	 */
	public void setCalendarService(final CalendarService calendarService) {
		this.calendarService = calendarService;
	}

	/**
	 * @param externalCalendarSubscriptionService
	 *            the externalCalendarSubscriptionService to set
	 */
	public void setExternalCalendarSubscriptionService(
			final ExternalCalendarSubscriptionService externalCalendarSubscriptionService) {
		this.externalCalendarSubscriptionService = externalCalendarSubscriptionService;
	}

	/*
	 * Callback class so that we can form references in a generic way.
	 */
	private final class CalendarChannelReferenceMaker implements MergedList.ChannelReferenceMaker {

		public String makeReference(final String siteId) {
			return CalendarEventEntityProviderImpl.this.calendarService.calendarReference(siteId,
					SiteService.MAIN_CONTAINER);
		}
	}

	/**
	 * Provides a list of merged calendars by iterating through all available calendars.
	 */
	private final class EntryProvider extends MergedListEntryProviderBase {

		/** calendar channels from hidden sites */
		private final List	excludedSites	= new ArrayList();

		public EntryProvider() {
			this(false);
		}

		public EntryProvider(final boolean excludeHiddenSites) {
			if (excludeHiddenSites) {
				final List<String> excludedSiteIds = CalendarEventEntityProviderImpl.this.sakaiProxy
						.getExcludedSitesFromTabs();
				if (excludedSiteIds != null) {
					for (final String siteId : excludedSiteIds) {
						this.excludedSites.add(CalendarEventEntityProviderImpl.this.calendarService.calendarReference(
								siteId, SiteService.MAIN_CONTAINER));
					}
				}
			}
		}

		/*
		 * (non-Javadoc)
		 * @see org.sakaiproject.util.MergedListEntryProviderBase#makeReference(java .lang.String)
		 */
		@Override
		public Object makeObjectFromSiteId(final String id) {
			final String calendarReference = CalendarEventEntityProviderImpl.this.calendarService.calendarReference(id,
					SiteService.MAIN_CONTAINER);
			Object calendar = null;

			if (calendarReference != null) {
				try {
					calendar = CalendarEventEntityProviderImpl.this.calendarService.getCalendar(calendarReference);
				} catch (final IdUnusedException e) {
					// The channel isn't there.
				} catch (final PermissionException e) {
					// We can't see the channel
				}
			}

			return calendar;
		}

		/*
		 * (non-Javadoc)
		 * @see org.chefproject.actions.MergedEntryList.EntryProvider#allowGet(java .lang.Object)
		 */
		public boolean allowGet(final String ref) {
			return !this.excludedSites.contains(ref)
					&& CalendarEventEntityProviderImpl.this.calendarService.allowGetCalendar(ref);
		}

		/*
		 * (non-Javadoc)
		 * @see org.chefproject.actions.MergedEntryList.EntryProvider#getContext( java.lang.Object)
		 */
		public String getContext(final Object obj) {
			if (obj == null) {
				return "";
			}

			final org.sakaiproject.calendar.api.Calendar calendar = (org.sakaiproject.calendar.api.Calendar) obj;
			return calendar.getContext();
		}

		/*
		 * (non-Javadoc)
		 * @see org.chefproject.actions.MergedEntryList.EntryProvider#getReference (java.lang.Object)
		 */
		public String getReference(final Object obj) {
			if (obj == null) {
				return "";
			}

			final org.sakaiproject.calendar.api.Calendar calendar = (org.sakaiproject.calendar.api.Calendar) obj;
			return calendar.getReference();
		}

		/*
		 * (non-Javadoc)
		 * @see org.chefproject.actions.MergedEntryList.EntryProvider#getProperties (java.lang.Object)
		 */
		public ResourceProperties getProperties(final Object obj) {
			if (obj == null) {
				return null;
			}

			final org.sakaiproject.calendar.api.Calendar calendar = (org.sakaiproject.calendar.api.Calendar) obj;
			return calendar.getProperties();
		}
	}

	/**
	 * Used by callback to convert channel references to channels.
	 */
	private final class CalendarReferenceToChannelConverter implements
			MergedListEntryProviderFixedListWrapper.ReferenceToChannelConverter {

		public Object getChannel(final String channelReference) {
			try {
				return CalendarEventEntityProviderImpl.this.calendarService.getCalendar(channelReference);
			} catch (final IdUnusedException e) {
				return null;
			} catch (final PermissionException e) {
				return null;
			}
		}
	}

}
