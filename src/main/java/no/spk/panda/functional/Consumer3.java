package no.spk.panda.functional;

@FunctionalInterface
public interface Consumer3<I1, I2, I3> {
    void accept(I1 t1, I2 t2, I3 t3);
}
