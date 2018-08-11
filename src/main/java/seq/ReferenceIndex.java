package seq;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

public class ReferenceIndex {
    private final int indexSubsequenceLength;
    @SuppressWarnings("Convert2MethodRef")
    private LoadingCache<String, List<Integer>> index =
                CacheBuilder
                        .newBuilder()
                        .build(CacheLoader.from(() -> new ArrayList<>()));

    public ReferenceIndex(String reference, int indexSubsequenceLength) throws ExecutionException {
        this.indexSubsequenceLength = indexSubsequenceLength;
        for (int i = 0; i < reference.length() - indexSubsequenceLength; i++) {
            String subReference = reference.substring(i, i + indexSubsequenceLength);
            index.get(subReference).add(i);
        }
    }

    public List<Integer> find(String searchString) throws ExecutionException {
        if (searchString.length() == indexSubsequenceLength){
            return index.get(searchString);
        }
        return Collections.emptyList();
    }
}
