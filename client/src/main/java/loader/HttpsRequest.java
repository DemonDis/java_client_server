package loader;

import java.util.UUID;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.InputStream;
import java.io.BufferedReader;
import java.net.URL;
import javax.json.Json;
import javax.json.JsonReader;
import javax.json.JsonObject;
import javax.json.JsonNumber;
import javax.json.JsonArray;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

public class HttpsRequest implements Runnable {
    private String name;
    private String requestType;
    private String urlArm;
    private String requestName;
    private String maxTime;
    private String requestBody;
    private String password;
    private String requestRole;
    private JsonArray addRequest;
    private JsonNumber rerun;
    private JsonObject clientInfo;
    private JsonObject orgInfo;
    private String stand;
    private Exception ex;

    static final Logger LOG = Log.getLogger(HttpsRequest.class);
    private GlobalStore globalStore = new GlobalStore();

    public HttpsRequest(String name, String requestType, String urlArm, String requestName, String maxTime, 
                        String requestBody, String password, String requestRole, JsonArray addRequest, 
                        JsonNumber rerun, JsonObject clientInfo, JsonObject orgInfo, String stand) {
        this.name = name;
        this.requestType = requestType;
        this.urlArm = urlArm;
        this.requestName = requestName;
        this.maxTime = maxTime;
        this.requestBody = requestBody;
        this.password = password;
        this.requestRole = requestRole;
        this.addRequest = addRequest;
        this.rerun = rerun;
        this.clientInfo = clientInfo;
        this.orgInfo = orgInfo;
        this.stand = stand;
    }

    @Override
    public void run() {
        ex = null;
        String urlARMLogin = "https://" + urlArm + "/v1/login";
        String urlARMRole = "https://" + urlArm + "/v1/role";
        String urlARMSocket = "wss://" + "localhost:3001" + "/v1/wss";

        String login = String.format("{\"close_previous_session\":\"true\", \"username\": \"%s\", \"password\": \"%s\"}", this.name, password);
        UUID uuid = UUID.randomUUID();
        String uuidString = uuid.toString();

        // Статика для клиента
        String clientSearchReason = clientInfo.getString("search_reason");
        String clientWalletNumber = clientInfo.getString("wallet_number");
        String clientLastName = clientInfo.getString("last_name");
        String clientFirstName = clientInfo.getString("first_name");

        // Статика для организации
        String orgSearchReason = orgInfo.getString("search_reason");
        String orgWalletNumber = orgInfo.getString("wallet_number");
        String orgShortName = orgInfo.getString("org_short_name");

        try {
            // Создаем контекст SSL для обхода проверки сертификатов (не рекомендуется для продакшн-среды)
            TrustManager[] trustAllCerts = new TrustManager[]{new TrustAllCertificatesManager()};
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            TrustAllHostsVerifier allHostsValid = new TrustAllHostsVerifier();
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

            // Логин
            URL url = new URL(urlARMLogin);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setConnectTimeout(10000);
            con.setDoOutput(true);
            con.setDoInput(true);

            try (OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream())) {
                LOG.info("® [TIME START] () user = {}: START = {}", Thread.currentThread().getName(), this.name, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss SSS")));
                writer.write(login);
            }

            // Чтение ответа
            try (InputStream is = con.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                JsonReader reader = Json.createReader(br)) {
                JsonObject obj = reader.readObject();
                String sessionId = obj.getString("session_id");

                // Выбор роли
                URL url2 = new URL(urlARMRole);
                HttpsURLConnection con2 = (HttpsURLConnection) url2.openConnection();
                con2.setRequestMethod("POST");
                con2.setRequestProperty("Content-Type", "application/json; utf-8");
                con2.setConnectTimeout(10000);
                con2.setDoOutput(true);
                con2.setDoInput(true);

                String role = String.format("{\"role\":\"%s\", \"session_id\":\"%s\"}", requestRole, sessionId);

                try (OutputStreamWriter writer2 = new OutputStreamWriter(con2.getOutputStream())) {
                    writer2.write(role);
                }

                // Чтение ответа для роли
                try (InputStream is2 = con2.getInputStream();
                    InputStreamReader isr2 = new InputStreamReader(is2);
                    BufferedReader br2 = new BufferedReader(isr2);
                    JsonReader reader2 = Json.createReader(br2)) {
                    JsonObject obj2 = reader2.readObject();
                    String tokenJWT = obj2.getString("token");

                    // Сокет
                    SslContextFactory.Client sslContextFactory = new SslContextFactory.Client();
                    sslContextFactory.setTrustAll(true);
                    sslContextFactory.setEndpointIdentificationAlgorithm("HTTPS");
                    HttpClient httpClient = new HttpClient(sslContextFactory);
                    httpClient.start();

                    WebSocketClient client = new WebSocketClient(httpClient);
                    client.start();

                    // Подключение к WebSocket
                    // SocketRequest socketRequest = new SocketRequest(
                    //     httpClient, client, 
                    //     this.name, LocalDateTime.now(), 
                    //     Thread.currentThread().getName(), this.urlArm, requestName, 
                    //     maxTime, addRequest, requestType, stand, rerun
                    // );

                    // Future<Session> fut = client.connect(socketRequest, URI.create(urlARMSocket));

                    // Дополнительные запросы
                    if (addRequest.size() > 0) {
                        Thread.sleep(500);
                    }

                    // Основной запрос
                    if (addRequest.size() >= 0) {
                        // Обработка основного запроса
                    }

                }
            }
        } catch (Exception e) {
            LOG.warn(e);
            synchronized (this) {
                this.ex = e;
            }
        }
    }

    public synchronized Exception getException() {
        return ex;
    }
}
