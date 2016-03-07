package org.jenkinsci.plugins.spokehubintegration.command;

import java.io.IOException;

import hudson.matrix.MatrixProject;
import hudson.maven.MavenModuleSet;
import hudson.maven.MavenModuleSetBuild;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.ParametersDefinitionProperty;
import hudson.model.StringParameterDefinition;
import hudson.tasks.Shell;

import org.apache.commons.io.FileUtils;
import org.jenkinsci.plugins.spokehubintegration.JSONResponse;
import org.jenkinsci.plugins.spokehubintegration.SlackData;
import org.jenkinsci.plugins.spokehubintegration.SlackMessage;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.kohsuke.stapler.StaplerResponse;

public class JenkinsReceiverTest {
	
	/*
	 * NOTE: the test classes must not extend junit.framework.TestCase class, 
	 * otherwise the tests will fail.
	 * 
	 * NOTE: @Rule public JenkinsRule jenkins = new JenkinsRule(); must be inserted 
	 * in all the test classes, otherwise the test cases will not have access to a 
	 * Jenkins instance. 
	 */
	
	// these variables follow the slash command syntax
	private static final int COMMAND_INDEX = 0;
	private static final int JOB_PARAMETER_INDEX = 1;
	private static final int COMMAND_PARAMETER_INDEX = 1;
	private static final int SCOPE_PARAMETER_INDEX = 2;
	
	@Rule 
	public JenkinsRule jenkins = new JenkinsRule();
	private JenkinsReceiver receiver;
	
	@Before
	public void setUp() {
		this.receiver = new JenkinsReceiver();
	}
	
	@After
	public void tearDown() {
		this.receiver = null;
	}
	
	/**
	 * Tests the performance of the build method when the user types the slash command
	 * "/jenkins build JOB" where JOB is the name of an existing project not parameterized.
	 */
	@Test
	public void testBuild1() {
		try {
			FreeStyleProject freestyle = this.jenkins.createFreeStyleProject("Free-style");
			
			SlackData data = new SlackData();
			String text = "build Free-style";
			data.setText(text);
			String user_name = "tommyv92";
			data.setUser_name(user_name);
			JSONResponse actual = this.receiver.build(data);
			
			String projectName = text.split(" ")[JOB_PARAMETER_INDEX];
			String message = Messages.buildScheduled(projectName);
			JSONResponse expected = new JSONResponse(new SlackMessage(message, 
					Messages.good()), StaplerResponse.SC_OK);
			
			while (freestyle.isInQueue());
			while (freestyle.isBuilding());
			FreeStyleBuild build = freestyle.getLastBuild();
			String log = FileUtils.readFileToString(build.getLogFile());
			
			assert expected.equals(actual);
			assert log.contains(Messages.slackCause(user_name));
		} catch (IOException e) {
			assert false;
		}
	}
	
	/**
	 * Tests the performance of the build method when the user types the slash command
	 * "/jenkins build JOB -p KEY=VALUE" where JOB is the name of an existing 
	 * parameterized project, KEY is the name of a parameter defined for the project 
	 * and VALUE is the value of the parameter.
	 */
	@Test
	public void testBuild2() {
		try {
			FreeStyleProject freestyle = this.jenkins.createFreeStyleProject("Free-style");
			ParametersDefinitionProperty property = new ParametersDefinitionProperty(
					new StringParameterDefinition("key", "value"));
			freestyle.addProperty(property);
			freestyle.getBuildersList().add(new Shell("echo $key"));
			
			SlackData data = new SlackData();
			String newValue = "newValue";
			String text = "build Free-style -p key=" + newValue;
			data.setText(text);
			String user_name = "tommyv92";
			data.setUser_name(user_name);
			JSONResponse actual = this.receiver.build(data);
			
			String projectName = text.split(" ")[JOB_PARAMETER_INDEX];
			String message = Messages.buildScheduled(projectName);
			JSONResponse expected = new JSONResponse(new SlackMessage(message, 
					Messages.good()), StaplerResponse.SC_OK);
			
			while (freestyle.isInQueue());
			while (freestyle.isBuilding());
			FreeStyleBuild build = freestyle.getLastBuild();
			String log = FileUtils.readFileToString(build.getLogFile());
			
			assert expected.equals(actual);
			assert log.contains(Messages.slackCause(user_name));
			assert log.contains(newValue);
		} catch (IOException e) {
			assert false;
		}
	}
	
