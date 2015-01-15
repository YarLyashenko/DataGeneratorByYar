package datagenerator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Yaroslav Lyashenko on 1/14/2015.
 */
public class ConfigurationReaderForDataGenerator {

    public static final String DATAGENERATOR_CUSTOMIZATION_XML = "dataGenerator_customization.xml";

    private HashMap<String, String> localizationCharSetsMap = new HashMap<>();
    private HashMap<String, Object> dataGeneratorConfigurationMap = new HashMap<>();
    private HashMap<String, ArrayList<HashMap<String, String>>> projectEntitiesMapsList = new HashMap<String, ArrayList<HashMap<String, String>>>();
    private ArrayList<String> userList = new ArrayList<>();
    private HashMap<String, ArrayList<String>> projectListsMap = new HashMap<>();

    public ConfigurationReaderForDataGenerator(String customizationXmlPath) {
        readCustomizationXml(customizationXmlPath);
        readUserList();
        readProjectList();
        readEntityFields();
    }

    public HashMap<String, String> getLocalizationCharSetsMap() {
        return localizationCharSetsMap;
    }

    public HashMap<String, Object> getDataGeneratorConfigurationMap() {
        return dataGeneratorConfigurationMap;
    }

    public HashMap<String, ArrayList<HashMap<String, String>>> getProjectEntitiesMapsList() {
        return projectEntitiesMapsList;
    }

    public ArrayList<String> getUserList() {
        return userList;
    }

    public HashMap<String, ArrayList<String>> getProjectListsMap() {
        return projectListsMap;
    }

