package no.spk.panda.functional;

@FunctionalInterface
public interface Function3<I1, I2, I3, R> {
    R apply(I1 i1, I2 i2, I3 i3);
}
