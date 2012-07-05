package com.inxmail.sfdc.oauth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.inxmail.sfdc.Sfdc;
import com.inxmail.sfdc.servlet.BaseServlet;


/**
 * Implements the application part in the <a
 * href="http://tools.ietf.org/html/draft-ietf-oauth-v2-10#section-1.4.1">OAuth2 web-server-flow</a>.
 * 
 * @author lgf 20.06.2012
 */
@WebServlet( name = "oauth", urlPatterns = { "/oauth", "/oauth/_callback" } )
public class WebServerFlowServlet extends BaseServlet
{

	private static final String AUTH_URL = Sfdc.ENVIRONMENT
			+ "/services/oauth2/authorize?response_type=code&client_id=" + OAuthClient.ID + "&redirect_uri="
			+ OAuthClient.REDIRECT_URI;


	@Override
	protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws	IOException
	{
		final OAuthLogin login = getLoginFromSession( request );

		if( null == login )
		{
			if( request.getRequestURI().endsWith( "oauth" ) )
			{
				// redirect the user to the authorization endpoint
				response.sendRedirect( AUTH_URL );
				return;
			}
			else
			{
				// callback after successful authentication from the authorization server
				String authorizationCode = request.getParameter( "code" );

				OAuthLogin oAuthLogin = new OAuthTokenService().requestToken( authorizationCode );

				storeLoginInSession( request, oAuthLogin );
			}
		}

		response.sendRedirect( request.getContextPath() + "/show-token" );
	}
}