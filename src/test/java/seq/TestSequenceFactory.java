package seq;

import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public class TestSequenceFactory {
    private static final Random random = new Random();

    public String createRandomSequence(int length) {
        return random.ints(0, 4)
                .mapToObj("ATCG"::charAt)
                .map(Objects::toString).limit(length)
                .collect(Collectors.joining());
    }

}
