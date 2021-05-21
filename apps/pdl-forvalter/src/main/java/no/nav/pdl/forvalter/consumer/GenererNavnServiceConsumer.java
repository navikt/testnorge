package no.nav.pdl.forvalter.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.config.credentials.GenererNavnServiceProperties;
import no.nav.pdl.forvalter.consumer.command.GenererNavnServiceCommand;
import no.nav.pdl.forvalter.consumer.command.VerifiserNavnServiceCommand;
import no.nav.registre.testnorge.libs.dto.generernavnservice.v1.NavnDTO;
import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Service
public class GenererNavnServiceConsumer {

    private static final String NAVN_URL = "/api/v1/navn";
    private static final String NAVN_CHECK_URL = NAVN_URL + "/check";

    private final WebClient webClient;
    private final AccessTokenService accessTokenService;
    private final NaisServerProperties properties;

    public GenererNavnServiceConsumer(AccessTokenService accessTokenService, GenererNavnServiceProperties properties) {
        this.accessTokenService = accessTokenService;
        this.properties = properties;
        this.webClient = WebClient
                .builder()
                .baseUrl(properties.getUrl())
                .build();
    }

    public Optional<NavnDTO> getNavn(Integer antall) {
        var accessToken = accessTokenService.generateToken(properties);
        return Arrays.asList(new GenererNavnServiceCommand(webClient, NAVN_URL, antall, accessToken.getTokenValue()).call())
                .stream().findFirst();
    }

    public Boolean verifyNavn(NavnDTO navn) {
        var accessToken = accessTokenService.generateToken(properties);
        return new VerifiserNavnServiceCommand(webClient, NAVN_CHECK_URL, navn, accessToken.getTokenValue()).call();
    }
}