	/**
	 * Tests the performance of the build method when the user types the slash command
	 * "/jenkins build JOB" where JOB is the name of an existing parameterized project.
	 */
	@Test
	public void testBuild3() {
		try {
			FreeStyleProject freestyle = this.jenkins.createFreeStyleProject("Free-style");
			String value = "value";
			ParametersDefinitionProperty property = new ParametersDefinitionProperty(
					new StringParameterDefinition("key", value));
			freestyle.addProperty(property);
			freestyle.getBuildersList().add(new Shell("echo $key"));
			
			SlackData data = new SlackData();
			String text = "build Free-style";
			data.setText(text);
			String user_name = "tommyv92";
			data.setUser_name(user_name);
			JSONResponse actual = this.receiver.build(data);
			
			String projectName = text.split(" ")[JOB_PARAMETER_INDEX];
			String message = Messages.buildScheduled(projectName);
			JSONResponse expected = new JSONResponse(new SlackMessage(message, 
					Messages.good()), StaplerResponse.SC_OK);
			
			while (freestyle.isInQueue());
			while (freestyle.isBuilding());
			FreeStyleBuild build = freestyle.getLastBuild();
			String log = FileUtils.readFileToString(build.getLogFile());
			
			assert expected.equals(actual);
			assert log.contains(Messages.slackCause(user_name));
			assert log.contains(value);
		} catch (IOException e) {
			assert false;
		}
	}
	
	/**
	 * Tests the performance of the build method when the user types the slash command
	 * "/jenkins build JOB -p KEY=VALUE" where JOB is the name of an existing parameterized 
	 * project and KEY=VALUE is only one of many parameters defined for the project.
	 */
	@Test
	public void testBuild4() {
		try {
			FreeStyleProject freestyle = this.jenkins.createFreeStyleProject("Free-style");
			String bool = "false";
			ParametersDefinitionProperty property = new ParametersDefinitionProperty(
					new StringParameterDefinition("key", "value"), 
					new StringParameterDefinition("bool", bool));
			freestyle.addProperty(property);
			freestyle.getBuildersList().add(new Shell("echo $key $bool"));
			
			SlackData data = new SlackData();
			String newValue = "newValue";
			String text = "build Free-style -p key=" + newValue;
			data.setText(text);
			String user_name = "tommyv92";
			data.setUser_name(user_name);
			JSONResponse actual = this.receiver.build(data);
			
			String projectName = text.split(" ")[JOB_PARAMETER_INDEX];
			String message = Messages.buildScheduled(projectName);
			JSONResponse expected = new JSONResponse(new SlackMessage(message, 
					Messages.good()), StaplerResponse.SC_OK);
			
			while (freestyle.isInQueue());
			while (freestyle.isBuilding());
			FreeStyleBuild build = freestyle.getLastBuild();
			String log = FileUtils.readFileToString(build.getLogFile());
			
			assert expected.equals(actual);
			assert log.contains(Messages.slackCause(user_name));
			assert log.contains(newValue);
			assert log.contains(bool);
		} catch (IOException e) {
			assert false;
		}
	}
	
	/**
	 * Tests the performance of the build method when the number of tokens present in 
	 * the slash command is less than two (excluding /jenkins).
	 */
	@Test
	public void testBuild5() {
		SlackData data = new SlackData();
		String command = "build";
		data.setText(command);
		JSONResponse actual = this.receiver.build(data);
		
		String message = Messages.incompleteCommandSyntax(command);
		JSONResponse expected = new JSONResponse(new SlackMessage(message, 
				Messages.danger()), StaplerResponse.SC_OK);
		
		assert expected.equals(actual);
	}
	
