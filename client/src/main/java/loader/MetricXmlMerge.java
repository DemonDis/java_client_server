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

public class MetricXmlMerge {
    private JsonArray users;
    private String url_arm;

    public MetricXmlMerge(JsonArray users, String url_arm) {
        this.users = users;
        this.url_arm = url_arm;
    }

    public void saveXmlMerge() throws ParserConfigurationException, TransformerException, TransformerFactoryConfigurationError, DOMException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss");
        LocalDateTime nowTime = LocalDateTime.now();
        String timeString = nowTime.format(formatter);

        String filePath = "./report/_index.xml";
        File file = new File(filePath);

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        if (file.exists()) { file.delete(); } 
        try (FileOutputStream output = new FileOutputStream(filePath)) {

            Document doc = docBuilder.newDocument();

            Node root = doc.getDocumentElement();
            Node xsl_file = doc.createProcessingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\"_merge_.xsl\"");
            doc.insertBefore(xsl_file, root);

            Element list = doc.createElement("list");
            doc.appendChild(list);
            list.setAttribute("url", url_arm);
            list.setAttribute("date", timeString);

        for (int i = 0; i < users.size() ; i++) {
            JsonObject usersObject = users.getJsonObject(i);
            String userString = usersObject.getString("user");

            Element user = doc.createElement("entry");
            user.setAttribute("name", userString + "_result.xml");
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