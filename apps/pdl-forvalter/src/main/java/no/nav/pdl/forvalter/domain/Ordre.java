package no.nav.pdl.forvalter.domain;

import no.nav.testnav.libs.dto.pdlforvalter.v1.OrdreResponseDTO;
import no.nav.testnav.libs.servletsecurity.domain.AccessToken;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@FunctionalInterface
public interface Ordre extends Function<AccessToken, Mono<OrdreResponseDTO.PdlStatusDTO>> {
}
