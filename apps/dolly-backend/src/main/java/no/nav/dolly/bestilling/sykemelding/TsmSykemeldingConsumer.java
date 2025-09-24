package no.nav.dolly.bestilling.sykemelding;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.util.Json;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.sykemelding.command.TsmSykemeldingDeleteCommand;
import no.nav.dolly.bestilling.sykemelding.command.TsmSykemeldingPostCommand;
import no.nav.dolly.bestilling.sykemelding.domain.TsmSykemeldingRequest;
import no.nav.dolly.bestilling.sykemelding.dto.NySykemeldingResponse;
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
public class TsmSykemeldingConsumer extends ConsumerStatus {

    private final WebClient webClient;
    private final TokenExchange tokenService;
    private final ServerProperties serverProperties;

    public TsmSykemeldingConsumer(
            TokenExchange accessTokenService,
            Consumers consumers,
            ObjectMapper objectMapper,
            WebClient webClient) {

        this.tokenService = accessTokenService;
        serverProperties = consumers.getTestnavSykemeldingProxy();
        this.webClient = webClient
                .mutate()
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    @Timed(name = "providers", tags = { "operation", "nysykemelding_opprett" })
    public Mono<NySykemeldingResponse> postTsmSykemelding(TsmSykemeldingRequest tsmSykemeldingRequest) {

        log.info("Sykemelding sendt til tsm-input-dolly {}", Json.pretty(tsmSykemeldingRequest));

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new TsmSykemeldingPostCommand(webClient, tsmSykemeldingRequest, token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = { "operation", "nysykemelding_delete" })
    public Mono<Void> deleteTsmSykemeldinger(String ident) {

        log.info("Sletter sykemeldinger i tsm for ident: {}", ident);

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new TsmSykemeldingDeleteCommand(webClient, ident, token.getTokenValue()).call());
    }

    @Override
    public String serviceUrl() {
        return serverProperties.getUrl();
    }

    @Override
    public String consumerName() {
        return "testnav-sykemelding-proxy";
    }
}
