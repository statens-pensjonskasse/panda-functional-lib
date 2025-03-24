package no.spk.panda.functional;

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

/**
 * Kombinerer/akkumulerer to og to elementer fra streamen med en valgfri initiell verdi først, returnerer en Stream som inneholder både siste resultat og alle delresultat
 * som blir produsert underveis.
 *
 * @param <T> typen av det man skal akkumulere på
 * @see Collectors#scanLeft1(BinaryOperator)
 */
public class ScanLeftCollector<T> implements Collector<T, ScanLeftCollector.Akkumulator<T>, Stream<T>> {
    public static class Akkumulator<T> {
        private LinkedList<T> resultat = new LinkedList<>();

        private Optional<T> forrige;

        public Akkumulator(final Optional<T> initiellVerdi) {
            forrige = requireNonNull(initiellVerdi, "initiellVerdi er påkrevd, men var null");
        }

        void concat(
                final T neste,
                final BinaryOperator<T> windowFunction
        ) {
            this.forrige = Optional.of(erstatt(neste, windowFunction));
            this.resultat = new LinkedList<>(resultat);
            forrige.ifPresent(resultat::add);
        }

        Stream<T> finish() {
            return resultat.stream();
        }

        private T erstatt(final T neste, final BiFunction<T, T, T> mapper) {
            return this.forrige.map(
                            forrige -> mapper.apply(
                                    forrige,
                                    neste
                            )
                    )
                    .orElse(neste);
        }
    }

    private final Optional<T> initiellVerdi;
    private final BinaryOperator<T> mapper;

    ScanLeftCollector(final Optional<T> initiellVerdi, final BinaryOperator<T> mapper) {
        this.initiellVerdi = requireNonNull(initiellVerdi, "initiellVerdi er påkrevd, men var null");
        this.mapper = requireNonNull(mapper, "mapper er påkrevd, men var null");
    }

    @Override
    public Supplier<Akkumulator<T>> supplier() {
        return () -> new Akkumulator<>(initiellVerdi);
    }

    @Override
    public BiConsumer<Akkumulator<T>, T> accumulator() {
        return (acc, neste) -> acc.concat(neste, mapper);
    }

    @Override
    public Function<Akkumulator<T>, Stream<T>> finisher() {
        return Akkumulator::finish;
    }

    @Override
    public BinaryOperator<Akkumulator<T>> combiner() {
        return (a, b) -> {
            throw new UnsupportedOperationException("Støttar ikkje parallellitet i scanLeft1(...): skal være sekvensiell");
        };
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.emptySet();
    }
}