	/**
	 * Tests the performance of the build method when the number of tokens present in 
	 * the slash command is greater than one and odd (excluding /jenkins).
	 */
	@Test
	public void testBuild6() {
		SlackData data = new SlackData();
		String command = "build Free-style -p";
		data.setText(command);
		JSONResponse actual = this.receiver.build(data);
		
		String message = Messages.incompleteCommandSyntax(command.split(" ")[COMMAND_INDEX]);
		JSONResponse expected = new JSONResponse(new SlackMessage(message, 
				Messages.danger()), StaplerResponse.SC_OK);
		
		assert expected.equals(actual);
	}
	
	/**
	 * Tests the performance of the build method when the user types the slash command 
	 * "/jenkins build JOB" where JOB is the name of a project that does not exist and.
	 */
	@Test
	public void testBuild7() {
		SlackData data = new SlackData();
		String text = "build Free-style";
		data.setText(text);
		JSONResponse actual = this.receiver.build(data);
		
		String projectName = text.split(" ")[JOB_PARAMETER_INDEX];
		JSONResponse expected = new JSONResponse(new SlackMessage(Messages.projectNotFound(projectName), 
				Messages.danger()), StaplerResponse.SC_OK);
		
		assert expected.equals(actual);
	}
	
	/**
	 * Tests the performance of the build method when the user types the slash command 
	 * "/jenkins build JOB PARAMETERS" where JOB is the name of an existing parameterized 
	 * project and PARAMETERS is a list of parameters greater than the number of parameters 
	 * defined for the JOB.
	 */
	@Test
	public void testBuild8() {
		try {
			FreeStyleProject freestyle = this.jenkins.createFreeStyleProject("Free-style");
			ParametersDefinitionProperty property = new ParametersDefinitionProperty(
					new StringParameterDefinition("key", "value"));
			freestyle.addProperty(property);
			
			SlackData data = new SlackData();
			String text = "build Free-style -p key=newValue -p key1=value1";
			data.setText(text);
			JSONResponse actual = this.receiver.build(data);
			
			String projectName = text.split(" ")[JOB_PARAMETER_INDEX];
			String message = Messages.tooManyParameters(projectName);
			JSONResponse expected = new JSONResponse(new SlackMessage(message, 
					Messages.danger()), StaplerResponse.SC_OK);
			
			assert expected.equals(actual);
		} catch (IOException e) {
			assert false;
		}
	}
	
	/**
	 * Tests the performance of the build method when the user types the slash command 
	 * "/jenkins build JOB p KEY=VALUE" where JOB is the name of an existing parameterized 
	 * project, p is syntactically wrong, KEY is the name of a parameter defined for the 
	 * project and VALUE is the value of the parameter.
	 */
	@Test
	public void testBuild9() {
		try {
			FreeStyleProject freestyle = this.jenkins.createFreeStyleProject("Free-style");
			ParametersDefinitionProperty property = new ParametersDefinitionProperty(
					new StringParameterDefinition("key", "value"));
			freestyle.addProperty(property);
			
			SlackData data = new SlackData();
			String parameter = "p key=newValue";
			String text = "build Free-style " + parameter;
			data.setText(text);
			JSONResponse actual = this.receiver.build(data);
			
			String projectName = text.split(" ")[JOB_PARAMETER_INDEX];
			String message = Messages.wrongParameterSyntax(parameter, projectName);
			JSONResponse expected = new JSONResponse(new SlackMessage(message, 
					Messages.danger()), StaplerResponse.SC_OK);
			
			assert expected.equals(actual);
		} catch (IOException e) {
			assert false;
		}
	}
	
