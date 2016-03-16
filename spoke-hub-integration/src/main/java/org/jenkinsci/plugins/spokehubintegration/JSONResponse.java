package org.jenkinsci.plugins.spokehubintegration;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * Class that implements {@link HttpResponse} interface.
 * 
 * @author Tommaso Montingelli
 *
 */
public class JSONResponse implements HttpResponse {

	private static final Logger LOGGER = Logger.getLogger(JSONResponse.class.getName());
	private JSONObject json;
    private int status;

    /**
     * Creates an HTTP response.
     * 
     * @param content response content
     * @param status status code of the response
     */
    public JSONResponse(Object content, int status) {
    	this.json = createResponse(content);
        this.status = status;
    }
    
    /**
     * Creates the response to be sent to Slack.
     * 
     * @param content response content
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
    
   /**
    * Sends an HTTP POST response to Slack.
    * 
    * @param response_url URL assigned to your Slash Command
    * @return true if the sending of the message is successfully performed, otherwise false
    */
    public boolean sendResponse(String response_url) {
    	boolean result = true;
    	HttpClient client = new HttpClient();
    	PostMethod post = new PostMethod(response_url);
    	post.addParameter("payload", this.json.toString());
    	post.getParams().setContentCharset("UTF-8");
    	String message;
    	try {
	    	int responseCode = client.executeMethod(post);
	        if(responseCode == HttpStatus.SC_OK) {
	        	message = Messages.sendingSucceeded();
	            LOGGER.log(Level.INFO, message);
	        } else {
	        	String response = post.getResponseBodyAsString();
	        	message = Messages.sendingFailed(response);
	            LOGGER.log(Level.SEVERE, message);
	            result = false;
	        }
		} catch (IOException e) {
			message = Messages.sendingFailed(e.getMessage());
			LOGGER.log(Level.SEVERE, message, e);
			result = false;
		} finally {
            post.releaseConnection();
        }
		
		return result;
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
		if (this.json == null) {
			if (other.json != null)
				return false;
		} else if (!this.json.equals(other.json))
			return false;
		if (this.status != other.status)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "JSONResponse [json=" + this.json + ", status=" + this.status + "]";
	}
	
}
