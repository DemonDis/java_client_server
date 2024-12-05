package loader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class MetricXmlRequestList {
    private JsonArray requestT;

    public MetricXmlRequestList(JsonArray requestT) {
        this.requestT = requestT;
    }

    public void saveXmlMergeRequestList() throws Exception {
        String filePath = "./report/_request.xml";
        File file = new File(filePath);

        if (file.exists() && !file.delete()) {
            throw new IOException("Failed to delete existing file");
        }

        try (FileOutputStream output = new FileOutputStream(filePath)) {
            Document doc = createDocument();
            Element list = doc.createElement("list");
            doc.appendChild(list);

            for (int i = 0; i < requestT.size(); i++) {
                JsonObject requestObject = requestT.getJsonObject(i);
                String requestType = requestObject.getString("request_type");

                Element user = doc.createElement("url");
                user.setAttribute("name", requestType);
                list.appendChild(user);
            }

            writeXml(doc, output);
        } catch (IOException | TransformerException | SAXException e) {
            throw new Exception("Error saving XML", e);
        }
    }

    private Document createDocument() throws Exception {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        return docBuilder.newDocument();
    }

    private void writeXml(Document doc, OutputStream output) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "0");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(output);
        transformer.transform(source, result);
    }
}
