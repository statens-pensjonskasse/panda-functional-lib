package no.spk.panda.functional;

import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

/**
 * Tar en start tilstand og handling og gjør så handlingen på alle elementene i streamen og gir tilbake et samlet resultat
 * <br>
 * FoldLeft støtter ikke {@link Stream#parallel() parallellitet}.
 *
 * @param <T> Type som holder det som blir "foldet" inn i.
 * @param <R> den akkumulerte tilstanden
 */
class FoldLeftCollector<T, R> implements Collector<T, FoldLeftCollector.Holder<T, R>, R> {
    private final R initial;
    private final BiFunction<R, T, R> reduce;

    FoldLeftCollector(final R initial, final BiFunction<R, T, R> reduce) {
        this.initial = initial;
        this.reduce = reduce;
    }

    @Override
    public Supplier<Holder<T, R>> supplier() {
        return () -> new Holder<>(initial, reduce);
    }

    @Override
    public BiConsumer<Holder<T, R>, T> accumulator() {
        return Holder::update;
    }

    @Override
    public BinaryOperator<Holder<T, R>> combiner() {
        return (h1, h2) -> {
            throw new UnsupportedOperationException("Støttar ikkje parallellitet i foldLeft(...): skal være sekvensiell");
        };
    }

    @Override
    public Function<Holder<T, R>, R> finisher() {
        return Holder::get;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.emptySet();
    }

    static class Holder<A, R> {
        private final BiFunction<R, A, R> reduce;
        private R verdi;

        private Holder(final R verdi, final BiFunction<R, A, R> reduce) {
            this.verdi = verdi;
            this.reduce = reduce;
        }

        void update(final A a) {
            verdi = reduce.apply(verdi, a);
        }

        R get() {
            return verdi;
        }
    }
}
