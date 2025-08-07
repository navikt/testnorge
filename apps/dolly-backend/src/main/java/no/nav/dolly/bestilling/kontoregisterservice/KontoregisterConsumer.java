package no.nav.dolly.bestilling.kontoregisterservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.kontoregisterservice.command.KontoregisterDeleteCommand;
import no.nav.dolly.bestilling.kontoregisterservice.command.KontoregisterGetCommand;
import no.nav.dolly.bestilling.kontoregisterservice.command.KontoregisterPostCommand;
import no.nav.dolly.config.Consumers;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.service.CheckAliveService;
import no.nav.testnav.libs.data.kontoregister.v1.HentKontoRequestDTO;
import no.nav.testnav.libs.data.kontoregister.v1.HentKontoResponseDTO;
import no.nav.testnav.libs.data.kontoregister.v1.KontoregisterResponseDTO;
import no.nav.testnav.libs.data.kontoregister.v1.OppdaterKontoRequestDTO;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Service
public class KontoregisterConsumer extends ConsumerStatus {


    private final WebClient webClient;
    private final TokenExchange tokenService;
    private final ServerProperties serverProperties;

    public KontoregisterConsumer(
            TokenExchange tokenService,
            Consumers consumers,
            ObjectMapper objectMapper,
            WebClient webClient,
            CheckAliveService checkAliveService) {

        super(checkAliveService);
        this.tokenService = tokenService;
        serverProperties = consumers.getTestnavKontoregisterPersonProxy();
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .build();
    }

    @Timed(name = "providers", tags = {"operation", "kontoregister_opprettKontonummer"})

    public Mono<KontoregisterResponseDTO> opprettKontonummer(OppdaterKontoRequestDTO bankdetaljer) {

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new KontoregisterPostCommand(webClient, bankdetaljer, token.getTokenValue()).call())
                .doOnNext(response -> log.info("Opprettet kontonummer for ident {} {}", bankdetaljer.getKontohaver(), response));
    }

    @Timed(name = "providers", tags = {"operation", "kontoregister_hentKonto"})
    public Mono<HentKontoResponseDTO> getKontonummer(String ident) {

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new KontoregisterGetCommand(webClient,
                        HentKontoRequestDTO.builder()
                                .kontohaver(ident)
                                .build(),
                        token.getTokenValue()).call())
                .doOnNext(response -> log.info("Hentet kontonummer for ident {} {}", ident, response));
    }

    @Timed(name = "providers", tags = {"operation", "kontoregister_slettKonto"})
    public Flux<KontoregisterResponseDTO> deleteKontonumre(List<String> identer) {

        return tokenService.exchange(serverProperties)
                .flatMapMany(token -> Flux.range(0, identer.size())
                        .delayElements(Duration.ofMillis(10))
                        .flatMap(index -> new KontoregisterDeleteCommand(
                                webClient, identer.get(index), token.getTokenValue()
                        ).call()));
    }

    @Override
    public String serviceUrl() {
        return serverProperties.getUrl();
    }

    @Override
    public String consumerName() {
        return "testnav-kontoregister-person-proxy";
    }
}
