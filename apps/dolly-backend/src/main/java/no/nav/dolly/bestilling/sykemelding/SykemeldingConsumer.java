package no.nav.dolly.bestilling.sykemelding;

import io.swagger.v3.core.util.Json;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.sykemelding.command.SykemeldingDeleteCommand;
import no.nav.dolly.bestilling.sykemelding.command.SykemeldingPostCommand;
import no.nav.dolly.bestilling.sykemelding.domain.dto.SykemeldingRequestDTO;
import no.nav.dolly.bestilling.sykemelding.domain.dto.SykemeldingResponseDTO;
import no.nav.dolly.config.Consumers;
import no.nav.dolly.metrics.Timed;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.reactivesecurity.exchange.TokenExchange;
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
        serverProperties = consumers.getTestnavDollyProxy();
        this.webClient = webClient
                .mutate()
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    @Timed(name = "providers", tags = { "operation", "sykemelding_opprett" })
    public Mono<SykemeldingResponseDTO> postSykemelding(SykemeldingRequestDTO sykemeldingRequestDTO) {

        log.info("Sykemelding sendt til dolly-proxy {}", Json.pretty(sykemeldingRequestDTO));

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new SykemeldingPostCommand(webClient, sykemeldingRequestDTO, token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = { "operation", "sykemelding_delete" })
    public Mono<Void> deleteSykemeldinger(String ident) {

        log.info("Sletter nye sykemeldinger for ident: {}", ident);

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new SykemeldingDeleteCommand(webClient, ident, token.getTokenValue()).call());
    }

    @Override
    public String serviceUrl() {
        return serverProperties.getUrl();
    }

    @Override
    public String consumerName() {
        return "testnav-dolly-proxy";
    }
}
