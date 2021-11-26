package no.nav.testnav.apps.personservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.personservice.consumer.command.GetTpsPersonCommand;
import no.nav.testnav.apps.personservice.credentials.TpsForvalterenProxyServiceProperties;
import no.nav.testnav.apps.personservice.domain.Person;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@Component
public class TpsForvalterenConsumer {

    private final WebClient webClient;
    private final TpsForvalterenProxyServiceProperties serviceProperties;
    private final TokenExchange tokenExchange;

    public TpsForvalterenConsumer(
            TpsForvalterenProxyServiceProperties serviceProperties,
            TokenExchange accessTokenService,
            ObjectMapper objectMapper
    ) {
        this.serviceProperties = serviceProperties;
        this.tokenExchange = accessTokenService;
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
                .baseUrl(serviceProperties.getUrl())
                .build();
    }

    public Mono<Optional<Person>> getPerson(String ident, String miljoe) {
        return tokenExchange
                .generateToken(serviceProperties)
                .flatMap(token -> new GetTpsPersonCommand(webClient, token.getTokenValue(), ident, miljoe).call())
                .map(value -> value.map(response -> new Person(response.getPerson())));
    }
}
