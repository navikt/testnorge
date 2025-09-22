package no.nav.dolly.bestilling.sykemelding;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.util.Json;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.sykemelding.command.SyfosmreglerSykemeldingPostCommand;
import no.nav.dolly.bestilling.sykemelding.domain.DetaljertSykemeldingRequest;
import no.nav.dolly.bestilling.sykemelding.dto.SykemeldingResponse;
import no.nav.dolly.config.Consumers;
import no.nav.dolly.metrics.Timed;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Service
public class SykemeldingConsumer extends ConsumerStatus {

    private final WebClient webClient;
    private final TokenExchange tokenService;
    private final ServerProperties serverProperties;
    private final MapperFacade mapperFacade;

    public SykemeldingConsumer(
            TokenExchange accessTokenService,
            Consumers consumers,
            ObjectMapper objectMapper,
            WebClient webClient, MapperFacade mapperFacade) {

        this.tokenService = accessTokenService;
        serverProperties = consumers.getTestnavSykemeldingApi();
        this.mapperFacade = mapperFacade;
        this.webClient = webClient
                .mutate()
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    @Timed(name = "providers", tags = { "operation", "detaljertsykemelding_opprett" })
    public Mono<SykemeldingResponse> postDetaljertSykemelding(DetaljertSykemeldingRequest detaljertSykemeldingRequest) {

        log.info("Detaljert Sykemelding sendt {}", Json.pretty(detaljertSykemeldingRequest));

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new SyfosmreglerSykemeldingPostCommand(webClient, detaljertSykemeldingRequest,
                        token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = { "operation", "detaljertsykemelding_opprett" })
    public Mono<SykemeldingResponse> postTsmSykemelding(DetaljertSykemeldingRequest detaljertSykemeldingRequest) {

        var sykemeldingRequest = mapperFacade.map(detaljertSykemeldingRequest, DetaljertSykemeldingRequest.class);

        log.info("Sykemelding sendt til tsm-input-dolly {}", Json.pretty(detaljertSykemeldingRequest));

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new SyfosmreglerSykemeldingPostCommand(webClient, detaljertSykemeldingRequest,
                        token.getTokenValue()).call());
    }

    @Override
    public String serviceUrl() {
        return serverProperties.getUrl();
    }

    @Override
    public String consumerName() {
        return "testnav-sykemelding-api";
    }
}
