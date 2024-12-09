package loader;

import javax.json.JsonNumber;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
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

public class MetricXml {
    private String name;
    private String requestType;
    private String resultTime;
    private String urlArm;
    private String requestName;
    private String maxTime;
    private JsonNumber status;
    private String stand;
    private JsonNumber rerun;

    // Конструктор
    public MetricXml(String name, String requestType, String resultTime, String urlArm, String requestName,
                     String maxTime, JsonNumber status, String stand, JsonNumber rerun) {
        this.name = name;
        this.requestType = requestType;
        this.resultTime = resultTime;
        this.urlArm = urlArm;
        this.requestName = requestName;
        this.maxTime = maxTime;
        this.status = status;
        this.stand = stand;
        this.rerun = rerun;
    }

    // Метод для сохранения XML отчета
    public void saveXml() throws ParserConfigurationException, TransformerException, TransformerFactoryConfigurationError, DOMException {
        String filePath = "./report/stands/" + stand + "/" + name + "_result.xml";
        File file = new File(filePath);
        
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        
        // Строка для статуса
        String statusString = status.longValue() == 0 ? "Success" : "Error";
        
        Document doc;
        // Если файл существует, то парсим и обновляем его
        if (file.exists()) {
            try {
                doc = docBuilder.parse(filePath);
            } catch (IOException | SAXException e) {
                e.printStackTrace();
                return;
            }
        } else {
            // Если файл не существует, создаем новый
            doc = docBuilder.newDocument();
            Node rootElement = doc.createElement("metrics");
            doc.appendChild(rootElement);
        }
        
        // Добавление элемента метрики в XML
        Node root = doc.getDocumentElement();
        Element metric = doc.createElement("metric");
        root.appendChild(metric);
        
        addMetricAttributes(metric, statusString);
        
        // Добавляем результат времени
        Element resultTimeElement = doc.createElement("result_time");
        resultTimeElement.setTextContent(resultTime);
        metric.appendChild(resultTimeElement);
        resultTimeElement.setAttribute("max_time", maxTime);
        
        // Запись в файл
        try (FileOutputStream output = new FileOutputStream(filePath)) {
            writeXml(doc, output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Метод для добавления атрибутов к метке "metric"
    private void addMetricAttributes(Element metric, String statusString) {
        metric.setAttribute("requestName", requestType);
        metric.setAttribute("request", requestName);
        metric.setAttribute("status", statusString);
        metric.setAttribute("user", name);
        metric.setAttribute("rerun", rerun.toString());
    }

    // Метод для записи XML в файл
    private static void writeXml(Document doc, OutputStream output) throws TransformerFactoryConfigurationError, TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        // Настройка выводных параметров для преобразования
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        // Запись в поток
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(output);
        transformer.transform(source, result);
    }
}
