package no.nav.registre.testnorge.libs.common.command;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Callable;

import no.nav.testnav.libs.dto.organisasjon.v1.OrganisasjonDTO;

@Slf4j
@RequiredArgsConstructor
public class GetMNOrganisasjonCommand implements Callable<OrganisasjonDTO> {
    private final WebClient webClient;
    private final String accessToken;
    private final String orgnummer;
    private final String miljo;

    @SneakyThrows
    @Override
    public OrganisasjonDTO call() {
        log.info("Henter organisasjon {} fra Mini-Norge organisasjoner.", orgnummer);
        try {
            return webClient
                    .get()
                    .uri(builder -> builder
                            .path("/api/v1/organisasjoner/{orgnummer}")
                            .build(orgnummer)
                    )
                    .header("miljo", miljo)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(OrganisasjonDTO.class)
                    .block();
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        }
    }
}