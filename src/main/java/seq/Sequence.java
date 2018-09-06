package seq;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutionException;

public class Sequence implements Runnable{
    private final Processor processor;
    private final BlockingDeque<SingleRead> inQueue;
    private final Queue<Alignment> outQueue;

    Sequence(Processor processor, BlockingDeque<SingleRead> inQueue, Queue<Alignment> outQueue) {
        this.processor = processor;
        this.inQueue = inQueue;
        this.outQueue = outQueue;
    }

    public void run() {
        while(true) {
            try {
                process();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void process() throws InterruptedException, ExecutionException {
        SingleRead read = inQueue.take();
        List<Alignment> alignedRead = processor.process(read);
        outQueue.addAll(alignedRead);
    }
}
