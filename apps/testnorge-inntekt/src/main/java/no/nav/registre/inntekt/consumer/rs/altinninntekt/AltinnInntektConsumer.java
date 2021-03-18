package no.nav.registre.inntekt.consumer.rs.altinninntekt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriTemplate;

import no.nav.registre.inntekt.config.credentials.InntektsmeldingGeneratorServiceProperties;
import no.nav.registre.inntekt.consumer.rs.altinninntekt.command.GenererInntektsmeldingCommand;
import no.nav.registre.inntekt.consumer.rs.altinninntekt.dto.rs.RsInntektsmelding;
import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;

@Slf4j
@Component
public class AltinnInntektConsumer {

    private final WebClient webClient;
    private final AccessTokenService accessTokenService;
    private final NaisServerProperties properties;

    public AltinnInntektConsumer(
            InntektsmeldingGeneratorServiceProperties properties,
            AccessTokenService accessTokenService
    ) {
        this.accessTokenService = accessTokenService;
        this.properties = properties;
        this.webClient = WebClient.builder().baseUrl(properties.getUrl()).build();
    }

    public String getInntektsmeldingXml201812(RsInntektsmelding inntektsmelding) {
        AccessToken accessToken = accessTokenService.generateToken(properties);
        return new GenererInntektsmeldingCommand(webClient, inntektsmelding, accessToken.getTokenValue()).call();
    }
}
