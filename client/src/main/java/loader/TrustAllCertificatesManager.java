package loader;

import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;

public class TrustAllCertificatesManager implements X509TrustManager {
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }

    @Override
    public void checkClientTrusted(X509Certificate[] certs, String authType) {
        // Ничего не делаем, так как мы доверяем всем сертификатам
    }

    @Override
    public void checkServerTrusted(X509Certificate[] certs, String authType) {
        // Ничего не делаем, так как мы доверяем всем сертификатам
    }
}
