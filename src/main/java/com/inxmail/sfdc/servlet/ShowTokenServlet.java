package com.inxmail.sfdc.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.inxmail.sfdc.oauth.OAuthLogin;


/**
 * @author lgf 20.06.2012
 */
@WebServlet( name = "show-token", urlPatterns = { "/show-token" } )
public class ShowTokenServlet extends BaseServlet
{

	protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException,
			IOException
	{
		final OAuthLogin login = getLoginFromSession( request );
		if( null != login )
		{
			final PrintWriter writer = response.getWriter();

			writer.println( "<html><body>" );
			writer.println( "Logged in to org: " + login.getOrgId() );
			writer.println( "<br/>" );
			writer.println( "Instance URL is: " + login.getInstanceUrl() );
			writer.println( "<br/>" );
			writer.println( "With user: " + login.getUserName() + " (" + login.getUserId() + ")" );

			writer.println( "<hr/>" );

			writer.println( "Current access token is: " + login.getAccessToken() );
			writer.println( "<br/>" );
			writer.println( "</html></body>" );
		}
	}

}