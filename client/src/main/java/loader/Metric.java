package loader;

import java.io.InputStream;
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

        InputStream config_file = new FileInputStream("./report/_conf.json");
        JsonReader reader = Json.createReader(config_file);
        JsonObject config_fileObject = reader.readObject();
        reader.close();

        JsonArray usersArray = config_fileObject.getJsonArray("users");
        JsonArray requestsArray = config_fileObject.getJsonArray("request");

        MetricLog metricLog = new MetricLog();
        metricLog.saveLogs();

        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setIncludes(new String[]{"**/*.xml"});
        scanner.setBasedir(new File("./report"));
        scanner.setCaseSensitive(false);
        scanner.scan();
        String[] files = scanner.getIncludedFiles();
        for (String file : files) {
            String fileXmlUser = "./report/" + file;
            File file_del = new File(fileXmlUser);
            file_del.delete();
        }

        try {
            MetricXmlMerge metricXmlMerge = new MetricXmlMerge(usersArray, config_fileObject.getString("url"));
            metricXmlMerge.saveXmlMerge();
            MetricXmlRequestList metricXmlRequestList = new MetricXmlRequestList(requestsArray);
            metricXmlRequestList.saveXmlMergeRequestList();
        } catch (ParserConfigurationException e) { System.out.println(e); 
        } catch (TransformerException e) { System.out.println(e); }

        for (int i = 0; i < requestsArray.size(); i++) {

            JsonObject requestObject = requestsArray.getJsonObject(i);
            String request_type = requestObject.getString("request_type");
            String name = requestObject.getString("name");
            String max_time = requestObject.getString("max_time");
            String request_body = requestObject.getString("request_body");
            JsonNumber timeout = requestObject.getJsonNumber("timeout");

            for (int j = 0; j < usersArray.size(); j++) {
            JsonObject usersObject = usersArray.getJsonObject(j);
            String userString = usersObject.getString("user");
            String roleString = usersObject.getString("role");
            String password = usersObject.getString("password");

                Thread threadStart = new Thread( 
                    new HttpsRequest(
                        userString,
                        request_type, 
                        config_fileObject.getString("url"), 
                        name, 
                        max_time,
                        request_body,
                        password,
                        roleString
                    )
                );
                System.out.println(threadStart.getState() + " " + threadStart.getName());
                threadStart.start();
                System.out.println(threadStart.getState() + " " + threadStart.getName());

                if ( j == usersArray.size() - 1) {
                    Thread.sleep(timeout.longValue());
                }
            } 

            if ( i == requestsArray.size() - 1) {
                System.out.println("⛔ END ⛔");
                System.exit(0);
            }
        }
    }
}
