package seq;

import com.google.common.base.Supplier;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.jooq.lambda.Seq;

import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.jooq.lambda.Seq.seq;

public class ReadCache {
    private final LoadingCache<Integer, CircularFifoBuffer> cache;

    public ReadCache(int cacheSequenceLength) {
        cache = CacheBuilder
                .newBuilder()
                .build(CacheLoader.from(() -> new CircularFifoBuffer(cacheSequenceLength)));
    }

    public String rollingRead(SingleRead singleRead) throws ExecutionException {
        CircularFifoBuffer buffer = cache.get(singleRead.getLocationWithImage());
        buffer.add(singleRead.getNucleotide());
        Seq<String> seq = seq(buffer).map(Object::toString); // this will be fixed using commons-collections 4
        return seq.collect(Collectors.joining());
    }
}
