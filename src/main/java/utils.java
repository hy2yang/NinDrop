import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import spark.Request;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

public class utils {
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


}
