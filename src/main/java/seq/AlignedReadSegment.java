package seq;

import com.google.common.collect.Range;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.UUID;

public class AlignedReadSegment {
    private final UUID readId;
    private final Range<Integer> range;

    AlignedReadSegment(UUID readId, int start, int end) {
        this.readId = readId;
        this.range = new Range<>(start, end);
    }

    public UUID getReadId() {
        return readId;
    }

    public Range<Integer> getRange() {
        return range;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public int getStart() {
        return range.lowerEndpoint();
    }

    public int getEnd() {
        return range.upperEndpoint();
    }
}
