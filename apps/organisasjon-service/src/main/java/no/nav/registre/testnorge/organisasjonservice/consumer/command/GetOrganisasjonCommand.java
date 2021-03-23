package no.nav.registre.testnorge.organisasjonservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.concurrent.Callable;

import no.nav.registre.testnorge.organisasjonservice.consumer.dto.OrganisasjonDTO;

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
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            return null;
        }
    }
}
