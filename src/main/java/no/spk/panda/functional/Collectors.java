package no.spk.panda.functional;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static no.spk.panda.functional.Tuple.tuple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public interface Collectors {

    /**
     * Tar inn en stream og lager en ny stream av {@link Tuple2} med de originale elementene + deres index
     *
     * <pre>
     * Eksempel:
     * {@code Collectors.zipWithIndex(Stream.of("Mandag", "Tirsdag", "Whatever"))}
     * gir ut samme stream som:
     * {@code Stream.of(Tuple.tuple("Mandag", 0),Tuple.tuple("Tirsdag", 1), Tuple.tuple("Whatever", 2))}
     * </pre>
     *
     * @param <T>    Type {@link Tuple2}
     * @param stream innkommende stream av {@link Tuple2}
     * @return stream av {@link Tuple2}
     */
    static <T> Stream<Tuple2<T, Integer>> zipWithIndex(final Stream<T> stream) {
        final AtomicInteger index = new AtomicInteger(0);
        return stream.map(t -> tuple(t, index.getAndIncrement()));
    }

    /**
     * Tar inn to streamer og fletter disse inn i en stream av tuppler av Optional.
     * Hvis stream t har flere elementer enn stream u blirs u's side i tuppelen fylt opp med Optional.empty()
     *
     * <pre>
     * Eksempel1:
     * {@code Collectors.zipAll(Stream.of("Mandag", "Fredag", "Lørdag"), Stream.of("Tungt", "Snart", "Party!")}
     * gir ut samme stream som:
     * {@code Stream.of(Tuple.tuple(Optional.of("Mandag"), Optional.of("Tungt"),Tuple.tuple(Optional.of("Fredag"),Optional.of("Snart")), Tuple.tuple(Optional.of("Lørdag"), Optional.of("Party")))}
     * <br>
     * Eksemplel2:
     * {@code Collectors.zipAll(Stream.of("Mandag", "Fredag"), Stream.of(1, 5, 6)}
     * gir ut samme stream som:
     * {@code Stream.of(Tuple.tuple(Optional.of("Mandag"), Optional.of(1),Tuple.tuple(Optional.of("Fredag"),Optional.of(5)), Tuple.tuple(Optional.empty(), Optional.of(6)))}
     * </pre>
     *
     * @param streamT stream av type T
     * @param streamU stream av type U
     * @param <T>     typen T
     * @param <U>     typen U
     * @return stream av Tuple2 der 1. element er Optional av type T og 2. element er Optional av type U
     */
    static <T, U> Stream<Tuple2<Optional<T>, Optional<U>>> zipAll(final Stream<T> streamT, final Stream<U> streamU) {
        List<T> tList = streamT.toList();
        List<U> uList = streamU.toList();

        return IntStream.range(0, Math.max(tList.size(), uList.size()))
                .mapToObj(
                        i -> tuple(i < tList.size() ? Optional.of(tList.get(i)) : Optional.empty(),
                                i < uList.size() ? Optional.of(uList.get(i)) : Optional.empty())
                );
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    static <T, R> Collector<T, ?, Stream<R>> match(final Function<T, Optional<? extends R>> partialFunction, final Function<T, Optional<? extends R>>... partialFunctions) {
        final List<Function<T, Optional<? extends R>>> pfs = Stream.concat(
                        Stream.of(partialFunction),
                        Stream.of(partialFunctions)
                )
                .toList();
        return Collector.of(
                ArrayList<R>::new,
                (list, t) ->
                        pfs.stream()
                                .map(c -> c.apply(t))
                                .filter(Optional::isPresent)
                                .findFirst()
                                .flatMap(Function.identity())
                                .ifPresent(list::add)
                ,
                (list1, list2) -> {
                    final ArrayList<R> concat = new ArrayList<>();
                    concat.addAll(list1);
                    concat.addAll(list2);
                    return concat;
                },
                Collection::stream
        );
    }

    static <T, R> Collector<T, ?, Stream<R>> Case(final Class<R> cls) {
        return match(t -> Optional.of(t).filter(cls::isInstance).map(cls::cast));
    }

    /**
     * Gir et glidende vindu med størrelse 2, representert som en {@link Tuple2}.
     * <br>
     * Eksempel 1:
     * <br>
     * Gitt en Stream med følgende innhold: (1, 2, 3, 4, 5)
     * Når slidingWindow blir brukt som collector
     * Så skal følgende tupler bli returnert: (1, 2), (2, 3), (3, 4), (4, 5)
     * <br>
     * Eksempel 2:
     * <br>
     * Gitt en tom Stream
     * Når slidingWindow blir brukt som collector
     * Så skal en tom Stream bli returnert
     * <br>
     * Eksempel 3:
     * <br>
     * Gitt en Stream med følgende innhold: (1)
     * Når slidingWindow blir brukt som collector
     * Så skal samlingen feile, det er umulig å lage et vindu med to elementer fra en stream med kun 1 element
     *
     * @param <T> typen av den man skal samle opp.
     * @return en stream av "vinduer".
     * @throws UnsupportedOperationException dersom streamen inneholder kun 1 element, det er ikke mulig å produsere et vindu med to element i denne
     *                                       situasjonen, eller viss collectoren blir brukt på en parallell stream
     * @see Gatherers#slidingWindow()
     */
    static <T> Collector<T, List<Tuple2<T, Optional<T>>>, Stream<Tuple2<T, T>>> slidingWindow() {
        return new SlidingWindowCollector<>();
    }

    /**
     * Tar en starttilstand og handling og gjør så handlingen på alle elementene i streamen og gir tilbake et samlet resultat
     *
     * <pre>
     * Eksempel:
     * {@code Stream.of("Man", "Tirs", "God").collect(Collectors.foldLeft("", (akkumulertString, miniString) -> akkumulertString + miniString + "dag ")}
     * vil gi resultatet ut som er lik
     * {@code "Mandag Tirsdag Goddag "}
     * </pre>
     * <br>
     * foldLeft støtter ikke {@link Stream#parallel() parallellitet}.
     *
     * @param initial starttilstand for handlingen
     * @param reduce  handlingen som skal gjøres på den akkumulerte tilstanden + det nye elementet
     * @param <T>     Type som holder det som blir "foldet" inn i.
     * @param <R>     den akkumulerte tilstanden
     * @return den akkumulerte tilstanden
     * @throws UnsupportedOperationException dersom collectoren blir brukt på en parallell stream
     * @see java.util.stream.Gatherers#fold(java.util.function.Supplier, BiFunction)
     */
    static <T, R> Collector<T, ?, R> foldLeft(final R initial, final BiFunction<R, T, R> reduce) {
        return new FoldLeftCollector<>(initial, reduce);
    }

    /**
     * /**
     * Kombinerer/akkumulerer to og to elementer fra streamen, returnerer en Stream som inneholder både siste resultat og alle delresultat
     * som blir produsert underveis.
     * <br>
     * Scan-left kan ses på som en fold-left, men som returnerer resultatene for alle operasjonene utført underveis.
     * Det vil si at resultatet fra alle kall til <code>mapper</code>-funksjonen blir returnert.
     * <br>
     * ScanLeft støtter ikke {@link Stream#parallel() parallellitet}.
     * <br>
     * Eksempel 1:
     * <br>
     * Gitt en Stream med følgende innhold: (1, 2, 3, 4, 5)
     * Når scanLeft har en initiell verdi 10 og mapperen <code> (forrige, neste) -> forrige + neste</code>
     * Så skal en ny Stream med følgende innhold bli produsert: (11, 13, 16, 20, 25)
     * <br>
     * Eksempel 2:
     * <br>
     * Gitt en Stream med følgende innhold: (1)
     * Når scanLeft har en initiell verdi 10 og mapperen <code> (forrige, neste) -> forrige + neste</code>
     * Så skal en ny Stream med følgende innhold bli produsert: (11)
     * <br>
     * Eksempel 3:
     * <br>
     * Gitt en tom Stream
     * Når scanLeft har en initiell verdi 10 og mapperen <code> (forrige, neste) -> forrige + neste</code>
     * Så skal en tom Stream bli produsert
     *
     * @param <T>           typen av det man skal kombinere
     * @param initiellVerdi initiell verdi som kan kombineres med det første elementet
     * @param mapper        handlingen som skal gjøres på den akumullerte tilstanden + det nye elementet
     * @return en ny collector for scanleft
     * @throws UnsupportedOperationException dersom collectoren blir brukt på en parallell stream
     * @see #foldLeft(Object, BiFunction)
     * @see Gatherers#scanLeft(Optional, BinaryOperator)
     * @see java.util.stream.Gatherers#scan(java.util.function.Supplier, BiFunction)
     */
    static <T> Collector<T, ?, Stream<T>> scanLeft(final Optional<T> initiellVerdi, final BinaryOperator<T> mapper) {
        return new ScanLeftCollector<>(initiellVerdi, mapper);
    }

    /**
     * /**
     * Kombinerer/akkumulerer to og to elementer fra streamen, returnerer en Stream som inneholder både siste resultat og alle delresultat
     * som blir produsert underveis.
     * <br>
     * Scan-left-1 er en scan-left, som har en hardkodet initiell verdi som er Optional.empty().
     * <br>
     * ScanLeft1 støtter ikke {@link Stream#parallel() parallellitet}.
     * <br>
     * Eksempel 1:
     * <br>
     * Gitt en Stream med følgende innhold: (1, 2, 3, 4, 5)
     * Når scanLeft1 blir brukt som collector
     * Så skal en ny Stream med følgende innhold bli produsert: (1, 3, 6, 10, 15)
     * <br>
     * Eksempel 2:
     * <br>
     * Gitt en Stream med følgende innhold: (1)
     * Når scanLeft1 blir brukt som collector
     * Så skal en ny Stream med følgende innhold bli produsert: (1)
     * <br>
     * Eksempel 3:
     * <br>
     * Gitt en tom Stream
     * Når scanLeft1 blir brukt som collector
     * Så skal en tom Stream bli produsert
     *
     * @param <T>    typen av det man skal kombinere
     * @param mapper handlingen som skal gjøres på den akumullerte tilstanden + det nye elementet
     * @return en ny collector for scanleft1
     * @throws UnsupportedOperationException dersom collectoren blir brukt på en parallell stream
     * @see #scanLeft(Optional, BinaryOperator)
     * @see Gatherers#scanLeft1(BinaryOperator)
     */
    static <T> Collector<T, ?, Stream<T>> scanLeft1(final BinaryOperator<T> mapper) {
        return new ScanLeftCollector<>(Optional.empty(), mapper);
    }

    static <Key, Value> Collector<Tuple2<Key, Value>, ?, Map<Key, Value>> toMap() {
        return java.util.stream.Collectors.toMap(t -> t._1, t -> t._2);
    }

    /**
     * Pakker inn en collector som produserer et {@link Map}, og gjør om resultatet til en Stream ved å kalle
     * <code>mapper</code> på hvert (nøkkel, verdi)-par i mappet.
     * <br>
     * Dette lar en bruke {@link java.util.stream.Collectors#groupingBy(Function) groupingBy},
     * {@link java.util.stream.Collectors#toMap(Function, Function) toMap},
     * {@link java.util.stream.Collectors#partitioningBy(java.util.function.Predicate) partitioningBy} og lignende
     * midt i en stream-pipeline uten å måtte gå veien om <code>.entrySet().stream()</code>.
     *
     * <pre>
     * Eksempel:
     * {@code Stream.of("Mandag", "Tirsdag", "Torsdag").collect(mapToStream(groupingBy(s -> s.charAt(0)), (bokstav, dager) -> bokstav + "=" + dager.size()))}
     * gir ut samme elementer som:
     * {@code Stream.of("M=1", "T=2")}
     * </pre>
     * <br>
     * Rekkefølgen på elementene i den resulterende streamen er kun definert dersom <code>downstream</code> produserer et
     * map med definert iterasjonsrekkefølge, for eksempel {@link java.util.TreeMap} eller {@link java.util.LinkedHashMap}.
     *
     * @param downstream collectoren som produserer mappet
     * @param mapper     funksjonen som lager et element i resultat-streamen fra hvert (nøkkel, verdi)-par
     * @param <T>        typen av elementene i streamen som blir samlet opp
     * @param <K>        typen av nøklene i mappet
     * @param <V>        typen av verdiene i mappet
     * @param <R>        typen av elementene i den resulterende streamen
     * @return en collector som gir en stream av resultatene fra <code>mapper</code>
     * @see #mapToStream(Collector)
     * @see java.util.stream.Collectors#collectingAndThen(Collector, Function)
     */
    static <T, K, V, R> Collector<T, ?, Stream<R>> mapToStream(
            final Collector<T, ?, ? extends Map<K, V>> downstream,
            final BiFunction<? super K, ? super V, ? extends R> mapper
    ) {
        return collectingAndThen(
                downstream,
                map -> map.entrySet().stream().map(e -> mapper.apply(e.getKey(), e.getValue()))
        );
    }

    /**
     * Som {@link #mapToStream(Collector, BiFunction)}, men gir hvert (nøkkel, verdi)-par ut som en {@link Tuple2}.
     *
     * <pre>
     * Eksempel:
     * {@code Stream.of("Mandag", "Tirsdag", "Torsdag").collect(mapToStream(groupingBy(s -> s.charAt(0))))}
     * gir ut samme elementer som:
     * {@code Stream.of(tuple('M', List.of("Mandag")), tuple('T', List.of("Tirsdag", "Torsdag")))}
     * </pre>
     *
     * @param downstream collectoren som produserer mappet
     * @param <T>        typen av elementene i streamen som blir samlet opp
     * @param <K>        typen av nøklene i mappet
     * @param <V>        typen av verdiene i mappet
     * @return en collector som gir en stream av {@link Tuple2} med nøkkel som første og verdi som andre element
     * @see #mapToStream(Collector, BiFunction)
     */
    static <T, K, V> Collector<T, ?, Stream<Tuple2<K, V>>> mapToStream(
            final Collector<T, ?, ? extends Map<K, V>> downstream
    ) {
        return mapToStream(downstream, Tuple::tuple);
    }

    /**
     * Grupperer elementene etter <code>classifier</code> og gir hver (nøkkel, gruppe) til <code>mapper</code>,
     * der gruppen er en {@link List} av elementene som hører til nøkkelen.
     * <br>
     * Tilsvarer {@code mapToStream(groupingBy(classifier), mapper)}.
     *
     * <pre>
     * Eksempel:
     * {@code Stream.of("Mandag", "Tirsdag", "Torsdag").collect(groupingToStream(s -> s.charAt(0), (bokstav, dager) -> bokstav + "=" + dager.size()))}
     * gir ut samme elementer som:
     * {@code Stream.of("M=1", "T=2")}
     * </pre>
     * <br>
     * Rekkefølgen på gruppene i den resulterende streamen er ikke definert.
     *
     * @param classifier funksjonen som bestemmer hvilken gruppe et element hører til
     * @param mapper     funksjonen som lager et element i resultat-streamen fra hver (nøkkel, gruppe)
     * @param <T>        typen av elementene i streamen som blir samlet opp
     * @param <K>        typen av gruppenøklene
     * @param <R>        typen av elementene i den resulterende streamen
     * @return en collector som gir en stream av resultatene fra <code>mapper</code>
     * @see #groupingToStream(Function)
     * @see #mapToStream(Collector, BiFunction)
     */
    static <T, K, R> Collector<T, ?, Stream<R>> groupingToStream(
            final Function<? super T, ? extends K> classifier,
            final BiFunction<? super K, ? super List<T>, ? extends R> mapper
    ) {
        return mapToStream(groupingBy(classifier), mapper);
    }

    /**
     * Som {@link #groupingToStream(Function, BiFunction)}, men gir hver (nøkkel, gruppe) ut som en {@link Tuple2}
     * der andre element er gruppen som en {@link List}.
     *
     * <pre>
     * Eksempel:
     * {@code Stream.of("Mandag", "Tirsdag", "Torsdag").collect(groupingToStream(s -> s.charAt(0)))}
     * gir ut samme elementer som:
     * {@code Stream.of(tuple('M', List.of("Mandag")), tuple('T', List.of("Tirsdag", "Torsdag")))}
     * </pre>
     *
     * @param classifier funksjonen som bestemmer hvilken gruppe et element hører til
     * @param <T>        typen av elementene i streamen som blir samlet opp
     * @param <K>        typen av gruppenøklene
     * @return en collector som gir en stream av {@link Tuple2} med nøkkel som første og gruppen som andre element
     * @see #groupingToStream(Function, BiFunction)
     */
    static <T, K> Collector<T, ?, Stream<Tuple2<K, List<T>>>> groupingToStream(
            final Function<? super T, ? extends K> classifier
    ) {
        return groupingToStream(classifier, Tuple::tuple);
    }
}
