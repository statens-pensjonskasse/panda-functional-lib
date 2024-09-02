package no.spk.panda.functional;

@FunctionalInterface
public interface Function5<I1, I2, I3, I4, I5, R> {
    R apply(I1 i1, I2 i2, I3 i3, I4 i4, I5 i5);
}