package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import no.nav.testnav.libs.dto.organisasjon.v1.OrganisasjonDTO;

@Slf4j
@RequiredArgsConstructor
public class GetOrganisasjonCommand implements Callable<Mono<OrganisasjonDTO>> {
    private final WebClient webClient;
    private final String token;
    private final String orgnummer;
    private final String miljo;

    @Override
    public Mono<OrganisasjonDTO> call() {
        log.trace("Henter organisasjon med orgnummer {} fra {}...", orgnummer, miljo);
        return webClient
                .get()
                .uri(builder -> builder
                        .path("/api/v1/organisasjoner/{orgnummer}")
                        .build(orgnummer)
                )
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header("miljo", this.miljo)
                .retrieve()
                .bodyToMono(OrganisasjonDTO.class)
                .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(3))
                        .filter(throwable -> !(throwable instanceof WebClientResponseException.NotFound))
                ).onErrorResume(throwable -> throwable instanceof WebClientResponseException.NotFound, throwable -> Mono.empty());

    }
}
