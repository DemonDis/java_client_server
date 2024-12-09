package loader;

import java.io.*;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.tools.ant.DirectoryScanner;
import org.w3c.dom.DOMException;

public class FileWork {
    /**
     * Основной метод для работы с файлами и каталогами.
     */
    public void fileRun() throws IOException {
        JsonObject configFileObject = readConfigFile("./report/_conf.json");
        JsonArray usersArray = configFileObject.getJsonArray("users");
        JsonArray requestsArray = configFileObject.getJsonArray("requests");
        String standInfo = configFileObject.getString("stand_info");

        JsonObject standFileObject = readConfigFile("./report/__" + standInfo + ".json");
        String standName = standFileObject.getString("stand");

        // Создаем или очищаем директорию стенда
        File standDirectory = new File("./report/stands/" + standName);
        if (!standDirectory.exists()) {
            // Если папка не существует, создаем её
            standDirectory.mkdirs();
        }
        
        // Очищаем содержимое папки
        clearDirectory(standDirectory);

        // Удаляем старые XML файлы, если они существуют
        deleteXmlFiles(standDirectory);

        // Создаем папку заново (можно убрать, если не нужно создавать снова)
        standDirectory.mkdir();

        // Обрабатываем .txt файлы в каталоге log
        cleanUpLogDirectory();

        // Выполняем дальнейшую обработку с пользователями и запросами
        MetricXmlMerge metricXmlMerge = new MetricXmlMerge(
            usersArray, 
            standFileObject.getString("url"),
            standFileObject.getString("stand"), 
            standFileObject.getString("background")
        );
        try {
            metricXmlMerge.saveXmlMerge();
        } catch (DOMException | ParserConfigurationException | TransformerException
                | TransformerFactoryConfigurationError | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        MetricXmlRequestList metricXmlRequestList = new MetricXmlRequestList(
            requestsArray, 
            standFileObject.getString("stand"), 
            usersArray
        );
        try {
            metricXmlRequestList.saveXmlMergeRequestList();
        } catch (DOMException | ParserConfigurationException | TransformerException
                | TransformerFactoryConfigurationError | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Чтение JSON файла.
     * 
     * @param filePath Путь к JSON файлу
     * @return JsonObject, содержащий данные из файла
     * @throws IOException В случае ошибок при чтении файла
     */
    private JsonObject readConfigFile(String filePath) throws IOException {
        try (InputStream configFile = new FileInputStream(filePath);
             JsonReader reader = Json.createReader(configFile)) {
            return reader.readObject();
        }
    }

    /**
     * Очищение директории от файлов и папок.
     * 
     * @param directory Директория для очистки
     */
    private void clearDirectory(File directory) {
        if (directory.exists() && directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                if (file.isDirectory()) {
                    clearDirectory(file); // Рекурсивно очищаем папки
                } else {
                    file.delete(); // Удаляем файлы
                }
            }
        }
    }

    /**
     * Удаление старых .xml файлов в каталоге стенда.
     * 
     * @param standDirectory Каталог стенда
     */
    private void deleteXmlFiles(File standDirectory) {
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setIncludes(new String[]{"**/*.xml"});
        scanner.setBasedir(standDirectory);
        scanner.setCaseSensitive(false);
        scanner.scan();

        // Удаляем все найденные XML файлы
        for (String file : scanner.getIncludedFiles()) {
            File fileToDelete = new File(standDirectory, file);
            if (fileToDelete.exists()) {
                fileToDelete.delete(); // Удаляем файл
            }
        }
    }

    /**
     * Очистка директории логов от .txt файлов.
     */
    private void cleanUpLogDirectory() {
        File logDirectory = new File("./report/log");
        DirectoryScanner scannerTxt = new DirectoryScanner();
        scannerTxt.setIncludes(new String[]{"**/*.txt"});
        scannerTxt.setBasedir(logDirectory);
        scannerTxt.setCaseSensitive(false);
        scannerTxt.scan();

        for (String file : scannerTxt.getIncludedFiles()) {
            File fileToDelete = new File(logDirectory, file);
            if (fileToDelete.exists()) {
                fileToDelete.delete();
            }
        }
    }
}
