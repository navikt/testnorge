package no.nav.registre.sdForvalter.consumer.rs.ereg.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.net.URI;
import java.util.concurrent.Callable;

import no.nav.registre.sdForvalter.consumer.rs.response.ereg.OrganisasjonResponse;


@Slf4j
public class GetOrganisasjon implements Callable<OrganisasjonResponse> {
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
    public OrganisasjonResponse call() {
        try {
            log.info("Henter {} fra Ereg i {}...", orgnummer, miljo);
            final URI url = new UriTemplate(this.url + "/organisasjon/{orgnummer}").expand(miljo, orgnummer);
            ResponseEntity<OrganisasjonResponse> entity = restTemplate.getForEntity(
                    url,
                    OrganisasjonResponse.class
            );

            if (entity.hasBody()) {
                log.info("Hentet {} fra Ereg i {}", orgnummer, miljo);
                return entity.getBody();
            }
            if (entity.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.warn("{} ikke funnet for miljo: {}", orgnummer, miljo);
                return null;
            } else {
                log.error("Kan ikke hente ut {} fra: {}", orgnummer, miljo);
                return null;
            }

        } catch (Exception e) {
            log.error("Kan ikke hente ut {} fra: {}", orgnummer, miljo, e);
        }
        return null;
    }
}
