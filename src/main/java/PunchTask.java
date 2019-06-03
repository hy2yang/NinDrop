import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class PunchTask implements Runnable{

    private final DetailedRom detailedRom;
    private TaskStatus status;
    private AtomicLong transferred;
    private int bufferSize;

    private Socket socket;
    private DataOutputStream out;
    private BufferedOutputStream bos;
    private DataInputStream din;
    private BufferedInputStream bis;
    private long startTime;
    private long endTime;

    public PunchTask(DetailedRom detailedRom) {
        this.detailedRom = detailedRom;
        this.transferred = new AtomicLong(0);
        boolean done = false;
        while(!done){
            try{
                this.bis = new BufferedInputStream(new FileInputStream(detailedRom.getFile()));
                done = true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        setStatus(TaskStatus.INIT);
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public long getTransferred() {
        return transferred.get();
    }

    public DetailedRom getDetailedRom() {
        return detailedRom;
    }

    public long updateTransferred(long delta){
        return transferred.addAndGet(delta);
    }

    @JsonIgnore
    public String getProgressJson(){
        Map<String, Long> temp = new HashMap<>();
        temp.put("size", this.detailedRom.getFile().length());
        temp.put("transferred", getTransferred());
        return Utils.getJson(temp);
    }


    BufferedInputStream getBis(){
        return this.bis;
    }

    void closeBis(){
        try{
            this.bis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    boolean prepareStuff(String ip3ds, int port, int bufferSize){
        this.bufferSize = bufferSize;
        try{
            this.socket = new Socket(ip3ds, port);
            //socket.setKeepAlive(true);
            this.out = new DataOutputStream(socket.getOutputStream());
            this.bos = new BufferedOutputStream(out, bufferSize);
            this.din = new DataInputStream(socket.getInputStream());
            //this.bis = new BufferedInputStream(new FileInputStream(detailedRom.getFile()));
            this.setStatus(TaskStatus.READY);
            return true;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {}
        return false;
    }

    private void cleanUp(){
        try {
            this.bis.close();
            this.din.close();
            this.bos.close();
            this.out.close();
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean transmitLegacy(){
        File file = this.detailedRom.getFile();
        try {
            out.writeLong(file.length());
            out.flush();

            byte[] buffer = new byte[bufferSize << 10];
            this.startTime = System.currentTimeMillis();

            int length;
            while ((length = bis.read(buffer)) >= 0) {
                bos.write(buffer, 0, length);
                this.updateTransferred(length);
            }

            this.endTime = System.currentTimeMillis();
            return true;
        } catch (SocketTimeoutException ex) {
            System.err.println(ex.getMessage());
            return false;
        } catch (IOException e) {
            return false;
        } finally {
            cleanUp();
        }

    }

    synchronized void setStatus(TaskStatus s){
        this.status = s;
    }

    TaskStatus getStatus(){
        return this.status;
    }


    @Override
    public void run() {
        this.setStatus(TaskStatus.TRANSFERRING);
        boolean finished = transmitLegacy();
        if (finished) this.setStatus(TaskStatus.FINISHED);
        else this.setStatus(TaskStatus.ERROR);
    }
}
