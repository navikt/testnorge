package no.nav.dolly.bestilling.sykemelding;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.sykemelding.command.SyntSykemeldingPostCommand;
import no.nav.dolly.bestilling.sykemelding.domain.SyntSykemeldingRequest;
import no.nav.dolly.bestilling.sykemelding.dto.SykemeldingResponse;
import no.nav.dolly.config.credentials.SyntSykemeldingApiProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Service
public class SyntSykemeldingConsumer implements ConsumerStatus {

    private final WebClient webClient;
    private final TokenExchange tokenService;
    private final ServerProperties serviceProperties;

    public SyntSykemeldingConsumer(
            TokenExchange accessTokenService,
            SyntSykemeldingApiProperties serverProperties,
            ObjectMapper objectMapper,
            WebClient.Builder webClientBuilder
    ) {
        this.tokenService = accessTokenService;
        this.serviceProperties = serverProperties;
        this.webClient = webClientBuilder
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .baseUrl(serverProperties.getUrl())
                .clientConnector(
                        new ReactorClientHttpConnector(
                                HttpClient
                                        .create()
                                        .responseTimeout(Duration.ofSeconds(120))
                                        .resolver(spec -> spec.queryTimeout(Duration.ofSeconds(120)))))
                .build();
    }

    @Timed(name = "providers", tags = {"operation", "syntsykemelding_opprett"})
    public Mono<SykemeldingResponse> postSyntSykemelding(SyntSykemeldingRequest sykemeldingRequest) {

        log.info("SyntSykemelding sendt {}", sykemeldingRequest);
        return tokenService.exchange(serviceProperties)
                .flatMap(accessToken -> new
                        SyntSykemeldingPostCommand(webClient, sykemeldingRequest, accessToken.getTokenValue()).call());
    }

    @Override
    public String serviceUrl() {
        return serviceProperties.getUrl();
    }

    @Override
    public String consumerName() {
        return "testnav-synt-sykemelding-api";
    }

}

