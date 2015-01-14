import Utils.AlmRestClient;
import Utils.ConnectionProperties;
import datagenerator.ConfigurationReaderForDataGenerator;
import datagenerator.DataGenerator;
import datagenerator.EntityType;
import datagenerator.LocalizationLanguage;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;


public class AlmRestDefectsCreateTestWithDataGenerator {
    DataGenerator dataGenerator = new DataGenerator(new ConfigurationReaderForDataGenerator(ConfigurationReaderForDataGenerator.DATAGENERATOR_CUSTOMIZATION_XML));

    @BeforeClass
    public static void LoadProperties() {
        ConnectionProperties.ReadConfiguration();
    }


    @Test
    public void TestCreateOneDefectRussianFilledAllFields() {
        AlmRestClient restClient = new AlmRestClient();
        restClient.login();
        String generatedEntities = dataGenerator.generateStringInJsonFormatForAllFields(1, EntityType.DEFECT_ENTITY, LocalizationLanguage.RUSSIAN);
        Map<String, String> responsePost = restClient.sendRequest(
                "defects",
                "POST",
                generatedEntities,
                null);
        restClient.logout();
        String expectedOkStatusCode = "201";

        Assert.assertTrue("Status codes do not match. Expected is " + expectedOkStatusCode + ". Actual is " + responsePost.get("StatusCode"),
                responsePost.get("StatusCode").equals(expectedOkStatusCode));

    }

    @Test
    public void TestCreateOneDefectRussianFilledRequiredFields() {
        AlmRestClient restClient = new AlmRestClient();
        restClient.login();
        String generatedEntities = dataGenerator.generateStringInJsonFormatForRequiredOlnyFields(1, EntityType.DEFECT_ENTITY, LocalizationLanguage.RUSSIAN);
        Map<String, String> responsePost = restClient.sendRequest(
                "defects",
                "POST",
                generatedEntities,
                null);
        restClient.logout();
        String expectedOkStatusCode = "201";
        Assert.assertTrue("Status codes do not match. Expected is " + expectedOkStatusCode + ". Actual is " + responsePost.get("StatusCode"),
                responsePost.get("StatusCode").equals(expectedOkStatusCode));

    }

    @Test
    public void TestCreateTenDefectsEnglishFilledAllFields() {
        AlmRestClient restClient = new AlmRestClient();
        restClient.login();
        String generatedEntities = dataGenerator.generateStringInJsonFormatForAllFields(10, EntityType.DEFECT_ENTITY, LocalizationLanguage.ENGLISH);
        Map<String, String> responsePost = restClient.sendRequest(
                "defects",
                "POST",
                generatedEntities,
                null);
        restClient.logout();
        String expectedOkStatusCode = "201";
        Assert.assertTrue("Status codes do not match. Expected is " + expectedOkStatusCode + ". Actual is " + responsePost.get("StatusCode"),
                responsePost.get("StatusCode").equals(expectedOkStatusCode));

    }

    @Test
    public void TestCreateFiveDefectsGermanFilledAllFields() {
        AlmRestClient restClient = new AlmRestClient();
        restClient.login();
        String generatedEntities = dataGenerator.generateStringInJsonFormatForAllFields(5, EntityType.DEFECT_ENTITY, LocalizationLanguage.GERMAN);
        Map<String, String> responsePost = restClient.sendRequest(
                "defects",
                "POST",
                generatedEntities,
                null);
        restClient.logout();
        String expectedOkStatusCode = "201";
        Assert.assertTrue("Status codes do not match. Expected is " + expectedOkStatusCode + ". Actual is " + responsePost.get("StatusCode"),
                responsePost.get("StatusCode").equals(expectedOkStatusCode));

    }

    @Test
    public void TestCreateThreeDefectsGermanFilledRequiredFields() {
        AlmRestClient restClient = new AlmRestClient();
        restClient.login();
        String generatedEntities = dataGenerator.generateStringInJsonFormatForRequiredOlnyFields(3, EntityType.DEFECT_ENTITY, LocalizationLanguage.GERMAN);
        Map<String, String> responsePost = restClient.sendRequest(
                "defects",
                "POST",
                generatedEntities,
                null);
        restClient.logout();
        String expectedOkStatusCode = "201";
        Assert.assertTrue("Status codes do not match. Expected is " + expectedOkStatusCode + ". Actual is " + responsePost.get("StatusCode"),
                responsePost.get("StatusCode").equals(expectedOkStatusCode));

    }
}
