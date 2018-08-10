import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcessorTest {
    @Test
    public void testReturnsExactSingleMatches(@Mock ReferenceIndex referenceIndex, @Mock ReadCache readCache){
        SingleRead singleRead = new SingleRead('T', 1, 1);
        when(referenceIndex.find("T")).thenReturn(10);

        List<AlignedRead> alignedReads = new Processor(referenceIndex, readCache).process(singleRead);
        assertThat(alignedReads).containsExactly(new AlignedRead("T", 10));
    }
}