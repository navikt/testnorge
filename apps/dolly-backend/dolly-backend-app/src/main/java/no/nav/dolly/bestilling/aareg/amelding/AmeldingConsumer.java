package no.nav.dolly.bestilling.aareg.amelding;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.config.credentials.AmeldingServiceProperties;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.oauth2.config.NaisServerProperties;
import no.nav.dolly.security.oauth2.service.TokenService;
import no.nav.dolly.util.CheckAliveUtil;
import no.nav.testnav.libs.dto.ameldingservice.v1.AMeldingDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.nonNull;
import static no.nav.dolly.util.CallIdUtil.generateCallId;
import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Service
@Slf4j
public class AmeldingConsumer {

    private final TokenService tokenService;
    private final WebClient webClient;
    private final NaisServerProperties serviceProperties;

    public AmeldingConsumer(TokenService tokenService, AmeldingServiceProperties serviceProperties, ObjectMapper objectMapper) {
        this.tokenService = tokenService;
        this.serviceProperties = serviceProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serviceProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .build();
    }

    public Map<String, ResponseEntity<Void>> putAmeldingList(Map<String, AMeldingDTO> ameldingList, String miljoe) {

        String accessToken = serviceProperties.getAccessToken(tokenService);
        Map<String, ResponseEntity<Void>> ameldingMap = new HashMap<>();

        if (nonNull(accessToken)) {
            ameldingList.values().forEach(amelding ->
            {
                ResponseEntity<Void> response = putAmeldingdata(amelding, miljoe, accessToken);
                ameldingMap.put(amelding.getKalendermaaned().toString(), response);
            });
            return ameldingMap;
        } else
            throw new DollyFunctionalException(String.format("Klarte ikke å hente accessToken for %s", serviceProperties.getName()));
    }

    @Timed(name = "providers", tags = { "operation", "amelding_put" })
    public ResponseEntity<Void> putAmeldingdata(AMeldingDTO amelding, String miljoe, String accessTokenValue) {

        ResponseEntity<Void> response = webClient.put()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/amelding").build())
                .header(HttpHeaders.AUTHORIZATION, accessTokenValue)
                .header("Nav-Call-Id", generateCallId())
                .header("miljo", miljoe)
                .bodyValue(amelding)
                .retrieve()
                .toBodilessEntity().block();

        if (nonNull(response)) {
            return response;
        } else
            throw new DollyFunctionalException("Feil under innsending til Amelding-service");
    }

    public Map<String, String> checkAlive() {
        return CheckAliveUtil.checkConsumerAlive(serviceProperties, webClient, tokenService);
    }
}
