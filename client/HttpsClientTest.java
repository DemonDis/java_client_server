import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

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
        // char[] chars = new char[]{'A','B','C','D','E'};
        String urlParameters  = "param1=a&param2=b&param3=c";
        byte[] postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );
        int    postDataLength = postData.length;
        String request        = "https://localhost:4500/";
        // String request        = "https://localhost:3002";
        // URL    url            = new URL( request );

        URL url = new URL(request);
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        String json = "{\"name\":\"John\",\"age\":30,\"city\":\"New York\"}";
        String LINE_END = "\r\n";

        con.setRequestMethod( "POST" );
        con.setInstanceFollowRedirects( false );
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Access-Control-Allow-Origin", "*");
        con.setRequestProperty("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With");
        con.setRequestProperty("Access-Control-Allow-Credentials", "true");
        con.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));

        con.setUseCaches (false);
        con.setDoOutput(true);
        con.setDoInput(true);
        con.connect(); 
      
        try(DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
            // System.out.print(myData);
            wr.writeBytes( json );
            wr.flush();
            // wr.close();
        }
        
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