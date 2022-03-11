package no.nav.registre.testnorge.organisasjonservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.organisasjonservice.consumer.dto.OrganisasjonDTO;
import no.nav.testnav.libs.commands.utils.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class GetOrganisasjonCommand implements Callable<OrganisasjonDTO> {
    private final WebClient webClient;
    private final String token;
    private final String miljo;
    private final String orgnummer;

    @Override
    public OrganisasjonDTO call() {
        log.info("Henter organisasjon med orgnummer {}.", orgnummer);
        try {
            return webClient
                    .get()
                    .uri(builder -> builder
                            .path("/api/{miljo}/v1/organisasjon/{orgnummer}")
                            .queryParam("inkluderHierarki", true)
                            .queryParam("inkluderHistorikk", false)
                            .build(miljo, orgnummer)
                    )
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .bodyToMono(OrganisasjonDTO.class)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                            .filter(WebClientFilter::is5xxException))
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            log.warn("Fant ikke {}.", orgnummer);
            return null;
        }
    }
}
