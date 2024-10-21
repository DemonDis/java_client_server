import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;

import java.net.URI;
import java.net.URL;
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

import javax.json.*;
import java.util.concurrent.Future;
import java.security.cert.X509Certificate;

/*
Файл: MetricsTest.java
Описание: Метрики отклика арі
* Права (Copyright): (C) 2024
* @author Di @since 21.10.2024
*/

@WebSocket
public class MetricsTest {
    private static final Logger LOG = Log.getLogger(MetricsTest.class);
    static String url_s = "https://localhost:4500";
    
    static JsonObject login = Json.createObjectBuilder()
        .add("username", "login")
        .add("password", "1010")
    .build();

    public static void main(String[] args) throws Exception {

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

        URL url = new URL(url_s);
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
        reader.close();

        System.out.println("\n" + obj + "\n");

        String url_socket = "wss://localhost:8080/wss/v1";

        SslContextFactory.Client sslContextFactory = new SslContextFactory.Client();

        sslContextFactory.setTrustAll(true);
        sslContextFactory.setEndpointIdentificationAlgorithm("HTTPS");

        HttpClient httpClient = new HttpClient(sslContextFactory);
        try {
            httpClient.start();
            WebSocketClient client = new WebSocketClient(httpClient);
            client.start();
            MetricsTest socket = new MetricsTest();
            Future<Session> fut = client.connect(socket, URI.create(url_socket));
            Session session = fut.get();
            session.getRemote().sendString("Hello");
            session.getRemote().sendString("155-questions-active");
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
    public void onMessage(String msg) {
        LOG.info("onMessage() - {}", msg);
    }
}


// @WebSocket
// public class MetricsTest {
//     private static final Logger LOG = Log.getLogger(MetricsTest.class);

//     public static void main(String[] args) {
//         String url = "wss://localhost:8080/wss/v1";

//         SslContextFactory.Client sslContextFactory = new SslContextFactory.Client();

//         sslContextFactory.setTrustAll(true);
//         sslContextFactory.setEndpointIdentificationAlgorithm("HTTPS");

//         HttpClient httpClient = new HttpClient(sslContextFactory);
//         try {
//             httpClient.start();
//             WebSocketClient client = new WebSocketClient(httpClient);
//             client.start();
//             MetricsTest socket = new MetricsTest();
//             Future<Session> fut = client.connect(socket, URI.create(url));
//             Session session = fut.get();
//             session.getRemote().sendString("Hello");
//             session.getRemote().sendString("155-questions-active");
//         }
//         catch (Throwable t) {
//             LOG.warn(t);
//         }
//     }

//     @OnWebSocketConnect
//     public void onConnect(Session sess) {
//         // LOG.info("onConnect({})", sess);
//     }

//     @OnWebSocketClose
//     public void onClose(int statusCode, String reason) {
//         LOG.info("onClose({}, {})", statusCode, reason);
//     }

//     @OnWebSocketError
//     public void onError(Throwable cause) {
//         LOG.warn(cause);
//     }

//     @OnWebSocketMessage
//     public void onMessage(String msg) {
//         LOG.info("onMessage() - {}", msg);
//     }
// }