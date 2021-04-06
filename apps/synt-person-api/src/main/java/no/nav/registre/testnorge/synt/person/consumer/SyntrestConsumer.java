package no.nav.registre.testnorge.synt.person.consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import no.nav.registre.testnorge.synt.person.consumer.command.GetSyntPersonCommand;
import no.nav.registre.testnorge.synt.person.consumer.dto.SyntPersonDTO;

@Component
public class SyntrestConsumer {
    private final WebClient webClient;

    public SyntrestConsumer(@Value("${consumers.syntrest.url}") String url) {
        this.webClient = WebClient
                .builder()
                .baseUrl(url)
                .build();
    }

    public List<SyntPersonDTO> createSyntPerson(String antall) {
        return new GetSyntPersonCommand(webClient, antall).call();
    }
}
