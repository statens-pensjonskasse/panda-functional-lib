package no.spk.felles.functional;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

class StreamsTest {
    @Test
    void skal_flate_ut_straumane_og_levere_deira_innhold_i_samme_rekkefølge_som_før() {
        final Stream<?> a = Stream.of("Z", 1, 0L);
        final Stream<?> b = Stream.of("W", "Y", 1);
        final Stream<?> c = Stream.of(42L, "A", true);

        assertThat(
                Streams.concat(a, b, c)
        )
                .containsExactly(
                        "Z", 1, 0L, "W", "Y", 1, 42L, "A", true
                );
    }

    @Test
    void skal_flate_ut_ingenting_til_ingenting() {
        assertThat(
                Streams.concat(
                        Stream.empty(),
                        Stream.empty(),
                        Stream.empty()
                )
        )
                .isEmpty();
    }
}