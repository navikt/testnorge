package no.nav.testnav.apps.tenorsearchservice.consumers;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.tenorsearchservice.config.Consumers;
import no.nav.testnav.apps.tenorsearchservice.consumers.command.GetTenorTestdata;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class TenorClient {

    private final WebClient webClient;
    private final MaskinportenClient maskinportenClient;

    public TenorClient(Consumers consumers, MaskinportenClient maskinportenClient) {

        var uriFactory = new DefaultUriBuilderFactory();
        uriFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);

        this.webClient = WebClient
                .builder()
                .baseUrl(consumers.getTenorSearchService().getUrl())
                .uriBuilderFactory(uriFactory)
                .build();
        this.maskinportenClient = maskinportenClient;
    }

    public Mono<TenorResponse> getTestdata(String query) {

        return maskinportenClient.getAccessToken()
                .flatMap(token -> new GetTenorTestdata(webClient, query, token.value()).call());
    }
}
