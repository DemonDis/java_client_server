package loader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import javax.json.JsonArray;
import javax.json.JsonObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class MetricXmlMerge {
    private final JsonArray users;
    private final String urlArm;

    public MetricXmlMerge(JsonArray users, String urlArm) {
        this.users = users;
        this.urlArm = urlArm;
    }

    public void saveXmlMerge() {
        String currentTime = getCurrentTime();
        String timeServer = getTimeServer();

        String filePath = "./report/_index.xml";
        File file = new File(filePath);

        // Создание нового XML-документа
        try (FileOutputStream output = new FileOutputStream(filePath)) {
            Document doc = createXmlDocument();
            Node root = doc.getDocumentElement();

            // Добавление инструкции по стилю
            addProcessingInstruction(doc, root);

            // Добавление корневого элемента <list>
            Element listElement = createListElement(doc, currentTime, timeServer);
            doc.appendChild(listElement);

            // Добавление элементов <entry> для каждого пользователя
            addUserEntries(doc, listElement);

            // Запись XML в файл
            writeXml(doc, output);
        } catch (IOException | TransformerException | SAXException e) {
            e.printStackTrace();
        }
    }

    private String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss"));
    }

    private String getTimeServer() {
        return LocalDateTime.now()
                .atZone(ZoneId.of("+07:00"))
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss"));
    }

    private Document createXmlDocument() throws SAXException, IOException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.newDocument();
        } catch (Exception e) {
            throw new SAXException("Error creating XML document", e);
        }
    }

    private void addProcessingInstruction(Document doc, Node root) {
        Node xslFile = doc.createProcessingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\"_merge_.xsl\"");
        doc.insertBefore(xslFile, root);
    }

    private Element createListElement(Document doc, String currentTime, String timeServer) {
        Element list = doc.createElement("list");
        list.setAttribute("url", urlArm);
        list.setAttribute("date", timeServer);
        return list;
    }

    private void addUserEntries(Document doc, Element listElement) {
        for (int i = 0; i < users.size(); i++) {
            JsonObject userObject = users.getJsonObject(i);
            String userString = userObject.getString("user");

            Element userEntry = doc.createElement("entry");
            userEntry.setAttribute("name", userString + "_result.xml");
            listElement.appendChild(userEntry);
        }
    }

    private void writeXml(Document doc, OutputStream output) throws TransformerException {
        Transformer transformer = createTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(output);
        transformer.transform(source, result);
    }

    private Transformer createTransformer() throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        // Настройка свойств трансформера для красивого вывода
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        return transformer;
    }
}
