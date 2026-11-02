package no.spk.panda.functional;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.partitioningBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static no.spk.panda.functional.Collectors.Case;
import static no.spk.panda.functional.Collectors.groupingToStream;
import static no.spk.panda.functional.Collectors.mapToStream;
import static no.spk.panda.functional.Collectors.zipWithIndex;
import static no.spk.panda.functional.Tuple.tuple;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
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

    @Test
    void mapToStreamWithMapperShouldMapEachEntryOfGroupingBy() {
        Stream<String> result = Stream.of("Mandag", "Tirsdag", "Torsdag", "Onsdag")
                .collect(mapToStream(
                        groupingBy(s -> s.charAt(0)),
                        (bokstav, dager) -> bokstav + "=" + dager.size()
                ));

        assertThat(result).containsExactlyInAnyOrder("M=1", "T=2", "O=1");
    }

    @Test
    void mapToStreamWithoutMapperShouldGiveTuplesOfKeyAndValue() {
        Stream<Tuple2<Character, List<String>>> result = Stream.of("Mandag", "Tirsdag", "Torsdag")
                .collect(mapToStream(groupingBy(s -> s.charAt(0))));

        assertThat(result).containsExactlyInAnyOrder(
                tuple('M', List.of("Mandag")),
                tuple('T', List.of("Tirsdag", "Torsdag"))
        );
    }

    @Test
    void mapToStreamShouldWorkWithToMap() {
        Stream<Tuple2<String, Integer>> result = Stream.of("a", "bb", "ccc")
                .collect(mapToStream(toMap(s -> s, String::length)));

        assertThat(result).containsExactlyInAnyOrder(tuple("a", 1), tuple("bb", 2), tuple("ccc", 3));
    }

    @Test
    void mapToStreamShouldWorkWithPartitioningBy() {
        Stream<String> result = Stream.of(1, 2, 3, 4, 5)
                .collect(mapToStream(
                        partitioningBy(i -> i % 2 == 0),
                        (partall, tall) -> (partall ? "partall" : "oddetall") + tall
                ));

        assertThat(result).containsExactlyInAnyOrder("partall[2, 4]", "oddetall[1, 3, 5]");
    }

    @Test
    void mapToStreamShouldWorkWithDownstreamCollector() {
        Stream<Tuple2<Integer, Long>> result = Stream.of("a", "bb", "cc", "ddd")
                .collect(mapToStream(groupingBy(String::length, counting())));

        assertThat(result).containsExactlyInAnyOrder(tuple(1, 1L), tuple(2, 2L), tuple(3, 1L));
    }

    @Test
    void mapToStreamShouldPreserveOrderOfOrderedMap() {
        Stream<Character> result = Stream.of("Tirsdag", "Mandag", "Onsdag")
                .collect(mapToStream(
                        groupingBy(s -> s.charAt(0), TreeMap::new, toList()),
                        (bokstav, dager) -> bokstav
                ));

        assertThat(result).containsExactly('M', 'O', 'T');
    }

    @Test
    void mapToStreamOnEmptyStreamShouldGiveEmptyStream() {
        Stream<Tuple2<Object, List<Object>>> result = Stream.empty()
                .collect(mapToStream(groupingBy(o -> o)));

        assertThat(result).isEmpty();
    }

    @Test
    void groupingToStreamWithMapperShouldGiveEachGroupAsList() {
        Stream<String> result = Stream.of("Mandag", "Tirsdag", "Torsdag")
                .collect(groupingToStream(
                        s -> s.charAt(0),
                        (bokstav, dager) -> bokstav + "=" + dager.size()
                ));

        assertThat(result).containsExactlyInAnyOrder("M=1", "T=2");
    }

    @Test
    void groupingToStreamWithoutMapperShouldGiveTuplesOfKeyAndGroupList() {
        Stream<Tuple2<Character, List<String>>> result = Stream.of("Mandag", "Tirsdag", "Torsdag")
                .collect(groupingToStream(s -> s.charAt(0)));

        assertThat(result).containsExactlyInAnyOrder(
                tuple('M', List.of("Mandag")),
                tuple('T', List.of("Tirsdag", "Torsdag"))
        );
    }

    @Test
    void groupingToStreamOnEmptyStreamShouldGiveEmptyStream() {
        Stream<Tuple2<Object, List<Object>>> result = Stream.empty()
                .collect(groupingToStream(o -> o));

        assertThat(result).isEmpty();
    }
}
