package no.spk.panda.functional;

import static no.spk.panda.functional.Tuple.asMap;
import static no.spk.panda.functional.Tuple.tuple;
import static no.spk.panda.functional.Tuple.tupled;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

class TupleTest {

    @Test
    void tupled_skal_endre_en_funksjon_med_tuple2() {
        final BiFunction<Integer, Integer, Integer> sum = Integer::sum;
        final BiFunction<Integer, Integer, Integer> sumSquare = sum.andThen(i -> i * i);

        assertThat(sum.apply(3, 3)).isEqualTo(6);
        assertThat(sumSquare.apply(3, 3)).isEqualTo(36);

        final Tuple2<Integer, Integer> parMedTall = tuple(3, 3);

        assertThat(sum.apply(parMedTall._1, parMedTall._2)).isEqualTo(6);

        final Function<Tuple2<Integer, Integer>, Integer> parSum = tupled(sum);

        assertThat(parSum.apply(parMedTall)).isEqualTo(6);
    }

    @Test
    void tupled_skal_endre_en_funksjon_med_tuple3() {
        final Function3<Integer, Integer, Integer, Integer> volumBeregning = (h, b, d) -> h * b * d;

        final Tuple3<Integer, Integer, Integer> boksHBD = tuple(3, 4, 5);

        final Integer volumn1 = volumBeregning.apply(boksHBD._1, boksHBD._2, boksHBD._3);
        final Function<Tuple3<Integer, Integer, Integer>, Integer> boksVolumBeregning = tupled(volumBeregning);
        final Integer volumn2 = boksVolumBeregning.apply(boksHBD);

        assertThat(volumn1).isEqualTo(volumn2).isEqualTo(60);
    }

    @Test
    void asMap_should_handle_simle_tuple() {
        final Map<Integer, Integer> m = asMap(tuple(1, 2));
        assertThat(m).hasSize(1)
                .containsKey(1)
                .containsValue(2);
    }

    @Test
    void asMap_should_handle_double_tuple() {
        final Map<Integer, Integer> m = asMap(tuple(1, 2), tuple(3, 4));
        assertThat(m).hasSize(2)
                .containsKeys(1, 3)
                .containsValues(2, 4);
    }

    @Test
    void asMap_should_cast_exception_on_duplicate_key() {
        final Try<Map<Integer, Integer>> m = Try.doTry(() -> asMap(tuple(1, 6), tuple(1, 4)));
        assertThat(m.getCause())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Inneholder flere nøkler: '1'");
    }

    @Test
    void skal_kun_erstatte_2_basert_på_både_1_og_2() {
        assertThat(
                tuple(1, 2).erstatt2(Integer::sum)
        )
                .isEqualTo(tuple(1, 3));
    }
}
