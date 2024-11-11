
public class Metric {
    static String url_send = "https://localhost:4500";
    public static void main(String[] args) {

        Send sendtest = new Send("Вася", url_send);
        Thread sendtest_ = new Thread(sendtest);
        sendtest_.start();

        // System.out.println(sendtest_1.getName());
    }
}
