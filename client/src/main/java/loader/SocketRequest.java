package loader;

import java.time.LocalDateTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

import java.io.Reader;
import java.io.IOException;
import java.io.StringReader;
import java.io.FileWriter;
import java.io.File;

import javax.json.Json;
import javax.json.JsonReader;
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

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import java.security.cert.X509Certificate;
import java.util.UUID;

@WebSocket
public class SocketRequest {
    private String name;
    private LocalDateTime startTime;
    private String threadNumber;
    private String url_arm;
    private String request_name;
    private String max_time;

    public SocketRequest(String name, LocalDateTime startTime, String threadNumber, String url_arm, String request_name, String max_time) {
        this.name = name; 
        this.startTime = startTime;
        this.threadNumber = threadNumber;
        this.url_arm = url_arm;
        this.request_name = request_name;
        this.max_time = max_time;
    }

    static final Logger LOG = Log.getLogger(SocketRequest.class);

    @OnWebSocketConnect
    public void onConnect(Session sess) {
        LOG.info("🔗 onConnect 🔗 user = {}", this.name);
    }
    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        LOG.info("🔐 onClose 🔐 {}; user = {}; ({}, {})", threadNumber, this.name, statusCode, reason);
    };
    @OnWebSocketError
    public void onError(Throwable cause) {
        LOG.warn(cause);
    };
    @OnWebSocketMessage
    public void onMessage(String msg) throws ParserConfigurationException, TransformerException {
        UUID uuid = UUID.randomUUID();
        String uuidString = uuid.toString();

        JsonReader reader = Json.createReader( new StringReader(msg) );
        JsonObject obj = reader.readObject();
        String request_type = obj.getString("request_type");
        JsonNumber status = obj.getJsonNumber("status");
        LOG.info("📤 [ОТВЕТ] 📤 {}; user = {}; request = {}; status = {};", threadNumber, this.name, request_type, status);

        LocalDateTime endTime = LocalDateTime.now();
        Duration diff = Duration.between(startTime, endTime);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss SSS");
        // String resultTime = String.format("%d:%02d:%02d:%02d", diff.toHours(), diff.toMinutesPart(), diff.toSecondsPart(), diff.toMillisPart());
        String resultTime = String.format("%02d", diff.toSecondsPart());
        String startTimeString = startTime.format(formatter);
        String endTimeString = endTime.format(formatter);

        LOG.info("🧭 [TIME END] {}; user = {}; request = {}; END = {}", threadNumber, this.name, request_type, endTimeString);
        LOG.info("⌚️ [TIME] ⌚️ {}; user = {}; request = {}; TIME = {}",threadNumber, this.name, request_type, resultTime);

        MetricXml metricXml = new MetricXml(this.name, request_type, resultTime, this.url_arm, request_name, max_time);
        metricXml.saveXml();
    };
}