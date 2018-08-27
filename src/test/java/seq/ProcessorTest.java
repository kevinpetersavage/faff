package seq;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static seq.OptionalAssertions.assertThat;
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

        Processor processor = new Processor(referenceIndex, readCache, multiMatchCombiner);
        Optional<AlignedReadSegment> alignedReads = processor.process(singleRead);
        assertThat(alignedReads).isEqualTo(new AlignedReadSegment(locationWithinImage, 10));
    }
}