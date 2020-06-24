package no.nav.registre.testnorge.common.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.concurrent.Callable;

import no.nav.registre.testnorge.dto.organisasjon.v1.OrganisasjonDTO;

@Slf4j
@RequiredArgsConstructor
public class GetOrganisasjonCommand implements Callable<OrganisasjonDTO> {
    private final RestTemplate restTemplate;
    private final String url;
    private final String orgnummer;
    private final String miljo;

    @Override
    public OrganisasjonDTO call() throws Exception {
        log.info("Henter org {}", orgnummer);
        var response = restTemplate.exchange(
                RequestEntity.get(new URI(url + "/api/v1/organisasjoner/" + orgnummer)).header("miljo", this.miljo).build(),
                OrganisasjonDTO.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error(
                    "Klarer ikke å hente organsiasjon {}. Response kode {}.",
                    orgnummer,
                    response.getStatusCodeValue()
            );
        }
        return response.getBody();
    }
}