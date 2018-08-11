import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class AlignedReadSegment {
    private final String sequence;
    private final int location;

    public AlignedReadSegment(String sequence, int location) {
        this.sequence = sequence;
        this.location = location;
    }

    public String getSequence() {
        return sequence;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
