package org.jenkinsci.plugins.spokehubintegration;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

public class CommandControllerTest {
	
	/*
	 * NOTE: the test classes must not extend junit.framework.TestCase class, 
	 * otherwise the tests will fail.
	 * 
	 * NOTE: @Rule public JenkinsRule jenkins = new JenkinsRule(); must be inserted 
	 * in all the test classes, otherwise the test cases will not have access to a 
	 * Jenkins instance. 
	 */
	
	// this variable follow the slash command syntax
	private static final int COMMAND_INDEX = 0;
	
	@Rule 
	public JenkinsRule jenkins = new JenkinsRule();
	private Controller controller;
	private SlackData data;
	
	@Before
	public void setUp() {
		this.controller = CommandController.getInstance();
		this.data = new SlackData();
	}
	
	@After
	public void tearDown() {
		this.controller = null;
		this.data = null;
	}
	
	/**
	 * Tests the performance of the handleData method when it receives correct data from
	 * Slack.
	 */
	@Test
	public void testHandleData1() {
		this.data.setToken("9YKoANNRwOGAHvoPWGzWyPbE");
		this.data.setTeam_id("T0001");
		this.data.setTeam_domain("slackteamprova");
		this.data.setChannel_id("C2147483705");
		this.data.setChannel_name("general");
		this.data.setUser_id("U2147483697");
		this.data.setUser_name("tommyv92");
		this.data.setCommand("/jenkins");
		this.data.setText("help");
		this.data.setResponse_url("https://hooks.slack.com/commands/1234/5678");
		
		Object response = this.controller.handleData(this.data);
		
		assert response instanceof SlackMessage;
	}
	
	/**
	 * Tests the performance of the handleData method when the text field value is empty.
	 */
	@Test
	public void testHandleData2() {
		this.data.setText(new String());
		
		SlackMessage actual = (SlackMessage) this.controller.handleData(this.data);
		
		String message = Messages.commandNotTyped();
		SlackMessage expected = new SlackMessage(message, Messages.danger());
		
		assert expected.equals(actual);		
	}
	
	/**
	 * Tests the performance of the handleData method when the text field contains a 
	 * command that does not exist.
	 */
	@Test
	public void testHandleData3() {
		String text = "version";
		this.data.setText(text);
		
		SlackMessage actual = (SlackMessage) this.controller.handleData(this.data);
		
		String command = text.split(" ")[COMMAND_INDEX];
		String message = Messages.commandNotFound(command);
		SlackMessage expected = new SlackMessage(message, Messages.danger());
		
		assert expected.equals(actual);		
	}
	
	/**
	 * Tests the performance of the handleData method when the text field value is null.
	 */
	@Test
	public void testHandleData4() {
		try {
			this.controller.handleData(this.data);
			
			assert false;
		} catch (NullPointerException e) {
			assert true;
		}
	}
	
	/**
	 * Tests the performance of the handleData method when the data object is null.
	 */
	@Test
	public void testHandleData5() {
		try {
			this.data = null;
			this.controller.handleData(this.data);
			
			assert false;
		} catch (NullPointerException e) {
			assert true;
		}
	}

}
