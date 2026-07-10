package no.spk.panda.functional;

import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;
import static no.spk.panda.functional.Gatherers.slidingWindow;
import static no.spk.panda.functional.Tuple.tuple;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.List;
import java.util.stream.Stream;

import org.assertj.core.api.ListAssert;
import org.junit.jupiter.api.Test;

@SuppressWarnings({"unchecked", "RedundantSuppression"})
class SlidingWindowGathererTest {
    @Test
    void skal_produsere_vinduene_sekvensielt_og_parvis() {
        assertSlidingWindow(
                Stream.of(1, 2, 3, 4, 5, 6)
        )
                .containsExactly(
                        tuple(1, 2),
                        tuple(2, 3),
                        tuple(3, 4),
                        tuple(4, 5),
                        tuple(5, 6)
                );
    }

    @Test
    void skal_produsere_en_tom_stream_ved_bruk_på_en_tom_stream() {
        assertSlidingWindow(Stream.empty()).isEmpty();
    }

    @Test
    void skal_produsere_1_vindu_ved_bruk_på_en_stream_med_2_element() {
        assertSlidingWindow(
                Stream.of(1, 2)
        )
                .containsExactly(tuple(1, 2));
    }

    @Test
    void skal_feile_ved_bruk_av_slidingWindow_på_stream_med_1_element_for_å_sikre_at_en_oppdager_tap_av_informasjon_som_følge_av_feil_antagelsar() {
        //noinspection ResultOfMethodCallIgnored
        assertThatCode(
                () -> Stream.of(1).gather(slidingWindow()).toList()
        )
                .as("Forventet feil fra Stream.of(<kun 1 element>).gather(slidingWindow()).toList()")
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("slidingWindow er ikke støttet for en Stream som kun inneholder 1 element.");
    }

    @Test
    void skal_produsere_samme_resultat_ved_parallellitet() {
        assertThat(
                range(0, 100000)
                        .boxed()
                        .parallel()
                        .gather(slidingWindow())
                        .toList()
        )
                .as("parallell stream skal gi samme resultat som sekvensiell stream")
                .isEqualTo(
                        range(0, 100000)
                                .boxed()
                                .gather(slidingWindow())
                                .toList()
                );
    }

    @Test
    void skal_støtte_kortslutning_nedstrøms() {
        assertThat(
                Stream.of(1, 2, 3, 4, 5)
                        .gather(slidingWindow())
                        .limit(2)
                        .toList()
        )
                .as("(1, 2, 3, 4, 5).gather(slidingWindow()).limit(2)")
                .containsExactly(
                        tuple(1, 2),
                        tuple(2, 3)
                );
    }

    private ListAssert<Tuple2<Integer, Integer>> assertSlidingWindow(final Stream<Integer> input) {
        final List<Integer> buffer = input.toList();
        return assertThat(
                buffer
                        .stream()
                        .gather(slidingWindow())
                        .toList()
        )
                .as(
                        "(%s).gather(slidingWindow())",
                        buffer.stream().map(Object::toString).collect(joining(", "))
                );
    }
}
