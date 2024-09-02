package no.spk.panda.functional;

import static no.spk.panda.functional.TryApply.asStream;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

class TryApplyTest {

    @Test
    void testAsStreamEmpty() {
        final Try<Integer> opt = Try.failure(new IllegalStateException("Error message"));
        Stream<Integer> stream = asStream(opt);//ÆSJ!

        assertThat(0L).isEqualTo(stream.count());
    }

    @Test
    void testAsStreamFull() {
        final Try<Integer> opt = Try.success(1);
        final Stream<Integer> stream = asStream(opt);//ÆSJ!
        final List<Integer> liste = stream.toList();

        assertThat(1).isEqualTo(liste.size());
        assertThat(1).isEqualTo(liste.getFirst());
    }

    @Test
    void testTryApply2() {
        final Try<Integer> en = Try.success(1);
        final Try<Integer> to = Try.success(2);

        final Try<Integer> sumAvEnOgTo = TryApply.tryApply(
                en, to,
                Integer::sum
        );

        final Try<Integer> forventet = Try.success(3);
        assertThat(sumAvEnOgTo).isEqualTo(forventet);
    }

    @Test
    void testTryApply3() {
        final Try<Integer> en = Try.success(1);
        final Try<Integer> to = Try.success(2);
        final Try<Integer> tre = Try.success(3);

        final Try<Integer> sum = TryApply.tryApply(
                en, to, tre,
                (iEn, iTo, iTre) -> iEn + iTo + iTre
        );

        final Try<Integer> forventet = Try.success(6);
        assertThat(sum).isEqualTo(forventet);
    }

    @Test
    void testTryApply4() {
        final Try<Integer> en = Try.success(1);
        final Try<Integer> to = Try.success(2);
        final Try<Integer> tre = Try.success(3);
        final Try<Integer> fire = Try.success(4);

        final Try<Integer> sum = TryApply.tryApply(
                en, to, tre, fire,
                (iEn, iTo, iTre, iFire) -> iEn + iTo + iTre + iFire
        );

        final Try<Integer> forventet = Try.success(10);
        assertThat(sum).isEqualTo(forventet);
    }

    @Test
    void testTryApply5() {
        final Try<Integer> en = Try.success(1);
        final Try<Integer> to = Try.success(2);
        final Try<Integer> tre = Try.success(3);
        final Try<Integer> fire = Try.success(4);
        final Try<Integer> fem = Try.success(5);

        final Try<Integer> sum = TryApply.tryApply(
                en, to, tre, fire, fem,
                (iEn, iTo, iTre, iFire, iFem) -> iEn + iTo + iTre + iFire + iFem
        );

        final Try<Integer> forventet = Try.success(15);
        assertThat(sum).isEqualTo(forventet);
    }

    @Test
    void testTryMedForskjelligeTyper() {
        final Try<Integer> etTall = Try.success(1);
        final Try<String> enTekst = Try.success("2");

        final Try<String> samlet = TryApply.tryApply(
                etTall, enTekst,
                (tall, tekst) -> tall.toString() + tekst
        );

        final Try<String> forventet = Try.success("12");

        assertThat(samlet).isEqualTo(forventet);
    }

    @Test
    void testTryFailure() {
        final Try<Integer> en = Try.failure(new IllegalStateException("Error message"));
        final Try<Integer> to = Try.success(2);
        final Try<Integer> tre = Try.success(3);
        final Try<Integer> fire = Try.success(4);
        final Try<Integer> fem = Try.success(5);

        final Try<Integer> sum = TryApply.tryApply(
                en, to, tre, fire, fem,
                (iEn, iTo, iTre, iFire, iFem) -> iEn + iTo + iTre + iFire + iFem
        );

        assertThat(sum).isEqualTo(en);
    }
}
