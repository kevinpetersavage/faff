import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.BlockingDeque;

public class Sequence implements Runnable{
    private final Processor processor;
    private final BlockingDeque<SingleRead> inQueue;
    private final Queue<AlignedReadSegment> outQueue;

    Sequence(Processor processor, BlockingDeque<SingleRead> inQueue, Queue<AlignedReadSegment> outQueue) {
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
        Optional<AlignedReadSegment> alignedRead = processor.process(read);
        alignedRead.ifPresent(outQueue::add);
    }
}
