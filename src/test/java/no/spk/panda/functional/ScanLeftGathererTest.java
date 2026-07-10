package no.spk.panda.functional;

import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;
import static no.spk.panda.functional.Gatherers.scanLeft;
import static no.spk.panda.functional.Gatherers.scanLeft1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Gatherer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

class ScanLeftGathererTest {
    @Test
    void skal_kalle_akkumulatorfunksjon_en_gang_pr_element_i_streamen() {
        final AtomicInteger counter = new AtomicInteger(0);
        gather(
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
                gather(
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
                gather(
                        rangeClosed(1, 4),
                        scanLeft1(Integer::sum)
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
                gather(
                        rangeClosed(1, 5),
                        scanLeft1((acc, verdi) -> acc * verdi)
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
    void skal_produsere_en_tom_stream_ved_bruk_på_en_tom_stream() {
        assertThat(
                Stream.<Integer>empty()
                        .gather(scanLeft(Optional.of(10), Integer::sum))
                        .toList()
        )
                .isEmpty();

        assertThat(
                Stream.<Integer>empty()
                        .gather(scanLeft1(Integer::sum))
                        .toList()
        )
                .isEmpty();
    }

    @Test
    void skal_beholde_input_også_når_streamen_har_kun_1_element_og_initiell_verdi_er_empty() {
        final Object expected = new Object();
        assertThat(
                Stream.of(expected)
                        .gather(
                                scanLeft(
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
                        .gather(
                                scanLeft(
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
                        .gather(
                                scanLeft(
                                        Optional.empty(),
                                        (forrige, neste) -> {
                                            throw new AssertionError("Uforventa kall til vindufunksjonen");
                                        }
                                )
                        )
                        .toList()
        )
                .doesNotThrowAnyException();
    }

    @Test
    void skal_produsere_samme_resultat_ved_parallellitet() {
        assertThat(
                range(0, 100000)
                        .boxed()
                        .parallel()
                        .gather(scanLeft1(Integer::sum))
                        .toList()
        )
                .as("parallell stream skal gi samme resultat som sekvensiell stream")
                .isEqualTo(
                        range(0, 100000)
                                .boxed()
                                .gather(scanLeft1(Integer::sum))
                                .toList()
                );
    }

    @Test
    void skal_feile_dersom_initiell_verdi_er_null() {
        assertThatCode(
                () -> Gatherers.<Integer>scanLeft(null, Integer::sum)
        )
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("initiellVerdi er påkrevd, men var null");
    }

    @Test
    void skal_feile_dersom_mapper_er_null() {
        assertThatCode(
                () -> Gatherers.<Integer>scanLeft(Optional.empty(), null)
        )
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("mapper er påkrevd, men var null");
    }

    private static List<Integer> gather(final IntStream input, final Gatherer<Integer, ?, Integer> scan) {
        return input.boxed().gather(scan).toList();
    }
}
