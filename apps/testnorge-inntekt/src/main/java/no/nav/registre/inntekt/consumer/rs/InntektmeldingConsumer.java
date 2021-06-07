package no.nav.registre.inntekt.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.inntekt.config.credentials.InntektsmeldingServiceProperties;
import no.nav.registre.inntekt.consumer.rs.command.SaveInntektsmeldingCommand;
import no.nav.registre.testnorge.libs.dto.inntektsmeldingservice.v1.requests.InntektsmeldingRequest;
import no.nav.registre.testnorge.libs.dto.inntektsmeldingservice.v1.response.InntektsmeldingResponse;
import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;

@Slf4j
@Component
public class InntektmeldingConsumer {

    private final WebClient webClient;
    private final AccessTokenService accessTokenService;
    private final NaisServerProperties properties;

    public InntektmeldingConsumer(
            InntektsmeldingServiceProperties properties,
            AccessTokenService accessTokenService
    ) {
        this.accessTokenService = accessTokenService;
        this.properties = properties;
        this.webClient = WebClient.builder().baseUrl(properties.getUrl()).build();
    }

    public InntektsmeldingResponse opprett(InntektsmeldingRequest request, String navCallId){
        return accessTokenService.generateNonBlockedToken(properties)
                .flatMap(token -> new SaveInntektsmeldingCommand(webClient, request, token.getTokenValue(), navCallId).call())
                .block();
    }

}
