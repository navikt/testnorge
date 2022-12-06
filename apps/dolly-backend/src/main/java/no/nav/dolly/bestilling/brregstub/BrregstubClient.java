package no.nav.dolly.bestilling.brregstub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.brregstub.domain.RolleoversiktTo;
import no.nav.dolly.bestilling.brregstub.util.BrregstubMergeUtil;
import no.nav.dolly.bestilling.personservice.PersonServiceConsumer;
import no.nav.dolly.consumer.pdlperson.PdlPersonConsumer;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.breg.RsBregdata;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.util.TransactionHelperService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.encodeStatus;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getInfoVenter;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getVarselSlutt;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getVenterTekst;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrregstubClient implements ClientRegister {

    private static final String OK_STATUS = "OK";
    private static final String FEIL_STATUS = "Feil= ";

    private final BrregstubConsumer brregstubConsumer;
    private final PdlPersonConsumer pdlPersonConsumer;
    private final PersonServiceConsumer personServiceConsumer;
    private final MapperFacade mapperFacade;
    private final TransactionHelperService transactionHelperService;

    @Override
    public Flux<Void> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getBrregstub())) {

            progress.setBrregstubStatus(encodeStatus(getInfoVenter("BRREG")));
            transactionHelperService.persister(progress);

            personServiceConsumer.getPdlSyncReady(dollyPerson.getHovedperson())
                    .flatMap(isReady -> (isReady ?

                            getPersonData(dollyPerson.getHovedperson())
                                    .flatMap(personBolk -> mapRolleoversikt(bestilling.getBrregstub(), personBolk)
                                            .map(nyRolleoversikt -> brregstubConsumer.getRolleoversikt(dollyPerson.getHovedperson())
                                                    .map(eksisterendeRoller -> BrregstubMergeUtil.merge(nyRolleoversikt, eksisterendeRoller))
                                                    .flatMap(aktuelleRoller -> postRolleutskrift(aktuelleRoller))))
                                    .flatMap(Flux::from)
                                    .collect(Collectors.joining()) :

                            Mono.just(encodeStatus(getVarselSlutt("BRREG"))))
                    )
                    .subscribe(resultat -> {
                        progress.setBrregstubStatus(resultat);
                        transactionHelperService.persister(progress);
                    });

        }
        return Flux.just();
    }

    @Override
    public void release(List<String> identer) {

        brregstubConsumer.deleteRolleoversikt(identer)
                .subscribe(resp -> log.info("Sletting utført i Brregstub"));
    }

    @Override
    public boolean isDone(RsDollyBestilling kriterier, Bestilling bestilling) {

        return isNull(kriterier.getBrregstub()) ||
                bestilling.getProgresser().stream()
                        .allMatch(entry -> isNotBlank(entry.getBrregstubStatus()) &&
                !entry.getBrregstubStatus().contains(getVenterTekst()));
    }

    private Flux<PdlPersonBolk.PersonBolk> getPersonData(String ident) {

        return pdlPersonConsumer.getPdlPersoner(List.of(ident))
                .filter(pdlPersonBolk -> nonNull(pdlPersonBolk.getData()))
                .map(PdlPersonBolk::getData)
                .map(PdlPersonBolk.Data::getHentPersonBolk)
                .flatMap(Flux::fromIterable)
                .filter(personBolk -> nonNull(personBolk.getPerson()));
    }

    private Mono<RolleoversiktTo> mapRolleoversikt(RsBregdata bregdata, PdlPersonBolk.PersonBolk personbolk) {

        var context = new MappingContext.Factory().getContext();
        context.setProperty("personBolk", personbolk);

        return Mono.just(mapperFacade.map(bregdata, RolleoversiktTo.class, context));
    }

    private Mono<String> postRolleutskrift(RolleoversiktTo rolleoversiktTo) {

        log.info("BRREGSTUB sender rolleoversikt for {} {}", rolleoversiktTo.getFnr(), rolleoversiktTo);
        return brregstubConsumer.postRolleoversikt(rolleoversiktTo)
                .map(status -> isBlank(status.getError()) ? OK_STATUS : FEIL_STATUS + encodeStatus(status.getError()));
    }
}
