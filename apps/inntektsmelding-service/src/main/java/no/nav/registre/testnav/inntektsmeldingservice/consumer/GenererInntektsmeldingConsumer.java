package no.nav.registre.testnav.inntektsmeldingservice.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.testnav.inntektsmeldingservice.config.credentials.InntektsmeldingGeneratorServiceProperties;
import no.nav.registre.testnav.inntektsmeldingservice.consumer.command.GenererInntektsmeldingCommand;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsInntektsmelding;
import no.nav.testnav.libs.servletsecurity.config.NaisServerProperties;
import no.nav.testnav.libs.servletsecurity.service.AccessTokenService;

@Slf4j
@Component
public class GenererInntektsmeldingConsumer {

    private final WebClient webClient;
    private final AccessTokenService accessTokenService;
    private final NaisServerProperties properties;

    public GenererInntektsmeldingConsumer(
            InntektsmeldingGeneratorServiceProperties properties,
            AccessTokenService accessTokenService
    ) {
        this.accessTokenService = accessTokenService;
        this.properties = properties;
        this.webClient = WebClient.builder().baseUrl(properties.getUrl()).build();
    }

    public String getInntektsmeldingXml201812(RsInntektsmelding inntektsmelding) {
        return accessTokenService.generateToken(properties)
                .flatMap(token -> new GenererInntektsmeldingCommand(webClient, inntektsmelding, token.getTokenValue()).call())
                .block();
    }
}
