package no.nav.testnav.apps.tenorsearchservice.consumers;

import com.fasterxml.jackson.databind.JsonNode;
import no.nav.testnav.apps.tenorsearchservice.config.Consumers;
import no.nav.testnav.apps.tenorsearchservice.consumers.command.GetTenorTestdata;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class TenorClient {

    private final WebClient webClient;
    private final MaskinportenClient maskinportenClient;

    public TenorClient(Consumers consumers, MaskinportenClient maskinportenClient) {

        this.webClient = WebClient
                .builder()
                .baseUrl(consumers.getTenorSearchService().getUrl())
                .build();
        this.maskinportenClient = maskinportenClient;
    }

    public Mono<TenorResponse> getTestdata(String query) {

        return maskinportenClient.getAccessToken()
                .flatMap(token -> new GetTenorTestdata(webClient, query, token.value()).call());
    }
}