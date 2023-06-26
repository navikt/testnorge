package no.nav.testnav.endringsmeldingservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.testnav.endringsmeldingservice.config.credentias.TpsMessagingServiceProperties;
import no.nav.testnav.endringsmeldingservice.consumer.command.GetIdentEnvironmentsCommand;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Set;

@Component
public class TpsMessagingConsumer {
    private final WebClient webClient;
    private final ServerProperties serverProperties;
    private final TokenExchange accessTokenService;

    public TpsMessagingConsumer(
            TpsMessagingServiceProperties serverProperties,
            TokenExchange tokenExchange,
            ObjectMapper objectMapper) {

        this.serverProperties = serverProperties;
        this.accessTokenService = tokenExchange;

        ExchangeStrategies jacksonStrategy = ExchangeStrategies.builder()
                .codecs(config -> {
                    config.defaultCodecs()
                            .jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
                    config.defaultCodecs()
                            .jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
                }).build();


        this.webClient = WebClient
                .builder()
                .exchangeStrategies(jacksonStrategy)
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public Mono<Set<String>> hentMiljoer(String ident) {
        return accessTokenService
                .exchange(serverProperties)
                .flatMap(accessToken -> new GetIdentEnvironmentsCommand(webClient, ident, accessToken.getTokenValue()).call());
    }
}
