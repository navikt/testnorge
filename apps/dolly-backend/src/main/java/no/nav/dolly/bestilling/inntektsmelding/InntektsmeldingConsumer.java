package no.nav.dolly.bestilling.inntektsmelding;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.inntektsmelding.command.OpprettInntektsmeldingCommand;
import no.nav.dolly.bestilling.inntektsmelding.domain.InntektsmeldingResponse;
import no.nav.dolly.config.Consumers;
import no.nav.dolly.metrics.Timed;
import no.nav.testnav.libs.dto.inntektsmeldingservice.v1.requests.InntektsmeldingRequest;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
            WebClient webClient) {

        this.tokenService = tokenService;
        serverProperties = consumers.getTestnavInntektsmeldingService();
        this.webClient = webClient.mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    @Timed(name = "providers", tags = {"operation", "inntektsmelding_opprett"})
    public Mono<InntektsmeldingResponse> postInntektsmelding(InntektsmeldingRequest inntekstsmelding) {

        var callId = getNavCallId();
        log.info("[INNTEKT-TRACE] Inntektsmelding med ident {} callId {} sendt {}",
                inntekstsmelding.getArbeidstakerFnr(), callId, inntekstsmelding);

        return tokenService.exchange(serverProperties)
                .doOnError(error -> log.error("[INNTEKT-TRACE] Token-exchange feilet for {}: {}",
                        inntekstsmelding.getArbeidstakerFnr(), error.getMessage(), error))
                .switchIfEmpty(Mono.defer(() -> {
                    log.error("[INNTEKT-TRACE] Token-exchange returnerte tomt svar for {} mot {}",
                            inntekstsmelding.getArbeidstakerFnr(), serverProperties.getName());
                    return Mono.empty();
                }))
                .flatMap(token -> {
                    log.info("[INNTEKT-TRACE] Token mottatt for {}, sender request til {}",
                            inntekstsmelding.getArbeidstakerFnr(), serverProperties.getUrl());
                    return new OpprettInntektsmeldingCommand(webClient,
                            token.getTokenValue(), inntekstsmelding, callId).call();
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.error("[INNTEKT-TRACE] Inntektsmelding-request returnerte tomt svar for {}",
                            inntekstsmelding.getArbeidstakerFnr());
                    return Mono.empty();
                }));
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