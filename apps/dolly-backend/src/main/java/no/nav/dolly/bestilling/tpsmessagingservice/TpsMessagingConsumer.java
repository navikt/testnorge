package no.nav.dolly.bestilling.tpsmessagingservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.config.credentials.TpsMessagingServiceProperties;
import no.nav.dolly.domain.resultset.tpsmessagingservice.bankkonto.TpsMessagingRequest;
import no.nav.dolly.domain.resultset.tpsmessagingservice.bankkonto.TpsMessagingResponse;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.config.NaisServerProperties;
import no.nav.dolly.util.CheckAliveUtil;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Service
public class TpsMessagingConsumer {

    private static final String UTENLANDSK_BANKKONTO_URL = "/api/v1/personer/{ident}/bankkonto-utenlandsk";
    private static final String NORSK_BANKKONTO_URL = "/api/v1/personer/{ident}/bankkonto-norsk";
    private static final String SPRAAKKODE_URL = "/api/v1/personer/{ident}/spraakkode";
    private static final String EGENANSATT_URL = "/api/v1/personer/{ident}/egenansatt";
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
    public ResponseEntity<List<TpsMessagingResponse>> sendUtenlandskBankkontoRequest(TpsMessagingRequest request) {

        return sendTpsMessagingRequest(request, UTENLANDSK_BANKKONTO_URL);
    }


    @Timed(name = "providers", tags = { "operation", "tps_messaging_createNorskBankkonto" })
    public ResponseEntity<List<TpsMessagingResponse>> sendNorskBankkontoRequest(TpsMessagingRequest request) {

        return sendTpsMessagingRequest(request, NORSK_BANKKONTO_URL);
    }

    @Timed(name = "providers", tags = { "operation", "tps_messaging_createNorskBankkonto" })
    public ResponseEntity<List<TpsMessagingResponse>> sendEgenansattRequest(TpsMessagingRequest request) {

        return sendTpsMessagingRequest(request, EGENANSATT_URL);
    }

    @Timed(name = "providers", tags = { "operation", "tps_messaging_createNorskBankkonto" })
    public ResponseEntity<List<TpsMessagingResponse>> deleteEgenansattRequest(TpsMessagingRequest request) {

        return sendTpsMessagingRequest(request, EGENANSATT_URL);
    }

    @Timed(name = "providers", tags = { "operation", "tps_messaging_createNorskBankkonto" })
    public ResponseEntity<List<TpsMessagingResponse>> sendSpraakkodeRequest(TpsMessagingRequest request) {

        return sendTpsMessagingRequest(request, SPRAAKKODE_URL);
    }

    public Map<String, String> checkAlive() {
        return CheckAliveUtil.checkConsumerAlive(serviceProperties, webClient, tokenService);
    }

    private ResponseEntity<List<TpsMessagingResponse>> sendTpsMessagingRequest(TpsMessagingRequest request, String urlPath) {

        log.trace("Sender request på ident: {} til TPS messaging service: {}", request.ident(), request.body());

        ResponseEntity<List<TpsMessagingResponse>> response = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(urlPath)
                        .queryParam(MILJOER_QUERY, request.miljoer())
                        .build(request.ident()))
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .bodyValue(request.body())
                .retrieve().toEntityList(TpsMessagingResponse.class)
                .block();

        log.trace("Response fra TPS messaging service: {}", response);
        return response;
    }

}