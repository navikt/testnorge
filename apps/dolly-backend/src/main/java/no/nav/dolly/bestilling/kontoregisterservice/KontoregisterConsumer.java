package no.nav.dolly.bestilling.kontoregisterservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.kontoregisterservice.command.KontoregisterDeleteCommand;
import no.nav.dolly.bestilling.kontoregisterservice.command.KontoregisterGetCommand;
import no.nav.dolly.bestilling.kontoregisterservice.command.KontoregisterPostCommand;
import no.nav.dolly.config.credentials.KontoregisterConsumerProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.testnav.libs.dto.kontoregisterservice.v1.HentKontoRequestDTO;
import no.nav.testnav.libs.dto.kontoregisterservice.v1.HentKontoResponseDTO;
import no.nav.testnav.libs.dto.kontoregisterservice.v1.KontoregisterResponseDTO;
import no.nav.testnav.libs.dto.kontoregisterservice.v1.OppdaterKontoRequestDTO;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Service
public class KontoregisterConsumer implements ConsumerStatus {


    private final WebClient webClient;
    private final TokenExchange tokenService;
    private final ServerProperties serviceProperties;

    public KontoregisterConsumer(TokenExchange tokenService,
                                 KontoregisterConsumerProperties serverProperties,
                                 ObjectMapper objectMapper,
                                 ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.tokenService = tokenService;
        this.serviceProperties = serverProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    @Timed(name = "providers", tags = {"operation", "kontoregister_postBankkontoregister"})

    public Mono<KontoregisterResponseDTO> postKontonummerRegister(OppdaterKontoRequestDTO bankdetaljer) {

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> new KontoregisterPostCommand(webClient, bankdetaljer, token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = {"operation", "kontoregister_hentKonto"})
    public Mono<HentKontoResponseDTO> getKontonummer(String ident) {

        var requestDto = new HentKontoRequestDTO(ident, false);

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> new KontoregisterGetCommand(webClient, requestDto, token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = {"operation", "kontoregister_slettKonto"})
    public Flux<KontoregisterResponseDTO> deleteKontonumre(List<String> identer) {

        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> Flux.range(0, identer.size())
                        .delayElements(Duration.ofMillis(10))
                        .flatMap(index -> new KontoregisterDeleteCommand(
                                webClient, identer.get(index), token.getTokenValue()
                        ).call()));

    }

    @Override
    public String serviceUrl() {
        return serviceProperties.getUrl();
    }

    @Override
    public String consumerName() {
        return "testnav-kontoregister-person-proxy";
    }

}
