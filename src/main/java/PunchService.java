import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

class PunchService {

    private final List<PunchTask> queue;
    private final AtomicInteger index;
    private final ExecutorService puncher = Executors.newSingleThreadExecutor();

    private final int PORT_3DS = 5000;
    private String IP_3DS = "0.0.0.0";
    private boolean legacyWay = true;
    private int bufferSize = 256;

    private boolean readyForNew = false;
    private Socket socket;
    private DataOutputStream out;
    private BufferedOutputStream bos;
    private DataInputStream din;
    private long startTime;
    private long endTime;

    PunchService(){
        this.queue = new ArrayList<>();
        this.index = new AtomicInteger(0);
    }

    void setParams(Map<String, String> pMap){
        if (pMap.get(WebSocketPunch.KEY_LEGACY) != null){
            legacyWay = Boolean.parseBoolean(pMap.get(WebSocketPunch.KEY_LEGACY));
        }
        if (pMap.get(WebSocketPunch.KEY_BUFFER) != null){
            bufferSize = Integer.parseInt(pMap.get(WebSocketPunch.KEY_BUFFER));
        }
        if (pMap.get(WebSocketPunch.KEY_3DS_IP) != null){
            IP_3DS = pMap.get(WebSocketPunch.KEY_3DS_IP);
        }
    }

    synchronized void startAllInQueue(){
        if (legacyWay){
            startAllLegacy();
        }
        else {
            startAllNew();
        }
    }

    private void startAllNew() {

        while (!readyForNew){
            readyForNew = prepareForNew();
        }

        puncher.execute(this::transmitNew);
    }

    private void startAllLegacy(){
        // working on single or multiple files
        while (true){
            PunchTask cur;
            synchronized (queue){
                if (index.get()>=queue.size()) break;
                cur = queue.get(index.get());
                if (cur != null){
                    while(cur.getStatus()!=TaskStatus.READY){
                        cur.prepareStuff(IP_3DS, PORT_3DS, bufferSize);
                    }
                    puncher.execute(cur);
                    index.getAndIncrement();
                }
            }
        }
    }

    private void transmitNew(){
        //TODO not working
        synchronized (queue){
            try {
                out.writeInt(queue.size());
                this.startTime = System.currentTimeMillis();
                for (PunchTask i :queue){
                    int ack = din.readByte();
                    if (ack == 0) {
                        System.out.println("send_cancelled_remote");
                        break;
                    }

                    out.writeLong(i.getDetailedRom().getFile().length());

                    byte[] buffer = new byte[bufferSize << 10];
                    BufferedInputStream bis = i.getBis();
                    int length;
                    while ((length = bis.read(buffer)) != -1) {
                        bos.write(buffer, 0, length);
                        i.updateTransferred(length);
                    }

                    i.closeBis();
                }
                this.endTime = System.currentTimeMillis();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        readyForNew = false;
    }

    private boolean prepareForNew(){
        try{
            this.socket = new Socket(IP_3DS, PORT_3DS);
            //socket.setKeepAlive(true);
            this.out = new DataOutputStream(socket.getOutputStream());
            this.bos = new BufferedOutputStream(out, bufferSize);
            this.din = new DataInputStream(socket.getInputStream());
            //this.bis = new BufferedInputStream(new FileInputStream(detailedRom.getFile()));
            return true;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {}
        return false;
    }

    void emptyQueue(){
        synchronized (queue) {
            this.queue.clear();
            index.set(0);
        }
    }

    boolean addToQueue(String romURL) {
        File rom = new File(romURL);
        PunchTask tr = new PunchTask(new DetailedRom(rom));
        if (tr.getStatus() == TaskStatus.INIT) {
            synchronized (queue){
                return queue.add(tr);
            }
        }
        return false;
    }

    boolean deleteAt(int i){
        checkIndex(i);
        PunchTask tr;

        synchronized (queue){
            tr = queue.remove(i);
        }

        if (tr!=null) return false;

        if (tr.getStatus() == TaskStatus.FINISHED){
            index.decrementAndGet();
        }
        return true;
    }

    List<PunchTask> getAllTasks(){
        return new ArrayList<>(this.queue);
    }

    PunchTask getInfoOf(int i){
        checkIndex(i);
        synchronized (queue){
            return queue.get(i);
        }
    }

    TaskStatus getStatusOf(int i){
        checkIndex(i);
        return queue.get(i).getStatus();
    }

    private void checkIndex(int i){
        synchronized (queue){
            if(i<0 || i>=queue.size()){
                throw new IndexOutOfBoundsException();
            }
        }
    }

    public void shutdown(){
        puncher.shutdown();
    }
}
