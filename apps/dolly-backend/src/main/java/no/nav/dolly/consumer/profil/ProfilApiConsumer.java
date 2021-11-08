package no.nav.dolly.consumer.profil;

import io.swagger.v3.core.util.Json;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.config.credentials.ProfilApiProperties;
import no.nav.dolly.consumer.profil.command.GetProfilCommand;
import no.nav.dolly.security.config.NaisServerProperties;
import no.nav.dolly.util.CheckAliveUtil;
import no.nav.testnav.libs.dto.profil.v1.ProfilDTO;
import no.nav.testnav.libs.servletsecurity.action.GetUserInfo;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.UUID;

import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;

@Slf4j
@Component
public class ProfilApiConsumer {


    private final TokenExchange tokenService;
    private final WebClient webClient;
    private final NaisServerProperties serviceProperties;
    private final GetUserInfo getUserInfo;

    public ProfilApiConsumer(TokenExchange tokenService,
                             ProfilApiProperties serverProperties,
                             GetUserInfo getUserInfo) {
        this.tokenService = tokenService;
        this.serviceProperties = serverProperties;
        this.getUserInfo = getUserInfo;
        this.webClient = WebClient.builder()
                .baseUrl(serverProperties.getUrl()).build();
    }

    public ResponseEntity<ProfilDTO> getProfil() {

        getUserInfo.call().ifPresent(userInfo -> log.info("UserInfo: {}", Json.pretty(userInfo)));

        ResponseEntity<ProfilDTO> response =
                new GetProfilCommand(webClient,
                        serviceProperties.getAccessToken(tokenService),
                        getNavCallId()).call().block();

        if (nonNull(response) && !response.hasBody()) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }
        return response;
    }

    public Map<String, String> checkAlive() {
        return CheckAliveUtil.checkConsumerAlive(serviceProperties, webClient, tokenService);
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID());
    }
}
