package no.nav.dolly.bestilling.sykemelding;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.sykemelding.command.SykemeldingPostCommand;
import no.nav.dolly.bestilling.sykemelding.domain.DetaljertSykemeldingRequest;
import no.nav.dolly.bestilling.sykemelding.dto.SykemeldingResponse;
import no.nav.dolly.config.credentials.SykemeldingApiProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Service
public class SykemeldingConsumer implements ConsumerStatus {

    private final WebClient webClient;
    private final TokenExchange tokenService;
    private final ServerProperties serviceProperties;

    public SykemeldingConsumer(
            TokenExchange accessTokenService,
            SykemeldingApiProperties serverProperties,
            ObjectMapper objectMapper,
            WebClient.Builder webClientBuilder
    ) {
        this.tokenService = accessTokenService;
        this.serviceProperties = serverProperties;
        this.webClient = webClientBuilder
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    @Timed(name = "providers", tags = { "operation", "detaljertsykemelding_opprett" })
    public Mono<SykemeldingResponse> postDetaljertSykemelding(DetaljertSykemeldingRequest detaljertSykemeldingRequest) {

        log.info("Detaljert Sykemelding sendt {}", detaljertSykemeldingRequest);

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> new SykemeldingPostCommand(webClient, detaljertSykemeldingRequest,
                        token.getTokenValue()).call());
    }

    @Override
    public String serviceUrl() {
        return serviceProperties.getUrl();
    }

    @Override
    public String consumerName() {
        return "testnorge-sykemelding-api";
    }
}
