package com.inxmail.sfdc.oauth;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * JAXB binding-class for the user-identity response of the <a
 * href="http://login.salesforce.com/help/doc/en/remoteaccess_using_openid.htm">Identity-Service</a>.
 * 
 * @author lgf 21.06.2012
 */
@XmlRootElement
@XmlAccessorType( XmlAccessType.FIELD )
class UserIdentity
{

	@XmlElement( name = "user_id" )
	private String id;

	@XmlElement( name = "username" )
	private String name;

	@XmlElement( name = "organization_id" )
	private String orgId;

	@XmlElement( name = "urls" )
	private ServiceEndpoints endpoints;


	String getId()
	{
		return id;
	}


	String getName()
	{
		return name;
	}


	String getOrgId()
	{
		return orgId;
	}


	String getPartnerEndpoint()
	{
		return endpoints.getPartner();
	}


	String getRestEndpoint()
	{
		return endpoints.getRest();
	}


	String getBulkEndpoint()
	{
		return endpoints.getBulk();
	}


	/**
	 * JAXB binding-class for the nested urls object in the user-identity response.
	 * 
	 * @author lgf 27.06.2012
	 */
	@XmlAccessorType( XmlAccessType.FIELD )
	private static class ServiceEndpoints
	{
		/**
		 * The used API version used for each endpoints.
		 */
		private static final String API_VERSION = "24.0";

		private static final String VERSION_PLACEHOLDER = "{version}";

		// NOTE: user-identity service doesn't return informations about the bulk endpoint
		private static final String BULK_RESOURCE = "/services/async/" + VERSION_PLACEHOLDER;

		@XmlElement( name = "rest" )
		private String rest;

		@XmlElement( name = "partner" )
		private String partner;


		private String getRest()
		{
			return replaceVersion( rest );
		}


		private String getPartner()
		{
			return replaceVersion( partner );
		}


		private String getBulk()
		{
			return replaceVersion( BULK_RESOURCE );
		}


		private String replaceVersion( String unversioned )
		{
			return unversioned.replace( VERSION_PLACEHOLDER, API_VERSION );
		}
	}

}