package Utils;

import Model.RomInfo;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class RomInfoFinder {

    private HashMap<String, RomInfo> db;
    private static final String PATH_TO_XML = "3dstdb.xml";

    public RomInfoFinder(){
        this(new File(PATH_TO_XML));
    }

    public RomInfoFinder(File dbxml){
        db = new HashMap<>();
        try {
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            DefaultHandler romXMLHandler = new RomXMLHandler(this::addToDB);
            parser.parse(dbxml, romXMLHandler);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public RomInfo getRomInfoBySerial(String romSerial){
        if (db.containsKey(romSerial)) return db.get(romSerial);
        return null;
    }
    RomInfo addToDB(String serial, RomInfo romInfo){
        return this.db.put(serial,romInfo);
    }
}
