package no.nav.dolly.service;

@FunctionalInterface
public interface TriConsumer<S, T, U> {
    void apply(S var1, T var2, U var3);
}
