package no.nav.pdl.forvalter.domain;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

import no.nav.testnav.libs.dto.pdlforvalter.v1.OrdreResponseDTO;
import no.nav.testnav.libs.servletsecurity.domain.AccessToken;

@FunctionalInterface
public interface Ordre extends Function<AccessToken, Mono<OrdreResponseDTO.PdlStatusDTO>> {
}
