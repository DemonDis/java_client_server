package loader;

import java.io.FileInputStream;
import java.io.InputStream;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Metric {

    public static void main(String[] args) throws Exception {
        // Чтение конфигурационного файла
        InputStream configFile = new FileInputStream("./report/_conf.json");
        JsonReader reader = Json.createReader(configFile);
        JsonObject configFileObject = reader.readObject();
        reader.close();

        JsonArray usersArray = configFileObject.getJsonArray("users");
        JsonArray requestsArray = configFileObject.getJsonArray("requests");
        String standInfo = configFileObject.getString("stand_info");

        // Чтение файла с данными о стенде
        InputStream standFile = new FileInputStream("./report/__" + standInfo + ".json");
        JsonReader readerStand = Json.createReader(standFile);
        JsonObject standFileObject = readerStand.readObject();
        readerStand.close();

        JsonObject clientInfo = standFileObject.getJsonObject("client_info");
        JsonObject orgInfo = standFileObject.getJsonObject("org_info");
        String url = standFileObject.getString("url");
        String stand = standFileObject.getString("stand");

        // Создание и запуск обработки файлов
        FileWork fileWork = new FileWork();
        fileWork.fileRun();

        // Создание пула потоков
        ExecutorService executorService = Executors.newFixedThreadPool(usersArray.size());

        // Обработка каждого запроса
        for (int i = 0; i < requestsArray.size(); i++) {
            JsonObject requestObject = requestsArray.getJsonObject(i);
            String requestType = requestObject.getString("request_type").intern();
            String name = requestObject.getString("name");
            String maxTime = requestObject.getString("max_time");
            String requestBody = requestObject.getString("request_body");
            JsonNumber timeout = requestObject.getJsonNumber("timeout");
            JsonNumber rerun = requestObject.getJsonNumber("rerun");
            JsonArray addRequest = requestObject.getJsonArray("add_request");
            JsonArray userRequestArray = requestObject.getJsonArray("users");

            // Обработка пользователей для каждого запроса
            for (int j = 0; j < usersArray.size(); j++) {
                JsonObject userObject = usersArray.getJsonObject(j);
                String userString = userObject.getString("user");
                String userStringIntern = userString.substring(0, userString.lastIndexOf('_')).intern(); // Интернирование строки
                String roleString = userObject.getString("role");
                String password = userObject.getString("password");

                // Проверяем, если пользователь из запроса совпадает с данным пользователем
                for (int m = 0; m < userRequestArray.size(); m++) {
                    String userRequestString = userRequestArray.getString(m); // Получаем строку из JSON массива
                    if (userStringIntern.equals(userRequestString.intern())) { // Сравниваем интернированные строки
                        executorService.execute(new HttpsRequest(
                                userString, requestType, url, name, maxTime, requestBody, password, 
                                roleString, addRequest, rerun, clientInfo, orgInfo, stand
                        ));
                    }
                }

                // Если это последний пользователь, выводим информацию о таймауте и ожидаем завершения всех задач
                if (j == usersArray.size() - 1) {
                    System.out.println("\n");
                    System.out.println("[TIMEOUT] " + timeout.longValue() + " секунд");
                    executorService.awaitTermination(timeout.longValue(), TimeUnit.SECONDS);
                }
            }

            // После обработки всех пользователей для текущего запроса, генерируем XML-страницу
            if (i == requestsArray.size() - 1) {
                MetricXmlMainPage metricXmlMainPage = new MetricXmlMainPage();
                metricXmlMainPage.createPage();
                System.out.println("\n END");
                System.exit(0);
            }
        }
    }
}
