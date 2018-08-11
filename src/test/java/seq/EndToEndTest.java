package seq;

import org.jooq.lambda.Seq;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.fest.assertions.Assertions.assertThat;
import static org.jooq.lambda.Seq.seq;

public class EndToEndTest {
    private static final int referenceLength = 10000;
    private static final int readLength = 150;
    private static final int numberOfReads = 100;
    private final Random random = new Random();
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    private final TestSequenceFactory testSequenceFactory = new TestSequenceFactory();

    @Test
    public void test() throws ExecutionException {
        String reference = testSequenceFactory.createRandomSequence(referenceLength);

        Set<AlignedReadSegment> reads = readsFrom(reference);
        Seq<SingleRead> singleReads = regroupToSingleReads(reads);

        BlockingDeque<SingleRead> inQueue = new LinkedBlockingDeque<>();
        Queue<AlignedReadSegment> outQueue = new LinkedBlockingDeque<>();

        Processor processor = new Processor(new ReferenceIndex(reference, readLength), new ReadCache(readLength));
        executorService.submit(new Sequence(processor, inQueue, outQueue));

        inQueue.addAll(singleReads.toList());

        await().atMost(5, SECONDS).untilAsserted(() -> queueAndListAreEqual(outQueue, reads));
    }

    private boolean queueAndListAreEqual(Queue outQueue, Set<AlignedReadSegment> reads) {
        assertThat(new HashSet<>(Arrays.asList(outQueue.toArray()))).isEqualTo(reads);
        return true;
    }

    private Seq<SingleRead> regroupToSingleReads(Set<AlignedReadSegment> reads) {
        return seq(reads)
                .zip(Seq.range(0, reads.size()))
                .flatMap(t -> createSingleReads(t.v1, t.v2));
    }

    private Stream<SingleRead> createSingleReads(AlignedReadSegment read, Integer locationWithImage) {
        return Seq
                .range(0, read.getSequence().length())
                .map(imageItWouldBeReadFrom ->
                        new SingleRead(read.getSequence().charAt(imageItWouldBeReadFrom),
                                new SourceImageLocation(imageItWouldBeReadFrom, locationWithImage)));
    }

    private Set<AlignedReadSegment> readsFrom(String reference) {
        return seq(random.ints(0, referenceLength - readLength))
                .map(i -> new AlignedReadSegment(reference.substring(i, i + readLength), i))
                .limit(numberOfReads)
                .toSet();
    }
}
