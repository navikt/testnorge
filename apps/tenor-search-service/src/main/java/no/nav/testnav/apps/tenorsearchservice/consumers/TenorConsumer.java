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

        var token = "eyJraWQiOiJiZFhMRVduRGpMSGpwRThPZnl5TUp4UlJLbVo3MUxCOHUxeUREbVBpdVQwIiwiYWxnIjoiUlMyNTYifQ.eyJzY29wZSI6InNrYXR0ZWV0YXRlbjp0ZXN0bm9yZ2UvdGVzdGRhdGEucmVhZCIsImlzcyI6Imh0dHBzOi8vdGVzdC5tYXNraW5wb3J0ZW4ubm8vIiwiY2xpZW50X2FtciI6InByaXZhdGVfa2V5X2p3dCIsInRva2VuX3R5cGUiOiJCZWFyZXIiLCJleHAiOjE3MTQ2NTMzNzMsImlhdCI6MTcxNDY1MTU3MywiY2xpZW50X2lkIjoiYzUzYzQ0N2EtZmIzMS00YmYyLWI5YzQtNTk0MjhiZTdhZGRlIiwianRpIjoiT3F0OHBJT09YVXQ3emltQ09qTmxwcl8ybXhCQ1E5RjdPNTNfVDVkSk5SNCIsImNvbnN1bWVyIjp7ImF1dGhvcml0eSI6ImlzbzY1MjMtYWN0b3JpZC11cGlzIiwiSUQiOiIwMTkyOjg4OTY0MDc4MiJ9fQ.ny_j4ekAp2QDn0XkCKRiKeHNN0bzX7ZBq_vKpbln8fVG6R88JpXR3UxJpZe2xSLfwdCp7rmdnLDN09Zff5BMyCISFphCwUjiaaPdAvyu_JIl70LBC3uSjlIFKOp3YAVM_mSlfH2-9t1ZM7ZjXiyX8PMs7c8czI4DyAYBKUZOzMJLKG1__yY9BqnCoRNMjea4JMQohsUBRmabtZiu7L2nJ6_To1YV78XFr0elEO7FRYurCwW8Pl13CnsbF7g2JlTXFA4NLTHEOtW45RXUIHeJfyhGUU4gAY4fCImZA7DIpycO8vL8QF71VW2nmlO4Jyc9Zw6Vb8nDm-PjeOnr4yqHFw1_TI3xjL3SUCo_EwArsdXERhUsPSeLo7m0tmLKJKe5angYrheRD7-ZbLVdzKtIYMdx9iq1s7W8r_kOwOL6CZX8HBXJxMSDDW3BtaKbAwNGHxBxJA97TfPuGfXaannEkLx3O-FlHYdkzz5Pc2So3HNJ1qpTA4_Z6IXqvHthmVj6";

        return new GetTenorTestdata(webClient, query, Kilde.FREG, type, fields, antall, side, seed, token).call();
        //        return maskinportenConsumer.getAccessToken()
//                .flatMap(token -> new GetTenorTestdata(webClient, query, Kilde.FREG, type, fields, antall, side, seed, token.value()).call());
    }
}