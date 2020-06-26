package no.nav.registre.testnorge.organisasjon.consumer.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        try {
            log.debug("Henter {} fra Ereg i {}...", orgnummer, miljo);
            ResponseEntity<OrganisasjonDTO> entity = restTemplate.getForEntity(
                    new UriTemplate(this.url + "/organisasjon/{orgnummer}?inkluderHierarki=true&inkluderHistorikk=false")
                            .expand(miljo, orgnummer),
                    OrganisasjonDTO.class
            );

            if (entity.hasBody()) {
                log.debug("Hentet {} fra Ereg i {}", orgnummer, miljo);
                return entity.getBody();
            }
            if (entity.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.warn("{} ikke funnet for miljo: {}", orgnummer, miljo);
            } else {
                log.error("Kan ikke hente ut {} fra: {}", orgnummer, miljo);
            }
            return null;

        } catch (Exception e) {
            log.error("Kan ikke hente ut {} fra: {}", orgnummer, miljo, e);
        }
        return null;
    }
}