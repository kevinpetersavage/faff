package seq;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.fest.assertions.Assertions.assertThat;

class AlignmentTest {
    @Test
    void shouldMergeRanges(){
        UUID readId = UUID.randomUUID();
        Alignment original = new Alignment(readId, 10, 25);
        Alignment alignment = original.mergeIn(new AlignedReadSegment(readId, 20, 30));

        assertThat(alignment).isEqualTo(new Alignment(readId, 10, 30));
    }

    @Test
    void shouldAllowDistinctRanges(){
        UUID readId = UUID.randomUUID();
        Alignment original = new Alignment(readId, 10, 15);
        Alignment alignment = original.mergeIn(new AlignedReadSegment(readId, 20, 30));

        assertThat(alignment).isEqualTo(new Alignment(readId, ImmutableSet.of(
                Range.closedOpen(10, 15),
                Range.closedOpen(20, 30))));
    }

    @Test
    void shouldMergeMultipleRangesIfNeeded(){
        UUID readId = UUID.randomUUID();
        Alignment original = new Alignment(readId, 5, 15);
        Alignment disjoint = original.mergeIn(new AlignedReadSegment(readId, 20, 30));
        Alignment alignment = disjoint.mergeIn(new AlignedReadSegment(readId, 10, 25));

        assertThat(alignment).isEqualTo(new Alignment(readId, 5, 30));
    }
}