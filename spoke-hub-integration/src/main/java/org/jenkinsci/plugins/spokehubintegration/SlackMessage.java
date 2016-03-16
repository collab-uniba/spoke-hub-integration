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
	
	private SlashCommandGlobalConfiguration slashCommandConfiguration;
	private String text;
	private String color;
	private String response_type;

	/**
	 * Creates a new message.
	 * 
	 * @param text message text
	 * @param color border color of the message
	 */
    public SlackMessage(String text, String color) {
    	this.text = text;
    	this.color = color;
        this.slashCommandConfiguration = SlashCommandGlobalConfiguration.getInstance();
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
	 * @param text new message text
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
     * @param color new border color of the message
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
	 * @param response_type response type
	 */
	public void setResponse_type(String response_type) {
		this.response_type = response_type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.color == null) ? 0 : this.color.hashCode());
		result = prime * result
				+ ((this.response_type == null) ? 0 : this.response_type.hashCode());
		result = prime * result + ((this.text == null) ? 0 : this.text.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof SlackMessage))
			return false;
		SlackMessage other = (SlackMessage) obj;
		if (this.color == null) {
			if (other.color != null)
				return false;
		} else if (!this.color.equals(other.color))
			return false;
		if (this.response_type == null) {
			if (other.response_type != null)
				return false;
		} else if (!this.response_type.equals(other.response_type))
			return false;
		if (this.text == null) {
			if (other.text != null)
				return false;
		} else if (!this.text.equals(other.text))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SlackMessage [text=" + this.text + ", color=" + this.color
				+ ", response_type=" + this.response_type + "]";
	}

}
