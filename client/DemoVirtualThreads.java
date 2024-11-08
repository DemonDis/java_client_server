import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

interface ResponseCallBack{
  void onResponse(String a);
  void onError(Exception e);
}

public static void request(String request, ResponseCallBack responseCallBack) {
  
  new Thread(() -> {
      try {
          HttpURLConnection conn = (HttpURLConnection) new URL(request).openConnection();
          conn.setRequestMethod("GET");
          conn.setDoInput(true);
          BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
          String line, text = "";
          while((line = br.readLine()) != null) {
              text = line;
          }
          br.close();
          responseCallBack.onResponse(text);
      } catch (IOException e) {
          e.printStackTrace();
          responseCallBack.onError(e);
      }
  }).start();
}

request("", new ResponseCallBack() {
  @Override
  public void onResponse(String a) {
      //here string will be available
  }

  @Override
  public void onError(Exception e) {
      //error in case something failed
  }
});