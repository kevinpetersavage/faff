package seq;

import org.jooq.lambda.Seq;
import org.jooq.lambda.tuple.Tuple2;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.fest.assertions.Assertions.assertThat;
import static org.jooq.lambda.Seq.seq;
import static org.jooq.lambda.tuple.Tuple.tuple;

class EndToEndTest {
    private static final int referenceLength = 10000;
    private static final int readLength = 150;
    private static final int numberOfReads = 100;
    private static final int indexSequenceLength = 32;

    private final Random random = new Random();
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    private final TestSequenceFactory testSequenceFactory = new TestSequenceFactory();

    @Test
    void test() throws ExecutionException {
        String reference = testSequenceFactory.createRandomSequence(referenceLength);

        Set<Tuple2<String, AlignedReadSegment>> reads = readsFrom(reference);
        Set<AlignedReadSegment> alignments = seq(reads).map(Tuple2::v2).toSet();
        Seq<SingleRead> singleReads = regroupToSingleReads(reads);

        BlockingDeque<SingleRead> inQueue = new LinkedBlockingDeque<>();
        Queue<Alignment> outQueue = new LinkedBlockingDeque<>();

        ReferenceIndex referenceIndex = new ReferenceIndex(reference, indexSequenceLength);
        ReadCache readCache = new ReadCache(indexSequenceLength);
        MultiMatchCombiner combiner = new MultiMatchCombiner();
        Processor processor = new Processor(referenceIndex, readCache, combiner);
        executorService.submit(new Sequence(processor, inQueue, outQueue));

        inQueue.addAll(singleReads.toList());

        await().atMost(5, SECONDS).untilAsserted(() -> queueContains(outQueue, alignments));
    }

    private boolean queueContains(Queue<Alignment> outQueue, Set<AlignedReadSegment> reads) {
        seq(reads).map(AlignedReadSegment::getRange).forEach(expected ->
            assertThat(
                    seq(outQueue).map(Alignment::getRanges).flatMap(Seq::seq).toSet()
            ).contains(
                    expected
            )
        );
        return true;
    }

    private Seq<SingleRead> regroupToSingleReads(Set<Tuple2<String, AlignedReadSegment>> reads) {
        return seq(reads).flatMap(t -> createSingleReads(t.v1, t.v2.getReadId()));
    }

    private Stream<SingleRead> createSingleReads(String sequence, UUID locationWithImage) {
        return Seq
                .range(0, sequence.length())
                .map(imageItWouldBeReadFrom ->
                        new SingleRead(sequence.charAt(imageItWouldBeReadFrom),
                                new SourceImageLocation(imageItWouldBeReadFrom, locationWithImage)));
    }

    private Set<Tuple2<String, AlignedReadSegment>> readsFrom(String reference) {
        return seq(random.ints(0, referenceLength - readLength))
                .map(i -> tuple(
                        reference.substring(i, i + readLength),
                        new AlignedReadSegment(UUID.randomUUID(), i, i + readLength)
                ))
                .limit(numberOfReads)
                .toSet();
    }
}
