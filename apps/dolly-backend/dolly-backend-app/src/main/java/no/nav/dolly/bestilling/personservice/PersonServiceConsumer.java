package no.nav.dolly.bestilling.personservice;

import no.nav.dolly.bestilling.personservice.command.HentAktoerIdCommand;
import no.nav.dolly.bestilling.personservice.domain.AktoerIdent;
import no.nav.dolly.config.credentials.PersonServiceProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.oauth2.config.NaisServerProperties;
import no.nav.dolly.security.oauth2.service.TokenService;
import no.nav.dolly.util.CheckAliveUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.UUID;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;

@Service
public class PersonServiceConsumer {

    private final TokenService tokenService;
    private final WebClient webClient;
    private final NaisServerProperties serviceProperties;

    public PersonServiceConsumer(TokenService tokenService, PersonServiceProperties serverProperties) {
        this.tokenService = tokenService;
        this.serviceProperties = serverProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serverProperties.getUrl()).build();
    }

    @Timed(name = "providers", tags = { "operation", "aktoerregister_getId" })
    public AktoerIdent getAktoerId(String ident) {

        ResponseEntity<AktoerIdent> response =
                new HentAktoerIdCommand(webClient, serviceProperties.getAccessToken(tokenService), ident, getNavCallId()).call()
                        .block();

        if (isNull(response) || !response.hasBody()) {
            return new AktoerIdent();
        }
        return response.getBody();
    }

    public Map<String, String> checkAlive() {
        return CheckAliveUtil.checkConsumerAlive(serviceProperties, webClient, tokenService);
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID());
    }
}
