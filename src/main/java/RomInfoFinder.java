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
    private SAXParser parser;

    private DefaultHandler romXMLHandler = new RomXMLHandler(this::addToDB);

    RomInfoFinder(File dbxml){
        db = new HashMap<>();
        try {
            this.parser = SAXParserFactory.newInstance().newSAXParser();
            parser.parse(dbxml,romXMLHandler);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    RomInfo getRomInfoBySerial(String romSerial){
        return db.get(romSerial);
    }
    RomInfo addToDB(String serial, RomInfo romInfo){
        return this.db.put(serial,romInfo);
    }
}
