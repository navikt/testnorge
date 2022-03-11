package no.nav.dolly.bestilling.aareg.amelding.command;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.dto.organisasjon.v1.OrganisasjonDTO;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@Slf4j
public record GetOrganisasjonCommand(WebClient webClient,
                                     String token, String orgnummer,
                                     String miljo) implements Callable<OrganisasjonDTO> {
    @Override
    public OrganisasjonDTO call() {
        log.info("Henter organisasjon med orgnummer {} fra {}...", orgnummer, miljo);
        try {
            var organiasjon = webClient
                    .get()
                    .uri(builder -> builder
                            .path("/api/v1/organisasjoner/{orgnummer}")
                            .build(orgnummer)
                    )
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                    .header("miljo", this.miljo)
                    .retrieve()
                    .bodyToMono(OrganisasjonDTO.class)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                            .filter(WebClientFilter::is5xxException))
                    .block();
            log.info("Organisasjon {} hentet fra {}", orgnummer, miljo);
            return organiasjon;
        } catch (WebClientResponseException.NotFound e) {
            log.trace("Organisasjon med orgnummer {} ikke funnet i {}", orgnummer, miljo);
            return null;
        }
    }
}
