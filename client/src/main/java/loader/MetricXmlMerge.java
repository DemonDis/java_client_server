package loader;

import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import org.w3c.dom.*;

public class MetricXmlMerge {

    private JsonArray users;
    private String urlArm;
    private String standName;
    private String background;

    public MetricXmlMerge(JsonArray users, String urlArm, String standName, String background) {
        this.users = users;
        this.urlArm = urlArm;
        this.standName = standName;
        this.background = background;
    }

    public void saveXmlMerge() throws TransformerException, IOException, ParserConfigurationException {
        // Получаем текущее время в нужном формате
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss");
        LocalDateTime nowTime = LocalDateTime.now();
        String timeString = nowTime.format(formatter);
        String timeServer = nowTime.atZone(ZoneId.of("-07:00")).format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss").withZone(ZoneOffset.UTC));

        String filePath = "./report/stands/" + standName + "/_index.xml";
        File file = new File(filePath);
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;

        // Здесь исключение ParserConfigurationException не выбрасывается
        docBuilder = docFactory.newDocumentBuilder(); 

        if (file.exists()) {
            file.delete();
        }

        try (FileOutputStream output = new FileOutputStream(filePath)) {
            Document doc = docBuilder.newDocument();
            Node root = doc.getDocumentElement();
            Node xslFile = doc.createProcessingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\"../../merge_xsl\"");
            doc.insertBefore(xslFile, root);

            Element list = doc.createElement("list");
            doc.appendChild(list);
            list.setAttribute("url", urlArm);
            list.setAttribute("date", timeServer);
            list.setAttribute("stand", standName);
            list.setAttribute("background", background);

            for (int i = 0; i < users.size(); i++) {
                JsonObject userObject = users.getJsonObject(i);
                String userString = userObject.getString("user");
                Element entry = doc.createElement("entry");
                entry.setAttribute("name", userString + "_result.xml");
                list.appendChild(entry);
            }

            writeXml(doc, output);
        }
    }

    private static void writeXml(Document doc, OutputStream output) throws TransformerException {
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
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
