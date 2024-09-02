package no.spk.panda.functional;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static no.spk.panda.functional.Collectors.slidingWindow;
import static no.spk.panda.functional.Tuple.tuple;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.List;
import java.util.stream.Stream;

import org.assertj.core.api.ListAssert;
import org.junit.jupiter.api.Test;

@SuppressWarnings({"unchecked", "RedundantSuppression"})
class SlidingWindowCollectorTest {
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
    void skal_feile_ved_bruk_av_slidingWindow_på_tom_stream_for_å_sikre_at_en_oppdager_tap_av_informasjon_som_følge_av_feil_antagelsar() {
        //noinspection ResultOfMethodCallIgnored
        assertThatCode(
                () -> Stream.of(1).collect(slidingWindow())
        )
                .as("Forventet feil fra Stream.of(<kun 1 element>).collect(slidingWindow())")
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("slidingWindow er ikke støttet for en Stream som kun inneholder 1 element.");
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void skal_feile_ved_parallellitet() {
        assertThatCode(
                () -> range(0, 100000)
                        .boxed()
                        .parallel()
                        .collect(slidingWindow())
        )
                .as("feil ved bruk på parallell-stream")
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("Støttar ikkje parallellitet i slidingWindow");
    }

    private ListAssert<Tuple2<Integer, Integer>> assertSlidingWindow(final Stream<Integer> input) {
        final List<Integer> buffer = input.toList();
        return assertThat(
                buffer
                        .stream()
                        .collect(slidingWindow())
                        .collect(toList())
        )
                .as(
                        "(%s).collect(slidingWindow())",
                        buffer.stream().map(Object::toString).collect(joining(", "))
                );
    }
}
