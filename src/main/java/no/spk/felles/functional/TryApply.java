package no.spk.felles.functional;

import java.util.function.BiFunction;
import java.util.stream.Stream;

public interface TryApply {

    static <T> Stream<T> asStream(final Try<T> t) {
        return t.map(Stream::of).orElseGet(Stream::empty);
    }

    static <I1, I2, R> Try<R> tryApply(final Try<I1> oi1,
                                       final Try<I2> oi2,
                                       final BiFunction<I1, I2, R> f) {
        return oi1.flatMap(i1 ->
                oi2.map(i2 ->
                        f.apply(i1, i2)
                )
        );
    }

    static <I1, I2, I3, R> Try<R> tryApply(final Try<I1> oi1,
                                           final Try<I2> oi2,
                                           final Try<I3> oi3,
                                           final Function3<I1, I2, I3, R> f) {
        return oi1.flatMap(i1 ->
                oi2.flatMap(i2 ->
                        oi3.map(i3 ->
                                f.apply(i1, i2, i3)
                        )
                )
        );
    }

    static <I1, I2, I3, I4, R> Try<R> tryApply(final Try<I1> oi1,
                                               final Try<I2> oi2,
                                               final Try<I3> oi3,
                                               final Try<I4> oi4,
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

    static <I1, I2, I3, I4, I5, R> Try<R> tryApply(final Try<I1> oi1,
                                                   final Try<I2> oi2,
                                                   final Try<I3> oi3,
                                                   final Try<I4> oi4,
                                                   final Try<I5> oi5,
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

    static <I1, I2, I3, I4, I5, I6, R> Try<R> tryApply(final Try<I1> oi1,
                                                       final Try<I2> oi2,
                                                       final Try<I3> oi3,
                                                       final Try<I4> oi4,
                                                       final Try<I5> oi5,
                                                       final Try<I6> oi6,
                                                       final Function6<I1, I2, I3, I4, I5, I6, R> f) {
        return oi1.flatMap(i1 ->
                oi2.flatMap(i2 ->
                        oi3.flatMap(i3 ->
                                oi4.flatMap(i4 ->
                                        oi5.flatMap(i5 ->
                                                oi6.map(i6 ->
                                                        f.apply(i1, i2, i3, i4, i5, i6)
                                                )
                                        )
                                )
                        )
                )
        );
    }

    static <I1, I2, I3, I4, I5, I6, I7, R> Try<R> tryApply(final Try<I1> oi1,
                                                           final Try<I2> oi2,
                                                           final Try<I3> oi3,
                                                           final Try<I4> oi4,
                                                           final Try<I5> oi5,
                                                           final Try<I6> oi6,
                                                           final Try<I7> oi7,
                                                           final Function7<I1, I2, I3, I4, I5, I6, I7, R> f) {
        return oi1.flatMap(i1 ->
                oi2.flatMap(i2 ->
                        oi3.flatMap(i3 ->
                                oi4.flatMap(i4 ->
                                        oi5.flatMap(i5 ->
                                                oi6.flatMap(i6 ->
                                                        oi7.map(i7 ->
                                                                f.apply(i1, i2, i3, i4, i5, i6, i7)
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );
    }
}
