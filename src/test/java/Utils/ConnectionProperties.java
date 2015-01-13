package Utils;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConnectionProperties {
    public static Map<String, String> Config = new HashMap<>();

    public static void ReadConfiguration() {
        try {
            File fXmlFile = new File("src/test/resources/Configuration.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("root");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;
                    Config.put("Scheme", eElement.getElementsByTagName("Scheme").item(0).getTextContent());
                    Config.put("Server", eElement.getElementsByTagName("Server").item(0).getTextContent());
                    Config.put("Port", eElement.getElementsByTagName("Port").item(0).getTextContent());
                    Config.put("User", eElement.getElementsByTagName("User").item(0).getTextContent());
                    Config.put("Password", eElement.getElementsByTagName("Password").item(0).getTextContent());
                    Config.put("Domain", eElement.getElementsByTagName("Domain").item(0).getTextContent());
                    Config.put("Project", eElement.getElementsByTagName("Project").item(0).getTextContent());
                    Config.put("ReadOnlyUser", eElement.getElementsByTagName("ReadOnlyUser").item(0).getTextContent());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getAlmRestBaseURL() {
        return Config.get("Scheme") + "://" + Config.get("Server") + ":" + Config.get("Port") + "/qcbin/api/";
    }

    public static String getCharacterSet() {
        return Config.get("characters_set");
    }

    public static String getAlmRestDomainProject() {
        return Config.get("Scheme") + "://" + Config.get("Server") + ":" + Config.get("Port") + "/qcbin/api/domains/" + Config.get("Domain") + "/projects/" + Config.get("Project") + "/";
    }

    public static String getUser() {
        return Config.get("User");
    }

    public static String getPassword() {
        return Config.get("Password");
    }

}

