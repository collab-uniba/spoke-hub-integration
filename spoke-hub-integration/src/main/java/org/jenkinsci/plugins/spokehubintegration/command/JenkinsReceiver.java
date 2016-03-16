package org.jenkinsci.plugins.spokehubintegration.command;

import hudson.matrix.MatrixProject;
import hudson.maven.AbstractMavenProject;
import hudson.maven.MavenModuleSet;
import hudson.model.AbstractProject;
import hudson.model.ExternalJob;
import hudson.model.ParameterDefinition;
import hudson.model.ParameterValue;
import hudson.model.TopLevelItem;
import hudson.model.ParametersAction;
import hudson.model.ParametersDefinitionProperty;
import hudson.security.ACL;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import jenkins.model.Jenkins;

import org.jenkinsci.plugins.spokehubintegration.SlackData;
import org.jenkinsci.plugins.spokehubintegration.SlackMessage;
import org.jenkinsci.plugins.spokehubintegration.exception.WrongParameterSyntaxException;
import org.jenkinsci.plugins.spokehubintegration.exception.InvalidParameterException;
import org.jenkinsci.plugins.spokehubintegration.exception.WrongTestScopeException;

/**
 * Class that executes commands.
 * 
 * @author Tommaso Montingelli
 *
 */
public class JenkinsReceiver {
	
	private static final Logger LOGGER = Logger.getLogger(JenkinsReceiver.class.getName());
	private static final int KEY = 0;
	private static final int VALUE = 1;
	// these variables follow the slash command syntax
	private static final int COMMAND_INDEX = 0;
	private static final int JOB_PARAMETER_INDEX = 1;
	private static final int COMMAND_PARAMETER_INDEX = 1;
	private static final int P_PARAMETER_INDEX = 2;
	private static final int SCOPE_PARAMETER_INDEX = 2;
	private static final int CLASS_PARAMETER_INDEX = 3;
	
	/**
	 * Starts a build for any type of job.
	 * 
	 * @param data data sent by Slack
	 * @return response to the requested command
	 */
	public SlackMessage build(SlackData data) {
		String message;
		String text = data.getText();
		text = text.replaceAll("\\s*=\\s*", "=");
		String[] commandFields = text.split(" ");
		// check if the command syntax is correct
		if (!parseBuildCommand(commandFields)) {
			message = Messages.incompleteCommandSyntax(commandFields[COMMAND_INDEX]);
			LOGGER.log(Level.INFO, message);
			return new SlackMessage(message, Messages.danger());
		}
		
		String projectName = commandFields[JOB_PARAMETER_INDEX];
		AbstractProject<?, ?> project = Jenkins.getInstance().getItemByFullName(projectName, AbstractProject.class);
		// check if the project exists
		if (project == null) {
			message = Messages.projectNotFound(projectName);
			LOGGER.log(Level.INFO, message);
			return new SlackMessage(message, Messages.danger());
		}
		
		boolean success;
		String slackUser = data.getUser_name();
		ACL.impersonate(ACL.SYSTEM);
		
		int numberEnteredParameters = commandFields.length - 2 - (commandFields.length - 2) / 2;
		// check if the project is parameterized
		if (project.isParameterized()) {
			int numberExpectedParameters = getNumberJobParameters(project);
			// check if the number of expected parameters is less than 
			// the entered parameters
			if (numberExpectedParameters < numberEnteredParameters) {
				message = Messages.tooManyParameters(projectName);
				LOGGER.log(Level.INFO, message);
				return new SlackMessage(message, Messages.danger());
			}
			
			String parameters = ""; 
			if (numberEnteredParameters > 0) {
				parameters = text.substring(text.indexOf(commandFields[P_PARAMETER_INDEX]));
			}
			try {
				List<ParameterValue> values = parseBuildParameters(project, parameters);
				success = project.scheduleBuild(0, new SlackCause(slackUser), 
						new ParametersAction(values));
			} catch (WrongParameterSyntaxException e) {
				message = Messages.wrongParameterSyntax(e.getMessage(), projectName);
				LOGGER.log(Level.INFO, message, e);
				return new SlackMessage(message, Messages.danger());
			} catch (InvalidParameterException e) {
				message = Messages.invalidParameter(e.getMessage(), projectName);
				LOGGER.log(Level.INFO, message, e);
				return new SlackMessage(message, Messages.danger());
			}
		} else {
			if (numberEnteredParameters != 0) {
				message = Messages.buildNotParameterized(projectName);
				LOGGER.log(Level.INFO, message);
				return new SlackMessage(message, Messages.danger());
			}
			success = project.scheduleBuild(0, new SlackCause(slackUser));
		}
		
		// success indicates that the project has been added to the queue and 
		// if this happens, it will be true regardless of the result of the build
        if (success) {
        	// Slack Plugin will notify the result of the build
        	message = Messages.buildScheduled(projectName);
        	LOGGER.log(Level.INFO, message);
        	return new SlackMessage(message, Messages.good());
        } else {
        	message = Messages.buildNotScheduled(projectName);
        	LOGGER.log(Level.INFO, message);
        	return new SlackMessage(message, Messages.danger());
        }
	}
	
