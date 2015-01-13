package Utils;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.codehaus.plexus.util.Base64;

import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlmRestClient {
    public static CookieStorage cookieStorage = new CookieStorage();

    public AlmRestClient() {
        ConnectionProperties.ReadConfiguration();
    }

    public void login() {
        RestClient client = new RestClient();
        ClientResponse authResponse = client.resource(ConnectionProperties.getAlmRestBaseURL() + "authentication/sign-in")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic " + new String(Base64.encodeBase64(((ConnectionProperties.getUser() + ":" + ConnectionProperties.getPassword()).getBytes()))))
                .get();
        cookieStorage.rememberCookies(authResponse);
    }

    public void logout() {
        RestClient client = new RestClient();
        ClientResponse authResponse = client.resource(ConnectionProperties.getAlmRestBaseURL() + "authentication/sign-out")
                .accept(MediaType.APPLICATION_JSON)
                .get();
    }

    public Map<String, String> sendRequest(String Entity, String Method, String Body, Map<String, String> Headers) {
        RestClient client = new RestClient();
        Map<String, String> response = new HashMap<>();
        Resource requestResource = client.resource(ConnectionProperties.getAlmRestDomainProject() + Entity);
        cookieStorage.applyCookies(requestResource);
        switch (Method) {
            case "GET":
                ClientResponse responseGet = requestResource
                        .accept(MediaType.APPLICATION_JSON)
                        .get();
                for (Map.Entry<String, List<String>> element : responseGet.getHeaders().entrySet()) {

                }
                response.put("StatusCode", String.valueOf(responseGet.getStatusCode()));
                response.put("Body", responseGet.getEntity(String.class));
                return response;
            case "POST":
                ClientResponse responsePost = requestResource
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .post(Body);
                response.put("StatusCode", String.valueOf(responsePost.getStatusCode()));
                response.put("Body", responsePost.getEntity(String.class));
                return response;
            case "PUT":
                ClientResponse responsePut = requestResource
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .put(Body);
                response.put("StatusCode", String.valueOf(responsePut.getStatusCode()));
                response.put("Body", responsePut.getEntity(String.class));
                return response;
            case "DELETE":
                ClientResponse responseDelete = requestResource
                        .accept(MediaType.APPLICATION_JSON)
                        .delete();
                response.put("StatusCode", String.valueOf(responseDelete.getStatusCode()));
                response.put("Body", responseDelete.getEntity(String.class));
                return response;
        }
        return null;
    }

}
