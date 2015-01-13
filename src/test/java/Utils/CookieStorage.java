package Utils;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;

import javax.ws.rs.core.Cookie;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CookieStorage {
    public static final Pattern COOKIE_PATTERN = Pattern.compile("([^=]*)=([^;]*)(;.*)?", Pattern.DOTALL);
    Map<String, String> storedCookies = new HashMap<>();

    public void rememberCookies(ClientResponse response) {
        List<String> setCookies = response.getHeaders().get("Set-Cookie");
        for (String setCookie : setCookies) {
            Matcher matcher = COOKIE_PATTERN.matcher(setCookie);
            matcher.matches();
            String name = matcher.group(1);
            String value = matcher.group(2);
            storedCookies.put(name, value);
        }
    }

    public void applyCookies(Resource resource) {
        for (String cookie : storedCookies.keySet()) {
            String value = storedCookies.get(cookie);
            resource.cookie(new Cookie(cookie, value));
        }
    }
}
