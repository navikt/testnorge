package no.nav.dolly.bestilling.inntektsmelding;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.inntektsmelding.command.OpprettInntektsmeldingCommand;
import no.nav.dolly.bestilling.inntektsmelding.domain.InntektsmeldingRequest;
import no.nav.dolly.bestilling.inntektsmelding.domain.InntektsmeldingResponse;
import no.nav.dolly.config.credentials.InntektsmeldingServiceProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.config.NaisServerProperties;
import no.nav.dolly.util.CheckAliveUtil;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.UUID;

import static java.lang.String.format;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;

@Slf4j
@Service
public class InntektsmeldingConsumer {

    private final TokenExchange tokenService;
    private final WebClient webClient;
    private final NaisServerProperties serviceProperties;

    public InntektsmeldingConsumer(TokenExchange tokenService,
                                   InntektsmeldingServiceProperties serviceProperties,
                                   ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.tokenService = tokenService;
        this.serviceProperties = serviceProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serviceProperties.getUrl())
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    @Timed(name = "providers", tags = { "operation", "inntektsmelding_opprett" })
    public ResponseEntity<InntektsmeldingResponse> postInntektsmelding(InntektsmeldingRequest inntekstsmelding) {
        String callId = getNavCallId();
        log.info("Inntektsmelding med callId {} sendt", callId);

        return new OpprettInntektsmeldingCommand(webClient, serviceProperties.getAccessToken(tokenService), inntekstsmelding, callId).call()
                .block();
    }

    public Map<String, String> checkAlive() {
        return CheckAliveUtil.checkConsumerAlive(serviceProperties, webClient, tokenService);
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID());
    }

    public Map<String, Object> checkStatus() {
        final String TEAM_DOLLY = "Team Dolly";

        var statusMap =  CheckAliveUtil.checkConsumerStatus(
                serviceProperties.getUrl() + "/internal/isAlive",
                serviceProperties.getUrl() + "/internal/isReady",
                WebClient.builder().build());
        statusMap.put("team", TEAM_DOLLY);

        var inntektsmeldingGeneratorStatus = CheckAliveUtil.checkConsumerStatus(
                "https://testnav-inntektsmelding-generator-service.dev.intern.nav.no/internal/isAlive",
                "https://testnav-inntektsmelding-generator-service.dev.intern.nav.no/internal/isReady",
                WebClient.builder().build());
        inntektsmeldingGeneratorStatus.put("team", TEAM_DOLLY);

        return Map.of(
                "testnav-inntektsmelding-service", statusMap,
                "testnav-inntektsmelding-generator-service", inntektsmeldingGeneratorStatus
        );
    }
}
