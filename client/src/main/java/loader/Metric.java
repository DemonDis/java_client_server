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

/**
 * Файл: Metric.java
 * Описание: Метрики отклика API
 * Права (Copyright): (C) 2024
 *
 * @author Shibikin D
 * @since 03.12.2024
 */

public class Metric {

    public static void main(String[] args) throws Exception {
        InputStream configFile = new FileInputStream("./report/_conf.json");
        JsonReader reader = Json.createReader(configFile);
        JsonObject configFileObject = reader.readObject();
        reader.close();

        JsonArray usersArray = configFileObject.getJsonArray("users");
        JsonArray requestsArray = configFileObject.getJsonArray("requests");
        String standInfo = configFileObject.getString("stand_info");

        InputStream standFile = new FileInputStream("./report/__" + standInfo + ".json");
        JsonReader readerStand = Json.createReader(standFile);
        JsonObject standFileObject = readerStand.readObject();
        readerStand.close();

        JsonObject clientInfo = standFileObject.getJsonObject("client_info");
        JsonObject orgInfo = standFileObject.getJsonObject("org_info");
        String url = standFileObject.getString("url");
        String stand = standFileObject.getString("stand");

        FileWork fileWork = new FileWork();
        fileWork.fileRun();

        ExecutorService executorService = Executors.newFixedThreadPool(usersArray.size());

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

            for (int j = 0; j < usersArray.size(); j++) {
                JsonObject userObject = usersArray.getJsonObject(j);
                String userString = userObject.getString("user");
                String userStringIntern = (new String(userString.substring(0, userString.lastIndexOf('_')))).intern();
                String roleString = userObject.getString("role");
                String password = userObject.getString("password");

                for (int m = 0; m < userRequestArray.size(); m++) {
                    String userRequestStringIntern = (new String(userRequestArray.getString(m))).intern();
                    if (userStringIntern.equals(userRequestStringIntern)) {
                        executorService.execute(
                            new HttpsRequest(
                                userString, requestType, url,
                                name, maxTime, requestBody, password, 
                                roleString, addRequest, rerun, clientInfo, 
                                orgInfo, stand
                            )
                        );
                    }
                }
                if (j == usersArray.size() - 1) {
                    System.out.println("\n");
                    System.out.println("[TIMEOUT] " + timeout.longValue() + " секунд");
                    executorService.awaitTermination(timeout.longValue(), TimeUnit.SECONDS);
                }
            }
            if (i == requestsArray.size() - 1) {
                MetricXmlMainPage metricXmlMainPage = new MetricXmlMainPage();
                metricXmlMainPage.createPage();
                System.out.println("\n END");
                System.exit(0);
            }
        }
    }
}