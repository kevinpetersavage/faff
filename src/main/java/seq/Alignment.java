package seq;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.*;

import static org.jooq.lambda.Seq.seq;

public class Alignment {
    private final UUID locationWithinImage;
    private final List<Range<Integer>> ranges;

    Alignment(UUID locationWithinImage, int start, int end) {
        this(locationWithinImage, Collections.singletonList(Range.closedOpen(start, end)));
    }

    Alignment(UUID locationWithinImage, List<Range<Integer>> ranges) {
        this.locationWithinImage = locationWithinImage;
        this.ranges = ranges;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object that) {
        return EqualsBuilder.reflectionEquals(this, that);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    boolean intersects(AlignedReadSegment seg) {
        return seq(ranges).anyMatch(r -> r.isConnected(seg.getRange()));
    }

    Alignment mergeIn(AlignedReadSegment segment) {
        seq(ranges).map(r -> r.span())

        ImmutableList<Range<Integer>> newRanges = ImmutableList.<Range<Integer>>builder()
                .addAll(this.ranges)
                .add(segment.getRange())
                .build();
        return new Alignment(locationWithinImage, newRanges);
    }
}
