package no.nav.testnav.apps.tenorsearchservice.consumers;

import no.nav.testnav.apps.tenorsearchservice.config.Consumers;
import no.nav.testnav.apps.tenorsearchservice.consumers.command.GetTenorTestdata;
import no.nav.testnav.apps.tenorsearchservice.consumers.dto.InfoType;
import no.nav.testnav.apps.tenorsearchservice.consumers.dto.Kilde;
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
                .codecs(configurer -> configurer.defaultCodecs()
                        .maxInMemorySize(32 * 1024 * 1024))
                .build();
        this.maskinportenClient = maskinportenClient;
    }

    public Mono<TenorResponse> getTestdata(String query, Kilde kilde, InfoType type, String fields, Integer seed) {

        return getTestdata(query, kilde, type, fields, null, null, seed);
    }

    public Mono<TenorResponse> getTestdata(String query, Kilde kilde, InfoType type, Integer antall, Integer side, Integer seed) {

        return getTestdata(query, kilde, type, null, antall, side, seed);
    }

    public Mono<TenorResponse> getTestdata(String query, Kilde kilde, InfoType type, String fields, Integer antall, Integer side, Integer seed) {

        return maskinportenClient.getAccessToken()
                .flatMap(token -> new GetTenorTestdata(webClient, query, kilde, type, fields, antall, side, seed, token.value()).call());
    }
}