package no.nav.dolly.bestilling.service;

import no.nav.dolly.bestilling.ClientRegister;

import java.util.function.Function;

@FunctionalInterface
public interface GjenopprettSteg extends Function<ClientRegister, Boolean> {
}
