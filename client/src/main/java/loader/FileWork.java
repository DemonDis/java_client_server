package loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.tools.ant.DirectoryScanner;

public class FileWork {
    public void doFile() throws Exception {
        
        InputStream config_file = new FileInputStream("./report/_conf.json");
        JsonReader reader = Json.createReader(config_file);
        JsonObject config_fileObject = reader.readObject();
        
        reader.close();

        JsonArray usersArray = config_fileObject.getJsonArray("users");
        JsonArray requestsArray = config_fileObject.getJsonArray("request");

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
    }
}
