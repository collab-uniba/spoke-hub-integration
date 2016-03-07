package org.jenkinsci.plugins.spokehubintegration;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import hudson.Extension;
import hudson.util.FormValidation;
import jenkins.model.GlobalConfiguration;

/**
 * This class adds a new section called 'Slack Slash Command Settings' to the system 
 * configuration page extending the {@link GlobalConfiguration} class.
 * 
 * @author Tommaso Montingelli
 *
 */
@Extension
public class SlashCommandGlobalConfig extends GlobalConfiguration {
	
	/*
	 * NOTE: the name of these variables must match the corresponding value @field 
	 * present in config.jelly file
	 */
	
	private String slackSlashCommandToken;
	private boolean responseType;
	
	/**
	 * Loads the configuration data.
	 */
    public SlashCommandGlobalConfig() {
        load();
    }

    /**
     * Gets the slash command token set by the user in the system configuration page.
     * 
     * @return slash command token
     */
    public String getSlackSlashCommandToken() {
        return this.slackSlashCommandToken;
    }

    /**
     * Updates the slash command token.
     * 
     * @param slackSlashCommandToken - new slash command token
     */
    public void setSlackSlashCommandToken(String slackSlashCommandToken) {
        this.slackSlashCommandToken = slackSlashCommandToken;
    }

    /**
     * Gets the response type.
     * 
     * @return response type
     */
    public boolean getResponseType() {
		return this.responseType;
	}

    /**
     * Updates the response type.
     * 
     * @param responseType - new response type
     */
	public void setResponseType(boolean responseType) {
		this.responseType = responseType;
	}

	/**
     * Checks if the slash command token is set.
     * 
     * @param value - value of the slash command token
     * @return ok if the value of slash command token is not null or empty,
     * otherwise warning
     */
    public FormValidation doCheckSlackSlashCommandToken(@QueryParameter String value) {
        if (value == null || value.trim().isEmpty())
            return FormValidation.warning("Please set a Slack Slash Command Token");

        return FormValidation.ok();
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
        req.bindJSON(this, json);
        save();
        
        return true;
    }
    
    /**
     * Gets the global settings of the plugin.
     * 
     * @return global settings of the plugin
     */
    public static SlashCommandGlobalConfig getInstance() {
    	return GlobalConfiguration.all().get(SlashCommandGlobalConfig.class);
    }

}
