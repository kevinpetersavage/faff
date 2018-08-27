package seq;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcessorTest {
    @Test
    void testReturnsMatchesWhenFound(
            @Mock ReferenceIndex referenceIndex, @Mock ReadCache readCache, @Mock MultiMatchCombiner multiMatchCombiner
    ) throws ExecutionException {
        UUID locationWithinImage = UUID.randomUUID();
        SingleRead singleRead = new SingleRead('T', new SourceImageLocation(1, locationWithinImage));
        when(readCache.rollingRead(singleRead)).thenReturn("T");
        when(referenceIndex.find("T")).thenReturn(Collections.singletonList(10));
        AlignedReadSegment singleReadSegment = new AlignedReadSegment(locationWithinImage, 10, 11);
        List<Alignment> expected = Collections.singletonList(new Alignment(locationWithinImage, 10, 11));
        when(multiMatchCombiner.combineWithPrevious(Collections.singletonList(singleReadSegment)))
                .thenReturn(expected);

        Processor processor = new Processor(referenceIndex, readCache, multiMatchCombiner);
        List<Alignment> alignedReads = processor.process(singleRead);
        assertThat(alignedReads).isEqualTo(expected);
    }
}