	/**
	 * Tests the performance of the build method when the user types the slash command 
	 * "/jenkins build JOB -p KEY=VALUE" where JOB is the name of an existing parameterized 
	 * project and KEY=VALUE is a syntactically wrong parameter.
	 */
	@Test
	public void testBuild10() {
		try {
			FreeStyleProject freestyle = this.jenkins.createFreeStyleProject("Free-style");
			ParametersDefinitionProperty property = new ParametersDefinitionProperty(
					new StringParameterDefinition("key", "value"));
			freestyle.addProperty(property);
			
			SlackData data = new SlackData();
			String parameter = "-p 1key=newValue";
			String text = "build Free-style " + parameter;
			data.setText(text);
			JSONResponse actual = this.receiver.build(data);
			
			String projectName = text.split(" ")[JOB_PARAMETER_INDEX];
			String message = Messages.wrongParameterSyntax(parameter, projectName);
			JSONResponse expected = new JSONResponse(new SlackMessage(message, 
					Messages.danger()), StaplerResponse.SC_OK);
			
			assert expected.equals(actual);
		} catch (IOException e) {
			assert false;
		}
	}
	
	/**
	 * Tests the performance of the build method when the user types the slash command 
	 * "/jenkins build JOB -p KEY=VALUE" where JOB is the name of an existing parameterized 
	 * project and KEY is a non-defined parameter for the JOB.
	 */
	@Test
	public void testBuild11() {
		try {
			FreeStyleProject freestyle = this.jenkins.createFreeStyleProject("Free-style");
			ParametersDefinitionProperty property = new ParametersDefinitionProperty(
					new StringParameterDefinition("key", "value"));
			freestyle.addProperty(property);
			
			SlackData data = new SlackData();
			String newKey = "newKey";
			String parameter = "-p " + newKey + "=newValue";
			String text = "build Free-style " + parameter;
			data.setText(text);
			JSONResponse actual = this.receiver.build(data);
			
			String projectName = text.split(" ")[JOB_PARAMETER_INDEX];
			String message = Messages.invalidParameter(newKey, projectName);
			JSONResponse expected = new JSONResponse(new SlackMessage(message, 
					Messages.danger()), StaplerResponse.SC_OK);
			
			assert expected.equals(actual);
		} catch (IOException e) {
			assert false;
		}
	}
	
	/**
	 * Tests the performance of the build method when the user types the slash command
	 * "/jenkins build JOB PARAMETERS" where JOB is the name of an existing parameterized 
	 * project and PARAMETERS is a list of duplicate parameters at most equal to the 
	 * number of parameters defined for the JOB.
	 */
	@Test
	public void testBuild12() {
		try {
			FreeStyleProject freestyle = this.jenkins.createFreeStyleProject("Free-style");
			String bool = "false";
			ParametersDefinitionProperty property = new ParametersDefinitionProperty(
					new StringParameterDefinition("key", "value"), 
					new StringParameterDefinition("bool", bool));
			freestyle.addProperty(property);
			freestyle.getBuildersList().add(new Shell("echo $key $bool"));
			
			SlackData data = new SlackData();
			String key = "key";
			String text = "build Free-style -p " + key + "=newValue -p " + key + "=newValue";
			data.setText(text);
			JSONResponse actual = this.receiver.build(data);
			
			String projectName = text.split(" ")[JOB_PARAMETER_INDEX];
			String message = Messages.invalidParameter(key, projectName);
			JSONResponse expected = new JSONResponse(new SlackMessage(message, 
					Messages.danger()), StaplerResponse.SC_OK);
			
			assert expected.equals(actual);
		} catch (IOException e) {
			assert false;
		}
	}
	
