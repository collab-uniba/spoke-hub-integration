package org.jenkinsci.plugins.spokehubintegration.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.security.Principal;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;
import org.kohsuke.stapler.Ancestor;
import org.kohsuke.stapler.BindInterceptor;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.WebApp;
import org.kohsuke.stapler.bind.BoundObjectTable;
import org.kohsuke.stapler.lang.Klass;

/**
 * Class that implements {@link StaplerRequest} interface and it is used to perform 
 * build parameterized.
 * 
 * @author Tommaso Montingelli
 *
 */
public class ParameterizedStaplerRequest implements StaplerRequest {
	
	private String value;

	/**
	 * Stores the value entered by the user for the specified parameter.
	 * 
	 * @param value parameter value
	 */
	public ParameterizedStaplerRequest(String value) {
		this.value = value;
	}

	@Override
	public String getAuthType() {
		return null;
	}

	@Override
	public String getContextPath() {
		return null;
	}

	@Override
	public Cookie[] getCookies() {
		return new Cookie[0];
	}

	@Override
	public long getDateHeader(String arg0) {
		return 0;
	}

	@Override
	public String getHeader(String arg0) {
		return null;
	}

	@Override
	public Enumeration<?> getHeaderNames() {
		return null;
	}

	@Override
	public Enumeration<?> getHeaders(String arg0) {
		return null;
	}

	@Override
	public int getIntHeader(String arg0) {
		return 0;
	}

	@Override
	public String getMethod() {
		return null;
	}

	@Override
	public String getPathInfo() {
		return null;
	}

	@Override
	public String getPathTranslated() {
		return null;
	}

	@Override
	public String getQueryString() {
		return null;
	}

	@Override
	public String getRemoteUser() {
		return null;
	}

	@Override
	public String getRequestURI() {
		return null;
	}

	@Override
	public StringBuffer getRequestURL() {
		return null;
	}

	@Override
	public String getRequestedSessionId() {
		return null;
	}

	@Override
	public String getServletPath() {
		return null;
	}

	@Override
	public HttpSession getSession() {
		return null;
	}

	@Override
	public HttpSession getSession(boolean arg0) {
		return null;
	}

	@Override
	public Principal getUserPrincipal() {
		return null;
	}

