package example;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;

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
* @author Di @since 21.10.2024
*/

public class HttpsClientTest {
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
        // TrustAllCertificates trustAllCerts = new TrustAllCertificates();

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
    }
}