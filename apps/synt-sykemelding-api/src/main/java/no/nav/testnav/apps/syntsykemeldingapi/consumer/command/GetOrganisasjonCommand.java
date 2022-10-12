package no.nav.testnav.apps.syntsykemeldingapi.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.commands.utils.WebClientFilter;
import no.nav.testnav.libs.dto.organisasjon.v1.OrganisasjonDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class GetOrganisasjonCommand implements Callable<Mono<OrganisasjonDTO>> {
    private final WebClient webClient;
    private final String token;
    private final String orgnummer;
    private final String miljo;

    @Override
    public Mono<OrganisasjonDTO> call() {
        log.info("Henter organisasjon med orgnummer {} fra {}...", orgnummer, miljo);
        try {
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
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                            .filter(WebClientFilter::is5xxException));
        } catch (WebClientResponseException.NotFound e) {
            log.trace("Organisasjon med orgnummer {} ikke funnet i {}", orgnummer, miljo);
            throw e;
        }
    }
}
