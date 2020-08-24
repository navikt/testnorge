package no.nav.dolly.bestilling.sykemelding;

import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.SystemTyper.SYKEMELDING;
import static org.apache.cxf.common.util.PropertyUtils.isTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.service.TpsfPersonCache;
import no.nav.dolly.service.TransaksjonMappingService;

@Slf4j
@Service
@RequiredArgsConstructor
public class SykemeldingClient implements ClientRegister {

    private final SykemeldingConsumer sykemeldingConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final TransaksjonMappingService transaksjonMappingService;
    private final TpsfPersonCache tpsfPersonCache;
    private final MapperFacade mapperFacade;
    private final ObjectMapper objectMapper;

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, TpsPerson tpsPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getSykemelding())) {

            try {
                tpsfPersonCache.fetchIfEmpty(tpsPerson);

                if (!transaksjonMappingService.existAlready(SYKEMELDING, tpsPerson.getHovedperson(), null) || isOpprettEndre) {

                    boolean syntOk = postSyntSykemelding(bestilling, tpsPerson);
                    boolean detaljertOk = postDetaljertSykemelding(bestilling, tpsPerson);

                    if (isTrue(syntOk) || isTrue(detaljertOk)) {
                        progress.setSykemeldingStatus("OK");

                        if (isTrue(syntOk)) {
                            RsSyntSykemelding syntSykemelding = bestilling.getSykemelding().getSyntSykemelding();
                            saveTranskasjonId(
                                    syntSykemelding.getOrgnummer(),
                                    syntSykemelding.getArbeidsforholdId(),
                                    progress.getBestillingId(),
                                    tpsPerson.getHovedperson());
                        } else {
                            RsDetaljertSykemelding detaljertSykemelding = bestilling.getSykemelding().getDetaljertSykemelding();
                            saveTranskasjonId(
                                    detaljertSykemelding.getMottaker().getOrgNr(),
                                    "1",
                                    progress.getBestillingId(),
                                    tpsPerson.getHovedperson());
                        }
                    }
                }
            } catch (RuntimeException e) {

                progress.setSykemeldingStatus(errorStatusDecoder.decodeRuntimeException(e));
            }
        }
    }

    @Override
    public void release(List<String> identer) {

    }

    private boolean postDetaljertSykemelding(RsDollyUtvidetBestilling bestilling, TpsPerson tpsPerson) {

        if (nonNull(bestilling.getSykemelding().getDetaljertSykemelding())) {
            Person pasient = tpsPerson.getPerson(tpsPerson.getHovedperson());
            DetaljertSykemeldingRequest detaljertSykemeldingRequest = mapperFacade.map(BestillingPersonWrapper.builder()
                    .person(pasient)
                    .sykemelding(bestilling.getSykemelding().getDetaljertSykemelding())
                    .build(),
                    DetaljertSykemeldingRequest.class);

            ResponseEntity<String> responseDetaljert = sykemeldingConsumer.postDetaljertSykemelding(detaljertSykemeldingRequest);
            return responseDetaljert.getStatusCode().equals(HttpStatus.OK);
        }
        return false;
    }

    private boolean postSyntSykemelding(RsDollyUtvidetBestilling bestilling, TpsPerson tpsPerson) {

        if (nonNull(bestilling.getSykemelding().getSyntSykemelding())) {
            SyntSykemeldingRequest syntSykemeldingRequest = mapperFacade.map(bestilling.getSykemelding().getSyntSykemelding(), SyntSykemeldingRequest.class);
            syntSykemeldingRequest.setIdent(tpsPerson.getHovedperson());

            ResponseEntity<String> response = sykemeldingConsumer.postSyntSykemelding(syntSykemeldingRequest);
            return response.getStatusCode().equals(HttpStatus.OK);
        }
        return false;
    }

    private void saveTranskasjonId(String orgnr, String arbeidsforholdsId, Long bestillingsId, String ident) {

        transaksjonMappingService.save(
                TransaksjonMapping.builder()
                        .ident(ident)
                        .transaksjonId(toJson(SykemeldingTransaksjon.builder()
                                .orgnummer(orgnr)
                                .arbeidsforholdId(arbeidsforholdsId)
                                .bestillingsId(bestillingsId).build()))
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
