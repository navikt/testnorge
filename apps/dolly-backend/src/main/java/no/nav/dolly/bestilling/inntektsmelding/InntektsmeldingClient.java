package no.nav.dolly.bestilling.inntektsmelding;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.inntektsmelding.domain.InntektsmeldingRequest;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.TransaksjonMapping;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.mapper.MappingContextUtils;
import no.nav.dolly.service.TransaksjonMappingService;
import no.nav.dolly.util.TransactionHelperService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.SystemTyper.INNTKMELD;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.encodeStatus;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getInfoVenter;
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

    @Override
    public Flux<ClientFuture> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getInntektsmelding())) {

            progress.setInntektsmeldingStatus(bestilling.getEnvironments().stream()
                    .map(miljo -> String.format(STATUS_FMT, miljo, getInfoVenter("JOARK")))
                    .collect(Collectors.joining(",")));
            transactionHelperService.persister(progress);

            var context = MappingContextUtils.getMappingContext();
            context.setProperty("ident", dollyPerson.getHovedperson());

            var inntektsmeldingRequest = mapperFacade.map(bestilling.getInntektsmelding(), InntektsmeldingRequest.class, context);

            return Flux.from(
                    Flux.fromIterable(bestilling.getEnvironments())
                            .flatMap(environment -> {

                                inntektsmeldingRequest.setMiljoe(environment);
                                return postInntektsmelding(isOpprettEndre ||
                                                !transaksjonMappingService.existAlready(INNTKMELD, dollyPerson.getHovedperson(), environment),
                                        inntektsmeldingRequest, progress.getBestilling().getId());
                            })
                            .filter(StringUtils::isNotBlank)
                            .collect(Collectors.joining(","))
                            .map(status -> futurePersist(progress, status)));
        }
        return Flux.empty();
    }

    private ClientFuture futurePersist(BestillingProgress progress, String status) {

        return () -> {
            progress.setInntektsmeldingStatus(status);
            transactionHelperService.persister(progress);
            return progress;
        };
    }

    @Override
    public void release(List<String> identer) {

        // Inntektsmelding mangler pt. sletting
    }

    private Flux<String> postInntektsmelding(boolean isSendMelding,
                                             InntektsmeldingRequest inntektsmeldingRequest, Long bestillingid) {

        if (isSendMelding) {
            return inntektsmeldingConsumer.postInntektsmelding(inntektsmeldingRequest)
                    .map(response -> {
                        if (isBlank(response.getError())) {

                            transaksjonMappingService.saveAll(
                                    response.getDokumenter().stream()
                                            .map(dokument -> TransaksjonMapping.builder()
                                                    .ident(inntektsmeldingRequest.getArbeidstakerFnr())
                                                    .bestillingId(bestillingid)
                                                    .transaksjonId(toJson(dokument))
                                                    .datoEndret(LocalDateTime.now())
                                                    .miljoe(inntektsmeldingRequest.getMiljoe())
                                                    .system(INNTKMELD.name())
                                                    .build())
                                            .toList());

                            return inntektsmeldingRequest.getMiljoe() + ":OK";

                        } else {
                            log.error("Feilet å legge inn person: {} til Inntektsmelding miljø: {} feilmelding {}",
                                    inntektsmeldingRequest.getArbeidstakerFnr(), inntektsmeldingRequest.getMiljoe(), response.getError());

                            return String.format(STATUS_FMT, inntektsmeldingRequest.getMiljoe(), encodeStatus(response.getError()));

                        }
                    });
        } else {
            return Flux.just(inntektsmeldingRequest.getMiljoe() + ":OK");
        }
    }

    private String toJson(Object object) {

        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Feilet å konvertere domument fra inntektsmelding", e);
        }
        return null;
    }

}
