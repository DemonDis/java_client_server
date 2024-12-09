package loader;

import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.time.LocalDateTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonReader;
import javax.json.JsonObject;
import javax.json.JsonNumber;
import javax.json.JsonArray;

import org.eclipse.jetty.util.log.Log;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import java.util.UUID;

@WebSocket
public class SocketRequest {
    private JsonNumber rerun;
    private HttpClient httpClient;
    private WebSocketClient client;
    private String name;
    private LocalDateTime startTime;
    private String threadNumber;
    private String urlArm;
    private String requestName;
    private String maxTime;
    private JsonArray addRequest;
    private String requestStatic;
    private String stand;

    static final Logger LOG = Log.getLogger(SocketRequest.class);
    private GlobalStore globalStore = new GlobalStore();

    public SocketRequest(HttpClient httpClient, WebSocketClient client, String name, LocalDateTime startTime,
                         String threadNumber, String urlArm, String requestName, String maxTime,
                         JsonArray addRequest, String requestStatic, String stand, JsonNumber rerun) {
        this.httpClient = httpClient;
        this.client = client;
        this.name = name;
        this.startTime = startTime;
        this.threadNumber = threadNumber;
        this.urlArm = urlArm;
        this.requestName = requestName;
        this.maxTime = maxTime;
        this.addRequest = addRequest;
        this.requestStatic = requestStatic;
        this.stand = stand;
        this.rerun = rerun;
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        LOG.info("Подключение: пользователь = {}", this.name);
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        LOG.info("Закрытие: поток = {}; пользователь = {}; статус = {}; причина = {}", threadNumber, this.name, statusCode, reason);
    }

    @OnWebSocketError
    public void onError(Throwable cause) {
        LOG.warn("Ошибка WebSocket: {}", cause.getMessage(), cause);
    }

    @OnWebSocketMessage
    public void onMessage(String msg) throws ParserConfigurationException, TransformerException {
        UUID uuid = UUID.randomUUID();  // Генерация уникального идентификатора для запроса
        String uuidString = uuid.toString();

        // Чтение и парсинг JSON сообщения
        try (JsonReader reader = Json.createReader(new StringReader(msg))) {
            JsonObject obj = reader.readObject();
            String requestType = obj.getString("request_type").intern();
            String correlationId = obj.getString("correlation_id").intern();
            JsonNumber status = obj.getJsonNumber("status");

            processResponse(obj, requestType, status);  // Обработка ответа

            // Расчёт времени выполнения
            String resultTime = calculateTimeDifference();

            // Создание и сохранение метрики в XML
            MetricXml metricXml = new MetricXml(this.name, requestType, resultTime, this.urlArm, requestName, maxTime, status, stand, rerun);
            if (addRequest.size() > 0 && requestStatic.equals(requestType)) {
                metricXml.saveXml();  // Сохранение метрики в файл
            }

            // Закрытие соединений
            closeConnections();
        }
    }

    // Обработка ответа в зависимости от типа запроса
    private void processResponse(JsonObject obj, String requestType, JsonNumber status) {
        if (requestType.equals("form:org:grid") || requestType.equals("form:client:grid")) {
            JsonObject responseObject = obj.getJsonObject("response");
            if (responseObject != null && !responseObject.isEmpty()) {
                JsonArray data = responseObject.getJsonArray("data");
                if (data != null && !data.isEmpty()) {
                    JsonObject usersObject = data.getJsonObject(0);
                    JsonObject buttonObject = usersObject.getJsonObject("button");
                    if (buttonObject != null) {
                        globalStore.id = buttonObject.getString("id").intern();
                        LOG.info("ID = {}", globalStore.id);
                    }
                }
            }
        }

        // Логирование статуса
        String statusString = status.longValue() == 0 ? "Успех" : "Ошибка";
        LOG.info("[Ответ] пользователь = {}; запрос = {}; статус = {}; correlation_id = {}", threadNumber, this.name, requestType, statusString);
    }

    // Расчёт времени между началом и концом выполнения запроса
    private String calculateTimeDifference() {
        LocalDateTime endTime = LocalDateTime.now();
        Duration diff = Duration.between(startTime, endTime);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss SSS");
        return String.format("%02d:%02d", diff.toSecondsPart(), diff.toMillisPart());
    }

    // Закрытие HTTP и WebSocket соединений
    private void closeConnections() {
        try {
            client.stop();
            httpClient.stop();
        } catch (Exception e) {
            LOG.warn("Ошибка при остановке соединений: {}", e.getMessage(), e);
        }
    }
}
