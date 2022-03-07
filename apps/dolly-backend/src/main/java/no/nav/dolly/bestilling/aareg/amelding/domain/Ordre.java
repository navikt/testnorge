package no.nav.dolly.bestilling.aareg.amelding.domain;

import no.nav.testnav.libs.dto.pdlforvalter.v1.OrdreResponseDTO;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.function.Function;

@FunctionalInterface
public interface Ordre extends Function<String, Mono<Map<String, ResponseEntity<Void>>>> {
}
