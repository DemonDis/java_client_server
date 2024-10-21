package example;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpClientTest {

    public static void main(String[] args) throws Exception {

        HttpClient client = HttpClient.newBuilder().build();
        String json = "{\"send_request\":\"Отправляю из java-client\", \"request_type\":\"{}\"}";
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:3000/article"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(System.out::println)
                .join();

    }
}