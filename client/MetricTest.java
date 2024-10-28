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

public class MetricTest {
    public static void main(String[] args) {
        ThreadReq threadReq1 = new ThreadReq("login_1");
        Thread thread1 = new Thread(threadReq1);
        thread1.start();
    }
}

class ThreadReq implements Runnable {
    private String name;
    public ThreadReq(String name) { this.name = name; }
    // private Exception ex;

    public void run()  {
        System.out.println("!!!!!");
        // try {

        // } catch (Exception e) { synchronized(this) { this.ex = ex; } }
    }
    // public synchronized Exception getException() { return ex; }
}

