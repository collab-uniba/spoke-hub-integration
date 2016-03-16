package org.jenkinsci.plugins.spokehubintegration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.NameValuePair;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.kohsuke.stapler.StaplerResponse;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebRequestSettings;
import com.gargoylesoftware.htmlunit.WebResponse;

public class SlackWebhookEndpointTest {
	
	/*
	 * NOTE: the test classes must not extend junit.framework.TestCase class, 
	 * otherwise the tests will fail.
	 * 
	 * NOTE: @Rule public JenkinsRule jenkins = new JenkinsRule(); must be inserted 
	 * in all the test classes, otherwise the test cases will not have access to a 
	 * Jenkins instance. 
	 */
	
	@Rule 
	public JenkinsRule jenkins = new JenkinsRule();
	private SlashCommandGlobalConfiguration slashCommandConfiguration;
	private JenkinsRule.WebClient client;
	
	@Before
	public void setUp() {
		this.client = this.jenkins.createWebClient();
		this.slashCommandConfiguration = SlashCommandGlobalConfiguration.getInstance();
	}
	
	@After
	public void tearDown() {
		this.client = null;
		this.slashCommandConfiguration = null;
	}
	
	/**
	 * Tests the performance of the doIndex method when it receives a proper HTTP POST
	 * request.
	 */
	@Test
	public void testDoIndex1() {
		try {
			this.slashCommandConfiguration.setSlackSlashCommandToken("9YKoANNRwOGAHvoPWGzWyPbE");
			
			List<NameValuePair> data = new ArrayList<>();
			data.add(new NameValuePair("token", "9YKoANNRwOGAHvoPWGzWyPbE"));
			data.add(new NameValuePair("team_id", "T0001"));
			data.add(new NameValuePair("team_domain", "slackteamprova"));
			data.add(new NameValuePair("channel_id", "C2147483705"));
			data.add(new NameValuePair("channel_name", "general"));
			data.add(new NameValuePair("user_id", "U2147483697"));
			data.add(new NameValuePair("user_name", "tommyv92"));
			data.add(new NameValuePair("command", "/jenkins"));
			data.add(new NameValuePair("text", "help"));
			data.add(new NameValuePair("response_url", "https://hooks.slack.com/commands/1234/5678"));
			
			WebResponse response = makePostRequest(data);
			String content = response.getContentAsString();
			
			assert content.contains(Messages.requestReceived());
			assert content.contains(Messages.good());
			assert response.getStatusCode() == StaplerResponse.SC_OK;
		} catch (IOException e) {
			assert false;
		}
	}
	
	/**
	 * Tests the performance of the doIndex method when Slack sends an HTTP GET request.
	 */
	@Test
	public void testDoIndex2() {
		try {
			WebResponse response = makeGetRequest(null);
			
			assert response.getContentAsString().contains("POST required");
		} catch (IOException e) {
			assert false;
		}
	}
	
	/**
	 * Sends an HTTP POST request.
	 * 
	 * @param data content of the HTTP POST request
	 * @return response to the request
	 * @throws IOException
	 */
	private WebResponse makePostRequest(List<NameValuePair> data) throws IOException {
        WebRequestSettings request = new WebRequestSettings(
        		this.client.createCrumbedUrl("slashCommandWebhook/"), HttpMethod.POST);

        if (data != null)
            request.setRequestParameters(data);

        return this.client.loadWebResponse(request);
    }
	
	/**
	 * Sends an HTTP GET request.
	 * 
	 * @param data content of the HTTP GET request
	 * @return response to the request
	 * @throws IOException
	 */
	private WebResponse makeGetRequest(List<NameValuePair> data) throws IOException {
        WebRequestSettings request = new WebRequestSettings(
        		this.client.createCrumbedUrl("slashCommandWebhook/"), HttpMethod.GET);

        if (data != null)
            request.setRequestParameters(data);

        return this.client.loadWebResponse(request);
    }

}
