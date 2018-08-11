package seq;

import org.jooq.lambda.Unchecked;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.jooq.lambda.Seq.seq;
import static seq.OptionalAssertions.assertThat;

class ReadCacheTest {
    private final TestSequenceFactory testSequenceFactory = new TestSequenceFactory();

    @Test
    public void cachesAndReturnsRead(){
        String sequence = testSequenceFactory.createRandomSequence(10);

        ReadCache readCache = new ReadCache(100);
        Optional<String> fullReadSoFar = seq(sequence.chars())
                .map(n -> new SingleRead(n, new SourceImageLocation(1, 2)))
                .map(Unchecked.function(readCache::rollingRead))
                .findLast();
        assertThat(fullReadSoFar).isEqualTo(sequence);
    }

    @Test
    public void cachesAndReturnsPartialRead(){
        String sequence = testSequenceFactory.createRandomSequence(10);

        ReadCache readCache = new ReadCache(5);
        Optional<String> fullReadSoFar = seq(sequence.chars())
                .map(n -> new SingleRead(n, new SourceImageLocation(1, 2)))
                .map(Unchecked.function(readCache::rollingRead))
                .findLast();
        assertThat(fullReadSoFar).isEqualTo(sequence.substring(5));
    }
}