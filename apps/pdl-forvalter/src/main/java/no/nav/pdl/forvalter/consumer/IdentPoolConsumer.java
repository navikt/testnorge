package no.nav.pdl.forvalter.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.config.Consumers;
import no.nav.pdl.forvalter.consumer.command.IdentpoolGetLedigCommand;
import no.nav.pdl.forvalter.consumer.command.IdentpoolGetProdSjekkCommand;
import no.nav.pdl.forvalter.consumer.command.IdentpoolId32DeleteCommand;
import no.nav.pdl.forvalter.consumer.command.IdentpoolId32PostCommand;
import no.nav.pdl.forvalter.consumer.command.IdentpoolPostCommand;
import no.nav.pdl.forvalter.consumer.command.IdentpoolPostVoidCommand;
import no.nav.pdl.forvalter.dto.AllokerIdentRequest;
import no.nav.pdl.forvalter.dto.HentIdenterRequest;
import no.nav.pdl.forvalter.dto.IdentDTO;
import no.nav.pdl.forvalter.dto.IdentpoolLedigDTO;
import no.nav.pdl.forvalter.dto.ProdSjekkDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Slf4j
@Service
public class IdentPoolConsumer {

    private static final String ACQUIRE_IDENTS_URL = "/api/v1/identifikator";
    private static final String RELEASE_IDENTS_URL = ACQUIRE_IDENTS_URL + "/frigjoer";
    private static final String IBRUK_IDENTS_URL = ACQUIRE_IDENTS_URL + "/bruk";
    private static final String REKVIRERT_AV = "rekvirertAv=";

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;

    public IdentPoolConsumer(
            TokenExchange tokenExchange,
            Consumers consumers,
            WebClient webClient) {

        this.tokenExchange = tokenExchange;
        serverProperties = consumers.getIdentPool();
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public Mono<IdentDTO> acquireIdents(HentIdenterRequest request) {

        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> isTrue(request.getId2032()) ?
                        new IdentpoolId32PostCommand(webClient, request, token.getTokenValue()).call() :
                        new IdentpoolPostCommand(webClient, ACQUIRE_IDENTS_URL, null, request,
                                token.getTokenValue()).call()
                                .filter(identer -> !identer.isEmpty())
                                .map(List::getFirst));
    }

    public Mono<List<IdentDTO>> releaseIdents(Set<String> identer, Bruker bruker) {

        return tokenExchange.exchange(serverProperties)
                .flatMapMany(token -> Flux.merge(
                        new IdentpoolPostCommand(webClient, RELEASE_IDENTS_URL, REKVIRERT_AV + bruker, identer,
                                token.getTokenValue()).call()
                                .flatMapMany(Flux::fromIterable),
                        Flux.fromIterable(identer)
                                .filter(PersonDTO::isId2032)
                                .flatMap(ident ->
                                        new IdentpoolId32DeleteCommand(webClient, ident, token.getTokenValue()).call()
                                                .then(Mono.just(IdentDTO.builder().ident(ident).build())))))
                .collectList()
                .doOnNext(resultat -> log.info("Slettet identer mot identpool: {}", String.join(",", identer)));
    }

    public Flux<IdentpoolLedigDTO> getErLedig(Set<String> identer) {

        return tokenExchange.exchange(serverProperties)
                .flatMapMany(token -> Flux.fromIterable(identer)
                        .flatMap(ident ->
                                new IdentpoolGetLedigCommand(webClient, ident, token.getTokenValue()).call()));
    }

    public Flux<ProdSjekkDTO> getProdSjekk(Set<String> identer) {

        return tokenExchange.exchange(serverProperties)
                .flatMapMany(token ->
                        new IdentpoolGetProdSjekkCommand(webClient, identer, token.getTokenValue()).call());
    }

    public Mono<Void> allokerIdent(String ident) {

        return Mono.from(tokenExchange.exchange(serverProperties).flatMap(
                token -> new IdentpoolPostVoidCommand(webClient, IBRUK_IDENTS_URL, null,
                        AllokerIdentRequest.builder()
                                .personidentifikator(ident)
                                .bruker(Bruker.PDLF.name())
                                .build(),
                        token.getTokenValue()).call()));
    }

    public enum Bruker {PDLF, TPSF}
}