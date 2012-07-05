package com.inxmail.sfdc.oauth;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.inxmail.sfdc.Sfdc;
import com.inxmail.sfdc.SfdcException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;


/**
 * Service for obtaining {@link OAuthLogin OAuthLogins} with different OAuth2 flows.
 * 
 * @author lgf 21.06.2012
 */
public class OAuthTokenService
{
	private static final Client client = Client.create();

	/**
	 * Salesforce.com OAuth2 token resource.
	 */
	private static final String TOKEN_URL = Sfdc.ENVIRONMENT + "/services/oauth2/token";

	/**
	 * Flow independent form values.
	 */
	private static final MultivaluedMap<String, String> BASE_FORM = new MultivaluedMapImpl();
	static
	{
		BASE_FORM.add( "client_id", OAuthClient.ID );
		BASE_FORM.add( "client_secret", OAuthClient.SECRET );
	}


	/**
	 * Requests a {@link OAuthLogin} in the OAuth <a
	 * href="http://tools.ietf.org/html/draft-ietf-oauth-v2-10#section-4.1.2">OAuth2 user-password flow</a>.
	 * 
	 * @param username The salseforce.com username.
	 * @param password The salseforce.com password.
	 * @return {@link OAuthLogin}
	 * @throws SfdcException if the request fails.
	 */
	public OAuthLogin requestToken( String username, String password )
	{
		MultivaluedMap<String, String> authorizationForm = getBaseForm();
		authorizationForm.add( "grant_type", "password" );
		authorizationForm.add( "username", username );
		authorizationForm.add( "password", password );

		return requestToken( authorizationForm );
	}


	/**
	 * Requests a {@link OAuthLogin} in the OAuth <a
	 * href="http://tools.ietf.org/html/draft-ietf-oauth-v2-10#section-1.4.1">OAuth2 web-server flow</a>.
	 * 
	 * @param authorizationCode The authorization code recieved from salseforce.com
	 * @return {@link OAuthLogin}
	 * @throws SfdcException if the request fails.
	 */
	public OAuthLogin requestToken( String authorizationCode )
	{
		MultivaluedMap<String, String> authorizationForm = getBaseForm();
		authorizationForm.add( "grant_type", "authorization_code" );
		authorizationForm.add( "code", authorizationCode );
		authorizationForm.add( "redirect_uri", OAuthClient.REDIRECT_URI );

		return requestToken( authorizationForm );
	}


	/**
	 * Refresh the {@link OAuthLogin} access-token by using the <a
	 * href="http://tools.ietf.org/html/draft-ietf-oauth-v2-10#section-4.1.4">refresh-token</a>.
	 * 
	 * @param login {@link OAuthLogin} with a expired or empty <code>access-token</code>.
	 * @return a new {@link OAuthLogin} with a fresh <code>access-token</code>.
	 * @throws SfdcException if the request fails.
	 */
	public OAuthLogin refreshToken( OAuthLogin login )
	{
		if( null == login.getRefreshToken() )
			throw new IllegalArgumentException( "refresh-token must be not null!" );

		MultivaluedMap<String, String> refreshForm = getBaseForm();
		refreshForm.add( "grant_type", "refresh_token" );
		refreshForm.add( "refresh_token", login.getRefreshToken() );

		return requestToken( refreshForm );
	}


	/**
	 * Requests a specific OAuth token depending on the given form values.
	 * 
	 * @param form parameters for obtaining a flow specific token.
	 * @return {@link OAuthLogin} for the specific flow
	 * @throws SfdcException if the request fails.
	 */
	private OAuthLogin requestToken( MultivaluedMap<String, String> form )
	{
		WebResource resource = client.resource( TOKEN_URL );
		try
		{
			TokenResponse response = resource.post( TokenResponse.class, form );
			OAuthToken token = new OAuthToken( response.getAccessToken(), response.getRefreshToken() );
			// TODO PERF: fetch only for request not for a subsequent refresh. Or better fetch lazy.
			UserIdentity userIdentity = getUserIdentity( response.getUserIdResource(), token.getAccessToken() );

			return new OAuthLogin( response.getInstanceUrl(), userIdentity, token );
		}
		catch( UniformInterfaceException e )
		{
			throw new SfdcException( "Can't obtain OAuth token!", e );
		}
	}


	/**
	 * Obtains the user informations provided by the salesforce.com <a
	 * href="http://na14.salesforce.com/help/doc/en/remoteaccess_using_openid.htm">identity-service</a>
	 * 
	 * @param userIdResource the user-id resource
	 * @param accessToken a valid accessToken
	 * @return the {@link UserIdentity} for the given resource
	 * @throws SfdcException if the request fails.
	 */
	private UserIdentity getUserIdentity( String userIdResource, String accessToken )
	{
		WebResource resource = client.resource( userIdResource );
		try
		{
			return resource.header( HttpHeaders.AUTHORIZATION, "OAuth " + accessToken ).get( UserIdentity.class );
		}
		catch( UniformInterfaceException e )
		{
			throw new SfdcException( "Can't obtain user-informations!", e );
		}
	}


	/**
	 * @return a defensive copy of BASE_FORM.
	 */
	private MultivaluedMap<String, String> getBaseForm()
	{
		return new MultivaluedMapImpl( BASE_FORM );
	}


	/**
	 * JAXB binding-class for the salesforce.com OAuth2 token response.
	 * 
	 * @author lgf 20.06.2012
	 */
	@XmlRootElement( name = "OAuth" )
	@XmlAccessorType( XmlAccessType.FIELD )
	private static class TokenResponse
	{
		@XmlElement( name = "id" )
		private String userIdResource;

		@XmlElement( name = "instance_url" )
		private String instanceUrl;

		@XmlElement( name = "refresh_token" )
		private String refreshToken;

		@XmlElement( name = "access_token" )
		private String accessToken;


		private String getUserIdResource()
		{
			return userIdResource;
		}


		private String getInstanceUrl()
		{
			return instanceUrl;
		}


		private String getRefreshToken()
		{
			return refreshToken;
		}


		private String getAccessToken()
		{
			return accessToken;
		}

	}

}