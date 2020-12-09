package no.nav.registre.orgnrservice.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.orgnrservice.consumer.command.GetOrganisasjonCommand;
import no.nav.registre.testnorge.libs.dto.organisasjon.v1.OrganisasjonDTO;

@Slf4j
@Component
public class OrganisasjonApiConsumer {

    private WebClient webClient;

    OrganisasjonApiConsumer (@Value("${consumer.organisasjon-api.url}") String url) {
        this.webClient = WebClient.builder()
                .baseUrl(url)
                .build();
    }

    OrganisasjonDTO getOrgnrFraMiljoe (String orgnummer, String miljoe) {
        try {
            return new GetOrganisasjonCommand(webClient, orgnummer, miljoe).call();
        } catch( Exception e) {
            throw new RuntimeException("Kunne ikke hente organisasjon " + orgnummer + " fra miljoe " + miljoe);
        }
    }

    public OrganisasjonDTO getOrgnr (String orgnummer) {
        //evt loop gjennom miljø

        return getOrgnrFraMiljoe(orgnummer, "q1");
    }
}
