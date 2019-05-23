import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PunchService {

    private List<PunchTask> queue;

    PunchService(){
        this.queue = new ArrayList<>();
    }

    void startAllInQueue(){
        for (PunchTask i:queue){
            i.run();
        }
    }

    void emptyQueue(){
        this.queue.clear();
    }

    boolean addToQueue(String romURL) {
        //TODO use url/file/punchtask?
        File rom = new File(romURL);
        PunchTask task = new PunchTask(rom);
        if (task.getStatus() == TaskStatus.READY) {
            queue.add(task);
            return true;
        }
        return false;
    }

    boolean deleteAt(int index){
        checkIndex(index);
        queue.remove(index);
        return true;
    }

    List<PunchTask> getAll(){
        return new ArrayList<>(this.queue);
    }

    PunchTask getInfoOf(int index){
        checkIndex(index);
        return queue.get(index);
    }

    private void checkIndex(int index){
        if(index<0 || index>=queue.size()){
            throw new IndexOutOfBoundsException();
        }
    }
}
