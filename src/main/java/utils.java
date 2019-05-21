import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import spark.Request;

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

        String os = System.getProperty("os.name").toLowerCase();
        Runtime rt = Runtime.getRuntime();

        try{
            if (os.indexOf( "win" ) >= 0) {
                rt.exec( "rundll32 url.dll,FileProtocolHandler " + url);
            } else if (os.indexOf( "mac" ) >= 0) {
                rt.exec( "open " + url);
            } else if (os.indexOf( "nix") >=0 || os.indexOf( "nux") >=0) {
                String[] browsers = {"epiphany", "firefox", "mozilla", "konqueror",
                        "netscape","opera","links","lynx"};
                StringBuilder cmd = new StringBuilder();
                for (int i=0; i<browsers.length; i++)
                    cmd.append( (i==0  ? "" : " || " ) + browsers[i] +" \"" + url + "\" ");

                rt.exec(new String[] { "sh", "-c", cmd.toString() });

            } else {
                return;
            }
        }catch (Exception e){
            System.out.println("fail to start browser, please open "+url+" manually");
            return;
        }
    }


}
