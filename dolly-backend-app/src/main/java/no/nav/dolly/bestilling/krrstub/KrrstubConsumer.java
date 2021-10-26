package no.nav.dolly.bestilling.krrstub;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.config.credentials.KrrstubProxyProperties;
import no.nav.dolly.domain.resultset.krrstub.DigitalKontaktdata;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.oauth2.config.NaisServerProperties;
import no.nav.dolly.security.oauth2.service.TokenService;
import no.nav.dolly.util.CheckAliveUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.lang.String.format;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_PERSON_IDENT;
import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Service
public class KrrstubConsumer {

    private static final String DIGITAL_KONTAKT_URL = "/api/v1/kontaktinformasjon";
    private static final String PERSON_DIGITAL_KONTAKT_URL = "/api/v1/person/kontaktinformasjon";

    private final WebClient webClient;
    private final TokenService tokenService;
    private final NaisServerProperties serviceProperties;

    public KrrstubConsumer(TokenService tokenService, KrrstubProxyProperties serverProperties, ObjectMapper objectMapper) {
        this.tokenService = tokenService;
        this.serviceProperties = serverProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .build();
    }

    @Timed(name = "providers", tags = { "operation", "krrstub_getKontaktdata" })
    public ResponseEntity<List<DigitalKontaktdata>> getDigitalKontaktdata(String ident) {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(PERSON_DIGITAL_KONTAKT_URL)
                        .build())
                .header(HEADER_NAV_CALL_ID, getNavCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header(HEADER_NAV_PERSON_IDENT, ident)
                .header(HttpHeaders.AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .retrieve().toEntityList(DigitalKontaktdata.class)
                .block();
    }

    @Timed(name = "providers", tags = { "operation", "krrstub_createKontaktdata" })
    public ResponseEntity<Object> createDigitalKontaktdata(DigitalKontaktdata digitalKontaktdata) {

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(DIGITAL_KONTAKT_URL)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .header(HEADER_NAV_CALL_ID, getNavCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header(HttpHeaders.AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .bodyValue(digitalKontaktdata)
                .retrieve().toEntity(Object.class)
                .block();
    }

    @Timed(name = "providers", tags = { "operation", "krrstub_deleteKontaktdata" })
    public ResponseEntity<Object> deleteDigitalKontaktdata(Long id) {

        return webClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path(DIGITAL_KONTAKT_URL)
                        .pathSegment(id.toString())
                        .build())
                .header(HEADER_NAV_CALL_ID, getNavCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header(HttpHeaders.AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .retrieve().toEntity(Object.class)
                .block();
    }

    public Map<String, String> checkAlive() {
        return CheckAliveUtil.checkConsumerAlive(serviceProperties, webClient, tokenService);
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID());
    }
}