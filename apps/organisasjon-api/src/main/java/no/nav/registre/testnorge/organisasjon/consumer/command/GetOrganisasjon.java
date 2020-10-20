package no.nav.registre.testnorge.organisasjon.consumer.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.concurrent.Callable;

import no.nav.registre.testnorge.organisasjon.consumer.dto.OrganisasjonDTO;


@Slf4j
public class GetOrganisasjon implements Callable<OrganisasjonDTO> {
    private final String url;
    private final String miljo;
    private final String orgnummer;
    private final RestTemplate restTemplate;

    public GetOrganisasjon(String url, String miljo, String orgnummer, RestTemplate restTemplate) {
        this.url = url;
        this.miljo = miljo;
        this.orgnummer = orgnummer;
        this.restTemplate = restTemplate;
    }

    @Override
    public OrganisasjonDTO call() {
        log.info("Henter {} fra EREG i {}...", orgnummer, miljo);
        ResponseEntity<OrganisasjonDTO> entity;
        try {
            entity = restTemplate.getForEntity(
                    new UriTemplate(this.url + "/organisasjon/{orgnummer}?inkluderHierarki=true&inkluderHistorikk=false")
                            .expand(miljo, orgnummer),
                    OrganisasjonDTO.class
            );
        } catch (HttpClientErrorException.NotFound e) {
            log.info("Finner ikke orgnaisasjon {} fra EREG i {}", orgnummer, miljo);
            return null;
        }
        if (entity.hasBody()) {
            log.info("Hentet {} fra EREG i {}", orgnummer, miljo);
            return entity.getBody();
        }
        return null;

    }
}