package com.home;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DefaultXMLManager implements XMLManager {
    DocumentBuilderFactory factory;
    Transformer transformer;
    File xmlFile;

    public DefaultXMLManager(final String filePath) throws TransformerConfigurationException {
        this.xmlFile = new File(filePath);
        this.factory = DocumentBuilderFactory.newInstance();
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        this.transformer = transformerFactory.newTransformer();
    }

    @Override
    public void createXml(String nameRoot) {
        if(!xmlFile.exists()) {
            Document document = null;

            try {
                DocumentBuilder builder = factory.newDocumentBuilder();
                document = builder.newDocument();
                document.normalizeDocument();
            } catch(ParserConfigurationException exception) {
                exception.printStackTrace();
            }

            if(document == null) {
                return;
            }

            document.appendChild(createElement(document, nameRoot));

            saveChangesDocument(document);
        }
    }

    @Override
    public void addElement(String elementTag, String parentTag) {
        Document document = parseNormalizeDocument(xmlFile);

        if(document == null) {
            return;
        }

        Element root = (Element) document.getFirstChild();
        Element parentElement = findElementByTag(root, parentTag);

        Element childElement = createElement(document, elementTag);
        parentElement.appendChild(childElement);

        saveChangesDocument(document);
    }

    @Override
    public void addNode(Nodeable nodeable, String childTag, String parentTag) {
        Document document = parseNormalizeDocument(xmlFile);

        if(document == null) {
            return;
        }

        Element root = (Element) document.getFirstChild();

        Element childElement = (Element) createNodeFromNodeable(document, childTag, nodeable);
        Element parentElement = findElementByTag(root, parentTag);
        parentElement.appendChild(childElement);


        saveChangesDocument(document);
    }

    @Override
    public void removeNode(Nodeable nodeable, String nodeTag, String parentTag) {
        Document document = parseNormalizeDocument(xmlFile);

        if(document == null) {
            return;
        }

        Element root = (Element) document.getFirstChild();

        Element parentElement = findElementByTag(root, parentTag);
        Node removeNode = findNodeByNodeable(parentElement.getElementsByTagName(nodeTag), nodeable);

        if(removeNode != null) {
            parentElement.removeChild(removeNode);
        }

        saveChangesDocument(document);
    }

    @Override
    public void editNode(Nodeable oldNodeable, Nodeable newNodeable, String nodeTag, String parentTag) {
        Document document = parseNormalizeDocument(xmlFile);

        if(document == null) {
            return;
        }

        Element root = (Element) document.getFirstChild();

        Element parentElement = findElementByTag(root, parentTag);

        Node oldNode = findNodeByNodeable(parentElement.getElementsByTagName(nodeTag), oldNodeable);

        if(oldNode != null) {
            Node newNode = createNodeFromNodeable(document, nodeTag, newNodeable);
            parentElement.replaceChild(newNode, oldNode);
        }

        saveChangesDocument(document);
    }


    private Document parseNormalizeDocument(File file) {
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);
            document.normalizeDocument();
            return document;
        } catch(ParserConfigurationException | SAXException | IOException exception) {
            exception.printStackTrace();
        }

        return null;
    }

    @Override
    public void saveXml(File toPath) {
        try {
            Files.copy(xmlFile.toPath(), toPath.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch(IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void loadXml(File fromPath) {
        Document document = parseNormalizeDocument(xmlFile);
        Document openedDocument = parseNormalizeDocument(fromPath);

        if(document == null || openedDocument == null) {
            return;
        }

        NodeList rootOpenedNodes = openedDocument.getFirstChild().getChildNodes();
        NodeList rootNodes = document.getFirstChild().getChildNodes();

        for(int i = 0; i < rootNodes.getLength(); i++) {
            Element openedCatalogue = (Element) rootOpenedNodes.item(i);
            Element catalogue = (Element) rootOpenedNodes.item(i);

            NodeList intoOpenedCatalogue = openedCatalogue.getChildNodes();
            int intoOpenedCatalogueLength = intoOpenedCatalogue.getLength();

            for(int j = 0; j < intoOpenedCatalogueLength; j++) {
                Node node = intoOpenedCatalogue.item(j);

                catalogue.appendChild(createNodeFromAnother(document, node));
            }
        }

        saveChangesDocument(document);
    }

    @Override
    public List<String[]> getListOf(String nodeListTag, String nodeTags, Nodeable nodeable) {
        List<String[]> resultList = new ArrayList<>();

        Document document = parseNormalizeDocument(xmlFile);

        if(document == null) {
            return resultList;
        }

        Element root = (Element) document.getFirstChild();

        Element nodes = findElementByTag(root, nodeListTag);

        Set<String> keys = nodeable.getValues().keySet();

        NodeList children = nodes.getElementsByTagName(nodeTags);

        for(int i = 0; i < children.getLength(); i++) {
            String[] tmpArray = new String[keys.size()];

            NodeList fields = children.item(i).getChildNodes();

            for(int j = 0; j < keys.size(); j++) {
                tmpArray[j] = fields.item(j).getTextContent();
            }

            resultList.add(tmpArray);
        }

        return resultList;
    }

    @Override
    public void clear(String parentTag) {
        Document document = parseNormalizeDocument(xmlFile);

        if(document == null) {
            return;
        }

        Element root = (Element) document.getFirstChild();

        Element node = findElementByTag(root, parentTag);

        NodeList children = node.getChildNodes();

        int length = children.getLength();

        for(int i = 0; i < length; i++) {
            node.removeChild(children.item(0));
        }

        saveChangesDocument(document);
    }

    private void saveChangesDocument(Document document) {
        try {
            DOMSource source = new DOMSource(document);

            StreamResult result = new StreamResult(xmlFile);

            transformer.transform(source, result);
        } catch(TransformerException exception) {
            exception.printStackTrace();
        }
    }

    private Element findElementByTag(Element parent, String tag) {
        NodeList nodes = parent.getElementsByTagName(tag);

        return (Element) nodes.item(nodes.getLength() - 1);
    }

    private Node findNodeByNodeable(NodeList nodes, Nodeable nodeable) {
        Node foundNode = null;

        String idKey = nodeable.getId()[0];
        String idValue = nodeable.getId()[1];

        for(int i = 0; i < nodes.getLength(); i++) {
            Element node = (Element) nodes.item(i);

            if(findElementByTag(node, idKey).getTextContent().equals(idValue)) {
                foundNode = node;
                break;
            }
        }

        return foundNode;
    }

    private Node createNodeFromNodeable(Document document, String nodeTag, Nodeable nodeable) {
        Element node = createElement(document, nodeTag);

        nodeable.getValues().forEach((field, value) -> {
            node.appendChild(createElementWithValue(document, field, value));
        });

        return node;
    }

    private Node createNodeFromAnother(Document document, Node anotherNode) {
        Element node = createElement(document, anotherNode.getNodeName());

        NodeList nodes = anotherNode.getChildNodes();

        for(int i = 0; i < nodes.getLength(); i++) {
            Element tmpNode = (Element) nodes.item(i);

            Element childElement = createElementWithValue(document, tmpNode.getTagName(), tmpNode.getTextContent());

            node.appendChild(childElement);
        }

        return node;
    }

    private Element createElement(Document document, String tag) {
        return document.createElement(tag);
    }

    private Element createElementWithValue(Document document, String tag, String value) {
        Element node = createElement(document, tag);
        node.setTextContent(value);
        return node;
    }
}
