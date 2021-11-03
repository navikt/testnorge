package no.nav.dolly.consumer.profil.command;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.dto.profil.v1.ProfilDTO;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;

@Slf4j
public record GetProfilCommand(WebClient webClient,
                               String token,
                               String jwtToken,
                               String callId) implements Callable<Mono<ResponseEntity<ProfilDTO>>> {

    private static final String PROFIL_URL = "/api/v1/profil";

    @Override
    public Mono<ResponseEntity<ProfilDTO>> call() {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(PROFIL_URL)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, token)
                .header(UserConstant.USER_HEADER_JWT, jwtToken)
                .header(HEADER_NAV_CALL_ID, callId)
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .retrieve()
                .toEntity(ProfilDTO.class);
    }
}
