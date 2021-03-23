package no.nav.registre.testnorge.libs.common.command.organisasjonservice.v1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import no.nav.registre.testnorge.libs.dto.organisasjon.v1.OrganisasjonDTO;

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
                            .path("/api/v1/organisasjoner/{orgnummer}")
                            .build(orgnummer)
                    )
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .header("miljo", miljo)
                    .retrieve()
                    .bodyToMono(OrganisasjonDTO.class)
                    .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(10)))
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            return null;
        }
    }
}
