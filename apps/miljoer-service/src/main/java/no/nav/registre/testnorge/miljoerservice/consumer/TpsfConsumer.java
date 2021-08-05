package no.nav.registre.testnorge.miljoerservice.consumer;

import static java.util.Objects.isNull;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;

import no.nav.testnav.libs.servletsecurity.config.NaisServerProperties;
import no.nav.testnav.libs.servletsecurity.service.AccessTokenService;
import no.nav.registre.testnorge.miljoerservice.config.credentias.TpsForvalterenProxyServiceProperties;
import no.nav.registre.testnorge.miljoerservice.response.MiljoerResponse;

@Service
@Slf4j
public class TpsfConsumer {

    private final WebClient webClient;
    private final NaisServerProperties serverProperties;
    private final AccessTokenService accessTokenService;

    public TpsfConsumer(
            TpsForvalterenProxyServiceProperties serverProperties,
            AccessTokenService accessTokenService
    ) {
        this.serverProperties = serverProperties;
        this.accessTokenService = accessTokenService;

        this.webClient = WebClient
                .builder()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public MiljoerResponse getAktiveMiljoer() {
        log.info("Henter aktive miljøer fra TPSF.");
        var accessToken = accessTokenService.generateToken(serverProperties).block();
        ResponseEntity<MiljoerResponse> response = webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/environments").build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken.getTokenValue())
                .retrieve()
                .toEntity(MiljoerResponse.class)
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(3)))
                .block();

        if (response == null || isNull(response.getBody()) || isNull(response.getBody().getEnvironments())) {
            throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Fant ingen miljøer");
        }

        response.getBody().getEnvironments().sort(String::compareTo);

        return response.getBody();
    }
}
