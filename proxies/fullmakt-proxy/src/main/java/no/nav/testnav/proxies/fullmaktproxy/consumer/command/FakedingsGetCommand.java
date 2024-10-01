package no.nav.testnav.proxies.fullmaktproxy.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class FakedingsGetCommand implements Callable<Mono<String>> {

    private static final String FAKEDINGS_URL = "/fake/tokenx";
    private final WebClient webClient;
    private final String ident;

    @Override
    public Mono<String> call() {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(FAKEDINGS_URL)
                        .queryParam("pid", ident)
                        .queryParam("aud", "dev-fss:pdl:pdl-fullmakt")
                        .queryParam("idp", "https://test.idporten.no")
                        .queryParam("iss", "https://fakedings.intern.dev.nav.no/fake")
                        .queryParam("client_id", "dev-fss:dolly:testnav-fullmakt-proxy")
                        .queryParam("scope", "openid")
                        .queryParam("acr", "Level4")
                        .build())
                .header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(throwable -> log.error("Feil ved henting av fakedings token i fullmakt-proxy", throwable))
                .doOnSuccess(response -> log.info("Fakedings token hentet: {}", response));
    }
}