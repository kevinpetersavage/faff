package seq;

import org.fest.assertions.Assertions;
import org.fest.assertions.ObjectAssert;

import java.util.Optional;

public class OptionalAssertions {
    public static <T> ObjectAssert assertThat(Optional<T> optional){
        Assertions.assertThat(optional.isPresent()).isTrue();
        return Assertions.assertThat(optional.get());
    }
}
