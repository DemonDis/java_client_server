package loader;

// import java.io.File;

// public class MetricXmlMainPage {
//     public void createPage() throws Exception {
         
//         // определяем объект для каталога
//         File dir = new File("report/stands");
//         // если объект представляет каталог
//         if(dir.isDirectory())
//         {
//             // получаем все вложенные объекты в каталоге
//             for(File item : dir.listFiles()){
              
//                  if(item.isDirectory()){
                      
//                      System.out.println("!!!!!!!!!!!! " + item.getName() + "  \t folder");
//                  }
//                  else{
                      
//                      System.out.println("!!!!!!!!!!!! " + item.getName() + "\t file");
//                  }
//              }
//         }
//     }
// }

import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;



public class MetricXmlMainPage {
    
    private static ArrayList<Employee> employees = new ArrayList<>();

    public void createPage() throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File("report/stands/dev/xml_file1.xml"));
        NodeList employeeElements = document.getDocumentElement().getElementsByTagName("list");

        
        for (int i = 0; i < employeeElements.getLength(); i++) {
            Node employee = employeeElements.item(i);
            NamedNodeMap attributes = employee.getAttributes();            
            employees.add(new Employee(
                attributes.getNamedItem("date").getNodeValue(),
                attributes.getNamedItem("url").getNodeValue(),
                attributes.getNamedItem("stand").getNodeValue()
            ));
        }
        for (Employee employee : employees)
            System.out.println(String.format("Информации о сотруднике: имя - %s, должность - %s.", employee.getName(), employee.getJob()));
    }
}

class Employee {
    private String date, url, stand;

    public Employee(String date, String url, String stand) {
        this.date = date;
        this.url = url;
        this.stand = stand;
    }

    public String getName() {
        return date;
    }

    public String getJob() {
        return url;
    }
}
