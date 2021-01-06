package no.nav.registre.testnorge.libs.common.command;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.concurrent.Callable;

import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.libs.dto.organisasjon.v1.OrganisasjonDTO;

@Slf4j
@DependencyOn("testnorge-organisasjon-api")
@RequiredArgsConstructor
public class GetOrganisasjonCommand implements Callable<OrganisasjonDTO> {
    private final WebClient webClient;
    private final String accessToken;
    private final String orgnummer;
    private final String miljo;

    @SneakyThrows
    @Override
    public OrganisasjonDTO call() {
        log.info("Henter organiasjon med orgnummer {}.", orgnummer);
        try {
            return webClient
                    .get()
                    .uri(builder -> builder
                            .path("/api/v1/organisasjoner/{orgnummer}")
                            .build(orgnummer)
                    )
                    .header("miljo", this.miljo)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(OrganisasjonDTO.class)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            return null;
        }
    }
}