    private void readCustomizationXml(String xmlPath) {
        HashMap<String, String> entitiesConfigurationFilesPathMap = new HashMap<>();

        try {
            ClassLoader classLoader = DataGenerator.class.getClassLoader();
            File file = new File(classLoader.getResource(xmlPath).getFile());

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("min_date");
            dataGeneratorConfigurationMap.put("min_date", ((Node) nodeList.item(0)).getTextContent());

            nodeList = doc.getElementsByTagName("max_date");
            dataGeneratorConfigurationMap.put("max_date", ((Node) nodeList.item(0)).getTextContent());

            nodeList = doc.getElementsByTagName("min_integer");
            dataGeneratorConfigurationMap.put("min_integer", ((Node) nodeList.item(0)).getTextContent());

            nodeList = doc.getElementsByTagName("max_integer");
            dataGeneratorConfigurationMap.put("max_integer", ((Node) nodeList.item(0)).getTextContent());

            nodeList = doc.getElementsByTagName("project_user_list");
            dataGeneratorConfigurationMap.put("project_user_list", ((Node) nodeList.item(0)).getTextContent());

            nodeList = doc.getElementsByTagName("project_lists");
            dataGeneratorConfigurationMap.put("project_lists", ((Node) nodeList.item(0)).getTextContent());

            nodeList = doc.getElementsByTagName("localization");
            for (int s = 0; s < nodeList.getLength(); s++) {
                Node node = nodeList.item(s);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String language = "";
                    String charSet = "";

                    NodeList elementsList = element.getElementsByTagName("language");
                    Element firstElement = (Element) elementsList.item(0);
                    NodeList firstElementNodes = firstElement.getChildNodes();
                    language = firstElementNodes.item(0).getNodeValue();

                    elementsList = element.getElementsByTagName("characters_set");
                    firstElement = (Element) elementsList.item(0);
                    firstElementNodes = firstElement.getChildNodes();
                    charSet = firstElementNodes.item(0).getNodeValue();

                    localizationCharSetsMap.put(language, charSet);
                }
            }

            nodeList = doc.getElementsByTagName("entity");
            for (int s = 0; s < nodeList.getLength(); s++) {
                Node node = nodeList.item(s);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String entityType = "";
                    String entityXmlPath = "";

                    NodeList elementsList = element.getElementsByTagName("type");
                    Element firstElement = (Element) elementsList.item(0);
                    NodeList firstElementNodes = firstElement.getChildNodes();
                    entityType = firstElementNodes.item(0).getNodeValue();

                    elementsList = element.getElementsByTagName("path");
                    firstElement = (Element) elementsList.item(0);
                    firstElementNodes = firstElement.getChildNodes();
                    entityXmlPath = firstElementNodes.item(0).getNodeValue();

                    entitiesConfigurationFilesPathMap.put(entityType, entityXmlPath);
                }
            }
            dataGeneratorConfigurationMap.put("entitiesConfigurationFilesPathMap", entitiesConfigurationFilesPathMap);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void readUserList() {
        String projectUsersXmlPath = dataGeneratorConfigurationMap.get("project_user_list").toString();

        try {
            ClassLoader classLoader = DataGenerator.class.getClassLoader();
            File file = new File(classLoader.getResource(projectUsersXmlPath).getFile());

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("User");
            for (int s = 0; s < nodeList.getLength(); s++) {
                Node node = nodeList.item(s);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    userList.add(element.getAttribute("Name"));
                }

            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void readProjectList() {
        String projectListXmlPath = dataGeneratorConfigurationMap.get("project_lists").toString();
        try {
            ClassLoader classLoader = DataGenerator.class.getClassLoader();
            File file = new File(classLoader.getResource(projectListXmlPath).getFile());

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("List");
            for (int s = 0; s < nodeList.getLength(); s++) {

                ArrayList<String> listItems = new ArrayList<>();
                String listId = "";
                Node node = nodeList.item(s);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    NodeList elementsList = element.getElementsByTagName("Id");
                    Element firstElement = (Element) elementsList.item(0);
                    NodeList firstElementNodes = firstElement.getChildNodes();
                    listId = firstElementNodes.item(0).getNodeValue();


                    NodeList itemsList = element.getElementsByTagName("Item");
                    for (int i = 0; i < itemsList.getLength(); i++) {
                        Node itemNode = itemsList.item(i);
                        if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element item = (Element) itemNode;
                            listItems.add(item.getAttribute("value"));
                        }

                    }
                }
                projectListsMap.put(listId, listItems);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void readEntityFields() {
        HashMap<String, String> entitiesConfigurationFilesPathMap = (HashMap<String, String>) dataGeneratorConfigurationMap.get("entitiesConfigurationFilesPathMap");
        for (String key : entitiesConfigurationFilesPathMap.keySet()) {
            ArrayList<HashMap<String, String>> entityFieldsMapsList = new ArrayList<>();
            String entityXmlPath = entitiesConfigurationFilesPathMap.get(key).toString();
            try {
                ClassLoader classLoader = DataGenerator.class.getClassLoader();
                File file = new File(classLoader.getResource(entityXmlPath).getFile());

                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(file);
                doc.getDocumentElement().normalize();

                NodeList nodeList = doc.getElementsByTagName("Field");
                for (int s = 0; s < nodeList.getLength(); s++) {
                    HashMap<String, String> entityFieldsMap = new HashMap<>();
                    Node node = nodeList.item(s);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) node;
                        entityFieldsMap.put("Name", element.getAttribute("Name"));
                        entityFieldsMap.put("Label", element.getAttribute("Label"));
                        entityFieldsMap.put("PhysicalName", element.getAttribute("PhysicalName"));

                        NodeList elementsList = element.getElementsByTagName("Size");
                        Element firstElement = (Element) elementsList.item(0);
                        NodeList firstElementNodes = firstElement.getChildNodes();
                        entityFieldsMap.put("Size", firstElementNodes.item(0).getNodeValue());

                        elementsList = element.getElementsByTagName("Required");
                        firstElement = (Element) elementsList.item(0);
                        firstElementNodes = firstElement.getChildNodes();
                        entityFieldsMap.put("Required", firstElementNodes.item(0).getNodeValue());

                        elementsList = element.getElementsByTagName("System");
                        firstElement = (Element) elementsList.item(0);
                        firstElementNodes = firstElement.getChildNodes();
                        entityFieldsMap.put("System", firstElementNodes.item(0).getNodeValue());

                        elementsList = element.getElementsByTagName("Editable");
                        firstElement = (Element) elementsList.item(0);
                        firstElementNodes = firstElement.getChildNodes();
                        entityFieldsMap.put("Editable", firstElementNodes.item(0).getNodeValue());

                        elementsList = element.getElementsByTagName("Type");
                        firstElement = (Element) elementsList.item(0);
                        firstElementNodes = firstElement.getChildNodes();
                        entityFieldsMap.put("Type", firstElementNodes.item(0).getNodeValue());

                        elementsList = element.getElementsByTagName("SupportsMultivalue");
                        firstElement = (Element) elementsList.item(0);
                        firstElementNodes = firstElement.getChildNodes();
                        entityFieldsMap.put("SupportsMultivalue", firstElementNodes.item(0).getNodeValue());

                        elementsList = element.getElementsByTagName("List-Id");
                        if (elementsList.getLength() > 0) {
                            firstElement = (Element) elementsList.item(0);
                            firstElementNodes = firstElement.getChildNodes();
                            entityFieldsMap.put("List-Id", firstElementNodes.item(0).getNodeValue());
                        }
                        entityFieldsMapsList.add(entityFieldsMap);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            projectEntitiesMapsList.put(key, entityFieldsMapsList);
        }
    }
}
