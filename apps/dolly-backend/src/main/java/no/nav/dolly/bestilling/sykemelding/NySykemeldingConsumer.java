package no.nav.dolly.bestilling.sykemelding;

import tools.jackson.databind.ObjectMapper;
import io.swagger.v3.core.util.Json;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.sykemelding.command.NySykemeldingDeleteCommand;
import no.nav.dolly.bestilling.sykemelding.command.NySykemeldingPostCommand;
import no.nav.dolly.bestilling.sykemelding.domain.dto.NySykemeldingRequestDTO;
import no.nav.dolly.bestilling.sykemelding.domain.dto.NySykemeldingResponseDTO;
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
public class NySykemeldingConsumer extends ConsumerStatus {

    private final WebClient webClient;
    private final TokenExchange tokenService;
    private final ServerProperties serverProperties;

    public NySykemeldingConsumer(
            TokenExchange accessTokenService,
            Consumers consumers,
            ObjectMapper objectMapper,
            WebClient webClient) {

        this.tokenService = accessTokenService;
        serverProperties = consumers.getTestnavSykemeldingProxy();
        this.webClient = webClient
                .mutate()
                .exchangeStrategies(getJacksonStrategy())
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    @Timed(name = "providers", tags = { "operation", "nysykemelding_opprett" })
    public Mono<NySykemeldingResponseDTO> postTsmSykemelding(NySykemeldingRequestDTO nySykemeldingRequestDTO) {

        log.info("Sykemelding sendt til tsm-input-dolly {}", Json.pretty(nySykemeldingRequestDTO));

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new NySykemeldingPostCommand(webClient, nySykemeldingRequestDTO, token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = { "operation", "nysykemelding_delete" })
    public Mono<Void> deleteTsmSykemeldinger(String ident) {

        log.info("Sletter nye sykemeldinger for ident: {}", ident);

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new NySykemeldingDeleteCommand(webClient, ident, token.getTokenValue()).call());
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
