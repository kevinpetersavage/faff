package seq;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class Processor {
    private final ReferenceIndex referenceIndex;
    private final ReadCache readCache;

    public Processor(ReferenceIndex referenceIndex, ReadCache readCache) {
        this.referenceIndex = referenceIndex;
        this.readCache = readCache;
    }

    public Optional<AlignedReadSegment> process(SingleRead read) throws ExecutionException {
        String rollingRead = readCache.rollingRead(read);
        List<Integer> location = referenceIndex.find(rollingRead);

        if (location.size() == 1) {
            return Optional.of(new AlignedReadSegment(rollingRead, location.get(0)));
        } else {
            return Optional.empty();
        }
    }
}
