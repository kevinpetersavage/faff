package seq;

import com.google.common.base.Supplier;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.jooq.lambda.Seq;

import java.util.StringJoiner;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.jooq.lambda.Seq.seq;

public class ReadCache {
    private Cache<Integer, CircularFifoBuffer> cache = CacheBuilder
            .newBuilder()
            .build(CacheLoader.from((Supplier<CircularFifoBuffer>) CircularFifoBuffer::new));

    public String rollingRead(SingleRead singleRead) throws ExecutionException {
        CircularFifoBuffer buffer = cache.get(singleRead.getLocationWithImage(), CircularFifoBuffer::new);
        Seq<String> seq = seq(buffer).map(Object::toString);
        return seq.collect(Collectors.joining());
    }
}
