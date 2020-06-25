package no.nav.registre.testnorge.synt.sykemelding.consumer;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import no.nav.registre.testnorge.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.dto.organisasjon.v1.OrganisasjonDTO;
import no.nav.registre.testnorge.synt.sykemelding.consumer.command.GetOrgansiasjonCommand;

@Component
@DependencyOn("organisasjon-api")
public class OrganisasjonConsumer {
    private final RestTemplate restTemplate;
    private final String url;

    public OrganisasjonConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${consumers.organisasjon.url}") String url
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.url = url;
    }

    @SneakyThrows
    public OrganisasjonDTO getOrganisasjon(String orgnummer) {
        return new GetOrgansiasjonCommand(restTemplate, url, orgnummer, "q1").call();
    }
}