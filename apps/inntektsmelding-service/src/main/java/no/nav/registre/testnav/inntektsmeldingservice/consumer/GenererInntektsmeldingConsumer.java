package no.nav.registre.testnav.inntektsmeldingservice.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnav.inntektsmeldingservice.config.Consumers;
import no.nav.registre.testnav.inntektsmeldingservice.consumer.command.GenererInntektsmeldingCommand;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsInntektsmelding;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class GenererInntektsmeldingConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;

    public GenererInntektsmeldingConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            WebClient.Builder webClientBuilder) {

        this.tokenExchange = tokenExchange;
        serverProperties = consumers.getInntektsmeldingGeneratorService();
        this.webClient = webClientBuilder
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public String getInntektsmeldingXml201812(RsInntektsmelding inntektsmelding) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new GenererInntektsmeldingCommand(webClient, inntektsmelding, token.getTokenValue()).call())
                .block();
    }
}
