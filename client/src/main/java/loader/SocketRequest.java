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

    static final Logger LOG = Log.getLogger(SocketRequest.class);
    GlobalStore globalStore = new GlobalStore();

    @OnWebSocketConnect
    public void onConnect(Session session) {
        LOG.info("onConnect: user = {}", this.name);
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        LOG.info("onClose: thread = {}; user = {}; status = {}; reason = {}", threadNumber, this.name, statusCode, reason);
    }

    @OnWebSocketError
    public void onError(Throwable cause) {
        LOG.warn(cause);
    }

    @OnWebSocketMessage
    public void onMessage(String msg) throws ParserConfigurationException, TransformerException {
        // LOG.info("[MSG]: {}", msg);
        UUID uuid = UUID.randomUUID();
        String uuidString = uuid.toString();

        JsonReader reader = Json.createReader(new StringReader(msg));
        JsonObject obj = reader.readObject();
        reader.close();

        String requestType = obj.getString("request_type").intern();
        String correlationId = obj.getString("correlation_id").intern();
        JsonNumber status = obj.getJsonNumber("status");

        if (requestType.equals("form:org:grid") || requestType.equals("form:client:grid")) {
            JsonObject responseObject = obj.getJsonObject("response");
            if (responseObject.size() != 0) {
                JsonArray data = responseObject.getJsonArray("data");
                if (data.size() != 0) {
                    JsonObject usersObject = data.getJsonObject(0);
                    JsonObject buttonObject = usersObject.getJsonObject("button");
                    globalStore.id = buttonObject.getString("id").intern();
                    LOG.info("id = {}", globalStore.id);
                }
            }
        }

        String statusString = status.longValue() == 0 ? "Success" : "Error";
        LOG.info("[RESPONSE] user = {}; request = {}; status = {}; correlation_id = {}", threadNumber, this.name, requestType, statusString);

        LocalDateTime endTime = LocalDateTime.now();
        Duration diff = Duration.between(startTime, endTime);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss SSS");
        String resultTime = String.format("%02d:%02d", diff.toSecondsPart(), diff.toMillisPart());

        String startTimeString = startTime.format(formatter);
        String endTimeString = endTime.format(formatter);
        LOG.info("[TIME END] user = {}; request = {}; END = {}", threadNumber, this.name, endTimeString);

        MetricXml metricXml = new MetricXml(this.name, requestType, resultTime, this.urlArm, requestName, maxTime, status, stand, rerun);

        // MetricLogTxt metricLogTxt = new MetricLogTxt(msg);
        // metricLogTxt.saveLogs();

        if (addRequest.size() >= 0) {
            if (requestStatic.equals(requestType)) {
                metricXml.saveXml();
            }
        }

        // try {
        //     client.stop();
        //     httpClient.stop();
        // } catch (Throwable t) {
        //     LOG.warn("Error while stopping: {}", t);
        // }
    }
}