	/**
	 * Checks the number of the arguments entered by a Slack user for the 
	 * command build.
	 * 
	 * @param commandFields entered arguments
	 * @return true if the number of arguments is greater than one and even,
	 * otherwise false
	 */
	private boolean parseBuildCommand(String[] commandFields) {
		// at this point commandFields will contain at least one element, 
		// that is the command to execute 
		return (commandFields.length > 1) && (commandFields.length % 2 == 0);
	}
	
	/**
	 * Gets the number of parameters defined for the project.
	 * 
	 * @param project project to build
	 * @return number of parameters
	 */
	private int getNumberJobParameters(AbstractProject<?, ?> project) {
		ParametersDefinitionProperty property = project.getProperty(ParametersDefinitionProperty.class);
		return property.getParameterDefinitions().size();
	}

	/**
	 * Check the syntax of the entered parameters and creates a list containing the 
	 * values to use for the build.
	 * 
	 * @param project project to build
	 * @param parameters entered parameters
	 * @return list of values
	 * @throws WrongParameterSyntaxException if the parameter syntax is wrong
	 * @throws InvalidParameterException if the entered parameter does not exist for
	 * the project 
	 */
	private List<ParameterValue> parseBuildParameters(AbstractProject<?, ?> project, String parameters) 
			throws WrongParameterSyntaxException, InvalidParameterException {
		List<ParameterValue> values = new ArrayList<>();
		ParametersDefinitionProperty property = project.getProperty(ParametersDefinitionProperty.class);
		// parameterDefinition is non editable
		List<ParameterDefinition> parameterDefinition = property.getParameterDefinitions();
		// create an editable list
		List<ParameterDefinition> definition = new ArrayList<>();;
		for (ParameterDefinition parameter : parameterDefinition) {
			definition.add(parameter);
		}
		if (!"".equals(parameters)) {
			String[] array = parameters.split(" ");
			for (int i = 0; i < array.length; i++) {
				// check if the syntax of the current parameter is correct
				if (!("-p".equals(array[i++]) && (array[i].matches("[a-zA-Z_$][\\w]*=\\S+")))) {
					throw new WrongParameterSyntaxException(array[i-1] + " " + array[i]);
				}
				
				boolean match = false;
				String key = array[i].split("=")[KEY];
				// checks if the entered parameter matches to one of the expected parameters
				for (ParameterDefinition parameter : definition) {
					if (key.equals(parameter.getName())) {
						String value = array[i].split("=")[VALUE];
			    		ParameterizedStaplerRequest request = new ParameterizedStaplerRequest(value);
			    		values.add(parameter.createValue(request));
			    		// remove the current parameter to avoid duplicates
			    		definition.remove(parameter);
			    		match = true;
			    		break;
					}
				}
				if (!match) {
					throw new InvalidParameterException(key);
				}
			}
		}
		
		if (!definition.isEmpty()) {
			// add default values for the missing parameters
			for (ParameterDefinition parameter : definition) {
				values.add(parameter.getDefaultParameterValue());
			}
		}
		
		return values;
	}

