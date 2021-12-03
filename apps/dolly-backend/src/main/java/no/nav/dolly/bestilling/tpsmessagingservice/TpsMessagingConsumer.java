package no.nav.dolly.bestilling.tpsmessagingservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.config.credentials.TpsMessagingServiceProperties;
import no.nav.dolly.domain.resultset.tpsmessagingservice.NorskBankkontoRequest;
import no.nav.dolly.domain.resultset.tpsmessagingservice.UtenlandskBankkontoRequest;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.config.NaisServerProperties;
import no.nav.dolly.util.CheckAliveUtil;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TpsIdentStatusDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TpsMeldingResponseDTO;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Service
public class TpsMessagingConsumer {

    private static final String UTENLANDSK_BANKKONTO_URL = "/api/v1/personer/{ident}/bankkonto-utenlandsk";
    private static final String NORSK_BANKKONTO_URL = "/api/v1/personer/{ident}/bankkonto-norsk";
    private static final String IDENTER_URL = "/api/v1/identer";
    private static final String MILJOER_PARAM = "miljoer";
    private static final String IDENTER_PARAM = "identer";

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

    @Timed(name = "providers", tags = {"operation", "tps_messaging_createUtenlandskBankkonto"})
    public List<TpsMeldingResponseDTO> sendUtenlandskBankkontoRequest(UtenlandskBankkontoRequest request) {

        log.trace("Sender utenlandsk bankkonto request på ident: {} til TPS messaging service: {}", request.ident(), request.body());

        var response = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(UTENLANDSK_BANKKONTO_URL)
                        .queryParam(MILJOER_PARAM, request.miljoer())
                        .build(request.ident()))
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .bodyValue(request.body())
                .retrieve()
                .bodyToMono(TpsMeldingResponseDTO[].class)
                .block();

        log.trace("Response fra TPS messaging service: {}", response);
        return Arrays.asList(response);
    }

    @Timed(name = "providers", tags = {"operation", "tps_messaging_createNorskBankkonto"})
    public List<TpsMeldingResponseDTO> sendNorskBankkontoRequest(NorskBankkontoRequest request) {

        log.trace("Sender norsk bankkonto request på ident: {} til TPS messaging service: {}", request.ident(), request.body());

        var response = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(NORSK_BANKKONTO_URL)
                        .queryParam(MILJOER_PARAM, request.miljoer())
                        .build(request.ident()))
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .bodyValue(request.body())
                .retrieve()
                .bodyToMono(TpsMeldingResponseDTO[].class)
                .block();

        log.trace("Response fra TPS messaging service: {}", response);
        return Arrays.asList(response);
    }

    @Timed(name = "providers", tags = {"operation", "tps_messaging_getIdenter"})
    public List<TpsIdentStatusDTO> getIdenter(List<String> identer, List<String> miljoer) {

        return Arrays.asList(webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(IDENTER_URL)
                        .queryParam(MILJOER_PARAM, miljoer)
                        .queryParam(IDENTER_PARAM, identer)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .retrieve()
                .bodyToMono(TpsIdentStatusDTO[].class)
                .block());
    }

    public Map<String, String> checkAlive() {
        return CheckAliveUtil.checkConsumerAlive(serviceProperties, webClient, tokenService);
    }
}