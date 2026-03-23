package no.nav.dolly.bestilling.nom;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.nom.command.NomAvsluttRessurs;
import no.nav.dolly.bestilling.nom.command.NomHentRessurs;
import no.nav.dolly.bestilling.nom.command.NomOpprettRessurs;
import no.nav.dolly.bestilling.nom.domain.NomRessursRequest;
import no.nav.dolly.bestilling.nom.domain.NomRessursResponse;
import no.nav.dolly.config.Consumers;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.reactivesecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Slf4j
@Service
public class NomConsumer {

    private final TokenExchange tokenService;
    private final WebClient webClient;
    private final ServerProperties serverProperties;

    public NomConsumer(
            TokenExchange tokenService,
            Consumers consumers,
            WebClient webClient) {

        this.tokenService = tokenService;
        serverProperties = consumers.getNomProxy();
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public Mono<NomRessursResponse> hentRessurs(String ident) {

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new NomHentRessurs(webClient, ident, token.getTokenValue()).call());
    }

    public Mono<NomRessursResponse> opprettRessurs(NomRessursRequest nomRessursRequest) {

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new NomOpprettRessurs(webClient, nomRessursRequest, token.getTokenValue()).call())
                .doOnNext(ressursResponse -> log.info("Opprettet ressurs i NOM med respons {}", ressursResponse));
    }

    public Mono<NomRessursResponse> avsluttRessurs(String ident, LocalDate sluttdato) {

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new NomAvsluttRessurs(webClient,
                        NomRessursRequest.builder()
                                .personident(ident)
                                .sluttDato(sluttdato)
                                .build(),
                        token.getTokenValue()).call());
    }
}
