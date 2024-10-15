import java.io.IOException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.EncodeException;
import javax.websocket.WebSocketContainer;

import org.glassfish.grizzly.ssl.SSLContextConfigurator;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;
import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.client.ClientProperties;

import java.util.logging.Level;
import java.util.logging.Logger;

public class App {

    public static void main(String[] args) {

        final WebSocketContainer container = ContainerProvider.getWebSocketContainer();

        String url = "wss://10.177.170.140:8443/signaling"; // or
        // "wss://echo.websocket.org"
        final ClientManager client = ClientManager.createClient();

        System.getProperties().put(SSLContextConfigurator.KEY_STORE_FILE, "/key/server");
        System.getProperties().put(SSLContextConfigurator.TRUST_STORE_FILE, "/key/server");
        System.getProperties().put(SSLContextConfigurator.KEY_STORE_PASSWORD, "123456");
        System.getProperties().put(SSLContextConfigurator.TRUST_STORE_PASSWORD, "123456");

        System.out.println("propery : " + System.getProperty(SSLContextConfigurator.KEY_STORE_FILE));

        final SSLContextConfigurator defaultConfig = new SSLContextConfigurator();

        defaultConfig.retrieve(System.getProperties());
        // or setup SSLContextConfigurator using its API.

        SSLEngineConfigurator sslEngineConfigurator = new SSLEngineConfigurator(defaultConfig, true, false, false);

        client.getProperties().put(ClientProperties.SSL_ENGINE_CONFIGURATOR, sslEngineConfigurator);
        System.out.println("put properties");
        try (javax.websocket.Session session = client.connectToServer(MyClientEndpoint.class, URI.create(url))) {
            for (int i = 1; i <= 10; ++i) {
                try {
                    System.out.println("send");
                    session.getBasicRemote().sendObject("init");
                    Thread.sleep(1000);
                } catch (EncodeException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (DeploymentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

 }


@ClientEndpoint
class MyClientEndpoint {
    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Connected to endpoint: " + session.getBasicRemote());
        try {
            String name = "Duke";
            System.out.println("Sending message to endpoint: " + name);
            session.getBasicRemote().sendText(name);
        } catch (IOException ex) {
            Logger.getLogger(MyClientEndpoint.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}