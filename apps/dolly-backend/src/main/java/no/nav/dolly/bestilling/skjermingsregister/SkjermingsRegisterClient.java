package no.nav.dolly.bestilling.skjermingsregister;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.skjermingsregister.domain.BestillingPersonWrapper;
import no.nav.dolly.bestilling.skjermingsregister.domain.SkjermingsDataRequest;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.service.DollyPersonCache;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static no.nav.dolly.bestilling.skjermingsregister.SkjermingUtil.getEgenansattDatoFom;
import static no.nav.dolly.bestilling.skjermingsregister.SkjermingUtil.getEgenansattDatoTom;
import static no.nav.dolly.bestilling.skjermingsregister.SkjermingUtil.isSkjerming;
import static no.nav.dolly.bestilling.skjermingsregister.SkjermingUtil.isTpsMessagingEgenansatt;
import static no.nav.dolly.bestilling.skjermingsregister.SkjermingUtil.isTpsfEgenansatt;
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


        if (isSkjerming(bestilling) || isTpsMessagingEgenansatt(bestilling) || isTpsfEgenansatt(bestilling)) {

            dollyPersonCache.fetchIfEmpty(dollyPerson);

            var skjermetFra = getEgenansattDatoFom(bestilling);
            var skjermetTil = getEgenansattDatoTom(bestilling);

            StringBuilder status = new StringBuilder();

            sendSkjermingDataRequests(dollyPerson, skjermetFra, skjermetTil, status);

            progress.setSkjermingsregisterStatus(isNotBlank(status) ? status.toString() : "OK");
        }
    }

    @Override
    public void release(List<String> identer) {

        skjermingsRegisterConsumer.deleteSkjerming(identer)
                .subscribe(response -> log.info("Slettet identer fra Skjermingsregisteret"));
    }

    private void sendSkjermingDataRequests(DollyPerson dollyPerson, LocalDateTime skjermetFra, LocalDateTime
            skjermetTil, StringBuilder status) {

        SkjermingsDataRequest skjerming = dollyPerson.isPdlfMaster() ?
                prepRequest(null, dollyPerson.getPdlfPerson().getPerson(), skjermetFra, skjermetTil) :
                prepRequest(dollyPerson.getPerson(dollyPerson.getHovedperson()), null, skjermetFra, skjermetTil);

        try {
            skjermingsRegisterConsumer.oppdaterPerson(skjerming);

        } catch (RuntimeException e) {
            status.append(errorStatusDecoder.decodeThrowable(e));
            log.error("Feilet å skjerme person: {}", dollyPerson.getHovedperson(), e);
        }
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
