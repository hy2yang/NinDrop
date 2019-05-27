import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

class PunchService {

    private final List<PunchTask> queue;
    private final AtomicInteger index;
    private final Executor puncher = newSingleThreadExecutor();

    PunchService(){
        this.queue = new ArrayList<>();
        this.index = new AtomicInteger(0);
    }

    void startAllInQueue(){
        while (true){
            PunchTask cur;
            synchronized (queue){
                if (index.get()>=queue.size()) break;
                cur = queue.get(index.get());
            }
            if (cur != null && cur.getStatus()==TaskStatus.READY){
                puncher.execute(cur);
                index.getAndIncrement();
            }
        }
    }

    void emptyQueue(){
        synchronized (queue) {
            this.queue.clear();
        }
        index.set(0);
    }

    boolean addToQueue(String romURL) {
        //TODO use url/file/punchtask?
        File rom = new File(romURL);
        PunchTask task = new PunchTask(rom);
        if (task.getStatus() == TaskStatus.READY) {
            synchronized (queue){
                return queue.add(task);
            }
        }
        return false;
    }

    boolean deleteAt(int i){
        checkIndex(i);
        synchronized (queue){
            queue.remove(i);
            return true;
        }
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
}
