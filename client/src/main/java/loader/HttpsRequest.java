package loader;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonNumber;

/**
 * Класс для имитации выполнения HTTPS запроса.
 * В данном случае, просто выводит информацию в консоль.
 */
public class HttpsRequest implements Runnable {
    private String userString;
    private String requestType;
    private String url;
    private String name;
    private String maxTime;
    private String requestBody;
    private String password;
    private String roleString;
    private JsonArray addRequest;
    private JsonNumber rerun;
    private JsonObject clientInfo;
    private JsonObject orgInfo;
    private String stand;

    public HttpsRequest(String userString, String requestType, String url, String name,
                        String maxTime, String requestBody, String password, String roleString,
                        JsonArray addRequest, JsonNumber rerun, JsonObject clientInfo, JsonObject orgInfo, String stand) {
        this.userString = userString;
        this.requestType = requestType;
        this.url = url;
        this.name = name;
        this.maxTime = maxTime;
        this.requestBody = requestBody;
        this.password = password;
        this.roleString = roleString;
        this.addRequest = addRequest;
        this.rerun = rerun;
        this.clientInfo = clientInfo;
        this.orgInfo = orgInfo;
        this.stand = stand;
    }

    @Override
    public void run() {
        // Отладочные выводы в консоль для проверки выполнения потока
        System.out.println("HttpsRequest thread is starting for user: " + userString);

        // Симуляция работы запроса
        try {
            // Печать всех переданных данных
            System.out.println("Request Type: " + requestType);
            System.out.println("User: " + userString);

            // Симуляция выполнения (можно убрать или заменить реальной логикой)
            Thread.sleep(1000);  // Пауза 1 секунда

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Убедитесь, что поток прерывается корректно
        }

        System.out.println("HttpsRequest completed for user: " + userString);
    }
}
