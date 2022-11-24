package no.nav.dolly.bestilling.aareg.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.dto.organisasjon.v1.OrganisasjonDTO;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
public class OrganisasjonGetCommand implements Callable<Flux<OrganisasjonDTO>> {

    public static final String NOT_FOUND = "Ikke funnet";

    private final WebClient webClient;
    private final String orgnummer;
    private final String miljo;
    private final String token;

    @Override
    public Flux<OrganisasjonDTO> call() {

        return webClient
                .get()
                .uri(builder -> builder
                        .path("/api/v1/organisasjoner/{orgnummer}")
                        .build(orgnummer)
                )
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .header("miljo", this.miljo)
                .retrieve()
                .bodyToFlux(OrganisasjonDTO.class)
                .doOnError(WebClientFilter::logErrorMessage)
                .onErrorResume(error -> Flux.just(OrganisasjonDTO.builder()
                                .orgnummer(orgnummer)
                                .juridiskEnhet(NOT_FOUND)
                                .build()))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
