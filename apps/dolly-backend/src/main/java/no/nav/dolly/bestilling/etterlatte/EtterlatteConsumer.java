package no.nav.dolly.bestilling.etterlatte;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.etterlatte.command.EtterlattePostCommand;
import no.nav.dolly.bestilling.etterlatte.dto.VedtakRequestDTO;
import no.nav.dolly.bestilling.etterlatte.dto.VedtakResponseDTO;
import no.nav.dolly.config.Consumers;
import no.nav.dolly.metrics.Timed;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class EtterlatteConsumer extends ConsumerStatus {

    private final WebClient webClient;
    private final TokenExchange tokenService;
    private final ServerProperties serverProperties;

    public EtterlatteConsumer(
            TokenExchange tokenService,
            Consumers consumers,
            WebClient.Builder webClientBuilder) {

        this.tokenService = tokenService;
        this.serverProperties = consumers.getEtterlatte();
        this.webClient = webClientBuilder
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    @Timed(name = "providers", tags = {"operation", "etterlatte_createData"})
    public Mono<VedtakResponseDTO> opprettVedtak(VedtakRequestDTO vedtakRequest) {

        log.info("Etterlatte opprett {}", vedtakRequest);
        return tokenService.exchange(serverProperties)
                .flatMap(token -> new EtterlattePostCommand(webClient, vedtakRequest, token.getTokenValue()).call());
    }

    @Override
    public String serviceUrl() {
        return serverProperties.getUrl();
    }

    @Override
    public String consumerName() {
        return "testnav-etterlatte-proxy";
    }
}