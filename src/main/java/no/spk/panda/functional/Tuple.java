package no.spk.panda.functional;

import static no.spk.panda.functional.Collectors.foldLeft;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

public interface Tuple {
    static <T1, T2> void forEach(
            final Stream<Tuple2<T1, T2>> tuples,
            final BiConsumer<T1, T2> f
    ) {
        tuples.forEach(t -> f.accept(t._1, t._2));
    }

    static <T1, T2, T3> void forEach(
            final Stream<Tuple3<T1, T2, T3>> tuples,
            final Consumer3<T1, T2, T3> f
    ) {
        tuples.forEach(t -> f.accept(t._1, t._2, t._3));
    }

    static <T1, T2, T3, T4> void forEach(
            final Stream<Tuple4<T1, T2, T3, T4>> tuples,
            final Consumer4<T1, T2, T3, T4> f
    ) {
        tuples.forEach(t -> f.accept(t._1, t._2, t._3, t._4));
    }

    /*
        Burde bruke Collectors.toMap, men den har litt svak feilmelding. Den skal være fikset i java9.
     */
    @SafeVarargs
    @SuppressWarnings("varargs")
    static <Key, Value> Map<Key, Value> asMap(final Tuple2<Key, Value> t1, final Tuple2<Key, Value>... tuples) {
        return Stream.concat(
                Stream.of(t1),
                Arrays.stream(tuples)
        ).collect(foldLeft(new HashMap<>(), (map, tuple) -> {
            if (map.containsKey(tuple._1)) {
                throw new IllegalStateException(String.format("Inneholder flere nøkler: '%s'", tuple._1));
            }
            map.put(tuple._1, tuple._2);
            return map;
        }));
    }

    static <T1, T2> Tuple2<T1, T2> tuple(final T1 _1, final T2 _2) {
        return new Tuple2<>(_1, _2);
    }

    static <T1, T2, T3> Tuple3<T1, T2, T3> tuple(final T1 _1, final T2 _2, final T3 _3) {
        return new Tuple3<>(_1, _2, _3);
    }

    static <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4> tuple(final T1 _1, final T2 _2, final T3 _3, final T4 _4) {
        return new Tuple4<>(_1, _2, _3, _4);
    }

    static <T1, T2, T3, T4, T5> Tuple5<T1, T2, T3, T4, T5> tuple(final T1 _1, final T2 _2, final T3 _3, final T4 _4, final T5 _5) {
        return new Tuple5<>(_1, _2, _3, _4, _5);
    }

    static <T1, T2, T3, T4, T5, T6> Tuple6<T1, T2, T3, T4, T5, T6> tuple(final T1 _1, final T2 _2, final T3 _3, final T4 _4, final T5 _5, final T6 _6) {
        return new Tuple6<>(_1, _2, _3, _4, _5, _6);
    }

    static <T1, T2, T3, T4, T5, T6, T7> Tuple7<T1, T2, T3, T4, T5, T6, T7> tuple(final T1 _1, final T2 _2, final T3 _3, final T4 _4, final T5 _5, final T6 _6, final T7 _7) {
        return new Tuple7<>(_1, _2, _3, _4, _5, _6, _7);
    }

    static <T1, T2, T3, T4, T5, T6, T7, T8> Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> tuple(final T1 _1, final T2 _2, final T3 _3, final T4 _4, final T5 _5, final T6 _6, final T7 _7, final T8 _8) {
        return new Tuple8<>(_1, _2, _3, _4, _5, _6, _7, _8);
    }

    static <T1, T2, T3, T4, T5, T6, T7, T8, T9> Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> tuple(final T1 _1, final T2 _2, final T3 _3, final T4 _4, final T5 _5, final T6 _6, final T7 _7, final T8 _8, final T9 _9) {
        return new Tuple9<>(_1, _2, _3, _4, _5, _6, _7, _8, _9);
    }

    static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Tuple10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> tuple(final T1 _1, final T2 _2, final T3 _3, final T4 _4, final T5 _5, final T6 _6, final T7 _7, final T8 _8, final T9 _9, final T10 _10) {
        return new Tuple10<>(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10);
    }

    // TUPLE

    static <T1, T2, R> Function<Tuple2<T1, T2>, R> tupled(final BiFunction<T1, T2, R> f) {
        return tuple -> f.apply(tuple._1, tuple._2);
    }

    static <T1, T2, T3, R> Function<Tuple3<T1, T2, T3>, R> tupled(final Function3<T1, T2, T3, R> f) {
        return tuple -> f.apply(tuple._1, tuple._2, tuple._3);
    }

    static <T1, T2, T3, T4, R> Function<Tuple4<T1, T2, T3, T4>, R> tupled(final Function4<T1, T2, T3, T4, R> f) {
        return tuple -> f.apply(tuple._1, tuple._2, tuple._3, tuple._4);
    }

    static <T1, T2, T3, T4, T5, R> Function<Tuple5<T1, T2, T3, T4, T5>, R> tupled(final Function5<T1, T2, T3, T4, T5, R> f) {
        return tuple -> f.apply(tuple._1, tuple._2, tuple._3, tuple._4, tuple._5);
    }

    static <T1, T2, T3, T4, T5, T6, R> Function<Tuple6<T1, T2, T3, T4, T5, T6>, R> tupled(final Function6<T1, T2, T3, T4, T5, T6, R> f) {
        return tuple -> f.apply(tuple._1, tuple._2, tuple._3, tuple._4, tuple._5, tuple._6);
    }

    static <T1, T2, T3, T4, T5, T6, T7, R> Function<Tuple7<T1, T2, T3, T4, T5, T6, T7>, R> tupled(final Function7<T1, T2, T3, T4, T5, T6, T7, R> f) {
        return tuple -> f.apply(tuple._1, tuple._2, tuple._3, tuple._4, tuple._5, tuple._6, tuple._7);
    }

    static <T1, T2, T3, T4, T5, T6, T7, T8, R> Function<Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>, R> tupled(final Function8<T1, T2, T3, T4, T5, T6, T7, T8, R> f) {
        return tuple -> f.apply(tuple._1, tuple._2, tuple._3, tuple._4, tuple._5, tuple._6, tuple._7, tuple._8);
    }

    static <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> Function<Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>, R> tupled(final Function9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R> f) {
        return tuple -> f.apply(tuple._1, tuple._2, tuple._3, tuple._4, tuple._5, tuple._6, tuple._7, tuple._8, tuple._9);
    }

    static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> Function<Tuple10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>, R> tupled(final Function10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> f) {
        return tuple -> f.apply(tuple._1, tuple._2, tuple._3, tuple._4, tuple._5, tuple._6, tuple._7, tuple._8, tuple._9, tuple._10);
    }


}
