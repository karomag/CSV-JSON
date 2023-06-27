import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";

//        List<Employee> list = parseCSV(columnMapping, fileName);
        List<Employee> list = parseXML("data.xml");

        String json = listToJson(list);

        writeString(json);
    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {

        CSVReader csvReader = null;
        try {
            csvReader = new CSVReader(new FileReader(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
        strategy.setType(Employee.class);
        strategy.setColumnMapping(columnMapping);

        CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                .withMappingStrategy(strategy)
                .build();
        List<Employee> list = csv.parse();
        return list;
    }

    public static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {}.getType();

        String json = gson.toJson(list, listType);

        return json;
    }

    public static void writeString(String json) {
        try (FileWriter file = new FileWriter("new_data.json")) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Employee> parseXML(String fileName)  {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document doc = null;
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(new File(fileName));
        } catch (IOException | SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        Node root = doc.getDocumentElement();
        NodeList nodeList = root.getChildNodes();

        List<Employee> list = new ArrayList<>();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node_ = nodeList.item(i);
            if (node_.getNodeType() == Node.ELEMENT_NODE) {
                Element employee = (Element) node_;
                NamedNodeMap map = employee.getAttributes();
                Employee employeeObj = new Employee(
                        Long.parseLong(employee.getElementsByTagName("id").item(0).getTextContent())
                        , employee.getElementsByTagName("firstName").item(0).getTextContent()
                        , employee.getElementsByTagName("lastName").item(0).getTextContent()
                        , employee.getElementsByTagName("country").item(0).getTextContent()
                        , Integer.parseInt(employee.getElementsByTagName("age").item(0).getTextContent()));
                list.add(employeeObj);
            }
        }

        return list;
    }
}
