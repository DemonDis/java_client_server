public class Send implements Runnable {
    private String name;
    private String url_send;
    public Send(String name, String url_send) {
        this.name = name;
        this.url_send = url_send;
    }
    public void run() {
        SendHttps sendHttps = new SendHttps();
        sendHttps.send(url_send);
        SendSocket sendSocket = new SendSocket();
        sendSocket.send();
    }
}