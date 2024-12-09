package loader;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.concurrent.Future;
import java.util.UUID;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.InputStream;
import java.io.BufferedReader;

import java.net.URL;
import java.net.URI;

import javax.json.Json;
import javax.json.JsonReader;
import javax.json.JsonObject;
import javax.json.JsonNumber;
import javax.json.JsonArray;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.WebSocketClient;

class HttpsRequest implements Runnable {
    private String name;
    private String request_type;
    private String url_arm;
    private String request_name;
    private String max_time;
    private String request_body;
    private String password;
    private String request_role;
    private JsonArray add_request;
    private JsonNumber rerun;
    private JsonObject client_info;
    private JsonObject org_info;
    private String stand;

    public HttpsRequest(String name, String request_type, String url_arm, String request_name, 
                        String max_time, String request_body, String password, String request_role, 
                        JsonArray add_request, JsonNumber rerun, JsonObject client_info, 
                        JsonObject org_info, String stand) {

        this.name = name;
        this.request_type = request_type;
        this.url_arm = url_arm;
        this.request_name = request_name;
        this.max_time = max_time;
        this.request_body = request_body;
        this.password = password;
        this.request_role = request_role;
        this.add_request = add_request;
        this.rerun = rerun;
        this.client_info = client_info;
        this.org_info = org_info;
        this.stand = stand;
    }

    private Exception ex;
    static final Logger LOG = Log.getLogger(HttpsRequest.class);
    GlobalStore globalStore = new GlobalStore();

    public void run() {
        ex = null;
        String urlARMLogin = "https://" + "localhost:3002" + "/v1/login";
        String urlARMRole = "https://" + "localhost:3002" + "/v1/role";
        String urlARMSocket = "wss://" + "localhost:3001" + "/v1/wss";
        String login = String.format("{\"close_previous_session\":\"true\", \"username\":\"%s\", \"password\":\"%s\"}", 
                                      this.name, password);
        UUID uuid = UUID.randomUUID();
        String uuidString = uuid.toString();

        // Статика для клиента
        String client_search_reason = client_info.getString("search_reason");
        String client_wallet_number = client_info.getString("wallet_number");
        String client_last_name = client_info.getString("last_name");
        String client_first_name = client_info.getString("first_name");

        // Статика для организации
        String org_search_reason = org_info.getString("search_reason");
        String org_wallet_number = org_info.getString("wallet_number");
        String org_short_name = org_info.getString("org_short_name");

        TrustManager[] trustAllCerts = new TrustManager[] { new TrustAllCertificatesManager() };

        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            TrustAllHostsVerifier allHostsValid = new TrustAllHostsVerifier();
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

            // Login
            URL url = new URL(urlARMLogin);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setConnectTimeout(10000);
            con.setDoOutput(true);
            con.setDoInput(true);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss SSS");
            LocalDateTime startTime = LocalDateTime.now();
            String startTimeString = startTime.format(formatter);

            DateTimeFormatter formatterTable = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            String transaction_timestamp_max = startTime.format(formatterTable) + ", 23:59:59";
            LocalDateTime newData = startTime.minusDays(30);
            String transaction_timestamp_min = newData.format(formatterTable) + ", 00:00:00";

            try (OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream())) {
                LOG.info("® [TIME START] (): user = (" + Thread.currentThread().getName() + " : START = " + startTimeString);
                writer.write(login);
            }

            InputStream is = con.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            JsonReader reader = Json.createReader(br);
            JsonObject obj = reader.readObject();
            br.close();
            isr.close();
            reader.close();
            String sessionId = obj.getString("session_id");

            // Выбор role
            URL url_2 = new URL(urlARMRole);
            HttpsURLConnection con_2 = (HttpsURLConnection) url_2.openConnection();
            con_2.setRequestMethod("POST");
            con_2.setRequestProperty("Content-Type", "application/json; utf-8");
            con_2.setConnectTimeout(10000);
            con_2.setDoOutput(true);
            con_2.setDoInput(true);

            String role = String.format("{\"role\":\"%s\", \"session_id\":\"%s\"}", request_role, sessionId);

            try (OutputStreamWriter writer_2 = new OutputStreamWriter(con_2.getOutputStream())) {
                writer_2.write(role);
            }

            InputStream is_2 = con_2.getInputStream();
            InputStreamReader isr_2 = new InputStreamReader(is_2);
            BufferedReader br_2 = new BufferedReader(isr_2);
            JsonReader reader_2 = Json.createReader(br_2);
            JsonObject obj_2 = reader_2.readObject();
            br_2.close();
            isr_2.close();
            reader_2.close();
            String tokenJWT = obj_2.getString("token");

            // Socket
            SslContextFactory.Client sslContextFactory = new SslContextFactory.Client();
            sslContextFactory.setTrustAll(true);
            sslContextFactory.setEndpointIdentificationAlgorithm("HTTPS");
            HttpClient httpClient = new HttpClient(sslContextFactory);

            try {
                httpClient.start();
                WebSocketClient client = new WebSocketClient(httpClient);
                client.start();
                SocketRequest socket = new SocketRequest(
                    httpClient, client, this.name, startTime, 
                    Thread.currentThread().getName(), this.url_arm,
                    request_name, max_time, add_request, 
                    request_type, stand, rerun
                );

                Future<Session> fut = client.connect(socket, URI.create(urlARMSocket));

                // for (int i = 0; i < rerun.longValue(); i++) {
                // Дополнительные запросы
                // if (add_request.size() > 0) {

                    String request_socket = String.format(request_body, uuidString, request_type, tokenJWT);
                    Session session = fut.get();
                    session.getRemote().sendString(request_socket);
                    LOG.info("📤 [ЗАПРОС] 📤 {}, user = {}; request = {}\n", Thread.currentThread().getName(), this.name, request_type);
                // }

                // Основной запрос
                // if (add_request.size() >= 0) {
                //     String request_socket = String.format(request_body, uuidString, request_type, tokenJWT);
                //     Session session = fut.get();
                //     session.getRemote().sendString(request_socket);
                //     LOG.info("📤 [ЗАПРОС] 📤 {}, user = {}; request = {}\n", Thread.currentThread().getName(), this.name, request_type);
                // }
        
            } catch (Throwable t) { LOG.warn(t); } 
        } catch (Exception e) { synchronized(this) { this.ex = ex; } }
    } public synchronized Exception getException() { return ex; }
}