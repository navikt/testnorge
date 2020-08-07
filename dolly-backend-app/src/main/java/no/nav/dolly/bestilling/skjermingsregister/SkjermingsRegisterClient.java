package no.nav.dolly.bestilling.skjermingsregister;

import static java.time.LocalDateTime.now;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static no.nav.dolly.domain.resultset.SystemTyper.SKJERMINGSREGISTER;

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
    private final TransaksjonMappingService transaksjonMappingService;
    private final ObjectMapper objectMapper;
    private final MapperFacade mapperFacade;
    private final TpsfPersonCache tpsfPersonCache;

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, TpsPerson tpsPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getTpsf())) {

            StringBuilder status = new StringBuilder();
            try {
                tpsfPersonCache.fetchIfEmpty(tpsPerson);
                Person hovedPerson = tpsPerson.getPerson(tpsPerson.getHovedperson());

                SkjermingsDataRequest skjermingsDataRequest = mapperFacade.map(tpsPerson.getPerson(tpsPerson.getHovedperson()), SkjermingsDataRequest.class);

                if (!transaksjonMappingService.existAlready(SKJERMINGSREGISTER, tpsPerson.getHovedperson(), null) || isOpprettEndre) {
                    if (isOpphoertEgenAnsatt(hovedPerson.getEgenAnsattDatoFom(), hovedPerson.getEgenAnsattDatoTom())) {
                        sendOpphoerSkjermingRequest(tpsPerson, status);
                    } else if (isAktivEgenansatt(hovedPerson.getEgenAnsattDatoFom(), hovedPerson.getEgenAnsattDatoTom())) {
                        sendSkjermingDataRequest(tpsPerson, status, skjermingsDataRequest);
                    }
                }
            } catch (RuntimeException e) {
                status.append(errorStatusDecoder.decodeRuntimeException(e));

                log.error("Feilet å skjerme person med ident: {}", tpsPerson.getHovedperson(), e);
            }
            progress.setSkjermingsregisterStatus(status.toString());
        }
    }

    private void sendSkjermingDataRequest(TpsPerson tpsPerson, StringBuilder status, SkjermingsDataRequest skjermingsDataRequest) {
        ResponseEntity<List<SkjermingsDataResponse>> response = skjermingsRegisterConsumer.postSkjerming(Collections.singletonList(skjermingsDataRequest));
        if (response.hasBody() && response.getStatusCode() == HttpStatus.CREATED) {
            status.append("OK");

            saveTranskasjonId(requireNonNull(response.getBody()), tpsPerson);
        }
    }

    private void sendOpphoerSkjermingRequest(TpsPerson tpsPerson, StringBuilder status) {
        ResponseEntity<String> response = skjermingsRegisterConsumer.putSkjerming(tpsPerson.getHovedperson());
        if (response.getStatusCode() == HttpStatus.OK) {
            status.append("OK");

            saveTranskasjonId(tpsPerson);
        }
    }

    @Override
    public void release(List<String> identer) {
        identer.forEach(skjermingsRegisterConsumer::deleteSkjerming);
    }

    private static boolean isAktivEgenansatt(LocalDateTime egenAnsattFom, LocalDateTime egenAnsattTom) {

        return nonNull(egenAnsattFom) && (isNull(egenAnsattTom) || now().isBefore(egenAnsattTom));
    }

    private static boolean isOpphoertEgenAnsatt(LocalDateTime egenAnsattFom, LocalDateTime egenAnsattTom) {

        return nonNull(egenAnsattFom) && nonNull(egenAnsattTom) && now().isAfter(egenAnsattTom);
    }

    private void saveTranskasjonId(List<SkjermingsDataResponse> response, TpsPerson tpsPerson) {

        transaksjonMappingService.save(
                TransaksjonMapping.builder()
                        .ident(tpsPerson.getHovedperson())
                        .transaksjonId(toJson(SkjermingsDataTransaksjon.builder()
                                .ansattSkjermetFra(response.get(0).getSkjermetFra())
                                .ansattSkjermetTil(response.get(0).getSkjermetTil())
                                .build()))
                        .datoEndret(LocalDateTime.now())
                        .system(SKJERMINGSREGISTER.name())
                        .build());
    }

    private void saveTranskasjonId(TpsPerson tpsPerson) {

        Person hovedPerson = tpsPerson.getPerson(tpsPerson.getHovedperson());
        transaksjonMappingService.save(
                TransaksjonMapping.builder()
                        .ident(tpsPerson.getHovedperson())
                        .transaksjonId(toJson(SkjermingsDataTransaksjon.builder()
                                .ansattSkjermetFra(hovedPerson.getEgenAnsattDatoFom().toString())
                                .ansattSkjermetTil(hovedPerson.getEgenAnsattDatoTom().toString())
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
