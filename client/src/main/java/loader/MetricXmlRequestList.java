package loader;

import org.w3c.dom.*;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
        JsonArrayBuilder jsonArrayUsersSecondLevel = Json.createArrayBuilder();
        JsonObjectBuilder jsonObjectUser = Json.createObjectBuilder();
        JsonObjectBuilder jsonObjectCount = Json.createObjectBuilder();

        // Проходим по всем запросам
        for (int i = 0; i < requestT.size(); i++) {
            JsonObject requestObject = requestT.getJsonObject(i);
            JsonArray userRequestArray = requestObject.getJsonArray("users");
            int count = 0;

            Map<String, Integer> userMap = new HashMap<>();

            // Для каждого пользователя проверяем, сколько раз он встречается
            for (int j = 0; j < usersT.size(); j++) {
                JsonObject usersObject = usersT.getJsonObject(j);
                String userString = usersObject.getString("user");
                String userStringIntern = userString.substring(0, userString.lastIndexOf('_')).intern();

                for (int m = 0; m < userRequestArray.size(); m++) {
                    String userRequestStringIntern = userRequestArray.getString(m).intern();
                    if (userStringIntern.equals(userRequestStringIntern)) {
                        count++;
                        userMap.merge(userRequestArray.getString(m), 1, Integer::sum);
                    }
                }
            }

            // Добавляем пользователей и их количество в jsonArrayFistLevel
            JsonArrayBuilder jsonArrayFistLevel = Json.createArrayBuilder();
            for (Map.Entry<String, Integer> entry : userMap.entrySet()) {
                jsonObjectUser.add("user", entry.getKey());
                jsonObjectUser.add("total", entry.getValue());
                jsonArrayFistLevel.add(jsonObjectUser.build());
            }

            jsonArrayUsersSecondLevel.add(jsonArrayFistLevel.build());
            userMap.clear();

            // Строим объект для кол-ва пользователей
            jsonObjectCount.add("col", String.valueOf(count));
            jsonArrayCount.add(jsonObjectCount.build());
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

            // Строим элементы XML для каждого запроса
            for (int i = 0; i < requestT.size(); i++) {
                JsonObject requestObject = requestT.getJsonObject(i);
                JsonArray requestLevel = array.getJsonArray(i);
                String request_type = requestObject.getString("request_type");
                String name = requestObject.getString("name");

                JsonObject requestForCol = newArrayCount.getJsonObject(i);
                String col = requestForCol.getString("col");

                // Добавляем элементы URL в XML
                Element userElement = doc.createElement("url");
                userElement.setAttribute("name", request_type);
                userElement.setAttribute("request", name);
                userElement.setAttribute("quantity", col);
                list.appendChild(userElement);

                // Добавляем пользователей к каждому запросу
                JsonArray usersArray = requestObject.getJsonArray("users");
                for (int j = 0; j < usersArray.size(); j++) {
                    JsonObject userLevel = requestLevel.getJsonObject(j);
                    String userName = userLevel.getString("user");
                    JsonNumber total = userLevel.getJsonNumber("total");

                    Element users = doc.createElement("user");
                    users.setTextContent(userName);
                    users.setAttribute("total", total.toString());
                    userElement.appendChild(users);
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
