package seq;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

class Processor {
    private final ReferenceIndex referenceIndex;
    private final ReadCache readCache;

    Processor(ReferenceIndex referenceIndex, ReadCache readCache, MultiMatchCombiner combiner) {
        this.referenceIndex = referenceIndex;
        this.readCache = readCache;
    }

    Optional<AlignedReadSegment> process(SingleRead read) throws ExecutionException {
        String rollingRead = readCache.rollingRead(read);
        List<Integer> location = referenceIndex.find(rollingRead);

        if (location.size() == 1) {
            return Optional.of(new AlignedReadSegment(read.getLocationWithImage(), location.get(0)));
        } else {
            return Optional.empty();
        }
    }
}
