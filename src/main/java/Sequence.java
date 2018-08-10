import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingDeque;

public class Sequence implements Runnable{
    private final Processor processor;
    private final BlockingDeque<SingleRead> inQueue;
    private final Queue<AlignedRead> outQueue;

    public Sequence(Processor processor, BlockingDeque inQueue, Queue<AlignedRead> outQueue) {
        this.processor = processor;
        this.inQueue = inQueue;
        this.outQueue = outQueue;
    }

    public void run() {
        while(true) {
            try {
                process();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void process() throws InterruptedException {
        SingleRead read = inQueue.take();
        List<AlignedRead> alignedRead = processor.process(read);
        outQueue.addAll(alignedRead);
    }
}
