package example;
import java.io.FileInputStream;
import java.io.InputStream;

import java.io.Reader;

import javax.json.*;

public class JsonWork {

    public static void main(String[] args) throws Exception {
        InputStream fis = new FileInputStream("person.json");
     
        JsonReader reader = Json.createReader(fis);
     
        JsonObject personObject = reader.readObject();
     
        reader.close();
     
        System.out.println("Name   : " + personObject.getString("name"));
        System.out.println("Age    : " + personObject.getInt("age"));
        System.out.println("Married: " + personObject.getBoolean("isMarried"));

        JsonObject addressObject = personObject.getJsonObject("address");
        System.out.println("Address: ");
        System.out.println(addressObject.getString("street"));
        System.out.println(addressObject.getString("zipCode"));
     
        System.out.println("Phone  : ");
        JsonArray phoneNumbersArray = personObject.getJsonArray("phoneNumbers");

        for (JsonValue jsonValue : phoneNumbersArray) {
            System.out.println(jsonValue.toString());
        }
    }
}