package no.spk.panda.functional;

import static java.lang.String.format;

import java.util.function.BinaryOperator;

public class ReduceFunctions {

    private ReduceFunctions() {}

    public static <T> BinaryOperator<T> finnKunEn() {
        return finnKunEn("Skulle kun hatt ett element men fant to:");
    }

    public static <T> BinaryOperator<T> finnKunEn(final String feilmelding) {
        return (a, b) -> {
            throw new IllegalStateException(
                    format(
                            """
                                    Programmeringsfeil: %s
                                    %s
                                    %s""",
                            feilmelding,
                            a,
                            b
                    )
            );
        };
    }
}
