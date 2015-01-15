package datagenerator;


import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Yaroslav Lyashenko on 1/12/2015.
 *
 *
 How to use DataGenerator:

 DataGenerator generator = new DataGenerator(new ConfigurationReaderForDataGenerator(ConfigurationReaderForDataGenerator.DATAGENERATOR_CUSTOMIZATION_XML));

 System.out.println(generator.generateStringInJsonFormatForRequiredOlnyFields(1,EntityType.DEFECT_ENTITY,LocalizationLanguage.ENGLISH));
 System.out.println(generator.generateStringInJsonFormatForAllFields(1, EntityType.DEFECT_ENTITY, LocalizationLanguage.GERMAN));
 System.out.println(generator.generateStringInJsonFormatForRequiredOlnyFields(20, EntityType.DEFECT_ENTITY, LocalizationLanguage.RUSSIAN));
 System.out.println(generator.generateStringInJsonFormatForAllFields(20, EntityType.DEFECT_ENTITY,LocalizationLanguage.GERMAN));

 */


public class DataGenerator implements EntityType, LocalizationLanguage {

    private HashMap<String, String> localizationCharSetsMap;
    private HashMap<String, Object> dataGeneratorConfigurationMap;
    private HashMap<String, ArrayList<HashMap<String, String>>> projectEntitiesMapsList;
    private ArrayList<String> userList = new ArrayList<>();
    private HashMap<String, ArrayList<String>> projectListsMap;

    public DataGenerator(ConfigurationReaderForDataGenerator config) {
        localizationCharSetsMap = config.getLocalizationCharSetsMap();
        dataGeneratorConfigurationMap = config.getDataGeneratorConfigurationMap();
        projectEntitiesMapsList = config.getProjectEntitiesMapsList();
        userList = config.getUserList();
        projectListsMap = config.getProjectListsMap();
    }

    public String generateStringInJsonFormatForRequiredOlnyFields(int quantityOfEntities, String entityName, String localizationLanguage) {
        return generateStringInJsonFormat(quantityOfEntities, entityName, localizationLanguage, true);
    }

    public String generateStringInJsonFormatForAllFields(int quantityOfEntities, String entityName, String localizationLanguage) {
        return generateStringInJsonFormat(quantityOfEntities, entityName, localizationLanguage, false);
    }

