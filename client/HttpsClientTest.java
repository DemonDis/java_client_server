import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

public class HttpsClientTest {

	// private static final String USER_AGENT = "Mozilla/5.0";

	// private static final String GET_URL = "https://localhost:9090/SpringMVCExample";

	// private static final String POST_URL = "https://localhost:9090/SpringMVCExample/home";

	private static final String POST_PARAMS = "userName=Pankaj";
    public static void main(String[] args) throws Exception {
        // Create a trust manager that does not validate certificate chains
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
 
        // Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
 
        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        String myData = "{\"username\":\"username\",\"password\":\"password\"}";
        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
 
        URL url = new URL("https://localhost:4500/");
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        con.setRequestMethod( "POST" );
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setUseCaches (true);
        con.setDoOutput(true);
        con.setDoInput(true);
 
        // try(OutputStream os = con.getOutputStream()) {
        //     byte[] input = POST_PARAMS.getBytes("utf-8");
        //     os.write(input, 0, input.length);           
        // }         
    
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