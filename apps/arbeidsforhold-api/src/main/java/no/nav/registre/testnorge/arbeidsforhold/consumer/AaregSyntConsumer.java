package no.nav.registre.testnorge.arbeidsforhold.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.testnorge.arbeidsforhold.consumer.commnad.SaveAmeldingCommand;
import no.nav.registre.testnorge.arbeidsforhold.domain.Opplysningspliktig;

@Slf4j
@Component
public class AaregSyntConsumer {
    private final WebClient webClient;

    public AaregSyntConsumer(
            @Value("${consumers.aaregsyntapi.url}") String baseUrl
    ) {
        this.webClient = WebClient
                .builder()
                .baseUrl(baseUrl)
                .build();
    }

    public void saveOpplysningspliktig(Opplysningspliktig opplysningspliktig) {
        log.info("Oppretter a-melding for opplysningspliktig {}...", opplysningspliktig.getOrgnummer());
        new SaveAmeldingCommand(webClient, opplysningspliktig.toEDAGM()).run();
        log.info("A-melding opprettet for opplysningspliktig {}.", opplysningspliktig.getOrgnummer());
    }
}
