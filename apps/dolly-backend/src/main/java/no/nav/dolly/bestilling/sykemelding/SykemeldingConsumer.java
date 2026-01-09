package no.nav.dolly.bestilling.sykemelding;

import io.swagger.v3.core.util.Json;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.sykemelding.command.DetaljertSykemeldingValideringPostCommand;
import no.nav.dolly.bestilling.sykemelding.domain.dto.DetaljertSykemeldingRequestDTO;
import no.nav.dolly.bestilling.sykemelding.domain.dto.DetaljertSykemeldingResponseDTO;
import no.nav.dolly.config.Consumers;
import no.nav.dolly.metrics.Timed;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Service
public class SykemeldingConsumer extends ConsumerStatus {

    private final WebClient webClient;
    private final TokenExchange tokenService;
    private final ServerProperties serverProperties;

    public SykemeldingConsumer(
            TokenExchange accessTokenService,
            Consumers consumers,
            ObjectMapper objectMapper,
            WebClient webClient) {

        this.tokenService = accessTokenService;
        serverProperties = consumers.getTestnavSykemeldingApi();
        this.webClient = webClient
                .mutate()
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    @Timed(name = "providers", tags = { "operation", "detaljertsykemelding_opprett" })
    public Mono<DetaljertSykemeldingResponseDTO> postDetaljertSykemelding(DetaljertSykemeldingRequestDTO detaljertSykemeldingRequestDTO) {

        log.info("Detaljert Sykemelding sendt {}", Json.pretty(detaljertSykemeldingRequestDTO));

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new DetaljertSykemeldingValideringPostCommand(webClient, detaljertSykemeldingRequestDTO,
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
