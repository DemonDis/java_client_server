package loader;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MetricXml {

    private final String name;
    private final String requestType;
    private final String resultTime;
    private final String urlArm;
    private final String requestName;
    private final String maxTime;

    public MetricXml(String name, String requestType, String resultTime, String urlArm, String requestName, String maxTime) {
        this.name = name;
        this.requestType = requestType;
        this.resultTime = resultTime;
        this.urlArm = urlArm;
        this.requestName = requestName;
        this.maxTime = maxTime;
    }

    public void saveXml() throws ParserConfigurationException, TransformerException, IOException, SAXException {
        String filePath = "./report/stands/dev/" + name + "_result.xml";
        File file = new File(filePath);

        // Создание XML-документа
        Document doc = createDocument(file);

        // Добавление элементов метрики в XML
        addMetricElement(doc);

        // Применение XPath для удаления пустых текстовых узлов
        removeEmptyTextNodes(doc);

        // Запись документа в файл
        try (FileOutputStream output = new FileOutputStream(filePath)) {
            writeXml(doc, output);
        }
    }

    private Document createDocument(File file) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();

        // Включаем валидацию и связываем схему
        docFactory.setValidating(true);
        docFactory.setIgnoringElementContentWhitespace(true); // Будет работать только с setValidating(true)

        // Устанавливаем схему для проверки (здесь указываем файл XSD)
        try {
            docFactory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
            docFactory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource", "path_to_your_schema.xsd"); // Путь к XSD
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        if (file.exists()) {
            return docBuilder.parse(file);  // Если файл существует, загружаем его
        } else {
            // Если файл не существует, создаем новый
            Document doc = docBuilder.newDocument();
            Node rootElement = doc.createElement("metrics");
            doc.appendChild(rootElement);

            return doc;
        }
    }

    private void addMetricElement(Document doc) {
        Node rootElement = doc.getDocumentElement();

        // Создаем элемент <metric>
        Element metric = doc.createElement("metric");
        rootElement.appendChild(metric);

        // Добавляем атрибуты к элементу <metric>
        metric.setAttribute("requestName", requestType);
        metric.setAttribute("request", requestName);

        // Создаем и добавляем элемент <result_time>
        Element resultTimeElement = doc.createElement("result_time");
        resultTimeElement.setTextContent(resultTime);
        resultTimeElement.setAttribute("max_time", maxTime);
        metric.appendChild(resultTimeElement);
    }

    private static void removeEmptyTextNodes(Document doc) {
        try {
            // Создаем XPath объект
            XPath xpath = XPathFactory.newInstance().newXPath();
            
            // Ищем все текстовые узлы, содержащие только пробелы
            String expression = "//text()[normalize-space() = '']";
            NodeList emptyTextNodes = (NodeList) xpath.evaluate(expression, doc, XPathConstants.NODESET);
            
            // Удаляем пустые текстовые узлы
            for (int i = 0; i < emptyTextNodes.getLength(); i++) {
                Node emptyTextNode = emptyTextNodes.item(i);
                Node parentNode = emptyTextNode.getParentNode();
                parentNode.removeChild(emptyTextNode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeXml(Document doc, OutputStream output) throws TransformerException {
        // Настройка трансформера для записи XML в файл с отступами, но без лишних строк
        Transformer transformer = createTransformer();

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(output);
        transformer.transform(source, result);
    }

    private static Transformer createTransformer() throws TransformerException {
        // Создание и настройка трансформера для минимального вывода XML с отступами
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        // Включаем отступы, но без лишних пробелов или строк
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");  // Убедимся, что XML декларация присутствует
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");  // Включаем отступы
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");  // Отступ в 2 пробела
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        return transformer;
    }
}
