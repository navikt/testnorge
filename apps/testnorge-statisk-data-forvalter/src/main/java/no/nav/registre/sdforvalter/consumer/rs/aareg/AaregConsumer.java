package no.nav.registre.sdforvalter.consumer.rs.aareg;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.sdforvalter.config.Consumers;
import no.nav.registre.sdforvalter.consumer.rs.aareg.command.GetArbeidsforholdCommand;
import no.nav.registre.sdforvalter.consumer.rs.aareg.command.PostArbeidsforholdCommand;
import no.nav.registre.sdforvalter.consumer.rs.aareg.response.ArbeidsforholdRespons;
import no.nav.testnav.libs.dto.aareg.v1.Arbeidsforhold;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class AaregConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;

    public AaregConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            WebClient webClient
    ) {
        serverProperties = consumers.getTestnavAaregProxy();
        this.tokenExchange = tokenExchange;
        this.webClient = webClient
                .mutate()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public ArbeidsforholdRespons opprettArbeidsforhold(Arbeidsforhold arbeidsforhold, String miljoe) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(accessToken -> new PostArbeidsforholdCommand(
                        webClient, miljoe, arbeidsforhold, accessToken.getTokenValue()).call())
                .block();
    }

    public ArbeidsforholdRespons hentArbeidsforhold(String ident, String miljoe) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(accessToken -> new GetArbeidsforholdCommand(
                        webClient, miljoe, ident, accessToken.getTokenValue()).call())
                .block();
    }

}
