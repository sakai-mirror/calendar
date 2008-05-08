package org.sakaiproject.calendar.caldav.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;

import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.entity.api.EntityPropertyNotDefinedException;
import org.sakaiproject.entity.api.EntityPropertyTypeException;
import org.sakaiproject.entity.api.HttpAccess;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.time.api.Time;
import org.sakaiproject.user.api.Preferences;
import org.sakaiproject.user.api.PreferencesEdit;
import org.sakaiproject.user.api.PreferencesService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.ContentHandler;

public class StubPreferencesService implements PreferencesService {
	
	class StubPreferences implements Preferences
	{

		public String getId() {
			// TODO Auto-generated method stub
			return null;
		}

		public Collection getKeys() {
			// TODO Auto-generated method stub
			return null;
		}

		public ResourceProperties getProperties(String key) {
			return new ResourceProperties()
			{

				public void addAll(ResourceProperties other) {
					// TODO Auto-generated method stub
					
				}

				public void addAll(Properties props) {
					// TODO Auto-generated method stub
					
				}

				public void addProperty(String name, String value) {
					// TODO Auto-generated method stub
					
				}

				public void addPropertyToList(String name, String value) {
					// TODO Auto-generated method stub
					
				}

				public void clear() {
					// TODO Auto-generated method stub
					
				}

				public Object get(String name) {
					// TODO Auto-generated method stub
					return null;
				}

				public boolean getBooleanProperty(String name)
						throws EntityPropertyNotDefinedException,
						EntityPropertyTypeException {
					// TODO Auto-generated method stub
					return false;
				}

				public ContentHandler getContentHander() {
					// TODO Auto-generated method stub
					return null;
				}

				public long getLongProperty(String name)
						throws EntityPropertyNotDefinedException,
						EntityPropertyTypeException {
					// TODO Auto-generated method stub
					return 0;
				}

				public String getNamePropAssignmentDeleted() {
					// TODO Auto-generated method stub
					return null;
				}

				public String getNamePropCalendarLocation() {
					// TODO Auto-generated method stub
					return null;
				}

				public String getNamePropCalendarType() {
					// TODO Auto-generated method stub
					return null;
				}

				public String getNamePropChatRoom() {
					// TODO Auto-generated method stub
					return null;
				}

				public String getNamePropCollectionBodyQuota() {
					// TODO Auto-generated method stub
					return null;
				}

				public String getNamePropContentLength() {
					// TODO Auto-generated method stub
					return null;
				}

				public String getNamePropContentType() {
					// TODO Auto-generated method stub
					return null;
				}

				public String getNamePropCopyright() {
					// TODO Auto-generated method stub
					return null;
				}

				public String getNamePropCopyrightAlert() {
					// TODO Auto-generated method stub
					return null;
				}

				public String getNamePropCopyrightChoice() {
					// TODO Auto-generated method stub
					return null;
				}

				public String getNamePropCreationDate() {
					// TODO Auto-generated method stub
					return null;
				}

				public String getNamePropCreator() {
					// TODO Auto-generated method stub
					return null;
				}

				public String getNamePropDescription() {
					// TODO Auto-generated method stub
					return null;
				}

				public String getNamePropDisplayName() {
					// TODO Auto-generated method stub
					return null;
				}

				public String getNamePropIsCollection() {
					// TODO Auto-generated method stub
					return null;
				}

				public String getNamePropModifiedBy() {
					// TODO Auto-generated method stub
					return null;
				}

				public String getNamePropModifiedDate() {
					// TODO Auto-generated method stub
					return null;
				}

				public String getNamePropNewAssignmentCheckAddDueDate() {
					// TODO Auto-generated method stub
					return null;
				}

				public String getNamePropNewAssignmentCheckAutoAnnounce() {
					// TODO Auto-generated method stub
					return null;
				}

				public String getNamePropReplyStyle() {
					// TODO Auto-generated method stub
					return null;
				}

				public String getNamePropStructObjType() {
					// TODO Auto-generated method stub
					return null;
				}

				public String getNamePropSubmissionPreviousFeedbackComment() {
					// TODO Auto-generated method stub
					return null;
				}

				public String getNamePropSubmissionPreviousFeedbackText() {
					// TODO Auto-generated method stub
					return null;
				}

				public String getNamePropSubmissionPreviousGrades() {
					// TODO Auto-generated method stub
					return null;
				}

				public String getNamePropSubmissionScaledPreviousGrades() {
					// TODO Auto-generated method stub
					return null;
				}

				public String getNamePropTo() {
					// TODO Auto-generated method stub
					return null;
				}

				public String getProperty(String name) {
					// TODO Auto-generated method stub
					return null;
				}

				public String getPropertyFormatted(String name) {
					// TODO Auto-generated method stub
					return null;
				}

				public List getPropertyList(String name) {
					// TODO Auto-generated method stub
					return null;
				}

				public Iterator getPropertyNames() {
					// TODO Auto-generated method stub
					return null;
				}

				public Time getTimeProperty(String name)
						throws EntityPropertyNotDefinedException,
						EntityPropertyTypeException {
					// TODO Auto-generated method stub
					return null;
				}

				public String getTypeUrl() {
					// TODO Auto-generated method stub
					return null;
				}

				public boolean isLiveProperty(String name) {
					// TODO Auto-generated method stub
					return false;
				}

				public void removeProperty(String name) {
					// TODO Auto-generated method stub
					
				}

				public void set(ResourceProperties other) {
					// TODO Auto-generated method stub
					
				}

				public Element toXml(Document doc, Stack stack) {
					// TODO Auto-generated method stub
					return null;
				}
			};
			}

