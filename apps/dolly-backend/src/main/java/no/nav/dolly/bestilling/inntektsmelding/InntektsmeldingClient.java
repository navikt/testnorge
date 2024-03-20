package no.nav.dolly.bestilling.inntektsmelding;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.inntektsmelding.domain.TransaksjonMappingDTO;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.TransaksjonMapping;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.mapper.MappingContextUtils;
import no.nav.dolly.service.TransaksjonMappingService;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.testnav.libs.dto.inntektsmeldingservice.v1.requests.InntektsmeldingRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.SystemTyper.INNTKMELD;
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

    @Override
    public Flux<ClientFuture> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getInntektsmelding())) {

            var context = MappingContextUtils.getMappingContext();
            context.setProperty("ident", dollyPerson.getIdent());

            return Flux.from(
                    Flux.fromIterable(bestilling.getEnvironments())
                            .flatMap(environment -> {
                                var request = mapperFacade.map(bestilling.getInntektsmelding(), InntektsmeldingRequest.class, context);
                                request.setMiljoe(environment);
                                return postInntektsmelding(isOpprettEndre ||
                                                !transaksjonMappingService.existAlready(INNTKMELD, dollyPerson.getIdent(), environment, bestilling.getId()),
                                        request, bestilling.getId());
                            })
                            .filter(StringUtils::isNotBlank)
                            .collect(Collectors.joining(","))
                            .map(status -> futurePersist(progress, status)));
        }
        return Flux.empty();
    }

    @Override
    public void release(List<String> identer) {

        // Inntektsmelding mangler pt. sletting
    }

    private ClientFuture futurePersist(BestillingProgress progress, String status) {

        return () -> {
            transactionHelperService.persister(progress,
                    BestillingProgress::getInntektsmeldingStatus,
                    BestillingProgress::setInntektsmeldingStatus, status);
            return progress;
        };
    }

    private Flux<String> postInntektsmelding(boolean isSendMelding,
                                             InntektsmeldingRequest inntektsmeldingRequest, Long bestillingid) {

        log.info("Sender inntektsmelding for person: {} til miljø: {}", inntektsmeldingRequest.getArbeidstakerFnr(), inntektsmeldingRequest.getMiljoe());
        if (isSendMelding) {
            return inntektsmeldingConsumer.postInntektsmelding(inntektsmeldingRequest)
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
                                                .miljoe(inntektsmeldingRequest.getMiljoe())
                                                .build();
                                        var json = toJson(TransaksjonMappingDTO
                                                .builder()
                                                .request(gjeldendeInntektRequest)
                                                .dokument(dokument)
                                                .build());
                                        log.info("Gjeldended inntektsmelding request for FNR {} har miljø {} og transaksjonId {}", gjeldendeInntektRequest.getArbeidstakerFnr(), gjeldendeInntektRequest.getMiljoe(), json);
                                        return TransaksjonMapping
                                                .builder()
                                                .ident(inntektsmeldingRequest.getArbeidstakerFnr())
                                                .bestillingId(bestillingid)
                                                .transaksjonId(json)
                                                .datoEndret(LocalDateTime.now())
                                                .miljoe(inntektsmeldingRequest.getMiljoe())
                                                .system(INNTKMELD.name())
                                                .build();
                                    })
                                    .toList();
                            log.info("Liste over requests for lagring: {}", entries);
                            transaksjonMappingService.saveAll(entries);

                            return inntektsmeldingRequest.getMiljoe() + ":OK";

                        } else {
                            log.error("Feilet å legge inn person: {} til Inntektsmelding miljø: {} feilmelding {}",
                                    inntektsmeldingRequest.getArbeidstakerFnr(), inntektsmeldingRequest.getMiljoe(), response.getError());

                            return String.format(STATUS_FMT, inntektsmeldingRequest.getMiljoe(),
                                    errorStatusDecoder.getErrorText(response.getStatus(), response.getError()));

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