	/**
	 * Performs tests for a maven job.
	 * 
	 * @param data data sent by Slack
	 * @return response to the requested command
	 */
	public SlackMessage test(SlackData data) {
		String message = null;
		String text = data.getText();
		text = text.replaceAll("\\s*,\\s*", ",");
		String[] commandFields = text.split(" ");
		// at this point commandFields will contain at least one element, 
		// that is the command to execute
		if (commandFields.length < 3) {
			message = Messages.incompleteCommandSyntax(commandFields[COMMAND_INDEX]);
			LOGGER.log(Level.INFO, message);
			return new SlackMessage(message, Messages.danger());
		}
		
		String projectName = commandFields[JOB_PARAMETER_INDEX];
		AbstractProject<?, ?> project = Jenkins.getInstance().getItemByFullName(projectName, AbstractProject.class);
		// check if the project exists
		if (project == null) {
			message = Messages.projectNotFound(projectName);
			LOGGER.log(Level.INFO, message);
			return new SlackMessage(message, Messages.danger());
		}
		
		// check if the project is a Maven Project
		if (!(project instanceof AbstractMavenProject)) {
			message = Messages.notMavenProject(projectName);
			LOGGER.log(Level.INFO, message);
			return new SlackMessage(message, Messages.danger());
		}
		
		boolean error;
		try {
			error = parseTestCommand(commandFields);
			if (error) {
				message = Messages.wrongCommandSyntax(commandFields[COMMAND_INDEX], projectName);
				LOGGER.log(Level.INFO, message);
				return new SlackMessage(message, Messages.danger());
			}
		} catch (WrongTestScopeException e) {
			message = Messages.invalidTestScope(e.getMessage());
			LOGGER.log(Level.INFO, message, e);
			return new SlackMessage(message, Messages.danger());
		}
		
		String response_url = data.getResponse_url();
		if (response_url == null || response_url.isEmpty()) {
			message = Messages.invalidResponseUrl();
			LOGGER.log(Level.SEVERE, message);
			return new SlackMessage(message, Messages.danger());
		}
		
		String slackUser = data.getUser_name();
		ACL.impersonate(ACL.SYSTEM);
		MavenModuleSet maven = (MavenModuleSet) project;
		String goals = maven.getGoals();
		maven.setGoals("test");
		String testScope = commandFields[SCOPE_PARAMETER_INDEX];
		String mavenOpts = null;
		if ("class".equals(testScope)) {
			mavenOpts = maven.getMavenOpts();
			String className = commandFields[CLASS_PARAMETER_INDEX];
			maven.setMavenOpts("-Dtest=" + className);
		}
		
		String color = null;
		try {
			maven.scheduleBuild2(0, new SlackCause(slackUser)).get();
			LOGGER.log(Level.INFO, Messages.testPerformed(projectName));
		} catch (InterruptedException | ExecutionException e) {
			message = Messages.testNotPerformed(projectName);
			color = Messages.danger();
        	LOGGER.log(Level.INFO, message, e);
		}
		
		restoreDefaultSettings(maven, goals, mavenOpts);
		
		// Slack Plugin will notify the result of the build
		return new SlackMessage(message, color);
	}

	/**
	 * Checks the number of the arguments entered by a Slack user for the 
	 * command test and its scope.
	 * 
	 * @param commandFields entered arguments
	 * @return true if the number of arguments is correct, otherwise false
	 * @throws WrongTestScopeException if the entered scope does not exist
	 */
	private boolean parseTestCommand(String[] commandFields) throws WrongTestScopeException {
		boolean error = true;
		String testScope = commandFields[SCOPE_PARAMETER_INDEX];
		switch (testScope) {
		case "all":
			if (commandFields.length == 3) {
				error = false;
			}
			break;
			
		case "class":
			if (commandFields.length == 4) {
				error = false;
			}
			break;

		default:
			throw new WrongTestScopeException(testScope);
		}
		
		return error;
	}
	