	/**
	 * Tests the performance of the build method when the user types the slash command 
	 * "/jenkins build JOB -p KEY=VALUE" where JOB is the name of an existing project 
	 * not parameterized and KEY=VALUE is a non-defined parameter for the JOB.
	 */
	@Test
	public void testBuild13() {
		try {
			this.jenkins.createFreeStyleProject("Free-style");
			
			SlackData data = new SlackData();
			String text = "build Free-style -p key=value";
			data.setText(text);
			JSONResponse actual = this.receiver.build(data);
			
			String projectName = text.split(" ")[JOB_PARAMETER_INDEX];
			String message = Messages.buildNotParameterized(projectName);
			JSONResponse expected = new JSONResponse(new SlackMessage(message, 
					Messages.danger()), StaplerResponse.SC_OK);
			
			assert expected.equals(actual);
		} catch (IOException e) {
			assert false;
		}
	}
	
	/**
	 * Tests the performance of the build method when the text field value is null.
	 */
	@Test
	public void testBuild14() {
		try {
			SlackData data = new SlackData();
			this.receiver.build(data);
			
			assert false;
		} catch (NullPointerException e) {
			assert true;
		}
	}
	
	/**
	 * Tests the performance of the build method when the data object is null.
	 */
	@Test
	public void testBuild15() {
		try {
			SlackData data = null;
			this.receiver.build(data);
			
			assert false;
		} catch (NullPointerException e) {
			assert true;
		}
	}
	
	/**
	 * Tests the performance of the test method when the user types the slash command
	 * "/jenkins test JOB all" where JOB is the name of an existing maven project
	 */
	@Test
	public void testTest1() {
		try {
			MavenModuleSet maven = this.jenkins.createMavenProject("Maven");
			String goals = "compile";
			maven.setGoals(goals);
			
			SlackData data = new SlackData();
			data.setText("test Maven all");
			String user_name = "tommyv92";
			data.setUser_name(user_name);
			JSONResponse actual = this.receiver.test(data);
			
			JSONResponse expected = new JSONResponse(new SlackMessage(null, null), 
					StaplerResponse.SC_OK);
			
			MavenModuleSetBuild build = maven.getLastBuild();
			String log = FileUtils.readFileToString(build.getLogFile());
			
			assert expected.equals(actual);
			assert goals.equals(maven.getGoals());
			assert log.contains(Messages.slackCause(user_name));
		} catch (IOException e) {
			assert false;
		}
	}
	
	/**
	 * Tests the performance of the test method when the user types the slash command
	 * "/jenkins test JOB class CLASS" where JOB is the name of an existing maven 
	 * project and CLASS is a list of classes.
	 */
	@Test
	public void testTest2() {
		try {
			MavenModuleSet maven = this.jenkins.createMavenProject("Maven");
			String goals = "compile";
			maven.setGoals(goals);
			String opts = maven.getMavenOpts();
			
			SlackData data = new SlackData();
			data.setText("test Maven class ClassName");
			String user_name = "tommyv92";
			data.setUser_name(user_name);
			JSONResponse actual = this.receiver.test(data);
			
			JSONResponse expected = new JSONResponse(new SlackMessage(null, null), 
					StaplerResponse.SC_OK);
			
			MavenModuleSetBuild build = maven.getLastBuild();
			String log = FileUtils.readFileToString(build.getLogFile());
			
			assert expected.equals(actual);
			assert goals.equals(maven.getGoals());
			assert opts == maven.getMavenOpts();
			assert log.contains(Messages.slackCause(user_name));
		} catch (IOException e) {
			assert false;
		}
	}
	
	/**
	 * Tests the performance of the test method when the number of tokens present in 
	 * the slash command is less than three (excluding /jenkins).
	 */
	@Test
	public void testTest3() {
		SlackData data = new SlackData();
		data.setText("test Maven");
		JSONResponse actual = this.receiver.test(data);
		
		String command = data.getText().split(" ")[COMMAND_INDEX];
		JSONResponse expected = new JSONResponse(new SlackMessage(Messages.incompleteCommandSyntax(command), 
				Messages.danger()), StaplerResponse.SC_OK);
		
		assert expected.equals(actual);
	}
	
