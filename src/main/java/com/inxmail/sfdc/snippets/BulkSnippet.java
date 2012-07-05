package com.inxmail.sfdc.snippets;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.MediaType;

import com.inxmail.sfdc.oauth.OAuthLogin;
import com.inxmail.sfdc.oauth.OAuthTokenService;
import com.sforce.ws.ConnectionException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;


/**
 * Simple <a href="http://www.salesforce.com/us/developer/docs/api_asynch/index.htm">rest-api</a> example.
 * 
 * @author lgf 20.06.2012
 */
public class BulkSnippet
{
	private static final String UPDATE_CAMPAIGN_JOB = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<jobInfo xmlns=\"http://www.force.com/2009/06/asyncapi/dataload\">" + "<operation>update</operation>"
			+ "<object>Campaign</object>" + "<contentType>CSV</contentType>" + "</jobInfo>";

	private static final String CLOSE_JOB = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<jobInfo xmlns=\"http://www.force.com/2009/06/asyncapi/dataload\">" + "<state>Closed</state>"
			+ "</jobInfo>";

	private String bulkEndpoint;

	private String accessToken;


	public static void main( String[] args ) throws Exception
	{
		OAuthLogin login = new OAuthTokenService().requestToken( User.NAME, User.PASSWORD );

		BulkSnippet snippets = new BulkSnippet( login.getBulkEndpoint(), login.getAccessToken() );
		System.out.println( "Campaign update BULK-Job completed: "
				+ snippets.updateCampaignName( "701d0000000UJlB", "Campaign updated on: " + new Date() ) );
	}


	public BulkSnippet( String bulkEndpoint, String accessToken )
	{
		this.bulkEndpoint = bulkEndpoint;
		this.accessToken = accessToken;
	}


	private String updateCampaignName( String id, String name ) throws ConnectionException, InterruptedException
	{
		Client client = Client.create();

		// create job
		final WebResource job = client.resource( bulkEndpoint ).path( "job" );
		Builder createJobBuilder = job.header( "X-SFDC-Session", accessToken );
		createJobBuilder = createJobBuilder.type( MediaType.APPLICATION_XML );
		String jobResp = createJobBuilder.post( String.class, UPDATE_CAMPAIGN_JOB );
		String jobId = extractIdElement( jobResp );

		// create batch
		WebResource batch = job.path( jobId ).path( "batch" );
		Builder batchBuilder = batch.header( "X-SFDC-Session", accessToken );
		batchBuilder = batchBuilder.type( "text/csv" );

		// csv batch content
		String batchContent = "Id,Name\n" + id + "," + name + "\n";
		batchBuilder.post( String.class, batchContent );

		// close job
		WebResource closeJob = job.path( jobId );
		Builder closeJobBuilder = closeJob.header( "X-SFDC-Session", accessToken );
		closeJobBuilder = closeJobBuilder.type( MediaType.APPLICATION_XML );
		closeJobBuilder.post( String.class, CLOSE_JOB );

		// await completion
		WebResource jobStatus = job.path( jobId );
		jobStatus = jobStatus.path( "batch" );
		Builder jobStatusBuilder = jobStatus.header( "X-SFDC-Session", accessToken );

		boolean complete = false;
		while( !complete )
		{
			String stateResp = jobStatusBuilder.get( String.class );
			for( String state : extractStateElements( stateResp ) )
			{
				if( !"Completed".equals( state ) )
				{
					Thread.sleep( 3000 );
					continue;
				}
			}
			break;
		}

		return jobId;
	}

	private static final Pattern ID_PATTERN = Pattern.compile( "<id>(.*)</id>" );


	private String extractIdElement( String xml )
	{
		Matcher matcher = ID_PATTERN.matcher( xml );
		matcher.find();
		return matcher.group( 1 );
	}

	private static final Pattern STATE_PATTERN = Pattern.compile( "<state>(.*)</state>" );


	private List<String> extractStateElements( String xml )
	{
		List<String> states = new ArrayList<>();

		Matcher matcher = STATE_PATTERN.matcher( xml );
		while( matcher.find() )
			states.add( matcher.group( 1 ) );

		return states;
	}
}