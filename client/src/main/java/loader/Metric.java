package loader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.io.FileInputStream;
import java.io.File;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonNumber;
import javax.json.JsonReader;
import org.apache.tools.ant.DirectoryScanner;

public class Metric {
    public static void main(String[] args) throws Exception {

        InputStream config_file = new FileInputStream("./report/_conf.json");
        JsonReader reader = Json.createReader(config_file);
        JsonObject config_fileObject = reader.readObject();
        reader.close();

        JsonArray usersArray = config_fileObject.getJsonArray("users");
        JsonArray requestsArray = config_fileObject.getJsonArray("request");

        // MetricLog metricLog = new MetricLog();
        // metricLog.saveLogs();

        // FileWork fileWork = new FileWork();
        // fileWork.doFile();

        // ExecutorService executorService = Executors.newFixedThreadPool(usersArray.size());
        
        // for (int i = 0; i < requestsArray.size(); i++) {

        //     JsonObject requestObject = requestsArray.getJsonObject(i);
        //     String request_type = requestObject.getString("request_type");
        //     String name = requestObject.getString("name");
        //     String max_time = requestObject.getString("max_time");
        //     String request_body = requestObject.getString("request_body");
        //     JsonNumber timeout = requestObject.getJsonNumber("timeout");

        //     for (int j = 0; j < usersArray.size(); j++) {
        //         JsonObject usersObject = usersArray.getJsonObject(j);
        //         String userString = usersObject.getString("user");
        //         String roleString = usersObject.getString("role");
        //         String password = usersObject.getString("password");
                
        //         executorService.execute(
        //             new HttpsRequest(
        //                 userString,
        //                 request_type, 
        //                 config_fileObject.getString("url"), 
        //                 name, 
        //                 max_time,
        //                 request_body,
        //                 password,
        //                 roleString
        //             )
        //         );

        //         if (j == usersArray.size() - 1) {
        //             System.out.println("📢 [TIMEOUT] " + timeout.longValue());
        //             TimeUnit.SECONDS.sleep(timeout.longValue());
        //         }
        //     } 

        //     if ( i == requestsArray.size() - 1) {
        //         System.out.println("⛔ END ⛔");
        //         System.exit(0);
        //     }
        // }
    
        // MetricXmlMainPage metricXmlMainPage = new MetricXmlMainPage();
        // metricXmlMainPage.createPage();

		Map<String, Integer> letters = new HashMap<String, Integer>();
		ArrayList<String> fakeArray = new ArrayList<String>();

        JsonArrayBuilder jsonArray = Json.createArrayBuilder();
        JsonObjectBuilder jsonObject = Json.createObjectBuilder();
        JsonArrayBuilder jsonArrayUsers = Json.createArrayBuilder();

        for (int i = 0; i < requestsArray.size(); i++) {
            JsonObject requestObject = requestsArray.getJsonObject(i);
            JsonArray try_user = requestObject.getJsonArray("try_user");
    
            // System.out.println("📢 " + try_user);

            for (int j = 0; j < usersArray.size(); j++) {
                JsonObject usersObject = usersArray.getJsonObject(j);
                String userString = usersObject.getString("user");
                String userCut = (new String(userString.substring(0, userString.lastIndexOf('_')))).intern();

                for (int m = 0; m < try_user.size(); m++) {
                    String userC = (new String(try_user.getString(m))).intern();

                    if (userC == userCut) {
                        // System.out.println("⛔ " + try_user.getString(m));
                        fakeArray.add(try_user.getString(m));
                    }
                }
            } 
          
            for (int h = 0; h < fakeArray.size(); h++) {
                String tempChar = fakeArray.get(h);
    
                if (!letters.containsKey(tempChar)) {
                    letters.put(tempChar, 1);
                } else {
                    letters.put(tempChar, letters.get(tempChar) + 1);
                }
            }


            for (Map.Entry<String, Integer> entry : letters.entrySet()) {
                jsonObject.add("user", entry.getKey());
                jsonObject.add("total", entry.getValue());
                jsonArray.add(jsonObject);
                
                System.out.println("Пользователь = " + entry.getKey() + ", Повторений = " + entry.getValue());
            }
            jsonArrayUsers.add(jsonArray);

            System.out.println("============");
            fakeArray.clear();
            letters.clear();

        }

        JsonArray array = jsonArrayUsers.build();
        System.out.println("⛔ " + array);
        
    }
}
