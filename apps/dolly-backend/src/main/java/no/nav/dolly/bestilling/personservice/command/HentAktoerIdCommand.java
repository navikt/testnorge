package no.nav.dolly.bestilling.personservice.command;

import no.nav.dolly.bestilling.personservice.domain.AktoerIdent;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;

public record HentAktoerIdCommand(WebClient webClient,
                                  String token, String ident,
                                  String callId) implements Callable<Mono<ResponseEntity<AktoerIdent>>> {

    private static final String AKTOERID_URL = "/api/v1/personer";

    @Override
    public Mono<ResponseEntity<AktoerIdent>> call() {


        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(AKTOERID_URL)
                        .pathSegment(ident)
                        .pathSegment("aktoerId")
                        .build())
                .header(HttpHeaders.AUTHORIZATION, token)
                .header(HEADER_NAV_CALL_ID, callId)
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .retrieve()
                .toEntity(AktoerIdent.class);
    }
}