	/**
	 * Tests the performance of the test method when the user types the slash command 
	 * "/jenkins test JOB SCOPE" where JOB is the name of a project that does not 
	 * exist and SCOPE is the scope of the test.
	 */
	@Test
	public void testTest4() {
		SlackData data = new SlackData();
		data.setText("test Maven all");
		JSONResponse actual = this.receiver.test(data);
		
		String projectName = data.getText().split(" ")[JOB_PARAMETER_INDEX];
		JSONResponse expected = new JSONResponse(new SlackMessage(Messages.projectNotFound(projectName), 
				Messages.danger()), StaplerResponse.SC_OK);
		
		assert expected.equals(actual);
	}
	
	/**
	 * Tests the performance of the test method when the user types the slash command 
	 * "/jenkins test JOB SCOPE" where JOB is the name of a project that is not a 
	 * maven project and SCOPE is the scope of the test.
	 */
	@Test
	public void testTest5() {
		try {
			this.jenkins.createFreeStyleProject("Free-style");
			SlackData data = new SlackData();
			data.setText("test Free-style all");
			JSONResponse actual = this.receiver.test(data);
			
			String projectName = data.getText().split(" ")[JOB_PARAMETER_INDEX];
			JSONResponse expected = new JSONResponse(new SlackMessage(Messages.notMavenProject(projectName), 
					Messages.danger()), StaplerResponse.SC_OK);
			
			assert expected.equals(actual);
		} catch (IOException e) {
			assert false;
		}
	}
	
	/**
	 * Tests the performance of the test method when the user types the slash command 
	 * "/jenkins test JOB SCOPE" where JOB is the name of an existing maven project
	 * and SCOPE is the scope of the test that does not exist.
	 */
	@Test
	public void testTest6() {
		try {
			this.jenkins.createMavenProject("Maven");
			
			SlackData data = new SlackData();
			data.setText("test Maven scope");
			JSONResponse actual = this.receiver.test(data);
			
			String scope = data.getText().split(" ")[SCOPE_PARAMETER_INDEX];
			JSONResponse expected = new JSONResponse(new SlackMessage(Messages.invalidTestScope(scope), 
					Messages.danger()), StaplerResponse.SC_OK);
			
			assert expected.equals(actual);
		} catch (IOException e) {
			assert false;
		}
	}
	
	/**
	 * Tests the performance of the test method when the user types the slash command 
	 * "/jenkins test JOB all TOKEN" where JOB is the name of an existing maven project
	 * and TOKEN is an additional parameter that is not accepted by the scope.
	 */
	@Test
	public void testTest7() {
		try {
			this.jenkins.createMavenProject("Maven");
			
			SlackData data = new SlackData();
			data.setText("test Maven all token");
			JSONResponse actual = this.receiver.test(data);
			
			String command = data.getText().split(" ")[COMMAND_INDEX];
			String projectName = data.getText().split(" ")[JOB_PARAMETER_INDEX];
			JSONResponse expected = new JSONResponse(new SlackMessage(Messages.wrongCommandSyntax(command, projectName), 
					Messages.danger()), StaplerResponse.SC_OK);
			
			assert expected.equals(actual);
		} catch (IOException e) {
			assert false;
		}
	}
	
	/**
	 * Tests the performance of the test method when the user types the slash command 
	 * "/jenkins test JOB class CLASS TOKEN" where JOB is the name of an existing 
	 * maven project, CLASS is a list of classes and TOKEN is an additional parameter 
	 * that is not accepted by the scope.
	 */
	@Test
	public void testTest8() {
		try {
			this.jenkins.createMavenProject("Maven");
			
			SlackData data = new SlackData();
			data.setText("test Maven class ClassName token");
			JSONResponse actual = this.receiver.test(data);
			
			String command = data.getText().split(" ")[COMMAND_INDEX];
			String projectName = data.getText().split(" ")[JOB_PARAMETER_INDEX];
			JSONResponse expected = new JSONResponse(new SlackMessage(Messages.wrongCommandSyntax(command, projectName), 
					Messages.danger()), StaplerResponse.SC_OK);
			
			assert expected.equals(actual);
		} catch (IOException e) {
			assert false;
		}
	}
	
