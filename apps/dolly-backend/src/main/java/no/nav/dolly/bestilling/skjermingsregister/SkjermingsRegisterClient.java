package no.nav.dolly.bestilling.skjermingsregister;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.skjermingsregister.domain.BestillingPersonWrapper;
import no.nav.dolly.bestilling.skjermingsregister.domain.SkjermingsDataRequest;
import no.nav.dolly.bestilling.skjermingsregister.domain.SkjermingsDataResponse;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.service.DollyPersonCache;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullPersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.EKTEFELLE_PARTNER;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.FAMILIERELASJON_BARN;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.FAMILIERELASJON_FORELDER;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkjermingsRegisterClient implements ClientRegister {

    private final SkjermingsRegisterConsumer skjermingsRegisterConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final MapperFacade mapperFacade;
    private final DollyPersonCache dollyPersonCache;

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {


        if ((nonNull(bestilling.getTpsf()) && nonNull(bestilling.getTpsf().getEgenAnsattDatoFom())) ||
                ((nonNull(bestilling.getSkjerming())) && nonNull(bestilling.getSkjerming().getEgenAnsattDatoFom()))) {

            dollyPersonCache.fetchIfEmpty(dollyPerson);

            var skjermetFra = nonNull(bestilling.getSkjerming())
                    ? bestilling.getSkjerming().getEgenAnsattDatoFom()
                    : bestilling.getTpsf().getEgenAnsattDatoFom();

            var skjermetTil = nonNull(bestilling.getSkjerming()) && nonNull(bestilling.getSkjerming().getEgenAnsattDatoTom())
                    ? bestilling.getSkjerming().getEgenAnsattDatoTom()
                    : nonNull(bestilling.getTpsf())
                    ? bestilling.getTpsf().getEgenAnsattDatoTom()
                    : null;

            StringBuilder status = new StringBuilder();

            sendSkjermingDataRequests(dollyPerson, skjermetFra, skjermetTil, status);

            progress.setSkjermingsregisterStatus(isNotBlank(status) ? status.toString() : "OK");
        }
    }

    @Override
    public void release(List<String> identer) {

        identer.forEach(skjermingsRegisterConsumer::deleteSkjerming);
    }

    private void sendSkjermingDataRequests(DollyPerson dollyPerson, LocalDateTime skjermetFra, LocalDateTime skjermetTil, StringBuilder status) {

        List<SkjermingsDataRequest> skjerminger;

        if (dollyPerson.isPdlfMaster()) {

            skjerminger = Stream.of(List.of(prepRequest(null, dollyPerson.getPdlfPerson().getPerson(), skjermetFra, skjermetTil)),
                            dollyPerson.getPdlfPerson().getRelasjoner().stream()
                                    .filter(relasjon -> Stream.of(EKTEFELLE_PARTNER, FAMILIERELASJON_BARN, FAMILIERELASJON_FORELDER)
                                            .anyMatch(relasjon2 -> relasjon2 == relasjon.getRelasjonType()))
                                    .map(FullPersonDTO.RelasjonDTO::getRelatertPerson)
                                    .map(person -> prepRequest(null, person, skjermetFra, skjermetTil))
                                    .toList())
                    .flatMap(Collection::stream)
                    .toList();
        } else {
            skjerminger = Stream.of(List.of(prepRequest(dollyPerson.getPerson(dollyPerson.getHovedperson()), null, skjermetFra, skjermetTil)),
                            dollyPerson.getPartnere().stream()
                                    .map(partner -> prepRequest(dollyPerson.getPerson(partner), null, skjermetFra, skjermetTil))
                                    .toList(),
                            dollyPerson.getBarn().stream()
                                    .map(barn -> prepRequest(dollyPerson.getPerson(barn), null, skjermetFra, skjermetTil))
                                    .toList(),
                            dollyPerson.getForeldre().stream()
                                    .map(foreldre -> prepRequest(dollyPerson.getPerson(foreldre), null, skjermetFra, skjermetTil))
                                    .toList())
                    .flatMap(Collection::stream)
                    .toList();
        }

        try {
            skjerminger.forEach(skjerming -> {
                if (isAlleredeSkjermet(skjerming.getPersonident()) && nonNull(skjermetTil)) {
                    skjermingsRegisterConsumer.putSkjerming(skjerming.getPersonident());
                } else if (!isAlleredeSkjermet(skjerming.getPersonident()) && isNull(skjermetTil)) {
                    skjermingsRegisterConsumer.postSkjerming(List.of(skjerming));
                }
            });
        } catch (RuntimeException e) {
            status.append(errorStatusDecoder.decodeRuntimeException(e));
            log.error("Feilet å skjerme person: {}", dollyPerson.getHovedperson(), e);
        }
    }

    private boolean isAlleredeSkjermet(String ident) {

        try {
            ResponseEntity<SkjermingsDataResponse> skjermingResponseEntity = skjermingsRegisterConsumer.getSkjerming(ident);
            log.info("Respons fra skjermingsregister: {}", skjermingResponseEntity.getBody());
            if (skjermingResponseEntity.getStatusCode().equals(HttpStatus.OK)) {
                return true;
            }
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return false;
            }
            throw e;
        }
        return false;
    }

    private SkjermingsDataRequest prepRequest(Person person, PersonDTO pdlfPerson,
                                              LocalDateTime skjermingFra, LocalDateTime skjermingTil) {

        return mapperFacade.map(BestillingPersonWrapper.builder()
                        .skjermetFra(skjermingFra)
                        .skjermetTil(skjermingTil)
                        .person(person)
                        .pdlfPerson(pdlfPerson)
                        .build(),
                SkjermingsDataRequest.class);
    }
}
