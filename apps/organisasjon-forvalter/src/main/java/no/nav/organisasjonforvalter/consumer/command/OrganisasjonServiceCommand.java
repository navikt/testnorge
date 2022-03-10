package no.nav.organisasjonforvalter.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.dto.organisasjon.v1.OrganisasjonDTO;
import no.nav.testnav.libs.servletcore.util.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class OrganisasjonServiceCommand implements Callable<Mono<OrganisasjonDTO>> {

    private static final String STATUS_URL = "/api/v1/organisasjoner/{orgnummer}";
    private static final String MILJOE = "miljo";

    private final WebClient webClient;
    private final String orgnummer;
    private final String environment;
    private final String token;

    @Override
    public Mono<OrganisasjonDTO> call() {

        return webClient.get()
                .uri(STATUS_URL.replace("{orgnummer}", orgnummer))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(MILJOE, environment)
                .retrieve()
                .bodyToMono(OrganisasjonDTO.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
