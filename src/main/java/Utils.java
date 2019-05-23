import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import spark.Request;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Utils {
    static ObjectMapper mapper;
    private static final int DEFAULT_PORT = 10233;
    static {
        mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    static void printReqDetails(Request req) {
        System.out.println("----------------------------------------------");
        System.out.println(req.requestMethod());
        System.out.println(req.pathInfo());
        System.out.println(req.params().toString());
        System.out.println(req.body());
        try {
            System.out.println(getJson(req.queryMap()));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        System.out.println("----------------------------------------------");

    }

    static String getJson(Object o) throws JsonProcessingException {
        return mapper.writeValueAsString(o);
    }

    static int getPortFromKeyboard(){
        Scanner keyboardIn = new Scanner(System.in);
        System.out.println("enter an integer (1023-65535) to specify service port");
        int pnum = keyboardIn.nextInt();
        if (pnum<=1023 || pnum>=65535) {
            pnum=DEFAULT_PORT;
            System.out.println("invalid port, use default port "+DEFAULT_PORT);
        }

        return pnum;
    }

    static void openBrowser(String url){
        if(Desktop.isDesktopSupported()){
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(url));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }else{
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec("xdg-open " + url);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getStringFromHex(String hex) {
        if ( (hex.length()&1) != 0) {
            System.out.println((hex.length()&1) + " " + hex);
            //System.out.println(hex.length() % 2 + " " + hex);
            return "";
        } else {
            String result = "";
            for (int i = 0; i < hex.length(); i += 2) {
                String hexPair = hex.substring(i, i + 2);
                if (!hexPair.equals("00")) {
                    char newChar = (char) getIntFromHex(hexPair);
                    result = result + String.valueOf(newChar);
                }
            }
            return result;
        }
    }

    public static int getIntFromHex(String hex) {
        if (!hex.startsWith("0x")) {
            return Integer.decode("0x" + hex);
        } else {
            return Integer.decode(hex);
        }
    }

    public static Map<String,Object> getMapFromJSON(String json)
            throws IOException {
        HashMap<String,Object> bodyMap = new HashMap<>();
        mapper.readValue(json, new TypeReference<HashMap<String,Object>>() {});
        return bodyMap;
    }


}
