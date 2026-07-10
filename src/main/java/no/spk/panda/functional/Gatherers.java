package no.spk.panda.functional;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Gatherer.Integrator.ofGreedy;
import static java.util.stream.Gatherer.ofSequential;
import static no.spk.panda.functional.Tuple.tuple;

import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.stream.Gatherer;
import java.util.stream.Stream;

/**
 * Fabrikker for {@link Gatherer}-baserte varianter av operasjonene i {@link Collectors} som ikke har en eksakt ekvivalent
 * i {@link java.util.stream.Gatherers}.
 * <br>
 * Gathererne brukes som mellomoperasjoner via {@link Stream#gather(Gatherer)}, i motsetning til collectorene som er terminaloperasjoner.
 * <br>
 * Alle gathererne er sekvensielle; ved bruk på en {@link Stream#parallel() parallell} stream blir elementene prosessert
 * sekvensielt og i rekkefølge, med samme resultat som for en sekvensiell stream.
 *
 * @see Collectors
 * @see java.util.stream.Gatherers
 */
public interface Gatherers {

    /**
     * Gir et glidende vindu med størrelse 2, representert som en {@link Tuple2}.
     * <br>
     * Merk at {@link java.util.stream.Gatherers#windowSliding(int)} ikke er ekvivalent: den produserer {@code List}-vinduer
     * i stedet for {@link Tuple2}, og produserer et delvis vindu for en stream med kun 1 element i stedet for å feile.
     * <br>
     * Eksempel 1:
     * <br>
     * Gitt en Stream med følgende innhold: (1, 2, 3, 4, 5)
     * Når slidingWindow blir brukt som gatherer
     * Så skal følgende tupler bli returnert: (1, 2), (2, 3), (3, 4), (4, 5)
     * <br>
     * Eksempel 2:
     * <br>
     * Gitt en tom Stream
     * Når slidingWindow blir brukt som gatherer
     * Så skal en tom Stream bli returnert
     * <br>
     * Eksempel 3:
     * <br>
     * Gitt en Stream med følgende innhold: (1)
     * Når slidingWindow blir brukt som gatherer
     * Så skal samlingen feile, det er umulig å lage et vindu med to elementer fra en stream med kun 1 element
     *
     * @param <T> typen av den man skal samle opp.
     * @return en gatherer som produserer "vinduer".
     * @throws UnsupportedOperationException dersom streamen inneholder kun 1 element, det er ikke mulig å produsere et vindu med to element i denne
     *                                       situasjonen. Merk at feilen først oppstår når streamen blir konsumert av en terminaloperasjon
     * @see Collectors#slidingWindow()
     */
    static <T> Gatherer<T, ?, Tuple2<T, T>> slidingWindow() {
        class Tilstand {
            private T forrige;
            private boolean harForrige;
            private long antall;
        }
        return ofSequential(
                Tilstand::new,
                ofGreedy((tilstand, element, downstream) -> {
                    tilstand.antall++;
                    if (tilstand.harForrige && !downstream.push(tuple(tilstand.forrige, element))) {
                        return false;
                    }
                    tilstand.forrige = element;
                    tilstand.harForrige = true;
                    return true;
                }),
                (tilstand, downstream) -> {
                    if (tilstand.antall == 1) {
                        throw new UnsupportedOperationException("slidingWindow er ikke støttet for en Stream som kun inneholder 1 element.");
                    }
                }
        );
    }

    /**
     * Kombinerer/akkumulerer to og to elementer fra streamen, produserer både siste resultat og alle delresultat
     * som blir produsert underveis.
     * <br>
     * Scan-left kan ses på som en fold-left, men som returnerer resultatene for alle operasjonene utført underveis.
     * Det vil si at resultatet fra alle kall til <code>mapper</code>-funksjonen blir produsert.
     * <br>
     * Med en initiell verdi som er tilstede er denne ekvivalent med {@link java.util.stream.Gatherers#scan(java.util.function.Supplier, java.util.function.BiFunction)},
     * men en tom initiell verdi (der det første elementet passerer uendret gjennom) kan ikke uttrykkes med scan.
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
     * @return en ny gatherer for scanleft
     * @see Collectors#scanLeft(Optional, BinaryOperator)
     * @see java.util.stream.Gatherers#scan(java.util.function.Supplier, java.util.function.BiFunction)
     */
    static <T> Gatherer<T, ?, T> scanLeft(final Optional<T> initiellVerdi, final BinaryOperator<T> mapper) {
        requireNonNull(initiellVerdi, "initiellVerdi er påkrevd, men var null");
        requireNonNull(mapper, "mapper er påkrevd, men var null");
        class Tilstand {
            private Optional<T> forrige = initiellVerdi;
        }
        return ofSequential(
                Tilstand::new,
                ofGreedy((tilstand, neste, downstream) -> {
                    final T resultat = tilstand.forrige
                            .map(forrige -> mapper.apply(forrige, neste))
                            .orElse(neste);
                    tilstand.forrige = Optional.of(resultat);
                    return downstream.push(resultat);
                })
        );
    }

    /**
     * Kombinerer/akkumulerer to og to elementer fra streamen, produserer både siste resultat og alle delresultat
     * som blir produsert underveis.
     * <br>
     * Scan-left-1 er en scan-left, som har en hardkodet initiell verdi som er Optional.empty().
     * <br>
     * Eksempel 1:
     * <br>
     * Gitt en Stream med følgende innhold: (1, 2, 3, 4, 5)
     * Når scanLeft1 blir brukt som gatherer
     * Så skal en ny Stream med følgende innhold bli produsert: (1, 3, 6, 10, 15)
     * <br>
     * Eksempel 2:
     * <br>
     * Gitt en Stream med følgende innhold: (1)
     * Når scanLeft1 blir brukt som gatherer
     * Så skal en ny Stream med følgende innhold bli produsert: (1)
     * <br>
     * Eksempel 3:
     * <br>
     * Gitt en tom Stream
     * Når scanLeft1 blir brukt som gatherer
     * Så skal en tom Stream bli produsert
     *
     * @param <T>    typen av det man skal kombinere
     * @param mapper handlingen som skal gjøres på den akumullerte tilstanden + det nye elementet
     * @return en ny gatherer for scanleft1
     * @see #scanLeft(Optional, BinaryOperator)
     * @see Collectors#scanLeft1(BinaryOperator)
     */
    static <T> Gatherer<T, ?, T> scanLeft1(final BinaryOperator<T> mapper) {
        return scanLeft(Optional.empty(), mapper);
    }
}
