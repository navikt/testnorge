package no.nav.dolly.bestilling.inntektsmelding;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.inntektsmelding.command.OpprettInntektsmeldingCommand;
import no.nav.dolly.bestilling.inntektsmelding.domain.InntektsmeldingResponse;
import no.nav.dolly.config.Consumers;
import no.nav.dolly.metrics.Timed;
import no.nav.testnav.libs.dto.inntektsmeldingservice.v1.requests.InntektsmeldingRequest;
import no.nav.testnav.libs.reactivecore.logging.WebClientLogger;
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
public class InntektsmeldingConsumer extends ConsumerStatus {

    private final TokenExchange tokenService;
    private final WebClient webClient;
    private final ServerProperties serverProperties;

    public InntektsmeldingConsumer(
            TokenExchange tokenService,
            Consumers consumers,
            WebClient webClient,
            WebClientLogger webClientLogger
    ) {
        this.tokenService = tokenService;
        serverProperties = consumers.getTestnavInntektsmeldingService();
        var webClientBuilder = webClient.mutate();
        webClientLogger.customize(webClientBuilder);
        this.webClient = webClientBuilder
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    @Timed(name = "providers", tags = {"operation", "inntektsmelding_opprett"})
    public Flux<InntektsmeldingResponse> postInntektsmelding(InntektsmeldingRequest inntekstsmelding) {

        var callId = getNavCallId();
        log.info("Inntektsmelding med ident {} callId {} sendt {}",
                inntekstsmelding.getArbeidstakerFnr(), callId, inntekstsmelding);

        return tokenService.exchange(serverProperties)
                .flatMapMany(token -> new OpprettInntektsmeldingCommand(webClient,
                        token.getTokenValue(), inntekstsmelding, callId).call());
    }

    @Override
    public String serviceUrl() {
        return serverProperties.getUrl();
    }

    @Override
    public String consumerName() {
        return "testnav-inntektsmelding-service";
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID());
    }

}