package org.jenkinsci.plugins.spokehubintegration;

/**
 * This class encapsulates the data sent by Slack using an HTTP POST request.
 * 
 * @author Tommaso Montingelli
 *
 */
public class SlackData {
	
	/*
	 * NOTE: this class does not respect Java Code Conventions because the variable names 
	 * reflect the names of the fields in the object JSON posted by Slack and are used to 
	 * bind each variable to the corresponding value
	 */
	
	private String token;
    private String team_id;
    private String team_domain;
    private String channel_id;
    private String channel_name;
    private String user_id;
    private String user_name;
    private String command;
    private String text;
    private String response_url;
    
    /**
     * Gets the slash command token.
     * 
     * @return slash command token
     */
	public String getToken() {
		return this.token;
	}
	
	/**
	 * Updates the slash command token.
	 * 
	 * @param token new slash command token
	 */
	public void setToken(String token) {
		this.token = token;
	}
	
	/**
	 * Gets the team id.
	 * 
	 * @return team id
	 */
	public String getTeam_id() {
		return this.team_id;
	}
	
	/**
	 * Updates the team id.
	 * 
	 * @param team_id new team id
	 */
	public void setTeam_id(String team_id) {
		this.team_id = team_id;
	}
	
	/**
	 * Gets the team domain.
	 * 
	 * @return team domain
	 */
	public String getTeam_domain() {
		return this.team_domain;
	}
	
	/**
	 * Updates the team domain.
	 * 
	 * @param team_domain new team domain
	 */
	public void setTeam_domain(String team_domain) {
		this.team_domain = team_domain;
	}
	
	/**
	 * Gets the channel id.
	 * 
	 * @return channel id
	 */
	public String getChannel_id() {
		return this.channel_id;
	}
	
	/**
	 * Updates the channel id.
	 * 
	 * @param channel_id new channel id
	 */
	public void setChannel_id(String channel_id) {
		this.channel_id = channel_id;
	}
	
	/**
	 * Gets the channel name.
	 * 
	 * @return channel name
	 */
	public String getChannel_name() {
		return this.channel_name;
	}
	
	/**
	 * Updates the channel name.
	 * 
	 * @param channel_name new channel name
	 */
	public void setChannel_name(String channel_name) {
		this.channel_name = channel_name;
	}
	
	/**
	 * Gets the user id.
	 * 
	 * @return user id
	 */
	public String getUser_id() {
		return this.user_id;
	}
	
	/**
	 * Updates the user id.
	 * 
	 * @param user_id new user id
	 */
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	
	/**
	 * Gets the username.
	 * 
	 * @return username
	 */
	public String getUser_name() {
		return this.user_name;
	}
	
	/**
	 * Updates the username.
	 * 
	 * @param user_name new username
	 */
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	
	/**
	 * Gets the slash command.
	 * 
	 * @return slash command
	 */
	public String getCommand() {
		return this.command;
	}
	
	/**
	 * Updates the slash command.
	 * 
	 * @param command new slash command
	 */
	public void setCommand(String command) {
		this.command = command;
	}
	
	/**
	 * Gets the message text that contains the command arguments.
	 * 
	 * @return message text
	 */
	public String getText() {
		return this.text;
	}
	
	/**
	 * Updates the message text.
	 * 
	 * @param text new message text
	 */
	public void setText(String text) {
		this.text = text;
	}
	
	/**
	 * Gets the response URL.
	 * 
	 * @return response URL
	 */
	public String getResponse_url() {
		return this.response_url;
	}
	
	/**
	 * Updates the response URL.
	 * 
	 * @param response_url new response URL
	 */
	public void setResponse_url(String response_url) {
		this.response_url = response_url;
	}
	
}
