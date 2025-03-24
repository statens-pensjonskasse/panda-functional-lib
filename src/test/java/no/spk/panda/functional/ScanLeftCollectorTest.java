package no.spk.panda.functional;

import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BinaryOperator;
import java.util.stream.Collector;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

class ScanLeftCollectorTest {
    @Test
    void skal_kalle_akkumulatorfunksjon_en_gang_pr_element_i_streamen() {
        final AtomicInteger counter = new AtomicInteger(0);
        collect(
                rangeClosed(1, 5),
                scanLeft(
                        Optional.of(0),
                        (acc, verdi) -> counter.incrementAndGet()
                )
        );
        assertThat(counter).hasValue(5);
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    @Test
    void skal_bruke_resultat_fra_forrige_kall_til_vindusfunksjonen_som_forrige_i_neste_kall_til_vindusfunksjonen_og_initiell_verdi_som_forrige_for_første_kall() {
        assertThat(
                collect(
                        rangeClosed(1, 5),
                        scanLeft(
                                Optional.of(10),
                                (acc, verdi) -> acc * verdi
                        )
                )
        )
                .containsExactly(
                        10 * 1,
                        10 * (1 * 2),
                        10 * ((1 * 2) * 3),
                        10 * (((1 * 2) * 3) * 4),
                        10 * ((((1 * 2) * 3) * 4) * 5)
                );
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    @Test
    void skal_ikke_endre_på_føste_verdi_hvis_initiell_verdi_er_tom() {
        assertThat(
                collect(
                        rangeClosed(1, 4),
                        scanLeft(
                                Optional.empty(),
                                Integer::sum
                        )
                )
        )
                .as("forrige + neste")
                .containsExactly(
                        1,
                        (1 + 2),
                        ((1 + 2) + 3),
                        (((1 + 2) + 3) + 4)
                );

        assertThat(
                collect(
                        rangeClosed(1, 5),
                        scanLeft(
                                Optional.empty(),
                                (acc, verdi) -> acc * verdi
                        )
                )
        )
                .as("forrige * neste")
                .containsExactly(
                        1,
                        (1 * 2),
                        ((1 * 2) * 3),
                        (((1 * 2) * 3) * 4),
                        ((((1 * 2) * 3) * 4) * 5)
                );
    }

    @Test
    void skal_beholde_input_også_når_streamen_har_kun_1_element_og_initiell_verdi_er_empty() {
        final Object expected = new Object();
        assertThat(
                Stream.of(expected)
                        .collect(
                                Collectors.scanLeft(
                                        Optional.empty(),
                                        (forrige, neste) -> new Object()
                                )
                        )
                        .findFirst()
        )
                .containsSame(expected);
    }

    @Test
    void skal_utføre_mappingen_på_input_også_når_streamen_har_kun_1_element_og_det_finnes_en_initiell_verdi() {
        final String original = "Elefant";
        final String initell = "Hei på ";
        assertThat(
                Stream.of(original)
                        .collect(
                                Collectors.scanLeft(
                                        Optional.of(initell),
                                        (forrige, neste) -> forrige + neste
                                )
                        )
        )
                .containsExactly("Hei på Elefant");
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void skal_ikkje_kalle_funksjon_når_input_har_kun_1_element_og_initiell_verdi_er_empty() {
        assertThatCode(() ->
                Stream
                        .of(new Object())
                        .collect(
                                Collectors.scanLeft(
                                        Optional.empty(),
                                        (forrige, neste) -> {
                                            throw new AssertionError("Uforventa kall til vindufunksjonen");
                                        }
                                )
                        )
        )
                .doesNotThrowAnyException();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void skal_feile_ved_parallellitet() {
        assertThatCode(
                () -> range(0, 100000)
                        .boxed()
                        .parallel()
                        .collect(
                                Collectors.scanLeft(
                                        Optional.empty(),
                                        (forrige, neste) -> neste
                                )
                        )
                        .findAny()
        )
                .as("feil ved bruk på parallell-stream")
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("Støttar ikkje parallellitet i scanLeft");
    }

    private static Collector<Integer, ?, Stream<Integer>> scanLeft(final Optional<Integer> initiellVerdi, final BinaryOperator<Integer> operasjon) {
        return new ScanLeftCollector<>(initiellVerdi, operasjon);
    }

    private static Stream<Integer> collect(final IntStream input, final Collector<Integer, ?, Stream<Integer>> fold) {
        return input.boxed().collect(fold);
    }
}
