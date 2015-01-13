package datagenerator;


import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * Created by ylyashenko on 1/12/2015.
 */
public class DataGenerator  {

    public static final String DEFECT_ENTITY = "Defect";
    private HashMap<String, String> localizationCharSetsMap = new HashMap<>();
    private HashMap<String, Object> dataGeneratorConfigurationMap = new HashMap<>();
    private HashMap<String, ArrayList<HashMap<String, String>>> projectEntitiesMapsList = new HashMap<String, ArrayList<HashMap<String, String>>>();
    private ArrayList<String> userList = new ArrayList<>();
    private HashMap<String, ArrayList<String>> projectListsMap = new HashMap<>();

    public DataGenerator() {
        readConfiguration();
    }


    public String generateStringInJsonFormatForRequiredOlnyFields(int quantityOfEntities, String entityName, String localizationLanguage) throws JSONException {
        return generateStringInJsonFormat(quantityOfEntities, entityName, localizationLanguage, true);
    }



    public String generateStringInJsonFormatForAllFields(int quantityOfEntities, String entityName, String localizationLanguage) throws JSONException {
        return generateStringInJsonFormat(quantityOfEntities, entityName, localizationLanguage, false);
    }



    private String generateStringInJsonFormat(int quantityOfEntities, String entityName, String localizationLanguage, boolean fillOnlyRequiredFields) throws JSONException {
        ArrayList<HashMap<String, Object>> listWithMapForJson = generateListWithMapOfFields(quantityOfEntities, entityName, localizationLanguage, fillOnlyRequiredFields);
        ArrayList<JSONObject> listJsonForEachOneEntity = new ArrayList<>();
        for (HashMap<String, Object> entityValuesMap : listWithMapForJson) {
            JSONObject generatedJsonForOneEntity = new JSONObject();
            for (String key : entityValuesMap.keySet()) {
                generatedJsonForOneEntity.put(key, entityValuesMap.get(key));
            }
            listJsonForEachOneEntity.add(generatedJsonForOneEntity);
        }
        String output = "{ \"data\":[";
        for (JSONObject jsonObject : listJsonForEachOneEntity) {
            output = output + jsonObject.toString() + ",";
        }
        output = output.substring(0, output.length() - 1);
        output = output + "],\"total-count\":" + listJsonForEachOneEntity.size() + "}";
        return output;
    }


    public ArrayList<HashMap<String, Object>> generateListWithMapOfAllFields(int quantityOfEntities, String entityName, String localizationLanguage) {
        return generateListWithMapOfFields(quantityOfEntities, entityName, localizationLanguage, false);
    }


    public ArrayList<HashMap<String, Object>> generateListWithMapOfRequiredFields(int quantityOfEntities, String entityName, String localizationLanguage) {
        return generateListWithMapOfFields(quantityOfEntities, entityName, localizationLanguage, true);
    }



