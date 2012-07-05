package com.inxmail.sfdc.snippets;

import java.util.Date;

import javax.ws.rs.core.MediaType;

import com.inxmail.sfdc.oauth.OAuthLogin;
import com.inxmail.sfdc.oauth.OAuthTokenService;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;


/**
 * Simple <a href="http://www.salesforce.com/us/developer/docs/api_rest/index.htm">rest-api</a> examples.
 * 
 * @author lgf 20.06.2012
 */
public class RestSnippet
{

	private static final Client client = Client.create();

	private final String restEndpoint;

	private final String token;


	public static void main( String[] args )
	{
		OAuthLogin login = new OAuthTokenService().requestToken( User.NAME, User.PASSWORD );
		RestSnippet snippets = new RestSnippet( login.getRestEndpoint(), login.getAccessToken() );

		System.out.println( "Aborted Campaigns: " + snippets.queryCampaigns() );
		System.out.println( "Campaign update successfull: "
				+ snippets.updateCampaignName( "701d0000000UJlB", "Campaign updated on: " + new Date() ) );
	}


	public RestSnippet( String restEndpoint, String token )
	{
		this.restEndpoint = restEndpoint;
		this.token = token;
	}


	private String queryCampaigns()
	{
		WebResource resource = client.resource( restEndpoint );
		resource = resource.path( "query" );

		final String query = "SELECT Id, Name FROM Campaign c WHERE c.Status = 'Aborted'";
		resource = resource.queryParam( "q", query );

		Builder builder = resource.header( "Authorization", "OAuth " + token );

		return builder.get( String.class );
	}


	private boolean updateCampaignName( String id, String name )
	{
		WebResource resource = client.resource( restEndpoint );
		resource = resource.path( "sobjects/Campaign/" + id );

		// sfdc workaround (PATCH is not support by jersey client)
		resource = resource.queryParam( "_HttpMethod", "PATCH" );

		Builder builder = resource.header( "Authorization", "OAuth " + token );
		builder = builder.type( MediaType.APPLICATION_JSON );

		String update = "{\"Name\"" + ":" + "\"" + name + "\"}";

		ClientResponse response = builder.post( ClientResponse.class, update );

		if( ClientResponse.Status.NO_CONTENT == response.getClientResponseStatus() )
			return true;
		else
			return false;
	}

}