package no.nav.dolly.bestilling.arbeidsplassencv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.arbeidsplassencv.dto.PAMCVDTO;
import no.nav.dolly.bestilling.personservice.PersonServiceConsumer;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.service.TransactionHelperService;
import no.nav.dolly.util.CallIdUtil;
import no.nav.testnav.libs.dto.arbeidsplassencv.v1.ArbeidsplassenCVDTO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.util.List;
import java.util.function.Consumer;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getInfoVenter;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO.AdresseBeskyttelse.STRENGT_FORTROLIG;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArbeidsplassenCVClient implements ClientRegister {

    private final ArbeidsplassenCVConsumer arbeidsplassenCVConsumer;
    private final MapperFacade mapperFacade;
    private final TransactionHelperService transactionHelperService;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final PersonServiceConsumer personServiceConsumer;

    @Override
    public Mono<BestillingProgress> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (isNull(bestilling.getArbeidsplassenCV())) {
            return Mono.empty();
        }

        return getPerson(dollyPerson)
                .flatMap(person -> Mono.just(person.getAdressebeskyttelse().stream()
                                .anyMatch(ab ->
                                        STRENGT_FORTROLIG.equals(ab.getGradering())))
                        .zipWith(Mono.just(person.getDoedsfall().stream()
                                .anyMatch(doed -> nonNull(doed.getDoedsdato())))))
                .flatMap(harAdressebeskyttelseEllerDoed -> {
                    if (isTrue(harAdressebeskyttelseEllerDoed.getT1())) {
                        return oppdaterStatus(progress, "Avvik: CV kan ikke opprettes - person har adressebeskyttelse STRENGT FORTROLIG");
                    } else if (isTrue(harAdressebeskyttelseEllerDoed.getT2())) {
                        return oppdaterStatus(progress, "Avvik: CV kan ikke opprettes - person er død");
                    } else {

                        return oppdaterStatus(progress, getInfoVenter("Arbeidsplassen"))
                                .then(Mono.just(bestilling.getArbeidsplassenCV()))
                                .flatMap(arbeidsplassenCV -> {
                                    var oppdatertOrdre = mapperFacade.map(arbeidsplassenCV, ArbeidsplassenCVDTO.class);
                                    bestilling.setArbeidsplassenCV(oppdatertOrdre);
                                    return transactionHelperService.persister(progress.getBestillingId(), bestilling)
                                            .then(Mono.just(arbeidsplassenCV));
                                })
                                .flatMap(oppdatertOrdre -> Mono.just(CallIdUtil.generateCallId())
                                        .flatMap(uuid -> arbeidsplassenCVConsumer.opprettPerson(dollyPerson.getIdent(), uuid)
                                                .flatMap(response ->
                                                        arbeidsplassenCVConsumer.godtaHjemmel(dollyPerson.getIdent(), uuid)
                                                                .then(Mono.just(mapperFacade.map(oppdatertOrdre, PAMCVDTO.class))
                                                                        .flatMap(request -> arbeidsplassenCVConsumer.oppdaterCV(dollyPerson.getIdent(),
                                                                                request, uuid, logRetries(progress)))))

                                                .map(status -> status.getStatus().is2xxSuccessful() ? "OK" :
                                                        errorStatusDecoder.getErrorText(HttpStatus.valueOf(status.getStatus().value()),
                                                                status.getFeilmelding()))

                                                .flatMap(resultat -> oppdaterStatus(progress, resultat))));
                    }
                });
    }

    private Mono<PdlPerson.Person> getPerson(DollyPerson dollyPerson) {

        return personServiceConsumer.getPdlPersoner(List.of(dollyPerson.getIdent()))
                .filter(pdlPersonBolk -> nonNull(pdlPersonBolk.getData()))
                .map(PdlPersonBolk::getData)
                .map(PdlPersonBolk.Data::getHentPersonBolk)
                .flatMap(Flux::fromIterable)
                .filter(personBolk -> nonNull(personBolk.getPerson()))
                .map(PdlPersonBolk.PersonBolk::getPerson)
                .next()
                .switchIfEmpty(Mono.just(new PdlPerson.Person()));
    }

    private Consumer<Retry.RetrySignal> logRetries(BestillingProgress progress) {

        return retrySignal -> {

            String retryStatus = "%s (antall forsøk: %d)".formatted(
                    getInfoVenter("Arbeidsplassen"),
                    retrySignal.totalRetries() + 2);

            oppdaterStatus(progress, retryStatus)
                    .subscribe();
        };
    }

    private Mono<BestillingProgress> oppdaterStatus(BestillingProgress progress, String status) {

        return transactionHelperService.persister(progress, BestillingProgress::setArbeidsplassenCVStatus, status);
    }

    @Override
    public void release(List<String> identer) {

        arbeidsplassenCVConsumer.deleteCVer(identer)
                .collectList()
                .subscribe(response -> log.info("Sletting utført mot ArbeidsplassenCV"));
    }
}
