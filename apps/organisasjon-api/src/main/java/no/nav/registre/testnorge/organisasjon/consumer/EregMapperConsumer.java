package no.nav.registre.testnorge.organisasjon.consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.testnorge.libs.common.command.CreateEregMappingCommand;
import no.nav.registre.testnorge.organisasjon.domain.Organisasjon;

@Component
public class EregMapperConsumer {
    private final WebClient webClient;

    public EregMapperConsumer(@Value("${consumers.ereg-mapper.url}") String baseUrl) {
        this.webClient = WebClient
                .builder()
                .baseUrl(baseUrl)
                .build();
    }

    public void createOrganisasjon(Organisasjon organisasjon, String miljoe) {
        new CreateEregMappingCommand(webClient, organisasjon.toEregMapperDTO(false), miljoe).run();
    }

    public void updateOrgansiasjon(Organisasjon organisasjon, String miljoe) {
        new CreateEregMappingCommand(webClient, organisasjon.toEregMapperDTO(true), miljoe).run();
    }
}
