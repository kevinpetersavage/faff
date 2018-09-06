package seq;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jooq.lambda.Seq;

import java.util.*;
import java.util.function.Predicate;

import static org.jooq.lambda.Seq.seq;

public class Alignment {
    private final UUID locationWithinImage;
    private final Set<Range<Integer>> ranges;

    Alignment(UUID locationWithinImage, int start, int end) {
        this(locationWithinImage, Collections.singleton(Range.closedOpen(start, end)));
    }

    Alignment(UUID locationWithinImage, Set<Range<Integer>> ranges) {
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
        Range<Integer> newRange = segment.getRange();
        Predicate<Range<Integer>> isConnected = newRange::isConnected;
        Range<Integer> connectedRange = seq(ranges).filter(isConnected)
                .concat(newRange)
                .reduce(Range::span)
                .orElse(newRange);

        Seq<Range<Integer>> disconnected = seq(ranges).filter(isConnected.negate());

        ImmutableSet.Builder<Range<Integer>> builder = ImmutableSet.<Range<Integer>>builder()
                .add(connectedRange)
                .addAll(disconnected);

        return new Alignment(locationWithinImage, builder.build());
    }
}
