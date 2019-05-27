import java.io.File;
import java.util.Objects;

public class PunchTask implements Runnable {


    //private volatile int p;
    private static RomInfoFinder romInfoFinder;
    private TaskStatus status = TaskStatus.INIT;
    private final File file;
    private final RomInfo info;
    private String serial;
    private final long size;

    static{
        romInfoFinder = new RomInfoFinder();
    }
    
    PunchTask(File file){
        this.file = file;
        this.serial = Utils.getSerialFromRom(file);
        this.size = file.length();
        //look up and set information
        this.info = findDetailedInfo(this.serial);
        setStatus(TaskStatus.READY);
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

    void transmit(){

    }

    @Override
    public void run() {
        this.setStatus(TaskStatus.TRANSFERRING);
        //TODO transimit this rom
        this.setStatus(TaskStatus.FINISHED);
    }
}
