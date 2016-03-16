package org.jenkinsci.plugins.spokehubintegration;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jenkinsci.plugins.spokehubintegration.command.Command;
import org.jenkinsci.plugins.spokehubintegration.command.JenkinsReceiver;
import org.jenkinsci.plugins.spokehubintegration.utility.XMLReader;

/**
 * Singleton class that implements {@link Controller} interface.
 * 
 * @author Tommaso Montingelli
 *
 */
public class CommandController implements Controller {

	private static final Logger LOGGER = Logger.getLogger(CommandController.class.getName());
	private static final int CLASS = 0;
	private static final int METHOD = 1;
	private static final int PARAMETER = 2;
	// this variable follow the slash command syntax
	private static final int COMMAND_INDEX = 0;
	private static CommandController controller;
	private XMLReader reader;
	
	/**
	 * Initializes a newly created {@link XMLReader} object.
	 */
	private CommandController() {
		this.reader = new XMLReader(this.getClass().getResourceAsStream("commands.xml"));
	}
	
	/**
	 * Gets the {@link CommandController} singleton.
	 * 
	 * @return the instance
	 */
	public static CommandController getInstance() {
		if (controller == null) {
			controller = new CommandController();
		}
		
		return controller;
	}
	
	@Override
	public Object handleData(SlackData data) {
		String message;
		String text = data.getText();
		text = text.replaceAll("\\s+", " ");
		data.setText(text);
		String command = text.split(" ")[COMMAND_INDEX];
		// check if the user typed the command
		if (command.isEmpty()) {
			message = Messages.commandNotTyped();
			LOGGER.log(Level.SEVERE, message);
			return new SlackMessage(message, Messages.danger());
		}
		
		List<String> pair = this.reader.getParameters(command);
		// check if the command exists
		if (pair.isEmpty()) {
			message = Messages.commandNotFound(command);
			LOGGER.log(Level.SEVERE, message);
			return new SlackMessage(message, Messages.danger());
		}
		
		String classe = pair.get(CLASS);
		String metodo = pair.get(METHOD);
		String parametro = pair.get(PARAMETER);
		Class<?> constructorClass;
		try {
			constructorClass = Class.forName(classe);
			Constructor<?> constructor = constructorClass.getConstructor(Command.class);
			Class<?> parameterClass = Class.forName(parametro);
			Constructor<?> parameterConstructor = parameterClass.getConstructor(JenkinsReceiver.class);
			Object o = constructor.newInstance(parameterConstructor.newInstance(new JenkinsReceiver()));
			Method m = constructorClass.getDeclaredMethod(metodo, data.getClass());
			m.setAccessible(true);
			return m.invoke(o, data);
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException 
				| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			message = Messages.notImplementedCommand(command);
			LOGGER.log(Level.SEVERE, message, e);
			return new SlackMessage(message, Messages.danger());
		}
	}

}
