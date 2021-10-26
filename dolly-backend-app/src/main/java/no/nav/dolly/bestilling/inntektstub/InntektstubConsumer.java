package no.nav.dolly.bestilling.inntektstub;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.inntektstub.domain.Inntektsinformasjon;
import no.nav.dolly.bestilling.inntektstub.domain.ValiderInntekt;
import no.nav.dolly.config.credentials.InntektstubProxyProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.oauth2.config.NaisServerProperties;
import no.nav.dolly.security.oauth2.service.TokenService;
import no.nav.dolly.util.CheckAliveUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Service
@Slf4j
public class InntektstubConsumer {

    private static final String INNTEKTER_URL = "/api/v2/inntektsinformasjon";
    private static final String DELETE_INNTEKTER_URL = "/api/v2/personer";
    private static final String VALIDER_INNTEKTER_URL = "/api/v2/valider";
    private static final String NORSKE_IDENTER_QUERY = "norske-identer";

    private final WebClient webClient;
    private final TokenService tokenService;
    private final NaisServerProperties serviceProperties;

    public InntektstubConsumer(TokenService tokenService, InntektstubProxyProperties serverProperties, ObjectMapper objectMapper) {
        this.tokenService = tokenService;
        this.serviceProperties = serverProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .build();
    }

    @Timed(name = "providers", tags = { "operation", "inntk_getInntekter" })
    public ResponseEntity<List<Inntektsinformasjon>> getInntekter(String ident) {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(INNTEKTER_URL)
                        .queryParam(NORSKE_IDENTER_QUERY, ident)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .retrieve().toEntityList(Inntektsinformasjon.class)
                .block();
    }

    @Timed(name = "providers", tags = { "operation", "inntk_deleteInntekter" })
    public ResponseEntity<Inntektsinformasjon> deleteInntekter(String ident) {

        return webClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path(DELETE_INNTEKTER_URL)
                        .queryParam(NORSKE_IDENTER_QUERY, ident)
                        .pathSegment(ident).build())
                .header(HttpHeaders.AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .retrieve().toEntity(Inntektsinformasjon.class)
                .block();
    }

    @Timed(name = "providers", tags = { "operation", "inntk_postInntekter" })
    public ResponseEntity<List<Inntektsinformasjon>> postInntekter(List<Inntektsinformasjon> inntektsinformasjon) {

        return
                webClient.post()
                        .uri(uriBuilder -> uriBuilder
                                .path(INNTEKTER_URL)
                                .build())
                        .header(HttpHeaders.AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                        .bodyValue(inntektsinformasjon)
                        .retrieve().toEntityList(Inntektsinformasjon.class)
                        .block();
    }

    @Timed(name = "providers", tags = { "operation", "inntk_validerInntekt" })
    public ResponseEntity<Object> validerInntekter(ValiderInntekt validerInntekt) {

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(VALIDER_INNTEKTER_URL)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .bodyValue(validerInntekt)
                .retrieve().toEntity(Object.class)
                .block();
    }

    public Map<String, String> checkAlive() {
        return CheckAliveUtil.checkConsumerAlive(serviceProperties, webClient, tokenService);
    }
}
