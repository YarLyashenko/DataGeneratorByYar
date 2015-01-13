import Utils.AlmRestClient;
import Utils.ConnectionProperties;
import datagenerator.DataGenerator;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;


public class AlmRestDefectsCreateTestWithDataGenerator {
    DataGenerator dataGenerator = new DataGenerator();

    @BeforeClass
    public static void LoadProperties() {
        ConnectionProperties.ReadConfiguration();
    }


    @Test
    public void TestCreateOneDefectRussianFilledAllFields() {
        AlmRestClient restClient = new AlmRestClient();
        restClient.login();
        Map<String, String> responsePost = restClient.sendRequest(
                "defects",
                "POST",
                dataGenerator.generateStringInJsonFormatForAllFields(1, DataGenerator.DEFECT_ENTITY, "Russian"),
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
        Map<String, String> responsePost = restClient.sendRequest(
                "defects",
                "POST",
                dataGenerator.generateStringInJsonFormatForRequiredOlnyFields(1, DataGenerator.DEFECT_ENTITY, "Russian"),
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
        Map<String, String> responsePost = restClient.sendRequest(
                "defects",
                "POST",
                dataGenerator.generateStringInJsonFormatForAllFields(10, DataGenerator.DEFECT_ENTITY, "English"),
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
        Map<String, String> responsePost = restClient.sendRequest(
                "defects",
                "POST",
                dataGenerator.generateStringInJsonFormatForAllFields(5, DataGenerator.DEFECT_ENTITY, "German"),
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
        Map<String, String> responsePost = restClient.sendRequest(
                "defects",
                "POST",
                dataGenerator.generateStringInJsonFormatForRequiredOlnyFields(3, DataGenerator.DEFECT_ENTITY, "German"),
                null);
        restClient.logout();
        String expectedOkStatusCode = "201";
        Assert.assertTrue("Status codes do not match. Expected is " + expectedOkStatusCode + ". Actual is " + responsePost.get("StatusCode"),
                responsePost.get("StatusCode").equals(expectedOkStatusCode));

    }
}
