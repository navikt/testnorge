package no.nav.dolly.bestilling.pdldata;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.pdldata.command.PdlDataCheckIdentCommand;
import no.nav.dolly.bestilling.pdldata.command.PdlDataHentCommand;
import no.nav.dolly.bestilling.pdldata.command.PdlDataOppdateringCommand;
import no.nav.dolly.bestilling.pdldata.command.PdlDataOpprettingCommand;
import no.nav.dolly.bestilling.pdldata.command.PdlDataOrdreCommand;
import no.nav.dolly.bestilling.pdldata.command.PdlDataSlettCommand;
import no.nav.dolly.bestilling.pdldata.command.PdlDataStanaloneCommand;
import no.nav.dolly.bestilling.pdldata.dto.PdlResponse;
import no.nav.dolly.config.Consumers;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.util.JacksonExchangeStrategyUtil;
import no.nav.dolly.util.RequestTimeout;
import no.nav.testnav.libs.data.pdlforvalter.v1.AvailibilityResponseDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.BestillingRequestDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.FullPersonDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonUpdateRequestDTO;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.List;

@Slf4j
@Service
public class PdlDataConsumer extends ConsumerStatus {

    private final TokenExchange tokenService;
    private final WebClient webClient;
    private final ServerProperties serverProperties;

    public PdlDataConsumer(
            TokenExchange tokenService,
            Consumers consumers,
            ObjectMapper objectMapper,
            WebClient webClient) {

        this.tokenService = tokenService;
        serverProperties = consumers.getTestnavPdlForvalter();
        this.webClient = webClient
                .mutate()
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create()
                        .responseTimeout(Duration.ofSeconds(RequestTimeout.REQUEST_DURATION))))
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(JacksonExchangeStrategyUtil.getJacksonStrategy(objectMapper))
                .build();
    }

    @Timed(name = "providers", tags = {"operation", "pdl_sendOrdre"})
    public Mono<PdlResponse> sendOrdre(String ident, boolean ekskluderEksternePersoner) {

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new PdlDataOrdreCommand(webClient, ident, ekskluderEksternePersoner,
                        token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = {"operation", "pdl_delete"})
    public Mono<List<Void>> slettPdl(List<String> identer) {

        return tokenService.exchange(serverProperties)
                .flatMapMany(token -> Flux.range(0, identer.size())
                        .delayElements(Duration.ofMillis(400))
                        .map(index -> new PdlDataSlettCommand(webClient, identer.get(index), token.getTokenValue()).call())
                        .flatMap(Flux::from))
                .collectList();
    }

    @Timed(name = "providers", tags = {"operation", "pdl_opprett"})
    public Mono<PdlResponse> opprettPdl(BestillingRequestDTO request) {

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new PdlDataOpprettingCommand(webClient, request, token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = {"operation", "pdl_oppdater"})
    public Mono<PdlResponse> oppdaterPdl(String ident, PersonUpdateRequestDTO request) {

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new PdlDataOppdateringCommand(webClient, ident, request, token.getTokenValue()).call());
    }

    public Flux<FullPersonDTO> getPersoner(List<String> identer) {

        return getPersoner(identer, 0, 10);
    }

    public Flux<FullPersonDTO> getPersoner(List<String> identer, Integer sidenummer, Integer sidestoerrelse) {

        return tokenService.exchange(serverProperties)
                .flatMapMany(token -> new PdlDataHentCommand(webClient, identer, sidenummer, sidestoerrelse,
                        token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = {"operation", "pdl_identCheck"})
    public Flux<AvailibilityResponseDTO> identCheck(List<String> identer) {

        return tokenService.exchange(serverProperties)
                .flatMapMany(token -> new PdlDataCheckIdentCommand(webClient, identer, token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = {"operation", "pdl_identer_standalone"})
    public Mono<String> putStandalone(String ident, Boolean standalone) {

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new PdlDataStanaloneCommand(webClient, ident, standalone, token.getTokenValue())
                        .call())
                .doOnNext(response -> log.info("Lagt til ident {} som standalone i PDL-forvalter", ident));
    }

    @Override
    public String serviceUrl() {
        return serverProperties.getUrl();
    }

    @Override
    public String consumerName() {
        return "testnav-pdl-forvalter";
    }

}
