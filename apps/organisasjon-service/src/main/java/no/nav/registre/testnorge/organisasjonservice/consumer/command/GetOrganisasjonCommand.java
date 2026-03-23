package no.nav.registre.testnorge.organisasjonservice.consumer.command;

import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.organisasjonservice.consumer.dto.OrganisasjonDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class GetOrganisasjonCommand implements Callable<Mono<OrganisasjonDTO>> {

    private final WebClient webClient;
    private final String token;
    private final String miljo;
    private final String orgnummer;

    @Override
    public Mono<OrganisasjonDTO> call() {
        log.info("Henter organisasjon med orgnummer {}.", orgnummer);
        return webClient
                .get()
                .uri(builder -> builder
                        .path("/ereg/api/{miljo}/v2/organisasjon/{orgnummer}")
                        .queryParam("inkluderHierarki", true)
                        .queryParam("inkluderHistorikk", false)
                        .build(miljo, orgnummer)
                )
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToMono(OrganisasjonDTO.class)
                .doOnSuccess(response -> log.info("Response: {}", Json.pretty(response)))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(WebClientResponseException.NotFound.class, e -> {
                    log.warn("Fant ikke {}.", orgnummer);
                    return Mono.empty();
                });
    }
}
