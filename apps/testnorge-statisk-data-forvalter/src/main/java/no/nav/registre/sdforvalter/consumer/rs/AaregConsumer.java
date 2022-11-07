package no.nav.registre.sdforvalter.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.sdforvalter.config.credentials.AaregProperties;
import no.nav.registre.sdforvalter.consumer.rs.command.PostArbeidsforholdCommand;
import no.nav.registre.sdforvalter.consumer.rs.domain.ArbeidsforholdRespons;
import no.nav.testnav.libs.dto.aareg.v1.Arbeidsforhold;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class AaregConsumer {
    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final AaregProperties properties;

    public AaregConsumer(
            AaregProperties serverProperties,
            TokenExchange tokenExchange,
            ExchangeFilterFunction metricsWebClientFilterFunction
    ) {
        this.properties = serverProperties;
        this.tokenExchange = tokenExchange;
        this.webClient = WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .baseUrl(serverProperties.getUrl())
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    public ArbeidsforholdRespons opprettArbeidsforhold(Arbeidsforhold arbeidsforhold, String miljoe) {
        return tokenExchange.exchange(properties)
                .flatMap(accessToken -> new PostArbeidsforholdCommand(
                        webClient, miljoe, arbeidsforhold, accessToken.getTokenValue()).call())
                .block();
    }

}
