package no.nav.dolly.bestilling.pensjonforvalter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.pensjonforvalter.command.GetPoppInntekterCommand;
import no.nav.dolly.bestilling.pensjonforvalter.command.GetPoppMiljoerCommand;
import no.nav.dolly.config.credentials.PoppTestdataProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.config.NaisServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Set;

import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Service
public class PoppTestdataConsumer implements ConsumerStatus {

    private final TokenExchange tokenService;
    private final WebClient webClient;
    private final NaisServerProperties serviceProperties;

    public PoppTestdataConsumer(TokenExchange tokenService,
                                PoppTestdataProperties serverProperties,
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

    @Timed(name = "providers", tags = {"operation", "popp_getMiljoer"})
    public Set<String> getMiljoer() {

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> new GetPoppMiljoerCommand(webClient, token.getTokenValue()).call())
                .block();
    }

    @Timed(name = "providers", tags = {"operation", "popp_getInntekter"})
    public JsonNode getInntekter(String ident, String miljoe) {

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> new GetPoppInntekterCommand(webClient, token.getTokenValue(), ident, miljoe).call())
                .block();
    }

    @Override
    public String serviceUrl() {
        return null;
    }

    @Override
    public String consumerName() {
        return null;
    }
}
