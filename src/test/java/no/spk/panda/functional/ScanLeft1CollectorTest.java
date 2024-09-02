package no.spk.panda.functional;

import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BinaryOperator;
import java.util.stream.Collector;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

class ScanLeft1CollectorTest {
    @Test
    void skal_kalle_akkumulatorfunksjon_en_gang_pr_element_i_streamen_etter_første_element() {
        final AtomicInteger counter = new AtomicInteger(0);
        collect(
                rangeClosed(1, 5),
                scanLeft1(
                        (acc, verdi) -> counter.incrementAndGet()
                )
        );
        assertThat(counter).hasValue(4);
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    @Test
    void skal_bruke_resultat_fra_forrige_kall_til_vindusfunksjonen_som_forrige_i_neste_kall_til_vindusfunksjonen() {
        assertThat(
                collect(
                        rangeClosed(1, 4),
                        scanLeft1(
                                Integer::sum
                        )
                )
        )
                .containsExactly(
                        1,
                        (1 + 2),
                        ((1 + 2) + 3),
                        (((1 + 2) + 3) + 4)
                );

        assertThat(
                collect(
                        rangeClosed(1, 5),
                        scanLeft1(
                                (acc, verdi) -> acc * verdi
                        )
                )
        )
                .containsExactly(
                        1, // Første element i streamen blir initiell verdi for akkumuleringa
                        (1 * 2),
                        ((1 * 2) * 3),
                        (((1 * 2) * 3) * 4),
                        ((((1 * 2) * 3) * 4) * 5)
                );
    }

    @Test
    void skal_beholde_input_også_når_streamen_har_kun_1_element() {
        final Object expected = new Object();
        assertThat(
                Stream.of(expected)
                        .collect(
                                Collectors.scanLeft1(
                                        (forrige, neste) -> new Object()
                                )
                        )
                        .findFirst()
        )
                .containsSame(expected);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void skal_ikkje_kalle_funksjon_når_input_har_kun_1_element() {
        Stream
                .of(new Object())
                .collect(
                        Collectors.scanLeft1(
                                (forrige, neste) -> {
                                    throw new AssertionError("Uforventa kall til vindufunksjonen");
                                }
                        )
                );
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void skal_feile_ved_parallellitet() {
        assertThatCode(
                () -> range(0, 100000)
                        .boxed()
                        .parallel()
                        .collect(
                                Collectors.scanLeft1(
                                        (forrige, neste) -> neste
                                )
                        )
                        .findAny()
        )
                .as("feil ved bruk på parallell-stream")
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("Støttar ikkje parallellitet i scanLeft1");
    }

    private static Collector<Integer, ?, Stream<Integer>> scanLeft1(final BinaryOperator<Integer> operasjon) {
        return new ScanLeft1Collector<>(operasjon);
    }

    private static Stream<Integer> collect(final IntStream input, final Collector<Integer, ?, Stream<Integer>> fold) {
        return input.boxed().collect(fold);
    }
}
