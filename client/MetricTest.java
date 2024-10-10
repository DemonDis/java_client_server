import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.OutputStreamWriter;

import java.security.cert.X509Certificate;

import java.net.URL;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net. ssl.SSLContext;
import javax.net.ssl.SSLSession;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import javax.json.*;

/*
Файл: MetricTest.java
Описание: Метрики отклика арі
* Права (Copyright): (C) 2024
* @author Di @since 10.10.2024
*/

public class MetricTest {
    static String login = "{\"username\": \"login\", \"password\":\"1010\"}";
    static String url_s = "https://localhost:4500";
    
    public static void main(String[] args) throws Exception {
 
    String json = "{'id': 1001,'firstName': 'Lokesh','lastName': 'Gupta','email': null}";
 
    JsonReader jsonReader = new JsonReader(new StringReader(json));
    jsonReader.setLenient(true);
    
        // JsonObject object = jsonReader.readObject();
        // jsonReader.close();
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
            writer.write(login);
        }
    
        String str = "{\"username\": \"login\", \"password\":\"1010\"}";

        Reader reader = new InputStreamReader(con.getInputStream());
        
        while (true) {
            int ch = reader.read();
            if (ch==-1) {
                break;
            }
            System.out.print((char)ch);
        }
    }
}