package loader;

import java.time.LocalDateTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonNumber;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.eclipse.jetty.client.HttpClient;

import java.util.UUID;

@WebSocket
public class SocketRequest {
    private final String name;
    private final LocalDateTime startTime;
    private final String threadNumber;
    private final String urlArm;
    private final String requestName;
    private final String maxTime;
    private final HttpClient httpClient;
    private final WebSocketClient client;

    static final Logger LOG = Log.getLogger(SocketRequest.class);

    public SocketRequest(String name, LocalDateTime startTime, String threadNumber, 
                         String urlArm, String requestName, String maxTime,
                         HttpClient httpClient, WebSocketClient client) {
        this.name = name; 
        this.startTime = startTime;
        this.threadNumber = threadNumber;
        this.urlArm = urlArm;
        this.requestName = requestName;
        this.maxTime = maxTime;
        this.httpClient = httpClient;
        this.client = client;
    }

    @OnWebSocketConnect
    public void onConnect(Session sess) {
        LOG.info("🔗 onConnect 🔗 user = {}", this.name);
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        LOG.info("🔐 onClose 🔐 thread = {}; user = {}; statusCode = {}; reason = {}", 
                  threadNumber, this.name, statusCode, reason);
    }

    @OnWebSocketError
    public void onError(Throwable cause) {
        LOG.warn("WebSocket error: {}", cause.getMessage(), cause);
    }

    @OnWebSocketMessage
    public void onMessage(String msg) {
        UUID uuid = UUID.randomUUID();
        String uuidString = uuid.toString();

        // Обрабатываем JSON сообщение
        JsonObject obj = Json.createReader(new StringReader(msg)).readObject();
        String requestType = obj.getString("request_type");
        JsonNumber status = obj.getJsonNumber("status");
        LOG.info("📤 [ОТВЕТ] 📤 thread = {}; user = {}; request = {}; status = {};", 
                  threadNumber, this.name, requestType, status);

        LocalDateTime endTime = LocalDateTime.now();
        Duration diff = Duration.between(startTime, endTime);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss SSS");
        String resultTime = String.format("%02d", diff.toMillisPart());  // Миллисекунды

        // Форматируем время старта и окончания
        String startTimeString = startTime.format(formatter);
        String endTimeString = endTime.format(formatter);

        LOG.info("🧭 [TIME END] thread = {}; user = {}; request = {}; END = {}", 
                  threadNumber, this.name, requestType, endTimeString);
        LOG.info("⌚️ [TIME] thread = {}; user = {}; request = {}; TIME = {}", 
                  threadNumber, this.name, requestType, resultTime);

        // Сохраняем метрики в XML
        MetricXml metricXml = new MetricXml(this.name, requestType, resultTime, this.urlArm, requestName, maxTime);
        try {
            metricXml.saveXml();
        } catch (Exception e) {
            LOG.warn("Ошибка сохранения XML: {}", e.getMessage());
        }

        // Закрытие WebSocket и HttpClient с гарантией
        try {
            if (httpClient != null) {
                httpClient.stop();
            }
            if (client != null) {
                client.stop();
            }
        } catch (Exception e) {
            LOG.warn("Ошибка при остановке клиентов: {}", e.getMessage());
        }
    }
}