		public ResourceProperties getProperties() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getReference() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getReference(String rootProperty) {
			// TODO Auto-generated method stub
			return null;
		}

		public String getUrl() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getUrl(String rootProperty) {
			// TODO Auto-generated method stub
			return null;
		}

		public Element toXml(Document doc, Stack stack) {
			// TODO Auto-generated method stub
			return null;
		}

		public int compareTo(Object o) {
			// TODO Auto-generated method stub
			return 0;
		}
		}

		public ResourceProperties getProperties() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getReference() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getReference(String rootProperty) {
			// TODO Auto-generated method stub
			return null;
		}

		public String getUrl() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getUrl(String rootProperty) {
			// TODO Auto-generated method stub
			return null;
		}

		public Element toXml(Document doc, Stack stack) {
			// TODO Auto-generated method stub
			return null;
		}

		public int compareTo(Object o) {
			// TODO Auto-generated method stub
			return 0;
		}
		

	public PreferencesEdit add(String id) throws PermissionException,
			IdUsedException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean allowUpdate(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	public void cancel(PreferencesEdit edit) {
		// TODO Auto-generated method stub

	}

	public void commit(PreferencesEdit edit) {
		// TODO Auto-generated method stub

	}

	public PreferencesEdit edit(String id) throws PermissionException,
			InUseException, IdUnusedException {
		// TODO Auto-generated method stub
		return null;
	}

	public Preferences getPreferences(String id) {
		return new StubPreferences();
	}

	public void remove(PreferencesEdit edit) {
		// TODO Auto-generated method stub

	}

	public String archive(String siteId, Document doc, Stack stack,
			String archivePath, List attachments) {
		// TODO Auto-generated method stub
		return null;
	}

	public Entity getEntity(Reference ref) {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection getEntityAuthzGroups(Reference ref, String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getEntityDescription(Reference ref) {
		// TODO Auto-generated method stub
		return null;
	}

	public ResourceProperties getEntityResourceProperties(Reference ref) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getEntityUrl(Reference ref) {
		// TODO Auto-generated method stub
		return null;
	}

	public HttpAccess getHttpAccess() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	public String merge(String siteId, Element root, String archivePath,
			String fromSiteId, Map attachmentNames, Map userIdTrans,
			Set userListAllowImport) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean parseEntityReference(String reference, Reference ref) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean willArchiveMerge() {
		// TODO Auto-generated method stub
		return false;
	}

}
