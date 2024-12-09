package loader;

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

public class MetricXmlMainPage {

    // Метод для создания страницы XML
    public void createPage() throws ParserConfigurationException, TransformerException,
            TransformerFactoryConfigurationError, DOMException {

        // Указание директории и файла для главной страницы
        File dir = new File("report/stands");
        String filePath = "report/_index.xml";
        File file = new File(filePath);

        // Если файл существует, удаляем его
        if (file.exists()) {
            file.delete();
        }

        // Создаем фабрику для построения XML
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        // Проверяем, является ли директория корректной
        if (dir.isDirectory()) {

            // Перебираем все элементы в директории
            for (File item : dir.listFiles()) {
                if (item.isDirectory()) {
                    System.out.println("Directory: " + item.getName());

                    if (file.exists()) {
                        // Если файл существует, добавляем новый элемент в XML
                        try {
                            Document doc = docBuilder.parse(filePath);
                            Node root = doc.getDocumentElement();
                            Element stand = doc.createElement("stand");
                            root.appendChild(stand);
                            stand.setAttribute("name", item.getName());

                            // Запись в файл
                            try (FileOutputStream output = new FileOutputStream(filePath)) {
                                writeXml(doc, output);
                            }
                        } catch (IOException | SAXException e) {
                            e.printStackTrace();
                        }
                    } else {
                        // Если файл не существует, создаем новый XML
                        try (FileOutputStream output = new FileOutputStream(filePath)) {
                            Document doc = docBuilder.newDocument();
                            Node rootElement = doc.createElement("list");
                            doc.appendChild(rootElement);

                            Element stand = doc.createElement("stand");
                            rootElement.appendChild(stand);
                            stand.setAttribute("name", item.getName());

                            // Добавление инструкции для XSLT
                            Node xslFile = doc.createProcessingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\"_main_page.xsl\"");
                            doc.insertBefore(xslFile, rootElement);

                            // Запись в файл
                            writeXml(doc, output);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    System.out.println(item.getName() + " \t file");
                }
            }
        }
    }

    // Метод для записи XML в поток
    private static void writeXml(Document doc, OutputStream output) throws TransformerFactoryConfigurationError, TransformerException {
        try {
            // Создание трансформера для записи XML
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            // Установка свойств для трансформации
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            // Трансформация и запись в поток
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(output);
            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
