package no.spk.panda.functional;

import static java.util.Objects.requireNonNull;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Hensikten med denne klassen er å håntere feilsituasjoner uten å kaste exceptions.
 * Det er to mulige tilstander: #{Try.Success} og #{Try.Failure}.
 * <p>
 * Den er en Monad ({@link Try#flatMap} }) og en Functor ({@link Try#map}).
 *
 * @param <T> Innholdstype for #{Try}
 */
public abstract class Try<T> {
    private Try() {
    }

    public static <T> Try<T> success(final T t) {
        return new Success<>(t);
    }

    public static <T> Try<T> failure(final Throwable throwable) {
        return new Failure<>(throwable);
    }

    public static <T> Try<T> asTry(final Optional<T> optional, final Supplier<Throwable> throwable) {
        return optional
                .map(Try::success)
                .orElseGet(() -> failure(throwable.get()));
    }

    public static <T> Try<T> doTry(final Supplier<T> supplier) {
        try {
            return success(supplier.get());
        } catch (final Throwable throwable) {
            return failure(throwable);
        }
    }

    public static <T> Try<T> doTryWithCatch(final Callable<T> supplier) {
        try {
            return success(supplier.call());
        } catch (final Throwable throwable) {
            return failure(throwable);
        }
    }

    public static <T> Try<Optional<T>> invert(final Optional<Try<T>> optionalTry) {
        return optionalTry
                .map(tTry ->
                        tTry.map(Optional::of)
                )
                .orElse(Try.success(Optional.empty()));
    }

    public abstract Optional<T> toOptional();

    public abstract T orElse(T t);

    public abstract T orElseGet(Supplier<T> supplier);

    public abstract <U> Try<U> map(Function<T, U> f);

    public abstract <U> Try<U> flatMap(Function<T, Try<U>> f);

    public abstract boolean isSuccess();

    public abstract <U> U fold(Function<Throwable, U> failureHandling, Function<T, U> successHandling);

    public abstract void ifSuccess(final Consumer<T> consumer);

    public abstract void ifFailure(final Consumer<Throwable> consumer);

    public abstract Try<T> peekIfSuccess(Consumer<T> resultat);

    /**
     * Hensikten er å håndtere noen typer throwable, men ikke nødvendigvis alle.
     *
     * @param recoverFunction #{PartialFunction} som kan gjøre at man kan komme tilbake fra en feilsituasjon.
     * @return hvis {@link #isSuccess()} returneres <code>this</code>, ellers returneres en ny Try som wrapper resultatet av å eksekvere
     * <code>recoverFunction</code>
     */
    public abstract Try<T> recover(Function<Throwable, T> recoverFunction);

    /**
     * Man prøver på nytt for de throwables som man er definert for
     *
     * @param recoverFunction #{PartialFunction} som kan gjøre at man kan komme tilbake fra en feilsituasjon.
     * @return hvis {@link #isSuccess()} returneres <code>this</code>, ellers returneres resultatet <code>recoverFunction</code> produserer
     */
    public abstract Try<T> recoverWith(Function<Throwable, Try<T>> recoverFunction);

    /**
     * Henter ut verdien gitt at det ikke har inntruffet en feil.
     * <br>
     * Bør kun benyttes i situasjoner der en tidligere har validert via {@link #isSuccess()} at det ikke har oppstått en feilsituasjon. Det vil
     * da ikke eksisterer noen verdi som kan hentes ut.
     *
     * @return verdien dersom objektet ikke representerer en feilsituasjon
     * @throws UnsupportedOperationException dersom {@link #isSuccess()} indikerer at objektet representerer en feilsituasjon
     */
    public abstract T get() throws UnsupportedOperationException;

    /**
     * Henter ut feilen for feilsituasjonen som objektet representerer.
     * <br>
     * Bør kun benyttes i situasjoner der en tidligere har validert via {@link #isSuccess()} at ein feilsituasjon har oppstått. Det er kun være
     * da en har en årsak tilgjengelig for uthenting.
     *
     * @return årsaken til at feilsituasjonen oppstod
     * @throws UnsupportedOperationException dersom objektet ikke representerer en feilsituasjon og {@link #isSuccess()} indikerer at ingen feil har
     *                                       inntruffet
     */
    public abstract Throwable getCause() throws UnsupportedOperationException;

    public final boolean isFailure() {
        return !isSuccess();
    }

    public static final class Success<T> extends Try<T> {

        private final T value;

        private Success(final T value) {
            this.value = requireNonNull(value, "value cannot be null");
        }

        @Override
        public Optional<T> toOptional() {
            return Optional.ofNullable(value);
        }

        @Override
        public T orElse(final T t) {
            return value;
        }

        @Override
        public T orElseGet(final Supplier<T> supplier) {
            return value;
        }

        @Override
        public <U> Try<U> map(final Function<T, U> f) {
            try {
                return new Success<>(f.apply(value));
            } catch (final Throwable t) {
                return failure(t);
            }
        }

        @Override
        public <U> Try<U> flatMap(final Function<T, Try<U>> f) {
            try {
                return f.apply(value);
            } catch (final Throwable t) {
                return failure(t);
            }
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public <U> U fold(final Function<Throwable, U> failureHandling, final Function<T, U> successHandling) {
            return successHandling.apply(value);
        }

        @Override
        public void ifSuccess(final Consumer<T> consumer) {
            consumer.accept(value);
        }

        @Override
        public void ifFailure(final Consumer<Throwable> consumer) {
        }

        @Override
        public Try<T> peekIfSuccess(final Consumer<T> consumer) {
            consumer.accept(value);
            return this;
        }

        @Override
        public Try<T> recover(final Function<Throwable, T> recoverFunction) {
            return this;
        }

        @Override
        public Try<T> recoverWith(final Function<Throwable, Try<T>> recoverFunction) {
            return this;
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public Throwable getCause() {
            throw new UnsupportedOperationException(this + " does not support Try.getCause()");
        }

        @Override
        public String toString() {
            return String.format("Try.Success(%s)", value);
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final Success<?> success = (Success<?>) o;

            return value.equals(success.value);
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }
    }

    public static final class Failure<T> extends Try<T> {
        private final Throwable throwable;

        private Failure(final Throwable throwable) {
            this.throwable = requireNonNull(throwable, "Error message cannot be null");
        }

        @Override
        public void ifSuccess(final Consumer<T> consumer) {
        }

        @Override
        public void ifFailure(final Consumer<Throwable> consumer) {
            consumer.accept(throwable);
        }

        @Override
        public Try<T> peekIfSuccess(final Consumer<T> resultat) {
            return this;
        }

        @Override
        public T get() {
            if (throwable instanceof Error) {
                throw (Error) throwable;
            }
            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            }
            final UnsupportedOperationException failure = new UnsupportedOperationException(this + " does not support Try.get()");
            failure.addSuppressed(throwable);
            throw failure;
        }

        @Override
        public Throwable getCause() {
            return throwable;
        }

        @Override
        public Optional<T> toOptional() {
            return Optional.empty();
        }

        @Override
        public T orElse(final T t) {
            return t;
        }

        @Override
        public T orElseGet(final Supplier<T> supplier) {
            return supplier.get();
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public <U> U fold(final Function<Throwable, U> failureHandling, final Function<T, U> successHandling) {
            return failureHandling.apply(throwable);
        }

        @Override
        public <U> Try<U> flatMap(final Function<T, Try<U>> f) {
            return new Failure<>(throwable);
        }

        @Override
        public <U> Try<U> map(final Function<T, U> f) {
            return new Failure<>(throwable);
        }

        @Override
        public Try<T> recover(final Function<Throwable, T> recoverFunction) {
            return Try.doTry(() -> recoverFunction.apply(throwable));
        }

        @Override
        public Try<T> recoverWith(final Function<Throwable, Try<T>> recoverFunction) {
            return Try.doTry(() -> recoverFunction.apply(throwable)).flatMap(Function.identity());
        }

        @Override
        public String toString() {
            return String.format("Try.Failure(%s)", throwable);
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final Failure<?> failure = (Failure<?>) o;

            return throwable.equals(failure.throwable);
        }

        @Override
        public int hashCode() {
            return throwable.hashCode();
        }
    }
}