	/**
	 * Tests the performance of the test method when the text field value is null.
	 */
	@Test
	public void testTest9() {
		try {
			SlackData data = new SlackData();
			this.receiver.test(data);
			
			assert false;
		} catch (NullPointerException e) {
			assert true;
		}
	}
	
	/**
	 * Tests the performance of the test method when the data object is null.
	 */
	@Test
	public void testTest10() {
		try {
			SlackData data = null;
			this.receiver.test(data);
			
			assert false;
		} catch (NullPointerException e) {
			assert true;
		}
	}
	
	/**
	 * Tests the performance of the listJobs method when the user types the slash command 
	 * "/jenkins list-jobs" and there are not jobs in Jenkins.
	 */
	@Test
	public void testListJobs1() {
		SlackData data = new SlackData();
		data.setText("list-jobs");
		JSONResponse actual = this.receiver.listJobs(data);
		
		JSONResponse expected = new JSONResponse(new SlackMessage(Messages.noJobsFound(), 
				Messages.good()), StaplerResponse.SC_OK);
		
		assert expected.equals(actual);
	}
	
	/**
	 * Tests the performance of the listJobs method when the user types the slash command 
	 * "/jenkins list-jobs" and there are jobs in Jenkins.
	 */
	@Test
	public void testListJobs2() {
		try {
			// can't test ExternalJob because it can't be created with this.jenkins
			// ExternalJob external = new ExternalJob("External");
			FreeStyleProject freestyle = this.jenkins.createFreeStyleProject("Free-style");
			MavenModuleSet maven = this.jenkins.createMavenProject("Maven");
			MatrixProject matrix = this.jenkins.createMatrixProject("Multi-configuration");
			
			SlackData data = new SlackData();
			data.setText("list-jobs");
			JSONResponse actual = this.receiver.listJobs(data);
			
			String message = "JOBS\n\n" 
					+ freestyle.getDisplayName() + " (Free-style Project)\n" 
					+ maven.getDisplayName() + " (Maven Project)\n" 
					+ matrix.getDisplayName() + " (Multi-configuration Project)\n";
			JSONResponse expected = new JSONResponse(new SlackMessage(message, 
					Messages.good()), StaplerResponse.SC_OK);
			
			assert expected.equals(actual);
		} catch (IOException e) {
			assert false;
		}
	}
	
	/**
	 * Tests the performance of the listJobs method when the number of tokens present in 
	 * the slash command is greater than one (excluding /jenkins).
	 */
	@Test
	public void testListJobs3() {
		SlackData data = new SlackData();
		data.setText("list-jobs token");
		JSONResponse actual = this.receiver.listJobs(data);
		
		String command = data.getText().split(" ")[COMMAND_INDEX];
		JSONResponse expected = new JSONResponse(new SlackMessage(Messages.tooManyArguments(command), 
				Messages.danger()), StaplerResponse.SC_OK);
		
		assert expected.equals(actual);
	}
	
	/**
	 * Tests the performance of the listJobs method when the text field value is null.
	 */
	@Test
	public void testListJobs4() {
		try {
			SlackData data = new SlackData();
			this.receiver.listJobs(data);
			
			assert false;
		} catch (NullPointerException e) {
			assert true;
		}
	}
	
	/**
	 * Tests the performance of the listJobs method when the data object is null.
	 */
	@Test
	public void testListJobs5() {
		try {
			SlackData data = null;
			this.receiver.listJobs(data);
			
			assert false;
		} catch (NullPointerException e) {
			assert true;
		}
	}
	
