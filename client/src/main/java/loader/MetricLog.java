package loader;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.io.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.*;

public class MetricLog {
    public void saveLogs() throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        LocalDateTime startTime = LocalDateTime.now();
        String startTimeString = startTime.format(formatter);

        JsonNodeFactory factory = new JsonNodeFactory(false);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode employee = factory.objectNode();
        employee.put("empId", 66);
        employee.put("firstName", "firstName");
        employee.put("lastName", "lastName");
        ArrayNode technologies = factory.arrayNode();
        technologies.add("111").add("222").add("333");
        employee.set("technologies", technologies);
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(employee));

        try {
            File myObj = new File("./log/" + startTimeString + "_log.json");
            FileWriter myWriter = new FileWriter("./log/" + startTimeString + "_log.json");

            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
                myWriter.write(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(employee));
                myWriter.close();
            }
        } catch (IOException e) {
            System.out.println("📢 An error occurred");
            e.printStackTrace();
        }

    }
}