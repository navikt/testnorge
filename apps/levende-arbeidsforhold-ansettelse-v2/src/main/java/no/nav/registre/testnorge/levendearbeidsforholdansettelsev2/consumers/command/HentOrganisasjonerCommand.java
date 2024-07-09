package no.nav.registre.testnorge.levendearbeidsforholdansettelsev2.consumers.command;

import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.avro.organisasjon.v1.Organisasjon;
import no.nav.testnav.libs.commands.utils.WebClientFilter;
import no.nav.testnav.libs.dto.organisasjon.v1.OrganisasjonDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class HentOrganisasjonerCommand implements Callable<Organisasjon> {
    private final WebClient webClient;
    private final String token;
    private final String miljo;
    private final String orgnummer;

    @Override
    public Organisasjon call() {
        log.info("Henter organisasjon med orgnummer {}.", orgnummer);
        try {
            return webClient
                    .get()
                    .uri(builder -> builder
                            .path("/api/{miljo}/v2/organisasjon/alle")
                            .queryParam("inkluderHierarki", true)
                            .queryParam("inkluderHistorikk", false)
                            .build(miljo, orgnummer)
                    )
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .bodyToMono(Organisasjon.class)
                    .doOnSuccess(response -> log.info("Response: {}", Json.pretty(response)))
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                            .filter(WebClientFilter::is5xxException))
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            log.warn("Fant ikke {}.", orgnummer);
            return null;
        }
    }
}