	/**
	 * Tests the performance of the help method when the user types the slash command 
	 * "/jenkins help".
	 */
	@Test
	public void testHelp1() {
		SlackData data = new SlackData();
		data.setText("help");
		JSONResponse actual = this.receiver.help(data);
		
		JSONResponse expected = new JSONResponse(new SlackMessage(Messages.help(), 
				Messages.good()), StaplerResponse.SC_OK);
		
		assert expected.equals(actual);
	}
	
	/**
	 * Tests the performance of the help method when the user types the slash command 
	 * "/jenkins help build".
	 */
	@Test
	public void testHelp2() {
		SlackData data = new SlackData();
		data.setText("help build");
		JSONResponse actual = this.receiver.help(data);
		
		JSONResponse expected = new JSONResponse(new SlackMessage(Messages.helpBuild(), 
				Messages.good()), StaplerResponse.SC_OK);
		
		assert expected.equals(actual);
	}
	
	/**
	 * Tests the performance of the help method when the user types the slash command 
	 * "/jenkins help test".
	 */
	@Test
	public void testHelp3() {
		SlackData data = new SlackData();
		data.setText("help test");
		JSONResponse actual = this.receiver.help(data);
		
		JSONResponse expected = new JSONResponse(new SlackMessage(Messages.helpTest(), 
				Messages.good()), StaplerResponse.SC_OK);
		
		assert expected.equals(actual);
	}
	
	/**
	 * Tests the performance of the help method when the user types the slash command 
	 * "/jenkins help list-jobs".
	 */
	@Test
	public void testHelp4() {
		SlackData data = new SlackData();
		data.setText("help list-jobs");
		JSONResponse actual = this.receiver.help(data);
		
		JSONResponse expected = new JSONResponse(new SlackMessage(Messages.helpListJobs(), 
				Messages.good()), StaplerResponse.SC_OK);
		
		assert expected.equals(actual);
	}
	
	/**
	 * Tests the performance of the help method when the user types the slash command 
	 * "/jenkins help help".
	 */
	@Test
	public void testHelp5() {
		SlackData data = new SlackData();
		data.setText("help help");
		JSONResponse actual = this.receiver.help(data);
		
		JSONResponse expected = new JSONResponse(new SlackMessage(Messages.helpHelp(), 
				Messages.good()), StaplerResponse.SC_OK);
		
		assert expected.equals(actual);
	}
	
	/**
	 * Tests the performance of the help method when the user types the slash command 
	 * "/jenkins help COMMAND" where COMMAND is the name of a command that does not exist.
	 */
	@Test
	public void testHelp6() {
		SlackData data = new SlackData();
		data.setText("help version");
		JSONResponse actual = this.receiver.help(data);
		
		String command = data.getText().split(" ")[COMMAND_PARAMETER_INDEX];
		JSONResponse expected = new JSONResponse(new SlackMessage(Messages.invalidCommand(command), 
				Messages.danger()), StaplerResponse.SC_OK);
		
		assert expected.equals(actual);
	}
	
	/**
	 * Tests the performance of the help method when the number of tokens present in the 
	 * slash command is greater than two (excluding /jenkins).
	 */
	@Test
	public void testHelp7() {
		SlackData data = new SlackData();
		data.setText("help token token");
		JSONResponse actual = this.receiver.help(data);
		
		String command = data.getText().split(" ")[COMMAND_INDEX];
		JSONResponse expected = new JSONResponse(new SlackMessage(Messages.tooManyArguments(command), 
				Messages.danger()), StaplerResponse.SC_OK);
		
		assert expected.equals(actual);
	}
	
	/**
	 * Tests the performance of the help method when the text field value is null.
	 */
	@Test
	public void testHelp8() {
		try {
			SlackData data = new SlackData();
			this.receiver.help(data);
			
			assert false;
		} catch (NullPointerException e) {
			assert true;
		}
	}
	
	/**
	 * Tests the performance of the help method when the data object is null.
	 */
	@Test
	public void testHelp9() {
		try {
			SlackData data = null;
			this.receiver.help(data);
			
			assert false;
		} catch (NullPointerException e) {
			assert true;
		}
	}

}
