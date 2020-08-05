package no.nav.dolly.bestilling.skjermingsregister;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.SystemTyper.SKJERMINGSREGISTER;
import static org.apache.cxf.common.util.PropertyUtils.isTrue;

import java.time.LocalDateTime;
import java.util.List;

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

        if (nonNull(bestilling.getSkjermingsRegister())) {

            StringBuilder status = new StringBuilder();
            try {
                SkjermingsDataRequest skjermingsDataRequest = mapperFacade.map(bestilling.getSkjermingsRegister(), SkjermingsDataRequest.class);

                tpsfPersonCache.fetchIfEmpty(tpsPerson);
                Person hovedPerson = tpsPerson.getPerson(tpsPerson.getHovedperson());
                if (isTrue(erAktivEgenansatt(hovedPerson.getEgenAnsattDatoFom(), hovedPerson.getEgenAnsattDatoTom()))) {

                    skjermingsDataRequest.setSkjermetPerson(SkjermingsDataRequest.SkjermetPerson.builder()
                            .fornavn(hovedPerson.getFornavn())
                            .etternavn(hovedPerson.getEtternavn())
                            .personident(hovedPerson.getIdent())
                            .skjermetFra(LocalDateTime.parse(hovedPerson.getEgenAnsattDatoFom()))
                            .skjermetTil(LocalDateTime.parse(hovedPerson.getEgenAnsattDatoTom()))
                            .build());

                    if (!transaksjonMappingService.existAlready(SKJERMINGSREGISTER, tpsPerson.getHovedperson(), null) || isOpprettEndre) {

                        ResponseEntity<SkjermingsDataResponse> response = skjermingsRegisterConsumer.postSkjerming(skjermingsDataRequest);
                        if (response.hasBody()) {
                            status.append("OK");

                            saveTranskasjonId(bestilling.getSkjermingsRegister().get(0).getSkjermetPerson(), tpsPerson.getHovedperson());
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

    private void saveTranskasjonId(RsSkjermingsRegister.SkjermetPerson skjermetPerson, String ident) {

        transaksjonMappingService.save(
                TransaksjonMapping.builder()
                        .ident(ident)
                        .transaksjonId(toJson(new SkjermingsDataTransaksjon(
                                skjermetPerson.getFornavn() + " " + skjermetPerson.getEtternavn(),
                                skjermetPerson.getSkjermetFra())))
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
