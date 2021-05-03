package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Set;

import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.command.GetLevendeIdenterCommand;

@Component
public class HodejegerenConsumer {

    private final WebClient webClient;

    public HodejegerenConsumer(
            @Value("${consumers.hodejegeren.url}") String url
    ) {
        this.webClient = WebClient
                .builder()
                .baseUrl(url)
                .build();
    }

    public Set<String> getIdenter(String miljo, int antall) {
        return new GetLevendeIdenterCommand(webClient, miljo, antall).call();
    }
}
