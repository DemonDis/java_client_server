
import java.net.URI;
import javax.websocket.*;

import org.glassfish.grizzly.ssl.SSLContextConfigurator;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;
import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.client.ClientProperties;

@ClientEndpoint
public class WSSClient {
   private static Object waitLock = new Object();

   @OnMessage
   public void onMessage(String message) {
      System.out.println("Received msg: " + message);        
   }

   private static void  wait4TerminateSignal() {
      synchronized(waitLock) {
         try { waitLock.wait(); } 
         catch (InterruptedException e) {}
      }
   }

   public static void main(String[] args) {
      String socketUrl = "wss://localhost:8080/wss/v1";
      WebSocketContainer container=null;
      Session session=null;
      
      try {


         ClientManager client = ClientManager.createClient();

         //System.getProperties().put("javax.net.debug", "all"); // Useful for debugging SSL interaction
         // The keystore in the next two lines is the same keystore you used for running the server,
         // likely in ${jetty.base}/etc/keystore
         System.getProperties().put(SSLContextConfigurator.KEY_STORE_FILE, "./arm.jks");
         System.getProperties().put(SSLContextConfigurator.TRUST_STORE_FILE, "./arm.jks");
         // The following two passwords are what you used for your self-signed cert
         System.getProperties().put(SSLContextConfigurator.KEY_STORE_PASSWORD, "MY_PASSWORD");
         System.getProperties().put(SSLContextConfigurator.TRUST_STORE_PASSWORD, "MY_PASSWORD");
         final SSLContextConfigurator defaultConfig = new SSLContextConfigurator();

         defaultConfig.retrieve(System.getProperties());
         // or setup SSLContextConfigurator using its API.

         SSLEngineConfigurator sslEngineConfigurator = new SSLEngineConfigurator(defaultConfig, true, false, false);
         client.getProperties().put(ClientProperties.SSL_ENGINE_CONFIGURATOR, sslEngineConfigurator);
         client.connectToServer(WSSClient.class ,  new URI("wss://localhost:8080/wss/v1"));
         System.out.println ("Connected .... ");



         // container = ContainerProvider.getWebSocketContainer(); 
         // session=container.connectToServer(WSSClient.class, URI.create(socketUrl)); 
         
         wait4TerminateSignal();
      } catch (Exception e) { e.printStackTrace(); }
      finally  {
         if(session!=null) {
            try { session.close(); } 
            catch (Exception e) { e.printStackTrace(); }
         }         
      } 
   }
}