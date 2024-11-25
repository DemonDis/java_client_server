package loader;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.concurrent.Future;
import java.util.UUID;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.InputStream;
import java.io.Reader;
import java.io.BufferedReader;

import java.net.URL;
import java.net.URI;
import java.net.URLConnection;

import javax.json.Json;
import javax.json.JsonReader;
import javax.json.JsonObject;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.security.cert.X509Certificate;

class HttpsRequest implements Runnable {
    private String name;
    private String request_type;
    private String url_arm;
    private String request_name;
    private String max_time;
    private String request_body;
    private String password;
    private String request_role;

    public HttpsRequest(String name, String request_type, String url_arm, String request_name, String max_time, String request_body, String password, String request_role) { 
        this.name = name;
        this.request_type = request_type;
        this.url_arm = url_arm;
        this.request_name = request_name;
        this.max_time = max_time;
        this.request_body = request_body;
        this.password = password;
        this.request_role = request_role;
    }

    private Exception ex;
    static final Logger LOG = Log.getLogger(HttpsRequest.class);

    public void run()  {
        ex=null;
        String urlARMLogin= "https://localhost:3002/v1/login";
        String urlARMRole = "https://localhost:3002/v1/role ";
        String urlARMSocket = "wss://localhost:3001/v1/wss";

        JsonObject login = Json.createObjectBuilder()
            .add("close_previous_session", "true")
            .add("username", this.name)
            .add("password", password)
        .build();

        UUID uuid = UUID.randomUUID();
        String uuidString = uuid.toString();

        TrustManager[] trustAllCerts = new TrustManager[] {
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                public void checkServerTrusted(X509Certificate[] certs, String authType) {}
            }
        };

        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) { return true; }
            };
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

            try(OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream())) {
                LOG.info("🧭 [TIME START] {}; user = {}; START = {}", Thread.currentThread().getName(), this.name, startTimeString);
                writer.write(login.toString());
            }

            InputStream is = con.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            LOG.info("[BR] {}", br);
            // JsonReader reader = Json.createReader(br);
            // JsonObject obj = reader.readObject();

            // String sessionId = obj.getString("session_id");
            // reader.close();
            // Выбор role
            URL url_2 = new URL(urlARMRole);
            HttpsURLConnection con_2 = (HttpsURLConnection) url_2.openConnection();
            con_2.setRequestMethod("POST");
            con_2.setRequestProperty("Content-Type", "application/json; utf-8");
            con_2.setConnectTimeout(10000);

            con_2.setDoOutput(true);
            con_2.setDoInput(true);

            JsonObject role = Json.createObjectBuilder()
                .add("role", request_role)
                .add("session_id", "sessionId")
            .build();

            try(OutputStreamWriter writer = new OutputStreamWriter(con_2.getOutputStream())) {
                writer.write(role.toString());
            }

            InputStream is_2 = con_2.getInputStream();
            InputStreamReader isr_2 = new InputStreamReader(is_2);
            BufferedReader br_2 = new BufferedReader(isr_2);

            JsonReader reader_2 = Json.createReader(br_2);
            JsonObject obj_2 = reader_2.readObject();
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
                    this.name, startTime, Thread.currentThread().getName(), 
                    this.url_arm, request_name, max_time,
                    httpClient, client
                );

                Future<Session> fut = client.connect(socket, URI.create(urlARMSocket));

                String request_socket = String.format(request_body, uuidString, request_type, tokenJWT);
                Session session = fut.get();
                session.getRemote().sendString(request_socket);
                LOG.info("📤 [ЗАПРОС] 📤 {}, user = {}; request = {}\n", Thread.currentThread().getName(), this.name, request_type);

            } catch (Throwable t) { LOG.warn(t); }
        } catch (Exception e) { synchronized(this) { this.ex = ex; } }
    } public synchronized Exception getException() { return ex; }
}