    private ArrayList<HashMap<String, Object>> generateListWithMapOfFields(int quantityOfEntities, String entityName, String localizationLanguage, boolean fillOnlyRequiredFields) {
        ArrayList<HashMap<String, Object>> outputListWithMap = new ArrayList<>();
        HashMap<String, String> localizationCharSetsMap = (HashMap<String, String>) dataGeneratorConfigurationMap.get("localizationCharSetsMap");
        String charSet = localizationCharSetsMap.get(localizationLanguage);

        if (projectEntitiesMapsList.containsKey(entityName) == false) {
            return null;
        }
        ArrayList<HashMap<String, String>> listWithFieldsMaps = projectEntitiesMapsList.get(entityName);

        for (int i = 0; i < quantityOfEntities; i++) {
            HashMap<String, Object> generatedEntityMap = new HashMap<>();
            for (HashMap<String, String> fieldMap : listWithFieldsMaps) {

                if (fieldMap.get("Editable").equals("false")) {
                    continue;
                }
                if (fieldMap.get("Name").equals("subject")) {
                    continue;
                }
                if (fieldMap.get("Required").equals("false") && fillOnlyRequiredFields) {
                    continue;
                }

                if (fieldMap.get("Type").equals("String")) {
                    generatedEntityMap.put(fieldMap.get("Name"), getRandomStringFromLocalizedCharset(fieldMap.get("Size"), charSet));
                } else if (fieldMap.get("Type").equals("Memo")) {
                    Random randomGenerator = new Random();
                    String generatedString = "Localization Charset: " + localizationLanguage + ". Field Type: " + fieldMap.get("Type") + ".   Field Name: " + fieldMap.get("Name") + ". Charset: " + charSet + " .  ";
                    generatedString = generatedString + getRandomStringFromLocalizedCharset(String.valueOf((500 + randomGenerator.nextInt(10000))), charSet);
                    generatedEntityMap.put(fieldMap.get("Name"), generatedString);
                } else if (fieldMap.get("Type").equals("Number")) {
                    generatedEntityMap.put(fieldMap.get("Name"), getRandomInteger(
                            Integer.parseInt((String) dataGeneratorConfigurationMap.get("min_integer")),
                            Integer.parseInt((String) dataGeneratorConfigurationMap.get("max_integer"))));
                } else if (fieldMap.get("Type").equals("Date")) {
                    generatedEntityMap.put(fieldMap.get("Name"), getStringWithRandomDate(
                            String.valueOf(dataGeneratorConfigurationMap.get("min_date")),
                            String.valueOf(dataGeneratorConfigurationMap.get("max_date"))));

                } else if (fieldMap.get("Type").equals("LookupList")) {
                    boolean supportsMultivalue = Boolean.parseBoolean(fieldMap.get("SupportsMultivalue"));
                    ArrayList<String> generatedFieldValue = getValuesFromList(
                            projectListsMap.get(fieldMap.get("List-Id")), supportsMultivalue);
                    if (generatedFieldValue.size() > 1) {
                        generatedEntityMap.put(fieldMap.get("Name"), generatedFieldValue);
                    } else if (generatedFieldValue.size() == 1) {
                        generatedEntityMap.put(fieldMap.get("Name"), generatedFieldValue.get(0));
                    }

                } else if (fieldMap.get("Type").equals("UsersList")) {
                    generatedEntityMap.put(fieldMap.get("Name"), getRandomUser(userList));
                }
            }
            outputListWithMap.add(generatedEntityMap);
        }
        return outputListWithMap;
    }


    private void readConfiguration() {
        readCustomizationXml("dataGenerator_customization.xml");
        readUserList();
        readProjectList();
        readEntityFields();
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
            dataGeneratorConfigurationMap.put("localizationCharSetsMap", localizationCharSetsMap);

        } catch (Exception e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }
        dataGeneratorConfigurationMap.put("userList", userList);
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
            e.printStackTrace();
        }
        dataGeneratorConfigurationMap.put("userList", userList);
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
                e.printStackTrace();
            }
            projectEntitiesMapsList.put(key, entityFieldsMapsList);
        }
        dataGeneratorConfigurationMap.put("projectEntitiesMapsList", projectEntitiesMapsList);
    }

    private String getStringWithRandomDate(String minDate, String maxDate) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dateMinDate = format.parse(minDate);
            Date dateMaxDate = format.parse(maxDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateMinDate);
            long minTime = calendar.getTimeInMillis();
            calendar.setTime(dateMaxDate);
            long maxTime = calendar.getTimeInMillis();
            long newTime = minTime + (long) (Math.random() * (maxTime - minTime));
            calendar.setTimeInMillis(newTime);
            String output = format.format(calendar.getTime());
            return output;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    private int getRandomInteger(int min, int max) {
        return (min + (int) (Math.random() * (max - min)));
    }

    private ArrayList<String> getValuesFromList(ArrayList<String> lookUpList, boolean multivalue) {
        int listSize = lookUpList.size();
        if (listSize < 2) {
            return lookUpList;
        }
        Random randomGenerator = new Random();
        ArrayList<String> output = new ArrayList<>();
        int amountOfReturnedListItems = 1;
        if (multivalue == true) {
            amountOfReturnedListItems = 2 + randomGenerator.nextInt(lookUpList.size() - 2);
        }
        Set<Integer> setOfIndexes = new HashSet<>();

        for (int i = 0; i < amountOfReturnedListItems; i++) {
            int randomInteger = randomGenerator.nextInt(lookUpList.size());
            if (setOfIndexes.contains(randomInteger)) {
                i--;
            }
            setOfIndexes.add(randomInteger);
        }
        for (Integer index : setOfIndexes) {
            output.add(lookUpList.get(index));
        }
        return output;
    }

    private String getRandomStringFromLocalizedCharset(String length, String charSet) {
        int lengthInt = Integer.parseInt(length);
        String output = "";
        Random randomGenerator = new Random();
        for (int i = 0; i < lengthInt; i++) {
            int charIndex = randomGenerator.nextInt(charSet.length());
            output = output + charSet.charAt(charIndex);
        }
        return output;
    }

    private String getRandomUser(ArrayList<String> userList) {
        Random randomGenerator = new Random();
        return userList.get(randomGenerator.nextInt(userList.size()));
    }

}

