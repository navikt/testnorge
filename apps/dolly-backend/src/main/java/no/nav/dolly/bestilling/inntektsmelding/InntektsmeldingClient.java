package no.nav.dolly.bestilling.inntektsmelding;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.inntektsmelding.domain.TransaksjonMappingDTO;
import no.nav.dolly.config.ApplicationConfig;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.TransaksjonMapping;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.mapper.MappingContextUtils;
import no.nav.dolly.service.TransaksjonMappingService;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.testnav.libs.dto.inntektsmeldingservice.v1.requests.InntektsmeldingRequest;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static no.nav.dolly.domain.resultset.SystemTyper.INNTKMELD;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.encodeStatus;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class InntektsmeldingClient implements ClientRegister {

    private static final String STATUS_FMT = "%s:%s";

    private final InntektsmeldingConsumer inntektsmeldingConsumer;
    private final MapperFacade mapperFacade;
    private final TransaksjonMappingService transaksjonMappingService;
    private final ObjectMapper objectMapper;
    private final TransactionHelperService transactionHelperService;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final ApplicationConfig applicationConfig;

    @Override
    public Flux<ClientFuture> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        return Flux.just(bestilling)
                .filter(RsDollyBestilling::isExistInntekstsmelding)
                .map(RsDollyBestilling::getInntektsmelding)
                .flatMap(inntektsmelding -> Flux.just(bestilling.getEnvironments())
                        .flatMap(miljoer -> Flux.fromIterable(miljoer)
                                .flatMap(environment -> {
                                    var context = MappingContextUtils.getMappingContext();
                                    context.setProperty("ident", dollyPerson.getIdent());
                                    context.setProperty("miljoe", environment);
                                    var request = mapperFacade.map(bestilling.getInntektsmelding(), InntektsmeldingRequest.class, context);
                                    return postInntektsmelding(isOpprettEndre ||
                                                    !transaksjonMappingService.existAlready(INNTKMELD, dollyPerson.getIdent(), environment, bestilling.getId()),
                                            request, bestilling.getId());
                                }))
                        .timeout(Duration.ofSeconds(applicationConfig.getClientTimeout()))
                        .onErrorResume(error -> getErrors(error, bestilling.getEnvironments()))
                        .collect(Collectors.joining(","))
                        .map(status -> futurePersist(progress, status)));
    }

    private Flux<String> getErrors(Throwable error, Set<String> environments) {

        return Flux.fromIterable(environments)
                .map(env -> STATUS_FMT.formatted(env,
                        encodeStatus(WebClientError.describe(error).getMessage())));
    }

    @Override
    public void release(List<String> identer) {

        // Inntektsmelding mangler pt. sletting
    }

    private ClientFuture futurePersist(BestillingProgress progress, String
            status) {

        return () -> {
            transactionHelperService.persister(progress,
                    BestillingProgress::getInntektsmeldingStatus,
                    BestillingProgress::setInntektsmeldingStatus, status);
            return progress;
        };
    }

    private Flux<String> postInntektsmelding(
            boolean isSendMelding,
            InntektsmeldingRequest inntektsmeldingRequest,
            Long bestillingid
    ) {
        final var miljoe = inntektsmeldingRequest.getMiljoe();
        if (isSendMelding) {
            return inntektsmeldingConsumer
                    .postInntektsmelding(inntektsmeldingRequest)
                    .map(response -> {
                        if (isBlank(response.getError())) {
                            var entries = response
                                    .getDokumenter()
                                    .stream()
                                    .map(dokument -> {
                                        var gjeldendeInntektRequest = InntektsmeldingRequest
                                                .builder()
                                                .arbeidstakerFnr(inntektsmeldingRequest.getArbeidstakerFnr())
                                                .inntekter(singletonList(inntektsmeldingRequest.getInntekter().get(response.getDokumenter().indexOf(dokument))))
                                                .joarkMetadata(inntektsmeldingRequest.getJoarkMetadata())
                                                .miljoe(miljoe)
                                                .build();
                                        var json = toJson(TransaksjonMappingDTO
                                                .builder()
                                                .request(gjeldendeInntektRequest)
                                                .dokument(dokument)
                                                .build());
                                        return TransaksjonMapping
                                                .builder()
                                                .ident(inntektsmeldingRequest.getArbeidstakerFnr())
                                                .bestillingId(bestillingid)
                                                .transaksjonId(json)
                                                .datoEndret(LocalDateTime.now())
                                                .miljoe(miljoe)
                                                .system(INNTKMELD.name())
                                                .build();
                                    })
                                    .toList();
                            transaksjonMappingService.saveAll(entries);

                            return miljoe + ":OK";

                        } else {
                            log.error("Feilet å legge inn person: {} til Inntektsmelding miljø: {} feilmelding {}",
                                    inntektsmeldingRequest.getArbeidstakerFnr(), miljoe, response.getError());

                            return String.format(STATUS_FMT, miljoe,
                                    errorStatusDecoder.getErrorText(response.getStatus(), response.getError()));

                        }
                    });
        } else {
            return Flux.just(miljoe + ":OK");
        }
    }

    private String toJson(Object object) {

        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Feilet å konvertere dokument fra inntektsmelding", e);
        }
        return null;
    }

}
