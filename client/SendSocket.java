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

@WebSocket
public class SendSocket {
    private static final Logger LOG = Log.getLogger(SendSocket.class);
    public void send() {
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
    public void onMessage(String msg) {
        LOG.info("onMessage() - {}", msg);
    }
}