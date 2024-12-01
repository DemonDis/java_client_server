package loader;

import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.io.FileInputStream;
import java.io.File;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonReader;

import org.apache.tools.ant.DirectoryScanner;

public class Metric {
    public static void main(String[] args) throws Exception {

        // InputStream config_file = new FileInputStream("./report/_conf.json");
        // JsonReader reader = Json.createReader(config_file);
        // JsonObject config_fileObject = reader.readObject();
        // reader.close();

        // JsonArray usersArray = config_fileObject.getJsonArray("users");
        // JsonArray requestsArray = config_fileObject.getJsonArray("request");

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
    
        MetricXmlMainPage metricXmlMainPage = new MetricXmlMainPage();
        metricXmlMainPage.createPage();

    }
}
