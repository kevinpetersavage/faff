package seq;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static seq.OptionalAssertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcessorTest {
    @Test
    public void testReturnsMatchesWhenFound(@Mock ReferenceIndex referenceIndex, @Mock ReadCache readCache) throws ExecutionException {
        SingleRead singleRead = new SingleRead('T', new SourceImageLocation(1, 1));
        when(readCache.rollingRead(singleRead)).thenReturn("T");
        when(referenceIndex.find("T")).thenReturn(Collections.singletonList(10));

        Optional<AlignedReadSegment> alignedReads = new Processor(referenceIndex, readCache).process(singleRead);
        assertThat(alignedReads).isEqualTo(new AlignedReadSegment("T", 10));
    }
}