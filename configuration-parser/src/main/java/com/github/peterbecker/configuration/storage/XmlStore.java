package com.github.peterbecker.configuration.storage;

import com.github.peterbecker.configuration.ConfigurationException;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A Store implemented through an XML object.
 */
public class XmlStore implements Store {
    private final Document doc;

    public XmlStore(Path resource) throws IOException {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            this.doc = db.parse(Files.newInputStream(resource));
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Unexpected internal error.", e);
        } catch (SAXException e) {
            throw new IOException("Could not parse XML file.", e);
        }
    }

    @Override
    public Optional<String> getValue(Key key) throws ConfigurationException {
        String contextPath = key.getContext().stream().reduce("/*", (a, b) -> a + "/" + b);
        try {
            XPath xpath = XPathFactory.newInstance().newXPath();
            NodeList nodes = (NodeList) xpath.evaluate(contextPath + "/" + key.getOptionName(), doc, XPathConstants.NODESET);
            if(nodes.getLength() == 0) {
                nodes = (NodeList) xpath.evaluate(contextPath + "/@" + key.getOptionName(), doc, XPathConstants.NODESET);
                if(nodes.getLength() == 0) {
                    return Optional.empty();
                }
            }
            if (nodes.getLength() > 1) {
                throw new ConfigurationException("More than one node matches " + contextPath);
            }
            return Optional.of(nodes.item(0).getTextContent());
        } catch (XPathExpressionException e) {
            throw new ConfigurationException("Can not identify node at " + contextPath, e);
        }
    }
}
