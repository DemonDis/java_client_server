import java.util.concurrent.Future;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.InputStream;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.IOException;

import java.net.URL;
import java.net.URI;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.json.Json;
import javax.json.JsonReader;
import javax.json.JsonObject;
import javax.json.JsonArray;
import javax.json.JsonValue;
import javax.json.JsonString;

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
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.security.cert.X509Certificate;

public class TestThread {
    static String url_send = "https://localhost:4500";
    static String url_send2 = "https://localhost:4501";
    public static void main(String[] args)  {
        
        SendTest sendtest1 = new SendTest("Вася", url_send);
        Thread sendtest_1 = new Thread(sendtest1);
        sendtest_1.start();

        SendTest sendtest2 = new SendTest("Пахан", url_send2);
        Thread sendtest_2 = new Thread(sendtest2);
        sendtest_2.start();

        System.out.println(sendtest_1.getName());

    }
}

class SendHttps {
    static JsonObject login = Json.createObjectBuilder()
        .add("username", "login")
        .add("password", "1010")
    .build();
    private Exception ex;

    public void send(String url_send) {
        try {
            TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

            URL url = new URL(url_send);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

            con.setRequestMethod( "POST" );
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setConnectTimeout(10000);
            con.setDoOutput(true);
            con.setDoInput(true);

            try (OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream())) {
                writer.write(login.toString());
            }

            InputStream is = con.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);

            JsonReader reader = Json.createReader (br);
            JsonObject obj = reader.readObject();

            System.out.println(obj);
        } catch (Exception e) { synchronized(this) { this.ex = ex; } }
    }
}

@WebSocket
public class SendSocket {
    private static final Logger LOG = Log.getLogger(SendSocket.class);
    public void send(String threadNumber) {
        String url_socket = "wss://localhost:8080/wss/v1";

        SslContextFactory.Client sslContextFactory = new SslContextFactory.Client();

        sslContextFactory.setTrustAll(true);
        sslContextFactory.setEndpointIdentificationAlgorithm("HTTPS");

        HttpClient httpClient = new HttpClient(sslContextFactory);
        try {
            httpClient.start();
            WebSocketClient client = new WebSocketClient(httpClient);
            client.start();
            SendSocket socket = new SendSocket();
            Future<Session> fut = client.connect(socket, URI.create(url_socket));
            Session session = fut.get();
            session.getRemote().sendString("{'name': 'Tor 1'}");
            session.getRemote().sendString("{'name': 'Tor 2'}");
            session.getRemote().sendString("{'name': 'Tor 3'}");
            session.getRemote().sendString("{'name': 'Tor 4'}");
            // session.close();
            
        }
        catch (Throwable t) {
            LOG.warn(t);
        }
    }
    @OnWebSocketConnect
    public void onConnect(Session sess) {
        // LOG.info("onConnect({})", sess);
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        LOG.info("onClose({}, {})", statusCode, reason);
    }

    @OnWebSocketError
    public void onError(Throwable cause) {
        LOG.warn(cause);
    }

    @OnWebSocketMessage
    public void onMessage(String msg, String threadNumber) {
        LOG.info("onMessage() - {}", msg , threadNumber);
    }
}

class SendTest implements Runnable {
    private String name;
    private String url_send;
    public SendTest(String name, String url_send) {
        this.name = name;
        this.url_send = url_send;
    }
    public void run() {
        SendHttps sendHttps = new SendHttps();
        sendHttps.send(url_send);
        SendSocket sendSocket = new SendSocket();
        sendSocket.send();
    }
}