package no.nav.dolly.bestilling.brregstub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.brregstub.domain.RolleoversiktTo;
import no.nav.dolly.bestilling.brregstub.util.BrregstubMergeUtil;
import no.nav.dolly.bestilling.personservice.PersonServiceConsumer;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.breg.RsBregdata;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.service.TransactionHelperService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.encodeStatus;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrregstubClient implements ClientRegister {

    private static final String OK_STATUS = "OK";
    private static final String FEIL_STATUS = "Feil= ";

    private final BrregstubConsumer brregstubConsumer;
    private final PersonServiceConsumer personServiceConsumer;
    private final MapperFacade mapperFacade;
    private final TransactionHelperService transactionHelperService;

    @Override
    public Mono<BestillingProgress> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (isNull(bestilling.getBrregstub())) {
            return Mono.empty();
        }

        return getPersonData(dollyPerson.getIdent())
                .map(personBolk -> mapRolleoversikt(bestilling.getBrregstub(), personBolk))
                .map(nyRolleoversikt -> brregstubConsumer.getRolleoversikt(dollyPerson.getIdent())
                        .map(eksisterendeRoller -> BrregstubMergeUtil.merge(nyRolleoversikt, eksisterendeRoller))
                        .flatMap(this::postRolleutskrift))
                .flatMap(Flux::from)
                .collect(Collectors.joining())
                .flatMap(status -> oppdaterStatus(progress, status));
    }

    @Override
    public void release(List<String> identer) {

        brregstubConsumer.deleteRolleoversikt(identer)
                .subscribe(resp -> log.info("Sletting utført i Brregstub"));
    }

    private Flux<PdlPersonBolk.PersonBolk> getPersonData(String ident) {

        return personServiceConsumer.getPdlPersoner(List.of(ident))
                .filter(pdlPersonBolk -> nonNull(pdlPersonBolk.getData()))
                .map(PdlPersonBolk::getData)
                .map(PdlPersonBolk.Data::getHentPersonBolk)
                .flatMap(Flux::fromIterable)
                .filter(personBolk -> nonNull(personBolk.getPerson()));
    }

    private RolleoversiktTo mapRolleoversikt(RsBregdata bregdata, PdlPersonBolk.PersonBolk personbolk) {

        var context = new MappingContext.Factory().getContext();
        context.setProperty("personBolk", personbolk);

        return mapperFacade.map(bregdata, RolleoversiktTo.class, context);
    }

    private Mono<String> postRolleutskrift(RolleoversiktTo rolleoversiktTo) {

        log.info("BRREGSTUB sender rolleoversikt for {} {}", rolleoversiktTo.getFnr(), rolleoversiktTo);
        return brregstubConsumer.postRolleoversikt(rolleoversiktTo)
                .map(status -> isBlank(status.getError()) ? OK_STATUS : FEIL_STATUS + encodeStatus(status.getError()));
    }

    private Mono<BestillingProgress> oppdaterStatus(BestillingProgress progress, String status) {

        return transactionHelperService.persister(progress, BestillingProgress::setBrregstubStatus, status);
    }
}
