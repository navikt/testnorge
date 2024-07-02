package no.nav.registre.testnorge.levendearbeidsforhold.consumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforhold.config.Consumers;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class EndreArbeidsforholdConsumer {
    private final WebClient webClient;
    private final ServerProperties serverProperties;
    private final TokenExchange tokenExchange;

    public EndreArbeidsforholdConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            ObjectMapper objectMapper) {

    serverProperties = consumers.getTestnavAaregProxy();
        log.info("ServerProperties: {}", serverProperties);
        this.tokenExchange = tokenExchange;
    ExchangeStrategies jacksonStrategy = ExchangeStrategies
            .builder()
            .codecs(
                    config -> {
                        config
                                .defaultCodecs()
                                .jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
                        config
                                .defaultCodecs()
                                .jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
                    })
            .build();
        this.webClient = WebClient
            .builder()
            .exchangeStrategies(jacksonStrategy)
                .baseUrl(serverProperties.getUrl())
            .build();
}
}
