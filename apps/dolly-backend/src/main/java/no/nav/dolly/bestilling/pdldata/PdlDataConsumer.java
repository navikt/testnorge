package no.nav.dolly.bestilling.pdldata;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pdldata.command.PdlDataCheckIdentCommand;
import no.nav.dolly.bestilling.pdldata.command.PdlDataHentCommand;
import no.nav.dolly.bestilling.pdldata.command.PdlDataOppdateringCommand;
import no.nav.dolly.bestilling.pdldata.command.PdlDataOpprettingCommand;
import no.nav.dolly.bestilling.pdldata.command.PdlDataOrdreCommand;
import no.nav.dolly.bestilling.pdldata.command.PdlDataSlettCommand;
import no.nav.dolly.bestilling.pdldata.command.PdlDataSlettUtenomCommand;
import no.nav.dolly.config.credentials.PdlDataForvalterProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.util.CheckAliveUtil;
import no.nav.dolly.util.JacksonExchangeStrategyUtil;
import no.nav.testnav.libs.dto.pdlforvalter.v1.AvailibilityResponseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BestillingRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullPersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonUpdateRequestDTO;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;

@Slf4j
@Service
public class PdlDataConsumer {

    private static final int BLOCK_SIZE = 10;

    private final TokenExchange tokenService;
    private final WebClient webClient;
    private final PdlDataForvalterProperties serviceProperties;

    public PdlDataConsumer(TokenExchange tokenService, PdlDataForvalterProperties serviceProperties, ObjectMapper objectMapper) {
        this.tokenService = tokenService;
        this.serviceProperties = serviceProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serviceProperties.getUrl())
                .exchangeStrategies(JacksonExchangeStrategyUtil.getJacksonStrategy(objectMapper))
                .build();
    }

    @Timed(name = "providers", tags = {"operation", "pdl_sendOrdre"})
    public String sendOrdre(String ident, boolean isTpsfMaster, boolean ekskluderEksternePersoner) {

        return new PdlDataOrdreCommand(webClient, ident, isTpsfMaster, ekskluderEksternePersoner,
                serviceProperties.getAccessToken(tokenService)).call().block();
    }

    @Timed(name = "providers", tags = {"operation", "pdl_delete"})
    public Mono<List<Void>> slettPdl(List<String> identer) {

        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> Flux.range(0, identer.size())
                        .map(index -> new PdlDataSlettCommand(webClient, identer.get(index), token.getTokenValue()).call())
                        .flatMap(Flux::from))
                .collectList();
    }

    @Timed(name = "providers", tags = {"operation", "pdl_delete_utenom"})
    public Mono<List<Void>> slettPdlUtenom(List<String> identer) {

        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> Flux.range(0, (identer.size() / BLOCK_SIZE) + 1)
                        .map(count -> new PdlDataSlettUtenomCommand(webClient,
                                identer.subList(count * BLOCK_SIZE, Math.min((count + 1) * BLOCK_SIZE, identer.size())),
                                token.getTokenValue()).call())
                        .flatMap(Flux::from))
                .collectList();
    }

    @Timed(name = "providers", tags = {"operation", "pdl_opprett"})
    public String opprettPdl(BestillingRequestDTO request) {

        return new PdlDataOpprettingCommand(webClient, request, serviceProperties.getAccessToken(tokenService)).call().block();
    }

    @Timed(name = "providers", tags = {"operation", "pdl_oppdater"})
    public String oppdaterPdl(String ident, PersonUpdateRequestDTO request) {

        return nonNull(request.getPerson()) ?
                new PdlDataOppdateringCommand(webClient, ident, request, serviceProperties.getAccessToken(tokenService)).call().block()
                : ident;
    }

    public List<FullPersonDTO> getPersoner(List<String> identer) {

        return getPersoner(identer, 0, 10);
    }

    public List<FullPersonDTO> getPersoner(List<String> identer, Integer sidenummer, Integer sidestoerrelse) {

        return List.of(new PdlDataHentCommand(webClient, identer, sidenummer, sidestoerrelse,
                serviceProperties.getAccessToken(tokenService)).call().block());
    }

    @Timed(name = "providers", tags = {"operation", "pdl_identCheck"})
    public List<AvailibilityResponseDTO> identCheck(List<String> identer) {

        return List.of(new PdlDataCheckIdentCommand(webClient, identer, serviceProperties.getAccessToken(tokenService)).call().block());
    }

    @Timed(name = "providers", tags = {"operation", "pdl_dataforvalter_alive"})
    public Map<String, String> checkAlive() {
        return CheckAliveUtil.checkConsumerAlive(serviceProperties, webClient, tokenService);
    }
}
