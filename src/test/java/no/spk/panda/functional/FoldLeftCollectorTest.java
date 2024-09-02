package no.spk.panda.functional;

import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.function.BiFunction;
import java.util.stream.Collector;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

class FoldLeftCollectorTest {
    @SuppressWarnings("PointlessArithmeticExpression")
    @Test
    void skal_kalle_akkumulatorfunksjon_en_gang_pr_element_i_streamen() {
        assertThat(
                collect(
                        rangeClosed(1, 1000),
                        foldLeft(
                                0,
                                (acc, verdi) -> acc + 1
                        )
                )
        )
                .isEqualTo(1000);

        assertThat(
                collect(
                        rangeClosed(1, 5),
                        foldLeft(
                                1,
                                (acc, verdi) -> acc * verdi
                        )
                )
        )
                .isEqualTo(1 * 2 * 3 * 4 * 5);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void skal_feile_ved_parallellitet() {
        assertThatCode(
                () -> range(0, 100000)
                        .boxed()
                        .parallel()
                        .collect(
                                Collectors.foldLeft(
                                        0,
                                        (forrige, neste) -> forrige
                                )
                        )
        )
                .as("feil ved bruk på parallell-stream")
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("Støttar ikkje parallellitet i foldLeft(...)");
    }

    private static Collector<Integer, ?, Integer> foldLeft(final int initial, final BiFunction<Integer, Integer, Integer> operasjon) {
        return new FoldLeftCollector<>(initial, operasjon);
    }

    private static Integer collect(final IntStream input, final Collector<Integer, ?, Integer> fold) {
        return input.boxed().collect(fold);
    }
}
