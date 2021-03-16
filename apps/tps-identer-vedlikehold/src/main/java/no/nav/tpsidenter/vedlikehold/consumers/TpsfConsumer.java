package no.nav.tpsidenter.vedlikehold.consumers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Set;

@Component
public class TpsfConsumer {

    private static final String TPS_PERSONER = "/api/v1/dolly/testdata/personer";

    private final WebClient webClient;

    public TpsfConsumer(
            @Value("${tps-forvalteren.rest.api.url}") String serverUrl
    ) {
        this.webClient = WebClient.builder()
                .baseUrl(serverUrl + TPS_PERSONER)
                .build();
    }

    public Object deleteIdentInTpsf(String ident) {

        return webClient.delete()
                .uri(uriBuilder -> uriBuilder.queryParam("identer", Set.of(ident)).build())
                .retrieve()
                .toEntity(Object.class)
                .block();
    }
}