	@Override
	public boolean isRequestedSessionIdFromCookie() {
		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromURL() {
		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromUrl() {
		return false;
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		return false;
	}

	@Override
	public boolean isUserInRole(String arg0) {
		return false;
	}

	@Override
	public Object getAttribute(String arg0) {
		return null;
	}

	@Override
	public Enumeration<?> getAttributeNames() {
		return null;
	}

	@Override
	public String getCharacterEncoding() {
		return null;
	}

	@Override
	public int getContentLength() {
		return 0;
	}

	@Override
	public String getContentType() {
		return null;
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		return null;
	}

	@Override
	public String getLocalAddr() {
		return null;
	}

	@Override
	public String getLocalName() {
		return null;
	}

	@Override
	public int getLocalPort() {
		return 0;
	}

	@Override
	public Locale getLocale() {
		return null;
	}

	@Override
	public Enumeration<?> getLocales() {
		return null;
	}

	@Override
	public String getParameter(String arg0) {
		return this.value;
	}

	@Override
	public Map<?, ?> getParameterMap() {
		return null;
	}

	@Override
	public Enumeration<?> getParameterNames() {
		return null;
	}

	@Override
	public String[] getParameterValues(String arg0) {
		return new String[] {this.value};
	}

	@Override
	public String getProtocol() {
		return null;
	}

	@Override
	public BufferedReader getReader() throws IOException {
		return null;
	}

	@Override
	public String getRealPath(String arg0) {
		return null;
	}

	@Override
	public String getRemoteAddr() {
		return null;
	}

	@Override
	public String getRemoteHost() {
		return null;
	}

	@Override
	public int getRemotePort() {
		return 0;
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String arg0) {
		return null;
	}

	@Override
	public String getScheme() {
		return null;
	}

	@Override
	public String getServerName() {
		return null;
	}

	@Override
	public int getServerPort() {
		return 0;
	}

	@Override
	public boolean isSecure() {
		return false;
	}

	@Override
	public void removeAttribute(String arg0) {
		// I do not need to use this method
	}

	@Override
	public void setAttribute(String arg0, Object arg1) {
		// I do not need to use this method
	}

	@Override
	public void setCharacterEncoding(String arg0) throws UnsupportedEncodingException {
		// I do not need to use this method
	}

	@Override
	public Stapler getStapler() {
		return null;
	}

	@Override
	public WebApp getWebApp() {
		return null;
	}

	@Override
	public String getRestOfPath() {
		return null;
	}

	@Override
	public String getOriginalRestOfPath() {
		return null;
	}

	@Override
	public ServletContext getServletContext() {
		return null;
	}

	@Override
	public String getRequestURIWithQueryString() {
		return null;
	}

	@Override
	public StringBuffer getRequestURLWithQueryString() {
		return null;
	}

	@Override
	public RequestDispatcher getView(Object it, String viewName) throws IOException {
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public RequestDispatcher getView(Class clazz, String viewName) throws IOException {
		return null;
	}

	@Override
	public RequestDispatcher getView(Klass<?> clazz, String viewName) throws IOException {
		return null;
	}

	@Override
	public String getRootPath() {
		return null;
	}

	@Override
	public String getReferer() {
		return null;
	}

	@Override
	public List<Ancestor> getAncestors() {
		return Collections.emptyList();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Ancestor findAncestor(Class type) {
		return null;
	}

	@Override
	public <T> T findAncestorObject(Class<T> type) {
		return null;
	}

	@Override
	public Ancestor findAncestor(Object o) {
		return null;
	}

	@Override
	public boolean hasParameter(String name) {
		return false;
	}

	@Override
	public String getOriginalRequestURI() {
		return null;
	}

	@Override
	public boolean checkIfModified(long timestampOfResource, StaplerResponse rsp) {
		return false;
	}

	@Override
	public boolean checkIfModified(Date timestampOfResource, StaplerResponse rsp) {
		return false;
	}

	@Override
	public boolean checkIfModified(Calendar timestampOfResource, StaplerResponse rsp) {
		return false;
	}

	@Override
	public boolean checkIfModified(long timestampOfResource, StaplerResponse rsp, long expiration) {
		return false;
	}

	@Override
	public void bindParameters(Object bean) {
		// I do not need to use this method
	}

	@Override
	public void bindParameters(Object bean, String prefix) {
		// I do not need to use this method
	}

	@Override
	public <T> List<T> bindParametersToList(Class<T> type, String prefix) {
		return Collections.emptyList();
	}

	@Override
	public <T> T bindParameters(Class<T> type, String prefix) {
		return null;
	}

	@Override
	public <T> T bindParameters(Class<T> type, String prefix, int index) {
		return null;
	}

	@Override
	public <T> T bindJSON(Class<T> type, JSONObject src) {
		return null;
	}

	@Override
	public <T> T bindJSON(Type genericType, Class<T> erasure, Object json) {
		return null;
	}

	@Override
	public void bindJSON(Object bean, JSONObject src) {
		// I do not need to use this method
	}

	@Override
	public <T> List<T> bindJSONToList(Class<T> type, Object src) {
		return Collections.emptyList();
	}

	@Override
	public BindInterceptor getBindInterceptor() {
		return null;
	}

	@Override
	public BindInterceptor setBindListener(BindInterceptor bindListener) {
		return null;
	}

	@Override
	public BindInterceptor setBindInterceptpr(BindInterceptor bindListener) {
		return null;
	}

	@Override
	public JSONObject getSubmittedForm() throws ServletException {
		return null;
	}

	@Override
	public FileItem getFileItem(String name) throws ServletException, IOException {
		return null;
	}

	@Override
	public boolean isJavaScriptProxyCall() {
		return false;
	}

	@Override
	public BoundObjectTable getBoundObjectTable() {
		return null;
	}

	@Override
	public String createJavaScriptProxy(Object toBeExported) {
		return null;
	}

}
