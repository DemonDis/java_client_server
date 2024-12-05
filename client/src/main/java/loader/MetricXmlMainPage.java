package loader;

import java.io.File;

public class MetricXmlMainPage {
    public void createPage() throws Exception {
         
        // определяем объект для каталога
        File dir = new File("report/stands");
        // если объект представляет каталог
        if(dir.isDirectory())
        {
            // получаем все вложенные объекты в каталоге
            for(File item : dir.listFiles()){
              
                 if(item.isDirectory()){
                      
                     System.out.println("!!!!!!!!!!!! " + item.getName() + "  \t folder");
                 }
                 else{
                      
                     System.out.println("!!!!!!!!!!!! " + item.getName() + "\t file");
                 }
             }
        }
    }
}