package Utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BackConfig {

    public static final int DEFAULT_PORT = 10233;
    public static final String KEY_3DS_IP = "3dsIP";
    public static final String KEY_LEGACY = "legacyWay";
    public static final String KEY_BUFFER = "bufferSize";
    public static final int PORT_3DS = 5000;

    private static final String[] valid3dsExtensionVals = {"cia"};
    private static final Set<String> valid3dsExtension = new HashSet<>(Arrays.asList(valid3dsExtensionVals));


    static boolean isExtensionValid(String extension){
        return valid3dsExtension.contains(extension);
    }

}
