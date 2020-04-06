package no.nav.dolly.bestilling.inntektsmelding;

import static java.util.Objects.nonNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.inntektsmelding.domain.InntektsmeldingRequest;
import no.nav.dolly.bestilling.inntektsmelding.domain.InntektsmeldingResponse;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.TransaksjonMapping;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.SystemTyper;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.repository.TransaksjonMappingRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class InntektsmeldingClient implements ClientRegister {

    private final InntektsmeldingConsumer inntektsmeldingConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final MapperFacade mapperFacade;
    private final TransaksjonMappingRepository transaksjonMappingRepository;

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, TpsPerson tpsPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getInntektsmelding())) {

            StringBuilder status = new StringBuilder();
            bestilling.getEnvironments().forEach(environment -> {

                InntektsmeldingRequest inntektsmeldingRequest = mapperFacade.map(bestilling.getInntektsmelding(), InntektsmeldingRequest.class);
                inntektsmeldingRequest.setArbeidstakerFnr(tpsPerson.getHovedperson());
                inntektsmeldingRequest.setMiljoe(environment);

                postInntektsmelding(inntektsmeldingRequest, status);
            });

            progress.setInntektsmeldingStatus(status.length() > 1 ? status.substring(1) : null);
        }
    }

    @Override
    public void release(List<String> identer) {

        // Inntektsmelding mangler pt. sletting
    }

    private void postInntektsmelding(InntektsmeldingRequest inntektsmeldingRequest, StringBuilder status) {

        try {
            ResponseEntity<InntektsmeldingResponse> response = inntektsmeldingConsumer.postInntektsmelding(inntektsmeldingRequest);

            if (response.hasBody()) {
                status.append(',')
                        .append(inntektsmeldingRequest.getMiljoe())
                        .append(":OK");

                transaksjonMappingRepository.saveAll(
                        response.getBody().getDokumenter().stream()
                                .map(dokument ->
                                        TransaksjonMapping.builder()
                                                .ident(inntektsmeldingRequest.getArbeidstakerFnr())
                                                .transaksjonId(dokument.getJournalpostId())
                                                .datoEndret(LocalDateTime.now())
                                                .miljoe(inntektsmeldingRequest.getMiljoe())
                                                .system(SystemTyper.INNTKMELD)
                                                .build())
                                .collect(Collectors.toList())
                );
            }

        } catch (RuntimeException re) {

            status.append(',')
                    .append(inntektsmeldingRequest.getMiljoe())
                    .append(':')
                    .append(errorStatusDecoder.decodeRuntimeException(re));

            log.error("Feilet å legge inn person: {} til Inntektsmelding miljø: {}",
                    inntektsmeldingRequest.getArbeidstakerFnr(), inntektsmeldingRequest.getMiljoe(), re);
        }
    }
}
