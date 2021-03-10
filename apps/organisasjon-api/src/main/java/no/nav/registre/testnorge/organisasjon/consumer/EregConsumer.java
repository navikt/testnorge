package no.nav.registre.testnorge.organisasjon.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import no.nav.registre.testnorge.organisasjon.consumer.command.GetOrganisasjon;
import no.nav.registre.testnorge.organisasjon.consumer.dto.OrganisasjonDTO;
import no.nav.registre.testnorge.organisasjon.domain.Organisasjon;


@Slf4j
@Component
public class EregConsumer {
    private final RestTemplate restTemplate;
    private final String eregUrl;

    public EregConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${consumers.ereg.url}") String eregUrl
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.eregUrl = eregUrl;
    }

    public Organisasjon getOrganisasjon(String orgnummer, String miljo) {
        OrganisasjonDTO response = new GetOrganisasjon(eregUrl, miljo, orgnummer, restTemplate).call();
        return response != null ? new Organisasjon(response) : null;
    }
}