    private String generateStringInJsonFormat(int quantityOfEntities, String entityName, String localizationLanguage, boolean fillOnlyRequiredFields) {
        ArrayList<HashMap<String, Object>> listWithMapForJson =
                generateListWithMapOfFields(quantityOfEntities, entityName, localizationLanguage, fillOnlyRequiredFields);
        class Wrapper {
            public Object data;
        }
        try {
            String result;
            if (quantityOfEntities == 1) {
                result = new ObjectMapper().writeValueAsString(listWithMapForJson.get(0));
            } else {
                Wrapper wrapper = new Wrapper();
                wrapper.data = listWithMapForJson;
                result = new ObjectMapper().writeValueAsString(wrapper);
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public ArrayList<HashMap<String, Object>> generateListWithMapOfAllFields(int quantityOfEntities, String entityName, String localizationLanguage) {
        return generateListWithMapOfFields(quantityOfEntities, entityName, localizationLanguage, false);
    }


    public ArrayList<HashMap<String, Object>> generateListWithMapOfRequiredFields(int quantityOfEntities, String entityName, String localizationLanguage) {
        return generateListWithMapOfFields(quantityOfEntities, entityName, localizationLanguage, true);
    }


    private ArrayList<HashMap<String, Object>> generateListWithMapOfFields(int quantityOfEntities, String entityName, String localizationLanguage, boolean fillOnlyRequiredFields) {
        ArrayList<HashMap<String, Object>> outputListWithMap = new ArrayList<>();
        if (localizationCharSetsMap.containsKey(localizationLanguage) == false) {
            throw new RuntimeException("Wrong localization language. No charset has been found for request \'" + localizationLanguage + "\'. Check if requested language is present in dataGenerator_customization.xml");
        }
        if (projectEntitiesMapsList.containsKey(entityName) == false) {
            throw new RuntimeException("Wrong requested entity type. No entity type has been found for request \'" + entityName + "\'. Check if requested entity is present in dataGenerator_customization.xml ");
        }
        String charSet = localizationCharSetsMap.get(localizationLanguage);
        ArrayList<HashMap<String, String>> listWithFieldsMaps = projectEntitiesMapsList.get(entityName);

        for (int i = 0; i < quantityOfEntities; i++) {
            HashMap<String, Object> generatedEntityMap = new HashMap<>();
            for (HashMap<String, String> fieldMap : listWithFieldsMaps) {
                if (fieldMap.get("Editable").equals("false")) {
                    continue;
                }
                if (fieldMap.get("Required").equals("false") && fillOnlyRequiredFields) {
                    continue;
                }
                if (entityName.equals(DEFECT_ENTITY) && fieldMap.get("Name").equals("subject")) {
                    continue;
                }

                String fieldType = fieldMap.get("Type");
                switch (fieldType) {
                    case "String":
                        generatedEntityMap.put(fieldMap.get("Name"), getRandomStringFromLocalizedCharset(Integer.parseInt(fieldMap.get("Size")), charSet));
                        break;

                    case "Memo":
                        Random randomGenerator = new Random();
                        String generatedString = "Localization Charset: " + localizationLanguage + ". Field Type: " + fieldType + ".   Field Name: " + fieldMap.get("Name") + ". Charset: " + charSet + " .  ";
                        generatedString = generatedString + getRandomStringFromLocalizedCharset((500 + randomGenerator.nextInt(10000)), charSet);
                        generatedEntityMap.put(fieldMap.get("Name"), generatedString);
                        break;

                    case "Number":
                        generatedEntityMap.put(fieldMap.get("Name"), getRandomInteger(
                                Integer.parseInt((String) dataGeneratorConfigurationMap.get("min_integer")),
                                Integer.parseInt((String) dataGeneratorConfigurationMap.get("max_integer"))));
                        break;
                    case "Date":
                        generatedEntityMap.put(fieldMap.get("Name"), getStringWithRandomDate(
                                String.valueOf(dataGeneratorConfigurationMap.get("min_date")),
                                String.valueOf(dataGeneratorConfigurationMap.get("max_date"))));
                        break;

                    case "LookupList":
                        boolean supportsMultivalue = Boolean.parseBoolean(fieldMap.get("SupportsMultivalue"));
                        ArrayList<String> generatedFieldValue = getValuesFromList(
                                projectListsMap.get(fieldMap.get("List-Id")), supportsMultivalue);
                        if (generatedFieldValue.size() > 1) {
                            generatedEntityMap.put(fieldMap.get("Name"), generatedFieldValue);
                        } else if (generatedFieldValue.size() == 1) {
                            generatedEntityMap.put(fieldMap.get("Name"), generatedFieldValue.get(0));
                        }
                        break;

                    case "UsersList":
                        generatedEntityMap.put(fieldMap.get("Name"), getRandomUser(userList));
                        break;
                }
//
            }
            outputListWithMap.add(generatedEntityMap);
        }
        return outputListWithMap;
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
            throw new RuntimeException(e);
        }

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
        ArrayList<String> output = new ArrayList<>();
        for (Integer index : setOfIndexes) {
            output.add(lookUpList.get(index));
        }

        return output;
    }

    private String getRandomStringFromLocalizedCharset(int length, String charSet) {
        Random randomGenerator = new Random();
        StringBuilder output = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int charIndex = randomGenerator.nextInt(charSet.length());
            output.append(charSet.charAt(charIndex));
        }
        return output.toString();
    }

    private String getRandomUser(ArrayList<String> userList) {
        Random randomGenerator = new Random();
        return userList.get(randomGenerator.nextInt(userList.size()));
    }

}

