package seq;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

import static org.fest.assertions.Assertions.assertThat;

class ReferenceIndexTest {
    TestSequenceFactory testSequenceFactory = new TestSequenceFactory();

    @Test
    public void testFindsSubReference() throws ExecutionException {
        String noRepeatingReference = nonRepeatingReference();
        ReferenceIndex referenceIndex = new ReferenceIndex(noRepeatingReference, 5);
        List<Integer> locations = referenceIndex.find(noRepeatingReference.substring(3, 7));
        assertThat(locations).containsExactly(3);
    }

    @Test
    public void doesntFindNonSubReference() throws ExecutionException {
        String reference = testSequenceFactory.createRandomSequence(10);
        ReferenceIndex referenceIndex = new ReferenceIndex(reference, 5);
        String cantPossiblyExistInReference = "can't";
        List<Integer> locations = referenceIndex.find(cantPossiblyExistInReference);
        assertThat(locations).isEmpty();
    }

    @Test
    public void testFindsMultipleLocationsIfTheyExist() throws ExecutionException {
        String nonRepeatingReference = nonRepeatingReference();
        String repeatingReference = nonRepeatingReference + nonRepeatingReference;
        ReferenceIndex referenceIndex = new ReferenceIndex(repeatingReference, 5);
        List<Integer> locations = referenceIndex.find(repeatingReference.substring(3, 7));
        assertThat(locations).containsExactly(3, 13);
    }

    private String nonRepeatingReference() {
        return IntStream
                .range(0, 10)
                .mapToObj(String::valueOf)
                .reduce((s1, s2) -> s1+s2)
                .orElse("");
    }
}