package no.spk.felles.functional;

import java.util.Optional;
import java.util.function.BiFunction;

public interface OptionalApply {

    static <I1, I2, R> Optional<R> optionalApply(final Optional<I1> oi1,
                                                 final Optional<I2> oi2,
                                                 final BiFunction<I1, I2, R> f) {
        return oi1.flatMap(i1 ->
                oi2.map(i2 ->
                        f.apply(i1, i2)
                )
        );
    }

    static <I1, I2, I3, R> Optional<R> optionalApply(final Optional<I1> oi1,
                                                     final Optional<I2> oi2,
                                                     final Optional<I3> oi3,
                                                     final Function3<I1, I2, I3, R> f) {
        return oi1.flatMap(i1 ->
                oi2.flatMap(i2 ->
                        oi3.map(i3 ->
                                f.apply(i1, i2, i3)
                        )
                )
        );
    }

    static <I1, I2, I3, I4, R> Optional<R> optionalApply(final Optional<I1> oi1,
                                                         final Optional<I2> oi2,
                                                         final Optional<I3> oi3,
                                                         final Optional<I4> oi4,
                                                         final Function4<I1, I2, I3, I4, R> f) {
        return oi1.flatMap(i1 ->
                oi2.flatMap(i2 ->
                        oi3.flatMap(i3 ->
                                oi4.map(i4 ->
                                        f.apply(i1, i2, i3, i4)
                                )
                        )
                )
        );
    }

    static <I1, I2, I3, I4, I5, R> Optional<R> optionalApply(final Optional<I1> oi1,
                                                             final Optional<I2> oi2,
                                                             final Optional<I3> oi3,
                                                             final Optional<I4> oi4,
                                                             final Optional<I5> oi5,
                                                             final Function5<I1, I2, I3, I4, I5, R> f) {
        return oi1.flatMap(i1 ->
                oi2.flatMap(i2 ->
                        oi3.flatMap(i3 ->
                                oi4.flatMap(i4 ->
                                        oi5.map(i5 ->
                                                f.apply(i1, i2, i3, i4, i5)
                                        )
                                )
                        )
                )
        );
    }

}
