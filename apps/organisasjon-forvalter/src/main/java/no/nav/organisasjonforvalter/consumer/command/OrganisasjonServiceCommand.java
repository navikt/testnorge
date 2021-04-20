package no.nav.organisasjonforvalter.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.libs.dto.organisasjon.v1.OrganisasjonDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class OrganisasjonServiceCommand implements Callable<OrganisasjonDTO> {

    private static final String STATUS_URL = "/api/v1/organisasjoner/{orgnummer}";
    private static final String MILJOE = "miljo";

    private final WebClient webClient;
    private final String orgnummer;
    private final String miljoe;
    private final String token;

    @Override
    public OrganisasjonDTO call() {

        return webClient.get()
                .uri(STATUS_URL.replace("{orgnummer}", orgnummer))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(MILJOE, miljoe)
                .retrieve()
                .bodyToMono(OrganisasjonDTO.class)
                .block();
    }
}
