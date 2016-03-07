package org.jenkinsci.plugins.spokehubintegration;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Class that implements {@link HttpResponse} interface.
 * 
 * @author Tommaso Montingelli
 *
 */
public class JSONResponse implements HttpResponse {

	private static final Logger LOGGER = Logger.getLogger(JSONResponse.class.getName());
	private String json;
    private int status;

    /**
     * Creates an HTTP response.
     * 
     * @param content - response content
     * @param status - status code of the response
     */
    public JSONResponse(Object content, int status) {
    	this.json = null;
        try {
            this.json = new ObjectMapper().writeValueAsString(createResponse(content));
        } catch (JsonProcessingException e) {
            this.json = new JSONObject().put("text", e.getMessage()).toString();
            LOGGER.log(Level.INFO, null, e);
        }
        this.status = status;
    }
    
    /**
     * Creates the response to be sent to Slack.
     * 
     * @param content - response content
     * @return response to be sent
     */
    private JSONObject createResponse(Object content) {
    	SlackMessage message = (SlackMessage) content;
    	JSONObject attachment = new JSONObject();
    	attachment.put("text", message.getText());
        attachment.put("color", message.getColor());
        JSONArray attachments = new JSONArray();
        attachments.add(attachment);
        
        JSONObject response = new JSONObject();
        response.put("response_type", message.getResponse_type());
        response.put("attachments", attachments);
        
    	return response;
    }

    @Override
    public void generateResponse(StaplerRequest req, StaplerResponse res, Object obj) 
    		throws IOException, ServletException {

        res.setStatus(this.status);
        res.setContentType("application/json");
        res.getWriter().println(this.json);
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.json == null) ? 0 : this.json.hashCode());
		result = prime * result + this.status;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof JSONResponse))
			return false;
		JSONResponse other = (JSONResponse) obj;
		if (json == null) {
			if (other.json != null)
				return false;
		} else if (!json.equals(other.json))
			return false;
		if (status != other.status)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "JSONResponse [json=" + this.json + ", status=" + this.status + "]";
	}
	
}
