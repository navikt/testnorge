package no.nav.dolly.bestilling.etterlatte;

import no.nav.dolly.domain.PdlPersonBolk;

import java.util.function.Function;

@FunctionalInterface
public interface Gjenlevende extends Function<PdlPersonBolk.PersonBolk, Boolean> {
}
