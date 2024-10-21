import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.CloseReason;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

import org.glassfish.grizzly.ssl.SSLContextConfigurator;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;
import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.container.grizzly.*;
// import org.glassfish.tyrus.container.grizzly.GrizzlyClientContainer;

public class ClientWebSocketEndpoint extends Endpoint {

    public static void main(String[] a) throws IOException{
        // igniteConfiguration.setQueryThreadPoolSize(2);
        ClientManager client = ClientManager.createClient();
        // client.getProperties().put(GrizzlyClientContainer.SHARED_CONTAINER, true);

        client.getProperties().put("org.glassfish.tyrus.incomingBufferSize", 17000000);

        
        //System.getProperties().put("javax.net.debug", "all");
        final SSLContextConfigurator defaultConfig = new SSLContextConfigurator();

        defaultConfig.retrieve(System.getProperties());

        SSLEngineConfigurator sslEngineConfigurator =
            new SSLEngineConfigurator(defaultConfig, true, false, false);
        client.getProperties().put(GrizzlyEngine.SSL_ENGINE_CONFIGURATOR,
            sslEngineConfigurator);
        Session session = null;
        final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

        try {
            
            session = client.connectToServer(ClientWebSocketEndpoint.class, cec, new URI("wss://qa.sockets.stackexchange.com/"));// or "wss://echo.websocket.org"

        } catch (DeploymentException | URISyntaxException e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen())
                session.close(new CloseReason(CloseReason.CloseCodes.GOING_AWAY, "Bye"));

        }

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        br.readLine();
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        session.addMessageHandler(new MessageHandler.Whole<String>() {

            @Override
            public void onMessage(String message) {
                System.out.println("Received message: "+message);
            }
        });
        try {
            session.getBasicRemote().sendText("1-questions-active");
            session.getBasicRemote().sendText("155-questions-active");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}