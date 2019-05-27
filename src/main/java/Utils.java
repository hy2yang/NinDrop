import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import spark.Request;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Utils {
    static ObjectMapper mapper;
    private static final int DEFAULT_PORT = 10233;
    private static final String CTR = "4354522D";
    private static final String GUION = "2D";
    private static final String NCCH = "4E434348";


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

    private static String getStringFromHex(String hex) {
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

    static String getSerialFromRom(File file){
        String serial = "";

        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(file, "r");
            raf.seek(0x3000);
            StringBuilder search = new StringBuilder();
            for (int i = 0; i < 0x1000; i++) {
                int part = raf.readByte();
                if (part < 0) part = part & 0xff;
                String hexPart = Integer.toHexString(part).toUpperCase();
                if (hexPart.length() == 1) {
                    hexPart = "0" + hexPart;
                }
                search.append(hexPart);
            }
            serial = findSerial(search.toString());

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (raf != null) {
                    raf.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return serial;
    }

    private static String findSerial(String searchBlock) {
        int start = searchBlock.indexOf(NCCH);
        String CTRBlock = searchBlock.substring(start + (0x50 * 2), start + (0x50 * 2) + (0x0A * 2));
        if (CTRBlock.startsWith(CTR) && CTRBlock.substring(10, 12).equals(GUION)) {
            return getStringFromHex(CTRBlock).split("-")[2];
        }

        return "";
    }

    private static int getIntFromHex(String hex) {
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
