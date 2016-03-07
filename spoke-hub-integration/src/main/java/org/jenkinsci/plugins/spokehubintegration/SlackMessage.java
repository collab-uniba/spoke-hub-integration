package org.jenkinsci.plugins.spokehubintegration;

/**
 * Class that contains message.
 * 
 * @author Tommaso Montingelli
 *
 */
public class SlackMessage {
	
	/*
	 * NOTE: this class does not respect Java Code Conventions because the variable names 
	 * reflect the names of the fields in the object JSON that will be sent to Slack
	 */
	
	private SlashCommandGlobalConfig slashCommandConfiguration;
	private String text;
	private String color;
	private String response_type;

	/**
	 * Creates a new message.
	 * 
	 * @param text - message text
	 * @param color - border color of the message
	 */
    public SlackMessage(String text, String color) {
    	this.text = text;
    	this.color = color;
        this.slashCommandConfiguration = SlashCommandGlobalConfig.getInstance();
    	if (this.slashCommandConfiguration.getResponseType()) {
        	this.response_type = Messages.inChannel();
		} else {
			this.response_type = Messages.ephemeral();
		}
    }

    /**
     * Gets the message text.
     * 
     * @return message text
     */
	public String getText() {
        return this.text;
    }
    
	/**
	 * Updates the message text.
	 * 
	 * @param text - new message text
	 */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Gets the border color of the message.
     * 
     * @return border color of the message
     */
    public String getColor() {
		return this.color;
	}

    /**
     * Sets the border color of the message.
     * 
     * @param color - new border color of the message
     */
	public void setColor(String color) {
		this.color = color;
	}

	/**
     * Gets the response type.
     * 
     * @return response type
     */
	public String getResponse_type() {
		return this.response_type;
	}

	/**
	 * Updates the response type.
	 * 
	 * @param response_type - response type
	 */
	public void setResponse_type(String response_type) {
		this.response_type = response_type;
	}

}
