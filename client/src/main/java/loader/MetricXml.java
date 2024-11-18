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

public class MetricXml {
    private String name;
    private String request_type;
    private String resultTime;
    private String url_arm;
    private String request_name;
    private String max_time;

    public MetricXml(String name, String request_type, String resultTime, String url_arm, String request_name, String max_time) {
        this.name = name;
        this.request_type = request_type;
        this.resultTime = resultTime;
        this.url_arm = url_arm;
        this.request_name = request_name;
        this.max_time = max_time;
    }

    public void saveXml() throws ParserConfigurationException, TransformerException, TransformerFactoryConfigurationError, DOMException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDateTime nowTime = LocalDateTime.now();
        String timeString = nowTime.format(formatter);

        String filePath = "./report/" + name +"_result.xml";
        File file = new File(filePath);

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        if (file.exists()) {
            try {
                Document doc = docBuilder.parse(filePath);
                Node root = doc.getDocumentElement();

                Element metric_2 = doc.createElement("metric");
                root.appendChild(metric_2);
                metric_2.setAttribute("requestName", request_type);
                metric_2.setAttribute("request", request_name);

                Element result_time_2 = doc.createElement("result_time");
                result_time_2.setTextContent(resultTime);
                metric_2.appendChild(result_time_2);
                result_time_2.setAttribute("max_time", max_time);

                try (FileOutputStream output = new FileOutputStream(filePath)) {
                    writeXml(doc, output);
                } catch (IOException e) { e.printStackTrace(); }

            } catch (SAXException ex) { ex.printStackTrace(System.out);
            } catch (IOException ex) { ex.printStackTrace(System.out); }

        }  else {
            try (FileOutputStream output = new FileOutputStream(filePath)) {
                Document doc = docBuilder.newDocument();

                Node root = doc.getDocumentElement();
                Node xsl_file = doc.createProcessingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\"stylesheet.xsl\"");
                doc.insertBefore(xsl_file, root);

                Element rootElement = doc.createElement("metrics");
                doc.appendChild(rootElement);

                Element metric = doc.createElement("metric");
                rootElement.appendChild(metric);
                metric.setAttribute("requestName", request_type);
                metric.setAttribute("request", request_name);

                Element result_time = doc.createElement("result_time");
                result_time.setTextContent(resultTime);
                metric.appendChild(result_time);
                result_time.setAttribute("max_time", max_time);

                writeXml(doc, output);
            } catch (IOException e) {  e.printStackTrace(); }
        }
    }

    private static void writeXml(Document doc, OutputStream output) throws TransformerFactoryConfigurationError, TransformerException {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.INDENT, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(output);
            transformer.transform(source, result); 
        } catch (Exception e) { System.out.println(e); }
    }
}