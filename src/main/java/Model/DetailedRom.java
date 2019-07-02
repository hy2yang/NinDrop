package Model;

import Utils.AppUtils;
import Utils.RomInfoFinder;

import java.io.File;
import java.util.Objects;

public class DetailedRom {

    private static RomInfoFinder romInfoFinder;

    private final File file;
    private final RomInfo romInfo;
    private String serial;
    private final long size;

    private final String fileName;

    static{
        romInfoFinder = new RomInfoFinder();
    }

    public DetailedRom(File file){
        this.file = file;
        this.serial = AppUtils.getSerialFromRom(file);
        this.size = file.length();
        this.fileName = file.getName();
        this.romInfo = findDetailedInfo(this.serial);
    }

    private RomInfo findDetailedInfo(String serial) {
        if (!Objects.equals(serial, "")){
            return romInfoFinder.getRomInfoBySerial(serial) ;
        }
        return null;
    }

    RomInfo getFileInfo(){
        return this.romInfo;
    }

    public File getFile(){ return this.file;}

    public RomInfo getRomInfo() {
        return romInfo;
    }

    public String getSerial() {
        return serial;
    }

    public long getSize() {
        return size;
    }

    public String getFileName() {
        return fileName;
    }


}
