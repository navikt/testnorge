package no.nav.registre.testnorge.arbeidsforhold.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.testnorge.arbeidsforhold.consumer.commnad.SaveOpplysningspliktigCommand;

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

    public void saveOpplysningspliktig(String xml) {
        new SaveOpplysningspliktigCommand(webClient, xml).run();
    }
}
