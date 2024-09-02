package no.spk.panda.functional;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.function.Function;

public final class Tuple5<T1, T2, T3, T4, T5> {
    public final T1 _1;
    public final T2 _2;
    public final T3 _3;
    public final T4 _4;
    public final T5 _5;

    public Tuple5(final T1 _1, final T2 _2, final T3 _3, final T4 _4, final T5 _5) {
        this._1 = requireNonNull(_1, "_1");
        this._2 = requireNonNull(_2, "_2");
        this._3 = requireNonNull(_3, "_3");
        this._4 = requireNonNull(_4, "_4");
        this._5 = requireNonNull(_5, "_5");
    }

    public <T6> Tuple6<T1, T2, T3, T4, T5, T6> append(final T6 _6) {
        return Tuple.tuple(_1, _2, _3, _4, _5, _6);
    }

    public <T0> Tuple6<T0, T1, T2, T3, T4, T5> prepend(final T0 _0) {
        return Tuple.tuple(_0, _1, _2, _3, _4, _5);
    }

    public <U> U map(final Function5<T1, T2, T3, T4, T5, U> f) {
        return Tuple.tupled(f).apply(this);
    }

    public <U> Tuple5<U, T2, T3, T4, T5> map1(final Function<T1, U> f) {
        return Tuple.tuple(f.apply(_1), _2, _3, _4, _5);
    }

    public <U> Tuple5<T1, U, T3, T4, T5> map2(final Function<T2, U> f) {
        return Tuple.tuple(_1, f.apply(_2), _3, _4, _5);
    }

    public <U> Tuple5<T1, T2, U, T4, T5> map3(final Function<T3, U> f) {
        return Tuple.tuple(_1, _2, f.apply(_3), _4, _5);
    }

    public <U> Tuple5<T1, T2, T3, U, T5> map4(final Function<T4, U> f) {
        return Tuple.tuple(_1, _2, _3, f.apply(_4), _5);
    }

    public <U> Tuple5<T1, T2, T3, T4, U> map5(final Function<T5, U> f) {
        return Tuple.tuple(_1, _2, _3, _4, f.apply(_5));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Tuple5<?, ?, ?, ?, ?> tuple5 = (Tuple5<?, ?, ?, ?, ?>) o;
        return Objects.equals(_1, tuple5._1) &&
                Objects.equals(_2, tuple5._2) &&
                Objects.equals(_3, tuple5._3) &&
                Objects.equals(_4, tuple5._4) &&
                Objects.equals(_5, tuple5._5);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_1, _2, _3, _4, _5);
    }

    @Override
    public String toString() {
        return "(" + _1 +
                ", " + _2 +
                ", " + _3 +
                ", " + _4 +
                ", " + _5 +
                ')';
    }
}
