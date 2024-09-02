package no.spk.panda.functional;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.function.Function;

public final class Tuple7<T1, T2, T3, T4, T5, T6, T7> {
    public final T1 _1;
    public final T2 _2;
    public final T3 _3;
    public final T4 _4;
    public final T5 _5;
    public final T6 _6;
    public final T7 _7;

    public Tuple7(final T1 _1, final T2 _2, final T3 _3, final T4 _4, final T5 _5, final T6 _6, final T7 _7) {
        this._1 = requireNonNull(_1, "_1");
        this._2 = requireNonNull(_2, "_2");
        this._3 = requireNonNull(_3, "_3");
        this._4 = requireNonNull(_4, "_4");
        this._5 = requireNonNull(_5, "_5");
        this._6 = requireNonNull(_6, "_6");
        this._7 = requireNonNull(_7, "_7");
    }

    public <U> U map(final Function7<T1, T2, T3, T4, T5, T6, T7, U> f) {
        return Tuple.tupled(f).apply(this);
    }

    public <T8> Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> append(final T8 _8) {
        return Tuple.tuple(_1, _2, _3, _4, _5, _6, _7, _8);
    }

    public <U> Tuple7<U, T2, T3, T4, T5, T6, T7> map1(final Function<T1, U> f) {
        return Tuple.tuple(f.apply(_1), _2, _3, _4, _5, _6, _7);
    }

    public <U> Tuple7<T1, U, T3, T4, T5, T6, T7> map2(final Function<T2, U> f) {
        return Tuple.tuple(_1, f.apply(_2), _3, _4, _5, _6, _7);
    }

    public <U> Tuple7<T1, T2, U, T4, T5, T6, T7> map3(final Function<T3, U> f) {
        return Tuple.tuple(_1, _2, f.apply(_3), _4, _5, _6, _7);
    }

    public <U> Tuple7<T1, T2, T3, U, T5, T6, T7> map4(final Function<T4, U> f) {
        return Tuple.tuple(_1, _2, _3, f.apply(_4), _5, _6, _7);
    }

    public <U> Tuple7<T1, T2, T3, T4, U, T6, T7> map5(final Function<T5, U> f) {
        return Tuple.tuple(_1, _2, _3, _4, f.apply(_5), _6, _7);
    }

    public <U> Tuple7<T1, T2, T3, T4, T5, U, T7> map6(final Function<T6, U> f) {
        return Tuple.tuple(_1, _2, _3, _4, _5, f.apply(_6), _7);
    }

    public <U> Tuple7<T1, T2, T3, T4, T5, T6, U> map7(final Function<T7, U> f) {
        return Tuple.tuple(_1, _2, _3, _4, _5, _6, f.apply(_7));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Tuple7<?, ?, ?, ?, ?, ?, ?> tuple7 = (Tuple7<?, ?, ?, ?, ?, ?, ?>) o;
        return Objects.equals(_1, tuple7._1) &&
                Objects.equals(_2, tuple7._2) &&
                Objects.equals(_3, tuple7._3) &&
                Objects.equals(_4, tuple7._4) &&
                Objects.equals(_5, tuple7._5) &&
                Objects.equals(_6, tuple7._6) &&
                Objects.equals(_7, tuple7._7);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_1, _2, _3, _4, _5, _6, _7);
    }

    @Override
    public String toString() {
        return "(" + _1 +
                ", " + _2 +
                ", " + _3 +
                ", " + _4 +
                ", " + _5 +
                ", " + _6 +
                ", " + _7 +
                ')';
    }
}
