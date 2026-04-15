package no.nav.pdl.forvalter.service;

import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface BiValidation<T extends DbVersjonDTO, R> {

    Mono<Void> validate(T artifact, R person);
}
