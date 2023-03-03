package no.nav.dolly.bestilling.inntektsmelding;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.inntektsmelding.command.OpprettInntektsmeldingCommand;
import no.nav.dolly.bestilling.inntektsmelding.domain.InntektsmeldingRequest;
import no.nav.dolly.bestilling.inntektsmelding.domain.InntektsmeldingResponse;
import no.nav.dolly.config.credentials.InntektsmeldingServiceProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.UUID;

import static java.lang.String.format;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;

@Slf4j
@Service
public class InntektsmeldingConsumer implements ConsumerStatus {

    private final TokenExchange tokenService;
    private final WebClient webClient;
    private final ServerProperties serviceProperties;

    public InntektsmeldingConsumer(TokenExchange tokenService,
                                   InntektsmeldingServiceProperties serviceProperties
    ) {

        this.tokenService = tokenService;
        this.serviceProperties = serviceProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serviceProperties.getUrl())
                .build();
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID());
    }

    @Timed(name = "providers", tags = {"operation", "inntektsmelding_opprett"})
    public Flux<InntektsmeldingResponse> postInntektsmelding(InntektsmeldingRequest inntekstsmelding) {

        var callId = getNavCallId();
        log.info("Inntektsmelding med callId {} sendt", callId);

        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> new OpprettInntektsmeldingCommand(webClient,
                        token.getTokenValue(), inntekstsmelding, callId).call());
    }

    @Override
    public String serviceUrl() {
        return serviceProperties.getUrl();
    }

    @Override
    public String consumerName() {
        return "testnav-inntektsmelding-service";
    }

}
