package no.spk.felles.functional;

import static no.spk.felles.functional.Try.asTry;
import static no.spk.felles.functional.Try.invert;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.BooleanAssert;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SoftAssertionsExtension.class)
class TryTest {

    @InjectSoftAssertions
    private SoftAssertions softly;

    private static final Integer HAPPY = 1;
    private static final Integer OH_SHIT = 100;

    @Test
    void successShouldNotContainNull() {
        try {
            Try.success(null);
            fail("Skal ikke håndtere null");
        } catch (NullPointerException npe) {
            //success
        }
    }

    @Test
    void successShouldMap() {
        final String lowerCase = "lower case";

        final Try<String> result = Try.success(lowerCase).map(String::toUpperCase);
        assertThat(result).isEqualTo(Try.success(lowerCase.toUpperCase()));

    }

    @Test
    void successShouldMapWhenFailing() {
        final String lowerCase = "lower case";

        final Try<String> result = Try.success(lowerCase).map(i -> {
            throw new NullPointerException("null er tull");
        });
        assertThat(result.getCause()).isInstanceOf(NullPointerException.class);
    }

    @Test
    void successShouldFlatMapWhenFailing() {
        final String lowerCase = "lower case";

        final Try<String> result = Try.success(lowerCase).flatMap(i -> {
            throw new NullPointerException("null er tull");
        });
        assertThat(result.getCause()).isInstanceOf(NullPointerException.class);
    }

    @Test
    void komme_tilbake_fra_en_feilsituasjon() {
        final List<Integer> list = Arrays.asList(0, 1);
        final AtomicBoolean kjørt = new AtomicBoolean(false);
        final Try<Integer> resultat = Try.doTry(() -> list.get(2)) // Feiler
                .recover(throwable -> {
                    kjørt.set(true);
                    return 3;
                });

        assertThat(resultat.get()).isEqualTo(3);

        assertThat(kjørt).isTrue();
    }

    @Test
    void håndere_mulig_feilsituasjon_som_ikke_inntreffer() {
        final List<Integer> list = Arrays.asList(0, 1);
        final AtomicBoolean kjørtFeilhåndtering = new AtomicBoolean(false);
        final Try<Integer> resultat = Try.doTry(() -> list.get(1)) // Feiler ikke
                .recover(throwable -> {                            // og denne blir ikke kjørt
                    kjørtFeilhåndtering.set(true);
                    return 3;
                });

        assertThat(resultat.get()).isEqualTo(1);
        assertThat(kjørtFeilhåndtering).isFalse();
    }

    @Test
    void successShouldFlatMapSuccess() {
        final String lowerCase = "lower case";

        final Try<String> result = Try.success(lowerCase).flatMap(s -> Try.success(s.toUpperCase()));
        assertThat(result).isEqualTo(Try.success(lowerCase.toUpperCase()));
    }

    @Test
    void successShouldFlatMapFailure() {
        final Throwable throwable = new IllegalStateException("Error message");

        final Try<String> result = Try.success(HAPPY).flatMap(s -> Try.failure(throwable));
        assertThat(result).isEqualTo(Try.failure(throwable));
    }

    @Test
    void successShouldBeASuccess() {
        assertThat(Try.success(HAPPY).isSuccess()).isTrue();
    }

    @Test
    void successShouldGetANonEmptyOptional() {
        Try<Integer> trying = Try.success(HAPPY);
        assertThat(Optional.of(HAPPY)).isEqualTo(trying.toOptional());
    }

    @Test
    void successShouldOrElseGetTheContainedValue() {
        final Try<Integer> trying = Try.success(HAPPY);
        assertThat(HAPPY).isEqualTo(trying.orElseGet(() -> 2));
    }

    @Test
    void successShouldOrElseReturnContainedValue() {
        final Try<Integer> trying = Try.success(HAPPY);
        assertThat(HAPPY).isEqualTo(trying.orElse(OH_SHIT));
    }

    @Test
    void successShouldOnFoldReturnRight() {
        Try<Integer> trying = Try.success(HAPPY);
        assertThat(HAPPY).isEqualTo(trying.<Integer>fold(
                i -> {
                    throw new IllegalStateException("Should never run this branch when in success state");
                },
                i -> i
        ));
    }

    @Test
    void success_should_get_value_successfully() {
        assertThat(Try.success(HAPPY).get()).isSameAs(HAPPY);
    }

