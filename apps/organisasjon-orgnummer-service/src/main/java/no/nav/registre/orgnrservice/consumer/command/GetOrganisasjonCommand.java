package no.nav.registre.orgnrservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Callable;

import no.nav.registre.testnorge.libs.dto.organisasjon.v1.OrganisasjonDTO;

@Slf4j
@RequiredArgsConstructor
public class GetOrganisasjonCommand implements Callable<OrganisasjonDTO> {

    private final WebClient webClient;
    private final String orgnummer;
    private final String miljoe;


    public OrganisasjonDTO call() {
        log.info("Henter orgnummer {} fra miljoe {}", orgnummer, miljoe);
        return webClient.get()
                .uri(uriBuilder ->
                        uriBuilder.path("/api/v1/organisasjoner/{orgnummer}")
                                .build(orgnummer)
                )
                .header("miljoe", miljoe)
                .retrieve()
                .bodyToMono(OrganisasjonDTO.class)
                .block();
    }
}
