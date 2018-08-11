package seq;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

import static org.fest.assertions.Assertions.assertThat;

class ReferenceIndexTest {
    private final TestSequenceFactory testSequenceFactory = new TestSequenceFactory();

    @Test
    public void testFindsSubReference() throws ExecutionException {
        String noRepeatingReference = nonRepeatingReference();
        int indexSubsequenceLength = 5;
        int startIndex = 3;

        ReferenceIndex referenceIndex = new ReferenceIndex(noRepeatingReference, indexSubsequenceLength);
        List<Integer> locations = referenceIndex.find(noRepeatingReference.substring(startIndex, startIndex + indexSubsequenceLength));

        assertThat(locations).containsExactly(startIndex);
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
        int indexSubsequenceLength = 5;
        int firstStartIndex = 3;

        ReferenceIndex referenceIndex = new ReferenceIndex(repeatingReference, indexSubsequenceLength);
        List<Integer> locations = referenceIndex.find(repeatingReference.substring(firstStartIndex, firstStartIndex + indexSubsequenceLength));

        assertThat(locations).containsExactly(firstStartIndex, firstStartIndex + nonRepeatingReference.length());
    }

    private String nonRepeatingReference() {
        return IntStream
                .range(0, 10)
                .mapToObj(String::valueOf)
                .reduce((s1, s2) -> s1+s2)
                .orElse("");
    }
}