package no.nav.testnav.levendearbeidsforholdansettelse.consumers;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.levendearbeidsforholdansettelse.config.Consumers;
import no.nav.testnav.levendearbeidsforholdansettelse.consumers.command.aareg.HentArbeidsforholdCommand;
import no.nav.testnav.levendearbeidsforholdansettelse.consumers.command.aareg.OpprettArbeidsforholdCommand;
import no.nav.testnav.libs.dto.levendearbeidsforhold.v1.Arbeidsforhold;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

@Slf4j
@Component
public class AaregConsumer {

    private final WebClient webClient;
    private final ServerProperties serverProperties;
    private final TokenExchange tokenExchange;

    public AaregConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            WebClient.Builder webClientBuilder) {

        this.serverProperties = consumers.getTestnavAaregProxy();
        this.tokenExchange = tokenExchange;

        this.webClient = webClientBuilder
                .baseUrl(serverProperties.getUrl())
                .clientConnector(
                        new ReactorClientHttpConnector(
                                HttpClient
                                        .create(ConnectionProvider.builder("AaregConsumer")
                                                .maxConnections(1)
                                                .pendingAcquireMaxCount(10000)
                                                .pendingAcquireTimeout(Duration.ofSeconds(3000))
                                                .build())
                        ))
                .build();
    }

    public Flux<Arbeidsforhold> hentArbeidsforhold(String ident) {

        return Flux.from(tokenExchange.exchange(serverProperties)
                .flatMapMany(token -> new HentArbeidsforholdCommand(webClient, token.getTokenValue(), ident).call()));
    }

    public Flux<HttpStatusCode> opprettArbeidsforhold(Arbeidsforhold requests) {

        return Flux.from(tokenExchange.exchange(serverProperties)
                .flatMap(token -> new OpprettArbeidsforholdCommand(webClient, requests, token.getTokenValue()).call())
                .map(ResponseEntity::getStatusCode));
    }
}