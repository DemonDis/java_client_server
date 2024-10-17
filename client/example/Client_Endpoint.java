package example;
import java.io.*;
import java.net.URI;
import javax.websocket.*;
import org.glassfish.tyrus.client.ClientManager;
import java.net.URISyntaxException;

@ClientEndpoint
public class Client_Endpoint {
  @OnOpen
  public void onOpen(Session Client_Session) {
    System.out.println("--- Connection Successful " + Client_Session.getId());
    try {
      Client_Session.getBasicRemote().sendText("Start");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @OnMessage
  public String onMessage(String Client_Message, Session Client_Session) {
    BufferedReader Buffered_Reader = new BufferedReader(new InputStreamReader(System.in));
    try {
      System.out.println("--- Message Received " + Client_Message);
      String User_Input = Buffered_Reader.readLine();
      return User_Input;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @OnClose
  public void onClose(Session Client_Session, CloseReason Close_Reason) {
    System.out.println("--- Session ID: " + Client_Session.getId());
    System.out.println("--- Closing Reason: " + Close_Reason);
  }

  public static void main(String[] args) {
    ClientManager Client_Manager = ClientManager.createClient();
    try {
      URI uri = new URI("ws://localhost:8080");
      Client_Manager.connectToServer(Client_Endpoint.class, uri);
      while (true) {
      }
    } catch (DeploymentException | URISyntaxException | InterruptedException e) {
      e.printStackTrace();
    }
  }
}