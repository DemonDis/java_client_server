package loader;

import java.io.*;
import java.util.concurrent.*;
import javax.json.*;

public class Metric {

    public static void main(String[] args) throws Exception {
        // Загружаем конфигурацию из файла
        JsonObject config = loadConfig("./report/_conf.json");

        // Подготовка объектов
        FileWork fileWork = new FileWork();
        fileWork.doFile();  // Предполагаем, что это не требует оптимизации

        // Получаем массивы пользователей и запросов
        JsonArray usersArray = config.getJsonArray("users");
        JsonArray requestsArray = config.getJsonArray("request");

        // Используем ExecutorService для параллельных запросов
        ExecutorService executorService = Executors.newFixedThreadPool(usersArray.size());

        try {
            // Для каждого запроса
            for (int i = 0; i < requestsArray.size(); i++) {
                JsonObject requestObject = requestsArray.getJsonObject(i);
                String requestType = requestObject.getString("request_type");
                String name = requestObject.getString("name");
                String maxTime = requestObject.getString("max_time");
                String requestBody = requestObject.getString("request_body");
                JsonNumber timeout = requestObject.getJsonNumber("timeout");

                // Отправляем запросы для каждого пользователя
                handleRequestsForUsers(usersArray, config, requestType, name, maxTime, requestBody, timeout, executorService);

                // Задержка между запросами
                System.out.println("📢 [TIMEOUT] " + timeout.longValue());
                TimeUnit.SECONDS.sleep(timeout.longValue());
            }

        } finally {
            // Завершаем работу ExecutorService
            shutdownExecutor(executorService);
        }

        // Создание страницы с метриками (по завершению работы)
        MetricXmlMainPage metricXmlMainPage = new MetricXmlMainPage();
        metricXmlMainPage.createPage();

        // Выход из программы
        System.out.println("⛔ Программа завершена ⛔");
        System.exit(0);  // Корректный выход из программы
    }

    private static JsonObject loadConfig(String filePath) throws IOException {
        // Использование try-with-resources для автоматического закрытия ресурсов
        try (InputStream configFile = new FileInputStream(filePath);
             JsonReader reader = Json.createReader(configFile)) {
            return reader.readObject();
        }
    }

    private static void handleRequestsForUsers(JsonArray usersArray, JsonObject config, String requestType,
                                                String name, String maxTime, String requestBody,
                                                JsonNumber timeout, ExecutorService executorService) {
        // Для каждого пользователя выполняем запрос
        for (int j = 0; j < usersArray.size(); j++) {
            JsonObject userObject = usersArray.getJsonObject(j);
            String user = userObject.getString("user");
            String role = userObject.getString("role");
            String password = userObject.getString("password");

            // Отправка запроса в отдельном потоке
            executorService.submit(new HttpsRequest(
                user, requestType, config.getString("url"), name, maxTime,
                requestBody, password, role
            ));
        }
    }

    private static void shutdownExecutor(ExecutorService executorService) {
        // Завершаем работу ExecutorService
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("Executor did not terminate in time");
                }
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
