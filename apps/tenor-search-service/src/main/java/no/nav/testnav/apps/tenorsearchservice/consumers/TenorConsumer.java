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
public class TenorConsumer {

    private final WebClient webClient;
    private final MaskinportenConsumer maskinportenConsumer;

    public TenorConsumer(
            Consumers consumers,
            MaskinportenConsumer maskinportenConsumer,
            WebClient webClient) {
        this.webClient = webClient
                .mutate()
                .baseUrl(consumers.getTenorSearchService().getUrl())
                .codecs(configurer -> configurer.defaultCodecs()
                        .maxInMemorySize(32 * 1024 * 1024))
                .build();
        this.maskinportenConsumer = maskinportenConsumer;
    }

    public Mono<TenorResponse> getTestdata(String query, InfoType type, String fields, Integer seed) {

        return getTestdata(query, type, fields, null, null, seed);
    }

    public Mono<TenorResponse> getTestdata(String query, InfoType type, Integer antall, Integer side, Integer seed) {

        return getTestdata(query, type, null, antall, side, seed);
    }

    public Mono<TenorResponse> getTestdata(String query, InfoType type, String fields, Integer antall, Integer side, Integer seed) {

        return maskinportenConsumer.getAccessToken()
                .flatMap(token -> new GetTenorTestdata(webClient, query, Kilde.FREG, type, fields, antall, side, seed, token.value()).call());
    }
}