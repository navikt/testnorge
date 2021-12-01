package no.nav.dolly.bestilling.tpsmessagingservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.config.credentials.TpsMessagingServiceProperties;
import no.nav.dolly.domain.resultset.tpsmessagingservice.bankkonto.NorskBankkontoRequest;
import no.nav.dolly.domain.resultset.tpsmessagingservice.bankkonto.TpsMessagingResponse;
import no.nav.dolly.domain.resultset.tpsmessagingservice.bankkonto.UtenlandskBankkontoRequest;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.config.NaisServerProperties;
import no.nav.dolly.util.CheckAliveUtil;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.UUID;

import static java.lang.String.format;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Service
public class TpsMessagingConsumer {

    private static final String UTENLANDSK_BANKKONTO_URL = "/api/v1/personer/{ident}/bankkonto-utenlandsk";
    private static final String NORSK_BANKKONTO_URL = "/api/v1/personer/{ident}/bankkonto-norsk";
    private static final String MILJOER_QUERY = "miljoer";

    private final WebClient webClient;
    private final TokenExchange tokenService;
    private final NaisServerProperties serviceProperties;

    public TpsMessagingConsumer(TokenExchange tokenService, TpsMessagingServiceProperties serverProperties, ObjectMapper objectMapper) {
        this.tokenService = tokenService;
        this.serviceProperties = serverProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .build();
    }

    @Timed(name = "providers", tags = { "operation", "tps_messaging_createUtenlandskBankkonto" })
    public ResponseEntity<TpsMessagingResponse> sendUtenlandskBankkontoRequest(UtenlandskBankkontoRequest request) {

        log.info("Sender utenlandsk bankkonto request på ident: {} til TPS messaging service: {}", request.ident(), request.body());

        ResponseEntity<TpsMessagingResponse> response = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(UTENLANDSK_BANKKONTO_URL)
                        .queryParam(MILJOER_QUERY, request.miljoer())
                        .build(request.ident()))
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .bodyValue(request.body())
                .retrieve().toEntity(TpsMessagingResponse.class)
                .block();

        log.info("Response fra TPS messaging service: {}", response);
        return response;
    }

    @Timed(name = "providers", tags = { "operation", "tps_messaging_createNorskBankkonto" })
    public ResponseEntity<TpsMessagingResponse> sendNorskBankkontoRequest(NorskBankkontoRequest request) {

        log.info("Sender norsk bankkonto request på ident: {} til TPS messaging service: {}", request.ident(), request.body());

        ResponseEntity<TpsMessagingResponse> response = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(NORSK_BANKKONTO_URL)
                        .queryParam(MILJOER_QUERY, request.miljoer())
                        .build(request.ident()))
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .bodyValue(request.body())
                .retrieve().toEntity(TpsMessagingResponse.class)
                .block();

        log.info("Response fra TPS messaging service: {}", response);
        return response;
    }

    public Map<String, String> checkAlive() {
        return CheckAliveUtil.checkConsumerAlive(serviceProperties, webClient, tokenService);
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID());
    }
}