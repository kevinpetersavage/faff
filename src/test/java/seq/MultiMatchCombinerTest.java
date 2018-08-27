package seq;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.fest.assertions.Assertions.assertThat;

class MultiMatchCombinerTest {
    @Test
    void shouldCombineOneMatch(){
        AlignedReadSegment readSegment = new AlignedReadSegment(UUID.randomUUID(), 10, 11);
        List<Alignment> alignment = new MultiMatchCombiner().combineWithPrevious(Collections.singletonList(readSegment));

        assertThat(alignment).isEqualTo(Collections.singletonList(new Alignment(UUID.randomUUID(), 10, 11)));
    }

    @Test
    void shouldCombineOverlapping(){
        AlignedReadSegment readSegment1 = new AlignedReadSegment(UUID.randomUUID(), 10, 20);
        AlignedReadSegment readSegment2 = new AlignedReadSegment(UUID.randomUUID(), 15, 25);

        MultiMatchCombiner multiMatchCombiner = new MultiMatchCombiner();
        multiMatchCombiner.combineWithPrevious(Collections.singletonList(readSegment1));
        List<Alignment> alignment = multiMatchCombiner.combineWithPrevious(Collections.singletonList(readSegment2));

        assertThat(alignment).isEqualTo(Collections.singletonList(new Alignment(UUID.randomUUID(), 10, 25)));
    }
}