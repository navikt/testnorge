package no.nav.dolly.bestilling.aareg.amelding;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.aareg.amelding.domain.Ordre;
import no.nav.dolly.config.credentials.AmeldingServiceProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.config.NaisServerProperties;
import no.nav.dolly.util.CheckAliveUtil;
import no.nav.testnav.libs.dto.ameldingservice.v1.AMeldingDTO;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static no.nav.dolly.util.CallIdUtil.generateCallId;
import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@Service
@Slf4j
public class AmeldingConsumer {

    private final TokenExchange tokenService;
    private final WebClient webClient;
    private final NaisServerProperties serviceProperties;

    public AmeldingConsumer(TokenExchange tokenService, AmeldingServiceProperties serviceProperties, ObjectMapper objectMapper) {
        this.tokenService = tokenService;
        this.serviceProperties = serviceProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serviceProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .build();
    }

    public Flux<Map<String, ResponseEntity<Void>>> createOrder(List<AMeldingDTO> ameldinger, String miljo) {
        String userJwt = getUserJwt();
        List<Ordre> orders = ameldinger
                .stream()
                .map(aMelding -> (Ordre) accessToken -> putAmeldingdata(aMelding, miljo, accessToken, userJwt)
                        .map(response -> Map.of(response.getKey(), response.getValue())))
                .toList();
        return sendOrder(orders);
    }

    public Flux<Map<String, ResponseEntity<Void>>> sendOrder(List<Ordre> orders) {
        String accessToken = serviceProperties.getAccessToken(tokenService);
        return Flux.concat(orders
                .stream()
                .map(order -> order.apply(accessToken))
                .collect(Collectors.toList())
        );
    }

    @Timed(name = "providers", tags = { "operation", "amelding_put" })
    public Mono<Entry<String, ResponseEntity<Void>>> putAmeldingdata(AMeldingDTO amelding, String miljoe, String accessTokenValue, String userJwt) {

        return webClient.put()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/amelding").build())
                .header(HttpHeaders.AUTHORIZATION, accessTokenValue)
                .header(UserConstant.USER_HEADER_JWT, userJwt)
                .header("Nav-Call-Id", generateCallId())
                .header("miljo", miljoe)
                .bodyValue(amelding)
                .retrieve().toBodilessEntity().map(response -> Map.entry(miljoe, response));
    }

    public Map<String, String> checkAlive() {
        return CheckAliveUtil.checkConsumerAlive(serviceProperties, webClient, tokenService);
    }
}
