package no.nav.dolly.bestilling.skjermingsregister;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.personservice.PersonServiceConsumer;
import no.nav.dolly.bestilling.skjermingsregister.domain.SkjermingBestilling;
import no.nav.dolly.bestilling.skjermingsregister.domain.SkjermingDataRequest;
import no.nav.dolly.bestilling.skjermingsregister.domain.SkjermingDataResponse;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.service.TransactionHelperService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static no.nav.dolly.bestilling.skjermingsregister.SkjermingUtil.getEgenansattDatoFom;
import static no.nav.dolly.bestilling.skjermingsregister.SkjermingUtil.getEgenansattDatoTom;
import static no.nav.dolly.bestilling.skjermingsregister.SkjermingUtil.isSkjerming;
import static no.nav.dolly.bestilling.skjermingsregister.SkjermingUtil.isTpsMessagingEgenansatt;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkjermingsRegisterClient implements ClientRegister {

    private final SkjermingsRegisterConsumer skjermingsRegisterConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final MapperFacade mapperFacade;
    private final PersonServiceConsumer personServiceConsumer;
    private final TransactionHelperService transactionHelperService;

    @Override
    public Mono<BestillingProgress> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (isSkjerming(bestilling) || isTpsMessagingEgenansatt(bestilling)) {

            return getPersonData(dollyPerson.getIdent())
                            .map(person -> prepRequest(bestilling, person))
                            .flatMap(request -> skjermingsRegisterConsumer.oppdaterPerson(request)
                                    .map(this::getStatus))
                            .collect(Collectors.joining())
                    .flatMap(status -> oppdaterStatus(progress, status));
        }
        return Mono.empty();
    }

    @Override
    public void release(List<String> identer) {

        skjermingsRegisterConsumer.deleteSkjerming(identer)
                .subscribe(response -> log.info("Slettet identer fra Skjermingsregisteret"));
    }

    private Flux<PdlPersonBolk.PersonBolk> getPersonData(String ident) {

        return personServiceConsumer.getPdlPersoner(List.of(ident))
                .filter(pdlPersonBolk -> nonNull(pdlPersonBolk.getData()))
                .map(PdlPersonBolk::getData)
                .map(PdlPersonBolk.Data::getHentPersonBolk)
                .flatMap(Flux::fromIterable)
                .filter(personBolk -> nonNull(personBolk.getPerson()));
    }

    private SkjermingDataRequest prepRequest(RsDollyUtvidetBestilling bestilling, PdlPersonBolk.PersonBolk person) {

        var context = new MappingContext.Factory().getContext();
        context.setProperty("personBolk", person);

        return mapperFacade.map(SkjermingBestilling.builder()
                        .skjermetFra(getEgenansattDatoFom(bestilling))
                        .skjermetTil(getEgenansattDatoTom(bestilling))
                        .build(),
                SkjermingDataRequest.class, context);
    }

    Mono<BestillingProgress> oppdaterStatus(BestillingProgress progress, String status) {

        return transactionHelperService.persister(progress, BestillingProgress::setSkjermingsregisterStatus, status);
    }

    String getStatus(SkjermingDataResponse resultat) {

        return isBlank(resultat.getError()) ? "OK" :
                errorStatusDecoder.getErrorText(null, resultat.getError());
    }
}
