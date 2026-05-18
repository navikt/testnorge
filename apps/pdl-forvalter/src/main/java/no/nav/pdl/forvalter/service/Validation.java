package no.nav.pdl.forvalter.service;

import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface Validation<T extends DbVersjonDTO> {

    Mono<Void> validate(T artifact);
}
