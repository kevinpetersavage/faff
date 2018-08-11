import org.jooq.lambda.Seq;

import java.util.List;
import java.util.Optional;

public class Processor {
    private final ReferenceIndex referenceIndex;
    private final ReadCache readCache;

    public Processor(ReferenceIndex referenceIndex, ReadCache readCache) {
        this.referenceIndex = referenceIndex;
        this.readCache = readCache;
    }

    public Optional<AlignedReadSegment> process(SingleRead read) {
        String rollingRead = readCache.rollingRead(read);
        List<Integer> location = referenceIndex.find(rollingRead);
        if (location.size() == 1) {
            return Optional.of(new AlignedReadSegment(rollingRead, location.get(0)));
        } else {
            return Optional.empty();
        }
    }
}
