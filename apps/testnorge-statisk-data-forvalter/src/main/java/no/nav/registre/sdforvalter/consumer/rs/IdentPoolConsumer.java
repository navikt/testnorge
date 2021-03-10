package no.nav.registre.sdforvalter.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.testnorge.libs.core.config.ApplicationProperties;
import no.nav.registre.testnorge.libs.dto.identpool.v1.FiktiveNavnDTO;

@Slf4j
@Component
public class IdentPoolConsumer {

    private final WebClient webClient;
    private final String applicationName;

    public IdentPoolConsumer(@Value("${consumers.identpool.url}") String url, ApplicationProperties applicationProperties) {
        this.applicationName = applicationProperties.getName();
        this.webClient = WebClient
                .builder()
                .baseUrl(url)
                .build();
    }

    public FiktiveNavnDTO getFiktivtNavn() {
        FiktiveNavnDTO[] response = webClient
                .get()
                .uri(builder -> builder.path("/api/v1/fiktive-navn/tilfeldig").queryParam("antall", 1).build())
                .retrieve()
                .bodyToMono(FiktiveNavnDTO[].class)
                .block();

        if (response == null || response.length == 0) {
            throw new RuntimeException("Klarte ikke å hente navn fra ident-pool");
        }

        return response[0];
    }

}
