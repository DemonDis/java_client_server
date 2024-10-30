
public class Metric {
    static String url_send = "https://localhost:4500";
    static String url_send2 = "https://localhost:4501";
    public static void main(String[] args) {

        Send sendtest2 = new Send("Вася", url_send);
        Thread sendtest_2 = new Thread(sendtest2);
        sendtest_2.start();

        // System.out.println(sendtest_1.getName());
    }
}
