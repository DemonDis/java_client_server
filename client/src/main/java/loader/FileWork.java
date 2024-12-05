package loader;

import java.io.*;
import java.nio.file.*;
import javax.json.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.tools.ant.DirectoryScanner;

public class FileWork {
    public void doFile() throws Exception {

        // Используем try-with-resources для автоматического закрытия потока
        try (InputStream configFile = new FileInputStream("./report/_conf.json");
             JsonReader reader = Json.createReader(configFile)) {
            
            JsonObject configFileObject = reader.readObject();
            
            JsonArray usersArray = configFileObject.getJsonArray("users");
            JsonArray requestsArray = configFileObject.getJsonArray("request");

            // Сканирование файлов
            DirectoryScanner scanner = new DirectoryScanner();
            scanner.setIncludes(new String[]{"**/*.xml"});
            scanner.setBasedir(new File("./report"));
            scanner.setCaseSensitive(false);
            scanner.scan();
            String[] files = scanner.getIncludedFiles();
            
            // Удаляем все найденные XML-файлы
            for (String file : files) {
                Path filePath = Paths.get("./report", file);
                if (Files.exists(filePath)) {
                    try {
                        Files.delete(filePath);
                        System.out.println("Удалён файл: " + filePath);
                    } catch (IOException e) {
                        System.err.println("Ошибка при удалении файла: " + filePath);
                    }
                }
            }

            // Мержим XML и генерируем список запросов
            try {
                MetricXmlMerge metricXmlMerge = new MetricXmlMerge(usersArray, configFileObject.getString("url"));
                metricXmlMerge.saveXmlMerge();

                MetricXmlRequestList metricXmlRequestList = new MetricXmlRequestList(requestsArray);
                metricXmlRequestList.saveXmlMergeRequestList();
            } catch (ParserConfigurationException | TransformerException e) {
                // Логируем исключения
                System.err.println("Ошибка при обработке XML: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