    @Test
    void success_should_fail_on_getCause() {
        final Try<Integer> trying = Try.success(HAPPY);
        assertThatThrownBy(trying::getCause)
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void success_should_run_ifSuccess() {
        assertIfSuccessInvoked(Try.success(HAPPY)).isTrue();
    }

    @Test
    void success_should_not_run_ifFailure() {
        assertIfFailureInvoked(Try.success(HAPPY)).isFalse();
    }

    @Test
    void failureShouldNotContainNullErrorMessage() {
        try {
            Try.failure(null);
            fail("Skal ikke håndtere null");
        } catch (NullPointerException npe) {
            //success
        }
    }

    @Test
    void failureShouldNotMap() {
        final Throwable throwable = new IllegalStateException("Failure");

        final Try<Integer> result = Try.<Integer>failure(throwable).map(i -> i + 1);
        assertThat(result).isEqualTo(Try.failure(throwable));
    }

    @Test
    void failureShouldFlatMapSuccess() {
        final Throwable throwable = new IllegalStateException("Error message");

        final Try<Integer> result = Try.<Integer>failure(throwable).flatMap(i -> Try.success(i + 1));
        assertThat(result).isEqualTo(Try.failure(throwable));
    }

    @Test
    void failureShouldFlatMapFailure() {
        final Throwable throwable = new IllegalStateException("Error message");

        final Try<Integer> result = Try.<Integer>failure(throwable).flatMap(s -> Try.failure(new IllegalAccessException()));
        assertThat(result).isEqualTo(Try.failure(throwable));
    }

    @Test
    void failureShouldNotBeASuccess() {
        assertFalse(Try.failure(new IllegalStateException("Error message")).isSuccess());
    }

    @Test
    void failureShouldGetAnEmptyOptional() {
        final Try<Integer> trying = Try.failure(new IllegalStateException("Error message"));
        assertThat(Optional.empty()).isEqualTo(trying.toOptional());
    }

    @Test
    void failureShouldOrElseGetTheSuppliedValue() {
        final Try<Integer> trying = Try.failure(new IllegalStateException("Error message"));
        assertThat(HAPPY).isEqualTo(trying.orElseGet(() -> HAPPY));
    }

    @Test
    void failureShouldOrElseReturnSuppliedValue() {
        final Try<Integer> trying = Try.failure(new IllegalStateException("Error message"));
        assertThat(HAPPY).isEqualTo(trying.orElse(HAPPY));
    }

    @Test
    void failureShouldOnFoldReturnRight() {
        final Try<Integer> trying = Try.failure(new IllegalStateException("Error message"));
        assertThat(HAPPY).isEqualTo(trying.<Integer>fold(
                failureString -> /* Ignoring failures string and returning */ HAPPY,
                i -> {
                    throw new IllegalStateException("Should never run this branch when in failure state");
                }
        ));
    }

    @Test
    void skal_produsere_failure_ved_feil_i_do_try_with_catch() {
        final Try<Integer> trying = Try.doTryWithCatch(() -> {
            throw new Exception("Error message");
        });
        assertThat(trying.isSuccess()).isFalse();
    }

    @Test
    void failureShouldOnThrowingInDoTry() {
        final Try<Integer> trying = Try.doTry(() -> {
            throw new IllegalStateException("Error message");
        });
        assertThat(trying.isSuccess()).isFalse();
    }

    @Test
    void failureShouldBeHappyWhenNotFailingWhenDoTry() {
        final Try<Integer> trying = Try.doTry(() -> HAPPY);
        assertThat(trying.isSuccess()).isTrue();
    }

    @Test
    void failureShouldOnEmptyInAsTry() {
        final Try<Integer> trying = asTry(Optional.empty(), () -> new IllegalStateException("Error message"));
        assertThat(trying.isSuccess()).isFalse();
    }

    @Test
    void failureShouldBeHappyWhenNotFailingWhenTryning() {
        final Try<Integer> trying = asTry(Optional.of(HAPPY), () -> new IllegalStateException("Error message"));
        assertThat(trying.isSuccess()).isTrue();
    }

    @Test
    void failureShouldOnEmptyInAsTrySupplier() {
        final Try<Integer> trying = asTry(Optional.empty(), () -> new IllegalStateException("Error message"));
        assertThat(trying.isSuccess()).isFalse();
    }

    @Test
    void failureShouldBeHappyWhenNotFailingWhenTryningSupplier() {
        final Try<Integer> trying = asTry(Optional.of(HAPPY), () -> new IllegalStateException("Error message"));
        assertThat(trying.isSuccess()).isTrue();
    }

    @Test
    void failure_should_fail_on_get() {
        final Exception expected = new Exception("Plz don't forget about me, I caused the original failure to occur");
        final Try<Integer> trying = Try.failure(expected);
        assertThatThrownBy(trying::get)
                .isInstanceOf(UnsupportedOperationException.class)
                .hasSuppressedException(expected);
    }

    @Test
    void failure_should_throw_original_exception_for_runtime_exceptions() {
        final RuntimeException expected = new NullPointerException();
        assertThatThrownBy(Try.failure(expected)::get)
                .isSameAs(expected)
                .hasNoSuppressedExceptions();
    }

    @Test
    void failure_should_throw_original_exception_for_errors() {
        final Error expected = new OutOfMemoryError();
        assertThatThrownBy(Try.failure(expected)::get)
                .isSameAs(expected)
                .hasNoSuppressedExceptions();
    }

    @Test
    void failure_should_successed_on_getCause() {
        final Throwable expected = new NullPointerException();
        assertThat(Try.failure(expected).getCause()).isSameAs(expected);
    }

    @Test
    void failure_should_run_ifFailure() {
        assertIfFailureInvoked(Try.failure(new NullPointerException())).isTrue();
    }

    @Test
    void failure_should_not_run_ifSuccess() {
        assertIfSuccessInvoked(Try.failure(new NullPointerException())).isFalse();
    }

    @Test
    void should_recover_from_failure() {
        final NullPointerException expected = new NullPointerException("Plz don't forget about me, I caused the original failure to occur");
        final Try<String> tryS = Try.<String>failure(expected)
                .recover(Throwable::getMessage);

        assertThat(tryS.get()).isEqualTo("Plz don't forget about me, I caused the original failure to occur");
    }

    @Test
    void should_recoverWith_from_failure_to_success() {
        final NullPointerException expected = new NullPointerException("Plz don't forget about me, I caused the original failure to occur");
        final Try<String> tryS = Try.<String>failure(expected)
                .recoverWith(t -> Try.success("Hurra"));

        assertThat(tryS.get()).isEqualTo("Hurra");
    }

    @Test
    void should_recoverWith_from_failure_to_new_failure() {
        final NullPointerException expected = new NullPointerException("Plz don't forget about me, I caused the original failure to occur");
        final Try<String> tryS = Try.<String>failure(new IllegalStateException("OPS"))
                .recoverWith(t -> Try.failure(expected));

        assertThatThrownBy(tryS::get).isSameAs(expected);
    }

    @Test
    void should_not_recover_when_it_does_not_match_thrown() {
        final NullPointerException expected = new NullPointerException("Plz don't forget about me, I caused the original failure to occur");
        final Try<String> tryS = Try.<String>failure(expected)
                .recoverWith(Try::failure);

        assertThatThrownBy(tryS::get).isSameAs(expected);
    }

    @Test
    void should_be_able_to_invert_empty_option() {
        assertThat(invert(Optional.empty())).isEqualTo(Try.success(Optional.empty()));
    }

    @Test
    void should_be_able_to_invert_optional_failure() {
        final Exception e = new NullPointerException();
        assertThat(invert(Optional.of(Try.failure(e)))).isEqualTo(Try.failure(e));
    }

    @Test
    void should_only_create_exception_on_failure() {
        final AtomicBoolean wasCalledOnSuccess = new AtomicBoolean(false);
        assertIsFailure(
                Optional.of("Definately successful"),
                () -> {
                    wasCalledOnSuccess.set(true);
                    return new RuntimeException();
                }
        )
                .isFalse();

        softly.assertThat(wasCalledOnSuccess.get())
                .as("Did Try.asTry(Optional.nonEmpty, Supplier<Exception>) invoke the Supplier?")
                .isFalse();

        final AtomicBoolean wasCalledOnFailure = new AtomicBoolean(false);
        assertIsFailure(
                Optional.empty(),
                () -> {
                    wasCalledOnFailure.set(true);
                    return new RuntimeException();
                }
        )
                .isTrue();
        softly.assertThat(wasCalledOnFailure.get())
                .as("Did Try.asTry(Optional.empty, Supplier<Exception>) invoke the Supplier?")
                .isTrue();
    }

    @Test
    void success_should_call_consumer_when_peeking() {
        assertPeekIfSuccessInvoked(Try.success(1)).isTrue();
    }

    @Test
    void failure_should_not_call_consumer_when_peeking() {
        assertPeekIfSuccessInvoked(Try.failure(new Exception())).isFalse();
    }

    private BooleanAssert assertPeekIfSuccessInvoked(final Try<Object> t) {
        final AtomicBoolean peeked = new AtomicBoolean(false);
        t.peekIfSuccess(v -> peeked.set(true));
        return softly.assertThat(peeked.get())
                .as("Did %s invoke the peekIfSuccess-consumer?", t);
    }

    private BooleanAssert assertIsFailure(final Optional<Object> verdi, final Supplier<Throwable> supl) {
        return softly.assertThat(
                        asTry(verdi, supl).isFailure()
                )
                .as("Try.asTry(%s) isFailure?", verdi);
    }

    private static AbstractBooleanAssert<?> assertIfSuccessInvoked(final Try<Integer> trying) {
        final AtomicBoolean flag = new AtomicBoolean(false);
        trying.ifSuccess(integer -> flag.set(true));
        return assertThat(flag.get())
                .as("Did %s invoke the ifSuccess-consumer?", trying);
    }

    private static AbstractBooleanAssert<?> assertIfFailureInvoked(final Try<Integer> trying) {
        final AtomicBoolean flag = new AtomicBoolean(false);
        trying.ifFailure(throwable -> flag.set(true));
        return assertThat(flag.get())
                .as("Did %s invoke the ifFailure-consumer?", trying);
    }
}
