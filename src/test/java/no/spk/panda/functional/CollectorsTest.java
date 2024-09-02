package no.spk.panda.functional;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static no.spk.panda.functional.Collectors.Case;
import static no.spk.panda.functional.Collectors.zipWithIndex;
import static no.spk.panda.functional.Tuple.tuple;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

class CollectorsTest {

    @Test
    void emptyShouldZipWithIndex() {
        Stream<Tuple2<Object, Integer>> result = zipWithIndex(Stream.empty());

        assertThat(result).isEmpty();
    }

    @Test
    void shouldZipWithIndex() {
        List<Integer> integers = asList(0, 1, 2, 3, 4, 5, 6);
        Stream<Tuple2<Integer, Integer>> result = zipWithIndex(integers.stream());

        List<Tuple2<Integer, Integer>> expected = integers
                .stream()
                .map(i -> tuple(i, i))
                .collect(toList());

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void shouldCollectCaseClass() {
        List<Integer> integers = asList(1, 2, 3, 4, 5, 6);
        Stream<String> stringStream = Stream.of("1", "2", "3", "4", "5", "6");
        Stream<Object> oddStream = Stream.concat(integers.stream(), stringStream);

        List<Integer> result = oddStream.collect(Case(Integer.class)).collect(toList());
        assertThat(result).isEqualTo(integers);
    }

    @Test
    void shouldZipAll() {
        Stream<Tuple2<Optional<String>, Optional<Integer>>> expected =
                Stream.of(tuple(Optional.of("A"), Optional.of(1)),
                        tuple(Optional.of("B"), Optional.of(2)),
                        tuple(Optional.of("C"), Optional.of(3)),
                        tuple(Optional.of("D"), Optional.of(4)));

        Stream<Tuple2<Optional<String>, Optional<Integer>>> result = Collectors.zipAll(Stream.of("A", "B", "C", "D"), Stream.of(1, 2, 3, 4));

        assertThat(result).isEqualTo(expected.collect(toList()));
    }

    @Test
    void leftEmptyShouldZipAll() {
        Stream<Tuple2<Optional<String>, Optional<Integer>>> expected =
                Stream.of(tuple(Optional.empty(), Optional.of(1)),
                        tuple(Optional.empty(), Optional.of(2)),
                        tuple(Optional.empty(), Optional.of(3)),
                        tuple(Optional.empty(), Optional.of(4)));

        Stream<Tuple2<Optional<String>, Optional<Integer>>> result = Collectors.zipAll(Stream.empty(), Stream.of(1, 2, 3, 4));

        assertThat(result).isEqualTo(expected.collect(toList()));
    }

    @Test
    void rightEmptyShouldZipAll() {
        Stream<Tuple2<Optional<String>, Optional<Integer>>> expected =
                Stream.of(tuple(Optional.of("A"), Optional.empty()),
                        tuple(Optional.of("B"), Optional.empty()),
                        tuple(Optional.of("C"), Optional.empty()),
                        tuple(Optional.of("D"), Optional.empty()));

        Stream<Tuple2<Optional<String>, Optional<Integer>>> result = Collectors.zipAll(Stream.of("A", "B", "C", "D"), Stream.empty());

        assertThat(result).isEqualTo(expected.collect(toList()));
    }

    @Test
    void emptyShouldZipAll() {
        Stream<Tuple2<Optional<String>, Optional<Integer>>> result = Collectors.zipAll(Stream.empty(), Stream.empty());

        assertThat(result).isEmpty();
    }

    @Test
    void heavyLeftStreamsShouldZipAll() {
        Stream<Tuple2<Optional<String>, Optional<Integer>>> expected =
                Stream.of(tuple(Optional.of("A"), Optional.of(1)),
                        tuple(Optional.of("B"), Optional.of(2)),
                        tuple(Optional.of("C"), Optional.empty()),
                        tuple(Optional.of("D"), Optional.empty()));

        Stream<Tuple2<Optional<String>, Optional<Integer>>> result = Collectors.zipAll(Stream.of("A", "B", "C", "D"), Stream.of(1, 2));

        assertThat(result).isEqualTo(expected.collect(toList()));
    }

    @Test
    void heavyRightStreamsShouldZipAll() {
        Stream<Tuple2<Optional<String>, Optional<Integer>>> expected =
                Stream.of(tuple(Optional.of("A"), Optional.of(1)),
                        tuple(Optional.of("B"), Optional.of(2)),
                        tuple(Optional.of("C"), Optional.of(3)),
                        tuple(Optional.empty(), Optional.of(4)));

        Stream<Tuple2<Optional<String>, Optional<Integer>>> result = Collectors.zipAll(Stream.of("A", "B", "C"), Stream.of(1, 2, 3, 4));

        assertThat(result).isEqualTo(expected.collect(toList()));
    }
}
