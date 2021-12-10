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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
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

        dollyPersonCache.fetchIfEmpty(dollyPerson);

        if ((nonNull(bestilling.getTpsf()) && nonNull(bestilling.getTpsf().getEgenAnsattDatoFom())) ||
                (nonNull(bestilling.getSkjerming()))) {

            var skjermetFra = nonNull(bestilling.getSkjerming())
                    ? bestilling.getSkjerming().getEgenAnsattDatoFom()
                    : bestilling.getTpsf().getEgenAnsattDatoFom();

            var skjermetTil = nonNull(bestilling.getSkjerming()) && nonNull(bestilling.getSkjerming().getEgenAnsattDatoTom())
                    ? bestilling.getSkjerming().getEgenAnsattDatoTom()
                    : bestilling.getTpsf().getEgenAnsattDatoTom();

            StringBuilder status = new StringBuilder();
            for (Person person : dollyPerson.getPersondetaljer()) {
                try {
                    SkjermingsDataRequest skjermingsDataRequest = mapperFacade.map(BestillingPersonWrapper.builder()
                                    .skjermetFra(skjermetFra)
                                    .skjermetTil(skjermetTil)
                                    .person(person)
                                    .build(),
                            SkjermingsDataRequest.class);
                    if (isAlleredeSkjermet(person) && nonNull(skjermetTil)) {
                        skjermingsRegisterConsumer.putSkjerming(person.getIdent());
                    } else if (!isAlleredeSkjermet(person) && isNull(skjermetTil)) {
                        skjermingsRegisterConsumer.postSkjerming(List.of(skjermingsDataRequest));
                    }
                } catch (RuntimeException e) {
                    status.append(errorStatusDecoder.decodeRuntimeException(e));
                    log.error("Feilet å skjerme person med ident: {}", person.getIdent(), e);
                    break;
                }
            }
            progress.setSkjermingsregisterStatus(isNotBlank(status) ? status.toString() : "OK");
        }
    }

    @Override
    public void release(List<String> identer) {

        identer.forEach(skjermingsRegisterConsumer::deleteSkjerming);
    }

    private boolean isAlleredeSkjermet(Person person) {

        try {
            ResponseEntity<SkjermingsDataResponse> skjermingResponseEntity = skjermingsRegisterConsumer.getSkjerming(person.getIdent());
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
}
