package loader;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.zip.*;
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

            // Получаем текущую дату и время для имени архива
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
            String currentDateTime = LocalDateTime.now().format(formatter);

            // Создаем архив с найденными XML файлами, включая текущую дату в имени
            File zipFile = new File("./report/backup_" + currentDateTime + ".zip");
            try (FileOutputStream fos = new FileOutputStream(zipFile);
                 ZipOutputStream zos = new ZipOutputStream(fos)) {

                for (String file : files) {
                    Path filePath = Paths.get("./report", file);
                    if (Files.exists(filePath)) {
                        // Добавляем файл в архив
                        try (FileInputStream fis = new FileInputStream(filePath.toFile())) {
                            ZipEntry zipEntry = new ZipEntry(file);
                            zos.putNextEntry(zipEntry);
                            
                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = fis.read(buffer)) >= 0) {
                                zos.write(buffer, 0, length);
                            }
                            zos.closeEntry();
                            System.out.println("Добавлен в архив файл: " + filePath);
                        } catch (IOException e) {
                            System.err.println("Ошибка при архивировании файла: " + filePath);
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Ошибка при создании архива: " + e.getMessage());
            }

            // Удаляем все найденные XML файлы после архивирования
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
