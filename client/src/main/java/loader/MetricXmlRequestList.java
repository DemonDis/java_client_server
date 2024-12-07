package loader;

import org.w3c.dom.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.json.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MetricXmlRequestList {
    private JsonArray requestT; 
    private String stand_name; 
    private JsonArray usersT;

    public MetricXmlRequestList(JsonArray requestT, String stand_name, JsonArray usersT) {
        this.requestT = requestT;
        this.stand_name = stand_name;
        this.usersT = usersT;
    }

    public void saveXmlMergeRequestList() throws ParserConfigurationException, TransformerException, 
                                                    TransformerFactoryConfigurationError, DOMException, IOException {

        JsonArrayBuilder jsonArrayCount = Json.createArrayBuilder();
        JsonArrayBuilder jsonArrayFistLevel = Json.createArrayBuilder();
        JsonArrayBuilder jsonArrayUsersSecondLevel = Json.createArrayBuilder();
         
        JsonObjectBuilder jsonObjectUser = Json.createObjectBuilder();
        JsonObjectBuilder jsonObjectCount = Json.createObjectBuilder();
        
        Map<String, Integer> newArray = new HashMap<String, Integer>();
        ArrayList<String> fakeArray = new ArrayList<String>();

        for (int i = 0; i < requestT.size(); i++) {
            JsonObject requestObject = requestT.getJsonObject(i);
            JsonArray userRequestArray = requestObject.getJsonArray("users");
            int count = 0;

            for (int j = 0; j < usersT.size(); j++) {
                JsonObject usersObject = usersT.getJsonObject(j);
                String userString = usersObject.getString("user");
                String userStringIntern = (new String(userString.substring(0, userString.lastIndexOf('_')))).intern();

                for (int m = 0; m < userRequestArray.size(); m++) {
                    String userRequestStringIntern = (new String(userRequestArray.getString(m))).intern();
                    if (userStringIntern.equals(userRequestStringIntern)) {
                        count++;
                        fakeArray.add(userRequestArray.getString(m));
                    }
                }
            }

            for (int h = 0; h < fakeArray.size(); h++) {
                String tempString = fakeArray.get(h);
                if (!newArray.containsKey(tempString)) {
                    newArray.put(tempString, 1);
                } else {
                    newArray.put(tempString, newArray.get(tempString) + 1);
                }
            }

            for (Map.Entry<String, Integer> entry : newArray.entrySet()) {
                jsonObjectUser.add("user", entry.getKey());
                jsonObjectUser.add("total", entry.getValue());
                jsonArrayFistLevel.add(jsonObjectUser);
            }

            jsonArrayUsersSecondLevel.add(jsonArrayFistLevel);
            fakeArray.clear();
            newArray.clear();

            jsonObjectCount.add("col", "" + count);
            JsonObject newObject = jsonObjectCount.build();
            jsonArrayCount.add(newObject);
        }

        JsonArray newArrayCount = jsonArrayCount.build();
        JsonArray array = jsonArrayUsersSecondLevel.build();

        String filePath = "./report/stands/" + stand_name + "/_request.xml";
        File file = new File(filePath);

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        if (file.exists()) {
            file.delete();
        }

        try (FileOutputStream output = new FileOutputStream(filePath)) {

            Document doc = docBuilder.newDocument();
            Element list = doc.createElement("list");
            doc.appendChild(list);

            for (int i = 0; i < requestT.size(); i++) {

                JsonArray requestLevel = array.getJsonArray(i);
                JsonObject requestObject = requestT.getJsonObject(i);
                String request_type = requestObject.getString("request_type");
                String name = requestObject.getString("name");

                JsonObject requestForCol = newArrayCount.getJsonObject(i);
                String col = requestForCol.getString("col");

                Element user = doc.createElement("url");
                user.setAttribute("name", request_type);
                user.setAttribute("request", name);
                user.setAttribute("quantity", col);
                list.appendChild(user);

                JsonArray usersArray = requestObject.getJsonArray("users");
                for (int j = 0; j < usersArray.size(); j++) {

                    JsonObject userLevel = requestLevel.getJsonObject(j);
                    String userName = userLevel.getString("user");
                    JsonNumber total = userLevel.getJsonNumber("total");

                    Element users = doc.createElement("user");
                    users.setTextContent(userName);
                    users.setAttribute("total", total.toString());
                    user.appendChild(users);
                }
            }

            writeXml(doc, output);
        }
    }

    private static void writeXml(Document doc, OutputStream output) throws TransformerFactoryConfigurationError, TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(output);
        transformer.transform(source, result);
    }
}