	/**
	 * Restores the default setting for the maven project tested.
	 * 
	 * @param maven maven project tested
	 * @param goals maven goals
	 * @param mavenOpts maven options
	 */
	private void restoreDefaultSettings(MavenModuleSet maven, String goals, String mavenOpts) {
		maven.setGoals(goals);
		maven.setMavenOpts(mavenOpts);
	}
	
	/**
	 * Lists all jobs.
	 * 
	 * @param data data sent by Slack
	 * @return response to the requested command
	 */
	public SlackMessage listJobs(SlackData data) {
		String message;
		String text = data.getText();
		String[] commandFields = text.split(" ");
		if (commandFields.length > 1) {
			message = Messages.tooManyArguments(commandFields[COMMAND_INDEX]);
			LOGGER.log(Level.SEVERE, message);
			return new SlackMessage(message, Messages.danger());
		}
		
		ACL.impersonate(ACL.SYSTEM);
		List<TopLevelItem> jobs = Jenkins.getInstance().getItems();
		// check if there are jobs in Jenkins
		if (jobs == null || jobs.isEmpty()) {
			message = Messages.noJobsFound();
			LOGGER.log(Level.INFO, message);
			return new SlackMessage(message, Messages.good());
		}
		
		message = "JOBS\n\n";
		String freestyleJobs = "";
		String mavenJobs = "";
		String externalJobs = "";
		String matrixJobs = "";
		for (TopLevelItem project : jobs) {
			String projectName = project.getDisplayName();
			// check the type of the project
			if (project instanceof AbstractMavenProject) {
				mavenJobs = mavenJobs.concat(projectName + " (Maven Project)\n");
			} else if (project instanceof ExternalJob) {
				externalJobs = externalJobs.concat(projectName + " (External Project)\n");
			} else if (project instanceof MatrixProject) {
				matrixJobs = matrixJobs.concat(projectName + " (Multi-configuration Project)\n");
			} else {
				freestyleJobs = freestyleJobs.concat(projectName + " (Free-style Project)\n");
			}
		}
		message = message.concat(freestyleJobs + mavenJobs + externalJobs + matrixJobs);
		
		return new SlackMessage(message, Messages.good());
	}
	
	/**
	 * Lists all the available commands or a detailed description of single command.
	 * 
	 * @param data data sent by Slack
	 * @return response to the requested command
	 */
	public SlackMessage help(SlackData data) {
		String text = data.getText();
		String[] commandFields = text.split(" ");
		String message;
		switch (commandFields.length) {
		case 1:
			message = Messages.help();
			break;
			
		case 2:
			String command = commandFields[COMMAND_PARAMETER_INDEX];
			try {
				message = helpCommand(command);
			} catch (InvalidParameterException e) {
				message = Messages.invalidCommand(command);
				LOGGER.log(Level.INFO, message, e);
				return new SlackMessage(message, Messages.danger());
			}
			break;

		default:
			message = Messages.tooManyArguments(commandFields[COMMAND_INDEX]);
			LOGGER.log(Level.INFO, message);
			return new SlackMessage(message, Messages.danger());
		}
		LOGGER.log(Level.INFO, message);
		
		return new SlackMessage(message, Messages.good());
	}

	/**
	 * Gets a detailed description of single command.
	 * 
	 * @param command command you want to get information
	 * @return detailed description of the command
	 * @throws InvalidParameterException if the entered command does not exist
	 */
	private String helpCommand(String command) throws InvalidParameterException {
		String message;
		switch (command) {
		case "build":
			message = Messages.helpBuild();
			break;
			
		case "test":
			message = Messages.helpTest();
			break;
			
		case "list-jobs":
			message = Messages.helpListJobs();
			break;
			
		case "help":
			message = Messages.helpHelp();
			break;

		default:
			throw new InvalidParameterException(command);
		}
		
		return message;
	}

}
