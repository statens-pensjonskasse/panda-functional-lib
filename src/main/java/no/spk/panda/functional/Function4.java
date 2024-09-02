package no.spk.panda.functional;

@FunctionalInterface
public interface Function4<I1, I2, I3, I4, R> {
    R apply(I1 i1, I2 i2, I3 i3, I4 i4);
}
