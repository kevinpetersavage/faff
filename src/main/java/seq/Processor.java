package seq;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.jooq.lambda.Seq.seq;

class Processor {
    private final ReferenceIndex referenceIndex;
    private final ReadCache readCache;
    private MultiMatchCombiner combiner;

    Processor(ReferenceIndex referenceIndex, ReadCache readCache, MultiMatchCombiner combiner) {
        this.referenceIndex = referenceIndex;
        this.readCache = readCache;
        this.combiner = combiner;
    }

    List<Alignment> process(SingleRead read) throws ExecutionException {
        String rollingRead = readCache.rollingRead(read);
        List<Integer> locations = referenceIndex.find(rollingRead);
        List<AlignedReadSegment> alignedReadSegments = seq(locations)
                .map(l -> new AlignedReadSegment(read.getLocationWithImage(), l, l + rollingRead.length()))
                .toList();
        return combiner.combineWithPrevious(alignedReadSegments);
    }
}
