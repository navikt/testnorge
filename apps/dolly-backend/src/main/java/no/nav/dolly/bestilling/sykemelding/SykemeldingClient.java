package no.nav.dolly.bestilling.sykemelding;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.sykemelding.domain.BestillingPersonWrapper;
import no.nav.dolly.bestilling.sykemelding.domain.DetaljertSykemeldingRequest;
import no.nav.dolly.bestilling.sykemelding.domain.SykemeldingTransaksjon;
import no.nav.dolly.bestilling.sykemelding.domain.SyntSykemeldingRequest;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.TransaksjonMapping;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.sykemelding.RsSykemelding.RsDetaljertSykemelding;
import no.nav.dolly.domain.resultset.sykemelding.RsSykemelding.RsSyntSykemelding;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.service.DollyPersonCache;
import no.nav.dolly.service.TransaksjonMappingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.SystemTyper.SYKEMELDING;

@Slf4j
@Service
@RequiredArgsConstructor
public class SykemeldingClient implements ClientRegister {

    private static final String STANDARD_ARBEIDSFORHOLD_ID = "1";

    private final SykemeldingConsumer sykemeldingConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final TransaksjonMappingService transaksjonMappingService;
    private final DollyPersonCache dollyPersonCache;
    private final MapperFacade mapperFacade;
    private final ObjectMapper objectMapper;

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getSykemelding())) {

            try {
                dollyPersonCache.fetchIfEmpty(dollyPerson);

                if (!transaksjonMappingService.existAlready(SYKEMELDING, dollyPerson.getHovedperson(), null) || isOpprettEndre) {

                    if (postSyntSykemelding(bestilling, dollyPerson)) {
                        RsSyntSykemelding syntSykemelding = bestilling.getSykemelding().getSyntSykemelding();
                        saveTransaksjonId(syntSykemelding.getOrgnummer(), syntSykemelding.getArbeidsforholdId(),
                                progress.getBestilling().getId(), dollyPerson.getHovedperson());

                    } else if (postDetaljertSykemelding(bestilling, dollyPerson)) {
                        RsDetaljertSykemelding detaljertSykemelding = bestilling.getSykemelding().getDetaljertSykemelding();
                        saveTransaksjonId(detaljertSykemelding.getMottaker().getOrgNr(), null,
                                progress.getBestilling().getId(), dollyPerson.getHovedperson());
                    }
                    progress.setSykemeldingStatus("OK");
                }
            } catch (RuntimeException e) {

                progress.setSykemeldingStatus(errorStatusDecoder.decodeRuntimeException(e));
            }
        }

    }

    @Override
    public void release(List<String> identer) {

    }

    private boolean postDetaljertSykemelding(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson) {

        if (nonNull(bestilling.getSykemelding().getDetaljertSykemelding())) {
            Person pasient = dollyPerson.getPerson(dollyPerson.getHovedperson());
            DetaljertSykemeldingRequest detaljertSykemeldingRequest = mapperFacade.map(BestillingPersonWrapper.builder()
                    .person(pasient)
                    .sykemelding(bestilling.getSykemelding().getDetaljertSykemelding())
                    .build(),
                    DetaljertSykemeldingRequest.class);

            ResponseEntity<String> responseDetaljert = sykemeldingConsumer.postDetaljertSykemelding(detaljertSykemeldingRequest);
            return HttpStatus.OK.equals(responseDetaljert.getStatusCode());
        }
        return false;
    }

    private boolean postSyntSykemelding(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson) {

        if (nonNull(bestilling.getSykemelding().getSyntSykemelding())) {
            SyntSykemeldingRequest syntSykemeldingRequest = mapperFacade.map(bestilling.getSykemelding().getSyntSykemelding(), SyntSykemeldingRequest.class);
            syntSykemeldingRequest.setIdent(dollyPerson.getHovedperson());
            if (isNull(syntSykemeldingRequest.getArbeidsforholdId())) {
                syntSykemeldingRequest.setArbeidsforholdId(STANDARD_ARBEIDSFORHOLD_ID);
            }

            ResponseEntity<String> response = sykemeldingConsumer.postSyntSykemelding(syntSykemeldingRequest);
            return HttpStatus.OK.equals(response.getStatusCode());
        }
        return false;
    }

    private void saveTransaksjonId(String orgnr, String arbeidsforholdsId, Long bestillingId, String ident) {

        transaksjonMappingService.save(
                TransaksjonMapping.builder()
                        .ident(ident)
                        .bestillingId(bestillingId)
                        .transaksjonId(toJson(SykemeldingTransaksjon.builder()
                                .orgnummer(orgnr)
                                .arbeidsforholdId(arbeidsforholdsId).build()))
                        .datoEndret(LocalDateTime.now())
                        .system(SYKEMELDING.name())
                        .build());
    }

    private String toJson(Object object) {

        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Feilet å konvertere transaksjonsId for sykemelding");
        }
        return null;
    }
}
