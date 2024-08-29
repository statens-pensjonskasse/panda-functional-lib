package no.spk.felles.functional;

import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class Tuple2<T1, T2> {
    public final T1 _1;
    public final T2 _2;

    public Tuple2(final T1 _1, final T2 _2) {
        this._1 = requireNonNull(_1, "_1");
        this._2 = requireNonNull(_2, "_2");
    }

    public <U> U map(final BiFunction<T1, T2, U> f) {
        return f.apply(_1, _2);
    }

    public <U> Tuple2<U, T2> map1(final Function<T1, U> f) {
        return Tuple.tuple(f.apply(_1), _2);
    }

    public <U> Tuple2<T1, U> map2(final Function<T2, U> f) {
        return Tuple.tuple(_1, f.apply(_2));
    }

    public <U> Tuple2<T1, U> erstatt2(final BiFunction<T1, T2, U> f) {
        return Tuple.tuple(_1, f.apply(_1, _2));
    }

    public <T3> Tuple3<T1, T2, T3> append(final T3 _3) {
        return Tuple.tuple(_1, _2, _3);
    }

    public <T0> Tuple3<T0, T1, T2> prepend(final T0 _0) {
        return Tuple.tuple(_0, _1, _2);
    }

    public Tuple2<T2, T1> swap() {
        return Tuple.tuple(_2, _1);
    }

    public Map<T1, T2> asMap() {
        return Tuple.asMap(this);
    }

    public T1 _1() {
        return _1;
    }

    public T2 _2() {
        return _2;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Tuple2<?, ?> tuple2 = (Tuple2<?, ?>) o;
        return Objects.equals(_1, tuple2._1) &&
                Objects.equals(_2, tuple2._2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_1, _2);
    }

    @Override
    public String toString() {
        return "(" + _1 +
                ", " + _2 +
                ')';
    }
}
