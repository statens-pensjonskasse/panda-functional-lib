package no.spk.panda.functional;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Streams {

    private Streams() {
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <T> Stream<T> concat(final Stream<? extends T>... streams) {
        return Stream
                .of(streams)
                .reduce(
                        Stream.empty(),
                        Stream::concat,
                        Stream::concat
                );
    }

    public static <T> void forEach(
            final Stream<T> verdier,
            final Consumer<T> handling
    ) {
        verdier.forEach(handling);
    }

    public static <T1, T2> void forEach(
            final Stream<T1> _1,
            final Stream<T2> _2,
            final BiConsumer<T1, T2> handling
    ) {
        final List<T2> tmp = _2.toList();
        _1.forEach(
                t1 -> tmp.forEach(
                        t2 -> handling.accept(t1, t2)
                )
        );
    }
}
