package org.jenkinsci.plugins.spokehubintegration.utility;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Class that provides methods for accessing and reading an XML file.
 * 
 * @author Tommaso Montingelli
 *
 */
public class XMLReader {

	private static final Logger LOGGER = Logger.getLogger(XMLReader.class.getName());
	private Document doc;

	/**
	 * Create a newly {@link Document} object.
	 * 
	 * @param path file path
	 */
	public XMLReader(String path) {
		File xmlFile = new File(path);
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
			this.doc = docBuilder.parse(xmlFile);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			LOGGER.log(Level.SEVERE, null, e);
		}
	}
	
	/**
	 * Create a newly {@link Document} object.
	 * 
	 * @param stream stream containing the content to be parsed
	 */
	public XMLReader(InputStream stream) {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
			this.doc = docBuilder.parse(stream);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			LOGGER.log(Level.SEVERE, null, e);
		}
	}

	/**
	 * Reads the values of the class and method parameters for the requested command.
	 * 
	 * @param command command requested
	 * @return values of the class and method parameters
	 */
	public List<String> getParameters(String command) {
		List<String> elements = new ArrayList<>();
		this.doc.getDocumentElement().normalize();
		NodeList nodeList = this.doc.getElementsByTagName("call");
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				if (element.getAttribute("command").matches(command)) {
					elements.add(getTagValue("class", element));
					elements.add(getTagValue("method", element));
					elements.add(getTagValue("parameter", element));
					break;
				}
			}
		}
		return elements;
	}

	/**
	 * Reads the value of a tag.
	 * 
	 * @param tag tag
	 * @param element node to which the tag belongs
	 * @return value of the tag
	 */
	private String getTagValue(String tag, Element element) {
		NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
		Node nodeValue = nodeList.item(0);
		return nodeValue.getNodeValue();
	}

}
