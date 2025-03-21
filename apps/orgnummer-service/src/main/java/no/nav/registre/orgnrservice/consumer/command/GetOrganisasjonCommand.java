package no.nav.registre.orgnrservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.dto.organisasjon.v1.OrganisasjonDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class GetOrganisasjonCommand implements Callable<Flux<OrganisasjonDTO>> {

    private final WebClient webClient;
    private final String token;
    private final String orgnummer;
    private final String miljo;

    @Override
    public Flux<OrganisasjonDTO> call() {
        log.info("Henter organisasjon med orgnummer {} fra {}...", orgnummer, miljo);
        return webClient
                .get()
                .uri(builder -> builder
                        .path("/api/v1/organisasjoner/{orgnummer}")
                        .build(orgnummer))
                .headers(WebClientHeader.bearer(token))
                .header("miljo", this.miljo)
                .retrieve()
                .bodyToFlux(OrganisasjonDTO.class)
                .doFinally(value -> log.info("Organisasjon {} hentet fra {}", orgnummer, miljo))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(throwable -> {
                    if (throwable instanceof WebClientResponseException webClientResponseException &&
                            webClientResponseException.getStatusCode() == HttpStatus.NOT_FOUND) {
                        log.trace("Organisasjon med orgnummer {} ikke funnet i {}", orgnummer, miljo);
                        return Flux.empty();
                    }
                    return Flux.error(throwable);
                });
    }

}