import org.jooq.lambda.Seq;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.jooq.lambda.Seq.seq;

public class EndToEndTest {
    private static final int referenceLength = 10000;
    private static final int readLength = 150;
    private static final int numberOfReads = 100;
    private static final Random random = new Random();
    private static final ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Test
    public void test(){
        String reference = createRandomReference();

        Set<AlignedRead> reads = readsFrom(reference);
        Seq<SingleRead> singleReads = regroupToSingleReads(reads);

        BlockingDeque<SingleRead> inQueue = new LinkedBlockingDeque<>();
        Queue<AlignedRead> outQueue = new LinkedBlockingDeque<>();

        Processor processor = new Processor(new ReferenceIndex(), new ReadCache());
        executorService.submit(new Sequence(processor, inQueue, outQueue));

        inQueue.addAll(singleReads.toList());

        await().atMost(5, SECONDS).until(() -> queueAndListAreEqual(outQueue, reads));
    }

    private String createRandomReference() {
        return random.ints(0, 4)
                .mapToObj("ATCG"::charAt)
                .map(Objects::toString).limit(referenceLength)
                .collect(Collectors.joining());
    }

    private Boolean queueAndListAreEqual(Queue outQueue, Set<AlignedRead> reads) {
        return new HashSet<>(Arrays.asList(outQueue.toArray())).equals(reads);
    }

    private Seq<SingleRead> regroupToSingleReads(Set<AlignedRead> reads) {
        return seq(reads)
                .zip(Seq.range(0, reads.size()))
                .flatMap(t -> createSingleReads(t.v1, t.v2));
    }

    private Stream<SingleRead> createSingleReads(AlignedRead read, Integer imageId) {
        return Seq
                .range(0, read.getSequence().length())
                .map(imageLocation -> new SingleRead(read.getSequence().charAt(imageLocation), imageId, imageLocation));
    }

    private Set<AlignedRead> readsFrom(String reference) {
        return seq(random.ints(0, referenceLength - readLength - 1))
                .map(i -> new AlignedRead(reference.substring(i, i+readLength), i))
                .limit(numberOfReads)
                .toSet();
    }
}
