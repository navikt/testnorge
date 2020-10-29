package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class SyntrestConsumer {
    private final WebClient webClient;

    public SyntrestConsumer(
            @Value("${consumers.syntrest.url}") String url
    ) {
        this.webClient = WebClient
                .builder()
                .baseUrl(url)
                .build();
    }
}
