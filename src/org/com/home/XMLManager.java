package org.com.home;
import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLManager {
	DocumentBuilderFactory factory;
	DocumentBuilder builder;
	Transformer transformer;
	File filepath;
	
	public XMLManager(File filepath) {
		this.filepath = filepath;
		factory = DocumentBuilderFactory.newInstance();
		
		createXML("DataBase");
	}
	
	private void createXML(String nameRoot) {
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			transformer = transformerFactory.newTransformer();
			builder = factory.newDocumentBuilder();
			
			Document doc = builder.newDocument();
						
			doc.appendChild(doc.createElement(nameRoot));
			
			DOMSource source = new DOMSource(doc);
			
			StreamResult file = new StreamResult(filepath);
			
			transformer.transform(source, file);
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void buildElement(String elementName) {
		try {
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(filepath);
			doc.getDocumentElement().normalize();
			
			Element root = (Element) doc.getFirstChild();
			
			Element element = doc.createElement(elementName);
			
			root.appendChild(element);
			
			DOMSource source = new DOMSource(doc);
			
			StreamResult file = new StreamResult(filepath);
			
			transformer.transform(source, file);
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void addElement(String elementName, String parentName) {
		try {
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(filepath);
			doc.getDocumentElement().normalize();
			
			Element root = (Element) doc.getFirstChild();
			
			Element childElement = doc.createElement(elementName);
			
			NodeList nodes = root.getElementsByTagName(parentName);
			Element parentElement = (Element) nodes.item(nodes.getLength() - 1);
			
			parentElement.appendChild(childElement);
			
			DOMSource source = new DOMSource(doc);
			
			StreamResult file = new StreamResult(filepath);
			
			transformer.transform(source, file);
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void addNode(NodeSync nodeSync, String childName, String parentName) {
		try {
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(filepath);
			doc.getDocumentElement().normalize();
			
			Element root = (Element) doc.getFirstChild();
			
			Element childElement = (Element) getNode(doc, childName, nodeSync);
			
			NodeList nodes = root.getElementsByTagName(parentName);
			Element parentElement = (Element) nodes.item(nodes.getLength() - 1);
			
			parentElement.appendChild(childElement);
			
			DOMSource source = new DOMSource(doc);
			
			StreamResult file = new StreamResult(filepath);
			
			transformer.transform(source, file);
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void removeNode(NodeSync nodeSync, String nodeName, String parentName) {
		try {
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(filepath);
			doc.getDocumentElement().normalize();
			
			Element root = (Element) doc.getFirstChild();
												
			Element nodes = (Element) root.getElementsByTagName(parentName).item(0);
						
			Node removeNode = findNode(nodes.getElementsByTagName(nodeName), nodeSync);
						
			if(removeNode != null)
				((Element) nodes).removeChild(removeNode);
			
			DOMSource source = new DOMSource(doc);
			
			StreamResult file = new StreamResult(filepath);
			
			transformer.transform(source, file);
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void editNode(NodeSync oldNodeSync, NodeSync newNodeSync, String nodeName, String parentName) {
		try {
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(filepath);
			doc.getDocumentElement().normalize();
			
			Element root = (Element) doc.getFirstChild();
									
			Element nodes = (Element) root.getElementsByTagName(parentName).item(0);
						
			Node oldNode = findNode(nodes.getElementsByTagName(nodeName), oldNodeSync);
			
			if(oldNode != null) {
				Node newNode = getNode(doc, nodeName, newNodeSync);
				((Element) nodes).replaceChild(newNode, oldNode);
			}
				
			
			DOMSource source = new DOMSource(doc);
			
			StreamResult file = new StreamResult(filepath);
			
			transformer.transform(source, file);
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private Node findNode(NodeList nodes, NodeSync nodeSync) {
		Node foundNode = null;
		
		String idKey = nodeSync.getIds().get(0);
		String idValue = nodeSync.getIds().get(1);
		
//		System.out.println( nodes.getLength());
				
		for(int i = 0; i < nodes.getLength(); i++) {
			Element node = (Element) nodes.item(i);
			
//			System.out.println("Date node >> " + node.getElementsByTagName(idKey).item(0).getTextContent());
//			System.out.println("Id value >> " + idValue);
			
			if(node.getElementsByTagName(idKey).item(0).getTextContent().equals(idValue)) {
				foundNode = node;
				break;
			}
		}
		
		return foundNode;
	}
	
	private Node getNode(Document doc, String nodeName, NodeSync nodeSync) {
		Element node = doc.createElement(nodeName);
		
		nodeSync.getMapElements().forEach((k, v) -> {
			node.appendChild(getElement(doc, k, v));
		});
				
		return node;
	}
	
	private Element getElement(Document doc, String name, String value) {
		Element node = doc.createElement(name);
		node.appendChild(doc.createTextNode(value));
		return node;
	}
}