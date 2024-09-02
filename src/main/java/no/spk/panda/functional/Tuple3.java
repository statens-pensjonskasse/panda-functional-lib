package no.spk.panda.functional;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.function.Function;

public final class Tuple3<T1, T2, T3> {
    public final T1 _1;
    public final T2 _2;
    public final T3 _3;

    public Tuple3(final T1 _1, final T2 _2, final T3 _3) {
        this._1 = requireNonNull(_1, "_1");
        this._2 = requireNonNull(_2, "_2");
        this._3 = requireNonNull(_3, "_3");
    }

    public <U> U map(final Function3<T1, T2, T3, U> f) {
        return Tuple.tupled(f).apply(this);
    }

    public <U> Tuple3<U, T2, T3> map1(final Function<T1, U> f) {
        return Tuple.tuple(f.apply(_1), _2, _3);
    }

    public <U> Tuple3<T1, U, T3> map2(final Function<T2, U> f) {
        return Tuple.tuple(_1, f.apply(_2), _3);
    }

    public <U> Tuple3<T1, T2, U> map3(final Function<T3, U> f) {
        return Tuple.tuple(_1, _2, f.apply(_3));
    }

    public <T4> Tuple4<T1, T2, T3, T4> append(final T4 _4) {
        return Tuple.tuple(_1, _2, _3, _4);
    }

    public <T0> Tuple4<T0, T1, T2, T3> prepend(final T0 _0) {
        return Tuple.tuple(_0, _1, _2, _3);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Tuple3<?, ?, ?> tuple3 = (Tuple3<?, ?, ?>) o;
        return Objects.equals(_1, tuple3._1) &&
                Objects.equals(_2, tuple3._2) &&
                Objects.equals(_3, tuple3._3);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_1, _2, _3);
    }

    @Override
    public String toString() {
        return "(" + _1 +
                ", " + _2 +
                ", " + _3 +
                ')';
    }
}
