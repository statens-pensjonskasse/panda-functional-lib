package no.spk.felles.functional;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.Test;

class OptionalApplyTest {

    @Test
    void testOptionalApply2() {
        final Optional<Integer> en = Optional.of(1);
        final Optional<Integer> to = Optional.of(2);

        final Optional<Integer> sumAvEnOgTo = OptionalApply.optionalApply(
                en, to,
                Integer::sum
        );

        final Optional<Integer> forventet = Optional.of(3);
        assertEquals(sumAvEnOgTo, forventet);
    }

    @Test
    void testOptionalApply3() {
        final Optional<Integer> en = Optional.of(1);
        final Optional<Integer> to = Optional.of(2);
        final Optional<Integer> tre = Optional.of(3);

        final Optional<Integer> sum = OptionalApply.optionalApply(
                en, to, tre,
                (iEn, iTo, iTre) -> iEn + iTo + iTre
        );

        final Optional<Integer> forventet = Optional.of(6);
        assertEquals(sum, forventet);
    }

    @Test
    void testOptionalApply4() {
        final Optional<Integer> en = Optional.of(1);
        final Optional<Integer> to = Optional.of(2);
        final Optional<Integer> tre = Optional.of(3);
        final Optional<Integer> fire = Optional.of(4);

        final Optional<Integer> sum = OptionalApply.optionalApply(
                en, to, tre, fire,
                (iEn, iTo, iTre, iFire) -> iEn + iTo + iTre + iFire
        );

        final Optional<Integer> forventet = Optional.of(10);
        assertEquals(sum, forventet);
    }

    @Test
    void testOptionalApply5() {
        final Optional<Integer> en = Optional.of(1);
        final Optional<Integer> to = Optional.of(2);
        final Optional<Integer> tre = Optional.of(3);
        final Optional<Integer> fire = Optional.of(4);
        final Optional<Integer> fem = Optional.of(5);

        final Optional<Integer> sum = OptionalApply.optionalApply(
                en, to, tre, fire, fem,
                (iEn, iTo, iTre, iFire, iFem) -> iEn + iTo + iTre + iFire + iFem
        );

        final Optional<Integer> forventet = Optional.of(15);
        assertEquals(sum, forventet);
    }
}
