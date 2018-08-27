package seq;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.jooq.lambda.Seq;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.jooq.lambda.Seq.seq;

class ReadCache {
    private final LoadingCache<UUID, CircularFifoBuffer> cache;

    ReadCache(int cacheSequenceLength) {
        cache = CacheBuilder
                .newBuilder()
                .build(CacheLoader.from(() -> new CircularFifoBuffer(cacheSequenceLength)));
    }

    String rollingRead(SingleRead singleRead) throws ExecutionException {
        CircularFifoBuffer buffer = cache.get(singleRead.getLocationWithImage());
        buffer.add(singleRead.getNucleotide());
        @SuppressWarnings("unchecked")
        Seq<String> seq = seq(buffer).map(Object::toString);
        return seq.collect(Collectors.joining());
    }
}
