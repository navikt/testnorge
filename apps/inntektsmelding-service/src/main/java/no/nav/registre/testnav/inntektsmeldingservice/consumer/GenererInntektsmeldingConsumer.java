package no.nav.registre.testnav.inntektsmeldingservice.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.testnav.inntektsmeldingservice.config.credentials.InntektsmeldingGeneratorServiceProperties;
import no.nav.registre.testnav.inntektsmeldingservice.consumer.command.GenererInntektsmeldingCommand;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsInntektsmelding;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;

@Slf4j
@Component
public class GenererInntektsmeldingConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties properties;

    public GenererInntektsmeldingConsumer(
            InntektsmeldingGeneratorServiceProperties properties,
            TokenExchange tokenExchange
    ) {
        this.tokenExchange = tokenExchange;
        this.properties = properties;
        this.webClient = WebClient.builder().baseUrl(properties.getUrl()).build();
    }

    public String getInntektsmeldingXml201812(RsInntektsmelding inntektsmelding) {
        return tokenExchange.exchange(properties)
                .flatMap(token -> new GenererInntektsmeldingCommand(webClient, inntektsmelding, token.getTokenValue()).call())
                .block();
    }
}
