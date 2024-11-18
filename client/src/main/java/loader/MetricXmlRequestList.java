package loader;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.DOMException;

import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactoryConfigurationError;

import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import javax.json.JsonArray;
import javax.json.JsonObject;

public class MetricXmlRequestList {
    private JsonArray requestT;

    public MetricXmlRequestList(JsonArray requestT) {
        this.requestT = requestT;
    }

    public void saveXmlMergeRequestList() throws ParserConfigurationException, TransformerException, TransformerFactoryConfigurationError, DOMException {
        String filePath = "./report/_request.xml";
        File file = new File(filePath);

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        if (file.exists()) { file.delete(); } 
        try (FileOutputStream output = new FileOutputStream(filePath)) {

            Document doc = docBuilder.newDocument();

            Element list = doc.createElement("list");
            doc.appendChild(list);

            for (int i = 0; i < requestT.size() ; i++) {
                JsonObject requestObject = requestT.getJsonObject(i);
                String request_type = requestObject.getString("request_type");

                Element user = doc.createElement("url");
                user.setAttribute("name", request_type);
                list.appendChild(user);
            }

            writeXml(doc, output);
        } catch (IOException e) {  e.printStackTrace(); }
        
    }

    private static void writeXml(Document doc, OutputStream output) throws TransformerFactoryConfigurationError, TransformerException {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(output);
            transformer.transform(source, result); 
        } catch (Exception e) { System.out.println(e); }
    }
}