import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Objects;
import java.util.function.BiConsumer;

public class RomXMLHandler extends DefaultHandler {

    private BiConsumer<String, RomInfo> callback;

    private final String[] fields = {"id", "region", "languages", "name"};
    private String[] seenVal = {"", "", "", ""};
    private boolean[] seen = {false, false, false, false};

    RomXMLHandler(BiConsumer<String, RomInfo> callback){
        this.callback = callback;
    }

    public void startElement(String uri, String localName,
                             String qName, Attributes attributes){
        if(qName.equalsIgnoreCase("GAME")){
            for (int i=0;i<seenVal.length;++i){
                seenVal[i] = "";
            }
            seenVal[3]=attributes.getValue("name");
        }
        else {
            for (int i=0;i<fields.length;++i){
                if (qName.equalsIgnoreCase(fields[i])){
                    seen[i] = true;
                    break;
                }
            }
        }
    }

    public void endElement(String uri, String localName, String qName){
        if(qName.equalsIgnoreCase("GAME")){
            if(!Objects.equals(seenVal[0], "")){
                callback.accept(seenVal[0],
                        new RomInfo(seenVal[0], seenVal[1], seenVal[2], seenVal[3]));
            }
        }
    }

    public void characters(char[] ch, int start, int length){
        for (int i=0;i<seen.length;++i){
            if (seen[i]){
                seenVal[i] = new String(ch,start,length);
                seen[i] = false;
            }
        }
    }
}
