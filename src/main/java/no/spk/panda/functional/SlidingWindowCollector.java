package no.spk.panda.functional;

import static no.spk.panda.functional.Tuple.tuple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

/**
 * Gir et glidende vindu med størrelse 2, representert som en {@link Tuple2}.
 *
 * @param <T> typen av den man skal samle opp.
 * @see Collectors#slidingWindow()
 */
class SlidingWindowCollector<T> implements Collector<T, List<Tuple2<T, Optional<T>>>, Stream<Tuple2<T, T>>> {
    @Override
    public Supplier<List<Tuple2<T, Optional<T>>>> supplier() {
        return ArrayList::new;
    }

    @Override
    public BiConsumer<List<Tuple2<T, Optional<T>>>, T> accumulator() {
        return (list, item) -> {
            if (!list.isEmpty()) {
                final int lastIndex = list.size() - 1;
                final T lastT = list.get(lastIndex)._1;
                list.set(lastIndex, tuple(lastT, Optional.of(item)));
            }
            list.add(tuple(item, Optional.empty()));
        };
    }

    @Override
    public Function<List<Tuple2<T, Optional<T>>>, Stream<Tuple2<T, T>>> finisher() {
        return list -> {
            if (list.size() == 1) {
                throw new UnsupportedOperationException("slidingWindow er ikke støttet for en Stream som kun inneholder 1 element.");
            }
            return list
                    .stream()
                    .filter(t -> t._2.isPresent())
                    .map(s -> tuple(s._1, s._2.get()));
        };
    }

    @Override
    public BinaryOperator<List<Tuple2<T, Optional<T>>>> combiner() {
        return (a, b) -> {
            throw new UnsupportedOperationException("Støttar ikkje parallellitet i slidingWindow(...): skal være sekvensiell");
        };
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.emptySet();
    }
}
