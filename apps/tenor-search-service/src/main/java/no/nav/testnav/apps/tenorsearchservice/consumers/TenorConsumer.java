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

    public TenorConsumer(Consumers consumers, MaskinportenConsumer maskinportenConsumer) {

        this.webClient = WebClient
                .builder()
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

        var token = "eyJraWQiOiJiZFhMRVduRGpMSGpwRThPZnl5TUp4UlJLbVo3MUxCOHUxeUREbVBpdVQwIiwiYWxnIjoiUlMyNTYifQ.eyJzY29wZSI6InNrYXR0ZWV0YXRlbjp0ZXN0bm9yZ2UvdGVzdGRhdGEucmVhZCIsImlzcyI6Imh0dHBzOi8vdGVzdC5tYXNraW5wb3J0ZW4ubm8vIiwiY2xpZW50X2FtciI6InByaXZhdGVfa2V5X2p3dCIsInRva2VuX3R5cGUiOiJCZWFyZXIiLCJleHAiOjE3MTQzOTYxMDksImlhdCI6MTcxNDM5NDMwOSwiY2xpZW50X2lkIjoiYzUzYzQ0N2EtZmIzMS00YmYyLWI5YzQtNTk0MjhiZTdhZGRlIiwianRpIjoicm1zWDVzOGJEaG5TNG44NWp2M0Q5ZU5POXVfSEVZQVJRRVZrSER3blR0SSIsImNvbnN1bWVyIjp7ImF1dGhvcml0eSI6ImlzbzY1MjMtYWN0b3JpZC11cGlzIiwiSUQiOiIwMTkyOjg4OTY0MDc4MiJ9fQ.tHj1sG5744ir4O-p28m0UNWHQOeyFH3qpD1iYj-CY230jbvJ7Rkw3LgZ6CjUqGJ1NYEhdYgufwhYRMz8sPALeMNhpwaDmlwIfsQMqkwhENQ2chtIvOXaA_CGmsR8da_R_WC7nFa-zdoQu8la0vVGqcu3tTugTqjGSJzVFCAtbiHkflN0_tyMFaIKhicpJTBiE5mSf2S7MOoj31T7b9ooTEka7EVyhkF0LjC72acI7ELzi3YIKUbjJyQP21DlklokjqSMXXMsavJvH0lwoedu58mKFVnQY6zHtzzEPQ6K08EU7AA1kkK2V0tsW8mSA24Rci5AzozJU6Z7XahLypIdWK1hyydK2G4ts8g4esWciGooxz-ONufDb_3skvvD2q5eY6BHIydS99vRHSTEVS-UED9r-Y1n3EcOTWn9D7_kpNOKJTSIF3ajyjaRs8tI3GDVj3LZ7ZP05meJXNH7XyjOw9y-AZQgtRQuPzHmxQOhAC9edwQib0bG5HJbXLsGc6nR";

        return new GetTenorTestdata(webClient, query, Kilde.FREG, type, fields, antall, side, seed, token).call();

//        return maskinportenConsumer.getAccessToken()
//                .flatMap(token -> new GetTenorTestdata(webClient, query, Kilde.FREG, type, fields, antall, side, seed, token.value()).call());
    }
}