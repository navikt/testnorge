package no.nav.pdl.forvalter.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.config.credentials.IdentPoolProperties;
import no.nav.pdl.forvalter.consumer.command.IdentpoolGetCommand;
import no.nav.pdl.forvalter.consumer.command.IdentpoolPostCommand;
import no.nav.pdl.forvalter.consumer.command.IdentpoolPostVoidCommand;
import no.nav.pdl.forvalter.dto.AllokerIdentRequest;
import no.nav.pdl.forvalter.dto.HentIdenterRequest;
import no.nav.pdl.forvalter.dto.IdentDTO;
import no.nav.pdl.forvalter.dto.IdentpoolStatusDTO;
import no.nav.testnav.libs.servletsecurity.config.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class IdentPoolConsumer {

    private static final String ACQUIRE_IDENTS_URL = "/api/v1/identifikator";
    private static final String RELEASE_IDENTS_URL = ACQUIRE_IDENTS_URL + "/frigjoer";
    private static final String IBRUK_IDENTS_URL = ACQUIRE_IDENTS_URL + "/bruk";
    private static final String BRUKER = "PDLF";
    private static final String REKVIRERT_AV = "rekvirertAv=" + BRUKER;

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties properties;

    public IdentPoolConsumer(TokenExchange tokenExchange, IdentPoolProperties properties) {
        this.tokenExchange = tokenExchange;
        this.properties = properties;
        this.webClient = WebClient
                .builder()
                .baseUrl(properties.getUrl())
                .build();
    }

    public Flux<List<IdentDTO>> acquireIdents(HentIdenterRequest request) {

        return Flux.from(tokenExchange.generateToken(properties).flatMap(
                token -> new IdentpoolPostCommand(webClient, ACQUIRE_IDENTS_URL, null, request,
                        token.getTokenValue()).call()));
    }

    public Flux<List<IdentDTO>> releaseIdents(List<String> identer) {

        return Flux.from(tokenExchange.generateToken(properties).flatMap(
                token -> new IdentpoolPostCommand(webClient, RELEASE_IDENTS_URL, REKVIRERT_AV, identer,
                        token.getTokenValue()).call()));
    }

    public Flux<IdentpoolStatusDTO> getIdents(Set<String> identer) {

        return tokenExchange.generateToken(properties)
                .flatMapMany(token -> Flux.concat(identer.stream()
                        .map(ident ->
                                new IdentpoolGetCommand(webClient, ACQUIRE_IDENTS_URL, ident, token.getTokenValue()).call())
                        .collect(Collectors.toList())));

    }

    public Mono<Void> allokerIdent(String ident) {

        return Mono.from(tokenExchange.generateToken(properties).flatMap(
                token -> new IdentpoolPostVoidCommand(webClient, IBRUK_IDENTS_URL, null,
                        AllokerIdentRequest.builder()
                                .personidentifikator(ident)
                                .bruker(BRUKER)
                                .build(),
                        token.getTokenValue()).call()));
    }
}