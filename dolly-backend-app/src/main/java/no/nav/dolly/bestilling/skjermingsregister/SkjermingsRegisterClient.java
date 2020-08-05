package no.nav.dolly.bestilling.skjermingsregister;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.SystemTyper.SKJERMINGSREGISTER;
import static org.apache.cxf.common.util.PropertyUtils.isTrue;

import java.time.LocalDateTime;
import java.util.Collections;
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
import no.nav.dolly.bestilling.skjermingsregister.domain.SkjermingsDataRequest;
import no.nav.dolly.bestilling.skjermingsregister.domain.SkjermingsDataResponse;
import no.nav.dolly.bestilling.skjermingsregister.domain.SkjermingsDataTransaksjon;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.TransaksjonMapping;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.skjermingsregister.RsSkjermingsRegister;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.service.TpsfPersonCache;
import no.nav.dolly.service.TransaksjonMappingService;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkjermingsRegisterClient implements ClientRegister {

    private final SkjermingsRegisterConsumer skjermingsRegisterConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final MapperFacade mapperFacade;
    private final TransaksjonMappingService transaksjonMappingService;
    private final ObjectMapper objectMapper;
    private final TpsfPersonCache tpsfPersonCache;

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, TpsPerson tpsPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getSkjermingsRegister()) && !bestilling.getSkjermingsRegister().isEmpty()) {

            StringBuilder status = new StringBuilder();
            try {
                RsSkjermingsRegister register = bestilling.getSkjermingsRegister().get(0);

                SkjermingsDataRequest skjermingsDataRequest = mapperFacade.map(register, SkjermingsDataRequest.class);

                tpsfPersonCache.fetchIfEmpty(tpsPerson);
                Person hovedPerson = tpsPerson.getPerson(tpsPerson.getHovedperson());
                hovedPerson.setEgenAnsattDatoFom(register.getSkjermetFra().toString());
                hovedPerson.setEgenAnsattDatoTom(register.getSkjermetTil().toString());
                if (isTrue(erAktivEgenansatt(hovedPerson.getEgenAnsattDatoFom(), hovedPerson.getEgenAnsattDatoTom()))) {

                    skjermingsDataRequest.setEtternavn(hovedPerson.getEtternavn());
                    skjermingsDataRequest.setFornavn(hovedPerson.getFornavn());
                    skjermingsDataRequest.setPersonident(hovedPerson.getIdent());

                    if (!transaksjonMappingService.existAlready(SKJERMINGSREGISTER, tpsPerson.getHovedperson(), null) || isOpprettEndre) {

                        ResponseEntity<List<SkjermingsDataResponse>> response = skjermingsRegisterConsumer.postSkjerming(Collections.singletonList(skjermingsDataRequest));
                        if (response.hasBody() && response.getStatusCode().equals(HttpStatus.CREATED)) {
                            status.append("OK");

                            saveTranskasjonId(response.getBody(), tpsPerson.getHovedperson());
                        }

                    }
                }
            } catch (RuntimeException e) {

                status.append(errorStatusDecoder.decodeRuntimeException(e));

                log.error("Feilet å skjerme person med ident: {}", tpsPerson.getHovedperson(), e);
            }
            progress.setSkjermingsregisterStatus(status.toString());
        }
    }

    @Override
    public void release(List<String> identer) {
        identer.forEach(skjermingsRegisterConsumer::deleteSkjerming);
    }

    private Boolean erAktivEgenansatt(String egenAnsattFom, String egenAnsattTom) {

        if (isNull(egenAnsattFom) || isNull(egenAnsattTom)) {
            return false;
        }

        LocalDateTime gjeldendeTidspunkt = LocalDateTime.now();
        LocalDateTime egenAnsattFomDateTime = LocalDateTime.parse(egenAnsattFom);
        LocalDateTime egenAnsattTomDateTime = LocalDateTime.parse(egenAnsattTom);
        return gjeldendeTidspunkt.isAfter(egenAnsattFomDateTime) && gjeldendeTidspunkt.isBefore(egenAnsattTomDateTime);
    }

    private void saveTranskasjonId(List<SkjermingsDataResponse> response, String ident) {

        transaksjonMappingService.save(
                TransaksjonMapping.builder()
                        .ident(ident)
                        .transaksjonId(toJson(SkjermingsDataTransaksjon.builder()
                                .heltNavn(response.get(0).getFornavn() + " " + response.get(0).getEtternavn())
                                .datoEndret(response.get(0).getEndretDato())
                                .datoOpprettet()
                                .build()))
                        .datoEndret(LocalDateTime.now())
                        .system(SKJERMINGSREGISTER.name())
                        .build());
    }

    private String toJson(Object object) {

        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Feilet å konvertere transaksjonsId for SkjermingsRegister");
        }
        return null;
    }
}
