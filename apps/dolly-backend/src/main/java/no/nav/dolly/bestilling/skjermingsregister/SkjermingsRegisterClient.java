package no.nav.dolly.bestilling.skjermingsregister;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.skjermingsregister.domain.SkjermingBestilling;
import no.nav.dolly.bestilling.skjermingsregister.domain.SkjermingDataRequest;
import no.nav.dolly.bestilling.skjermingsregister.domain.SkjermingDataResponse;
import no.nav.dolly.consumer.pdlperson.PdlPersonConsumer;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.util.TransactionHelperService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static no.nav.dolly.bestilling.skjermingsregister.SkjermingUtil.getEgenansattDatoFom;
import static no.nav.dolly.bestilling.skjermingsregister.SkjermingUtil.getEgenansattDatoTom;
import static no.nav.dolly.bestilling.skjermingsregister.SkjermingUtil.isSkjerming;
import static no.nav.dolly.bestilling.skjermingsregister.SkjermingUtil.isTpsMessagingEgenansatt;
import static no.nav.dolly.bestilling.skjermingsregister.SkjermingUtil.isTpsfEgenansatt;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getInfoVenter;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkjermingsRegisterClient implements ClientRegister {

    private final SkjermingsRegisterConsumer skjermingsRegisterConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final MapperFacade mapperFacade;
    private final PdlPersonConsumer pdlPersonConsumer;
    private final TransactionHelperService transactionHelperService;

    @Override
    public Flux<ClientFuture> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (isSkjerming(bestilling) || isTpsMessagingEgenansatt(bestilling) || isTpsfEgenansatt(bestilling)) {

            progress.setSkjermingsregisterStatus(getInfoVenter("Skjermingsregisteret"));
            transactionHelperService.persister(progress);

            return Flux.from(getPersonData(dollyPerson.getHovedperson())
                            .map(person -> prepRequest(bestilling, person))
                            .flatMap(request -> skjermingsRegisterConsumer.oppdaterPerson(request)
                                    .map(this::getStatus))
                            .collect(Collectors.joining()))
                    .map(status -> futurePersist(progress, status));
        }
        return Flux.empty();
    }

    private ClientFuture futurePersist(BestillingProgress progress, String status) {

        return () -> {
            progress.setSkjermingsregisterStatus(status);
            transactionHelperService.persister(progress);
            return progress;
        };
    }

    @Override
    public void release(List<String> identer) {

        skjermingsRegisterConsumer.deleteSkjerming(identer)
                .subscribe(response -> log.info("Slettet identer fra Skjermingsregisteret"));
    }

    private String getStatus(SkjermingDataResponse resultat) {

        return isBlank(resultat.getError()) ? "OK" :
                errorStatusDecoder.getErrorText(null, resultat.getError());
    }

    private Flux<PdlPersonBolk.PersonBolk> getPersonData(String ident) {

        return pdlPersonConsumer.getPdlPersoner(List.of(ident))
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
}
