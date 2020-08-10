package no.nav.dolly.bestilling.skjermingsregister;

import static java.time.LocalDateTime.now;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.SystemTyper.SKJERMINGSREGISTER;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

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
            tpsfPersonCache.fetchIfEmpty(tpsPerson);
            tpsPerson.getPersondetaljer().forEach(person -> {

                try {
                    SkjermingsDataRequest skjermingsDataRequest = mapperFacade.map(person, SkjermingsDataRequest.class);
                    checkAktivEllerOpphoertSkjerming(tpsPerson.getHovedperson(), status, person, skjermingsDataRequest);
                } catch (HttpClientErrorException e) {
                    if (e.getStatusCode() == HttpStatus.BAD_REQUEST && isAlleredeInSkjermingsRegister(person)) {
                        status.append(String.format("Ident: %s eksisterer allerede i skjermingsregister", person.getIdent()));
                        log.error("Ident: {} eksisterer allerede i skjermingsregister", person.getIdent(), e);
                    } else {
                        status.append(errorStatusDecoder.decodeRuntimeException(e));
                        log.error("Feilet å skjerme person med ident: {}", person.getIdent(), e);
                    }
                } catch (RuntimeException e) {
                    status.append(errorStatusDecoder.decodeRuntimeException(e));
                    log.error("Feilet å skjerme person med ident: {}", person.getIdent(), e);
                }
            });
            progress.setSkjermingsregisterStatus(status.toString());
        }
    }

    @Override
    public void release(List<String> identer) {

        identer.forEach(skjermingsRegisterConsumer::deleteSkjerming);
    }

    private void checkAktivEllerOpphoertSkjerming(String hovedperson, StringBuilder status, Person person, SkjermingsDataRequest skjermingsDataRequest) {

        if (isAktivSkjerming(person)) {
            sendSkjermingDataRequest(person, status, skjermingsDataRequest, hovedperson);
        } else if (isOpphoertSkjerming(person)) {
            sendOpphoerSkjermingRequest(person, status, hovedperson);
        }
    }

    private boolean isAlleredeInSkjermingsRegister(Person person) {

        if (nonNull(person)) {
            ResponseEntity<List<SkjermingsDataResponse>> skjermingResponseEntity = skjermingsRegisterConsumer.getSkjerming(person.getIdent());
            return (skjermingResponseEntity.getStatusCode().equals(HttpStatus.OK) && skjermingResponseEntity.hasBody());
        }
        return false;
    }

    private void sendSkjermingDataRequest(Person person, StringBuilder status, SkjermingsDataRequest skjermingsDataRequest, String hovedIdent) {

        ResponseEntity<List<SkjermingsDataResponse>> response = skjermingsRegisterConsumer.postSkjerming(Collections.singletonList(skjermingsDataRequest));
        if (response.hasBody() && response.getStatusCode() == HttpStatus.CREATED && person.getIdent().equals(hovedIdent)) {
            status.append("OK");

            saveTranskasjonId(person);
        } else if (response.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }
    }

    private void sendOpphoerSkjermingRequest(Person person, StringBuilder status, String hovedIdent) {

        ResponseEntity<String> response = skjermingsRegisterConsumer.putSkjerming(person.getIdent());
        if (response.getStatusCode() == HttpStatus.OK && person.getIdent().equals(hovedIdent)) {
            status.append("OK");

            saveTranskasjonId(person);
        }
    }

    private static boolean isAktivSkjerming(Person person) {

        return nonNull(person.getEgenAnsattDatoFom()) && (isNull(person.getEgenAnsattDatoTom()) || now().isBefore(person.getEgenAnsattDatoTom()));
    }

    private static boolean isOpphoertSkjerming(Person person) {

        return nonNull(person.getEgenAnsattDatoFom()) && nonNull(person.getEgenAnsattDatoTom()) && now().isAfter(person.getEgenAnsattDatoTom());
    }

    private void saveTranskasjonId(Person person) {

        transaksjonMappingService.save(
                TransaksjonMapping.builder()
                        .ident(person.getIdent())
                        .transaksjonId(toJson(SkjermingsDataTransaksjon.builder()
                                .ansattSkjermetFra(person.getEgenAnsattDatoFom().toString())
                                .ansattSkjermetTil(person.getEgenAnsattDatoTom().toString())
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
