import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Objects;

public class PunchTask implements Runnable {


    //private volatile int p;
    static RomInfoFinder romInfoFinder;
    static final String PATH_TO_XML = "3dstdb.xml";
    private TaskStatus status = TaskStatus.INIT;
    private final File file;
    private final RomInfo info;
    private String serial;
    private final long size;

    static{
        romInfoFinder = new RomInfoFinder(new File(PATH_TO_XML));
    }
    
    PunchTask(File file){
        this.file = file;
        this.serial = getSerialFromRom();
        this.size = file.length();
        //look up and set information
        this.info = findDetailedInfo(this.serial);
        setStatus(TaskStatus.READY);
    }

    private String getSerialFromRom(){
        //String hex = "";
        //String titleId = "";
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

    private String findSerial(String searchBlock) {
        final String CTR = "4354522D";
        final String GUION = "2D";
        final String NCCH = "4E434348";

        int start = searchBlock.indexOf(NCCH);
        String CTRBlock = searchBlock.substring(start + (0x50 * 2), start + (0x50 * 2) + (0x0A * 2));
        if (CTRBlock.startsWith(CTR) && CTRBlock.substring(10, 12).equals(GUION)) {
            return Utils.getStringFromHex(CTRBlock).split("-")[2];
        }

        return "";
    }

    private RomInfo findDetailedInfo(String serial) {
        if (!Objects.equals(serial, "")){
            return romInfoFinder.getRomInfoBySerial(serial) ;
        }
        return null;
    }

    RomInfo getFileInfo(){
        return this.info;
    }

    synchronized void setStatus(TaskStatus s){
        this.status = s;
    }

    TaskStatus getStatus(){
        return this.status;
    }

    @Override
    public void run() {

    }
}
