package no.nav.dolly.bestilling.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.personservice.PersonServiceClient;
import no.nav.dolly.bestilling.tpsmessagingservice.service.TpsPersonService;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.elastic.BestillingElasticRepository;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.metrics.CounterCustomRegistry;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.util.ThreadLocalContextLifter;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.dolly.util.WebClientFilter;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Operators;

import java.util.List;

import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.jpa.Testident.Master.PDL;
import static no.nav.dolly.util.MdcUtil.MDC_KEY_BESTILLING;

@Slf4j
@Service
public class ImportAvPersonerFraPdlService extends DollyBestillingService {

    private final PersonServiceClient personServiceClient;

    public ImportAvPersonerFraPdlService(IdentService identService,
                                         BestillingService bestillingService,
                                         ObjectMapper objectMapper,
                                         MapperFacade mapperFacade,
                                         List<ClientRegister> clientRegisters,
                                         CounterCustomRegistry counterCustomRegistry,
                                         ErrorStatusDecoder errorStatusDecoder,
                                         PdlDataConsumer pdlDataConsumer,
                                         TransactionHelperService transactionHelperService,
                                         PersonServiceClient personServiceClient,
                                         TpsPersonService tpsPersonService,
                                         BestillingElasticRepository bestillingElasticRepository) {
        super(
                identService,
                bestillingService,
                objectMapper,
                mapperFacade,
                clientRegisters,
                counterCustomRegistry,
                pdlDataConsumer,
                errorStatusDecoder,
                transactionHelperService,
                tpsPersonService,
                bestillingElasticRepository
        );
        this.personServiceClient = personServiceClient;
    }

    @Async
    public void executeAsync(Bestilling bestilling) {

        log.info("Bestilling med id=#{} og type={} er startet ...", bestilling.getId(), getBestillingType(bestilling));
        MDC.put(MDC_KEY_BESTILLING, bestilling.getId().toString());
        Hooks.onEachOperator(Operators.lift(new ThreadLocalContextLifter<>()));

        RsDollyBestillingRequest bestKriterier = getDollyBestillingRequest(bestilling);
        if (nonNull(bestKriterier)) {

            Flux.fromArray(bestilling.getPdlImport().split(","))
                    .flatMap(testnorgeIdent -> opprettProgress(bestilling, PDL, testnorgeIdent)
                            .flatMap(progress -> opprettDollyPerson(testnorgeIdent, progress, bestilling.getBruker())
                                    .doOnNext(dollyPerson -> leggIdentTilGruppe(testnorgeIdent,
                                            progress, bestKriterier.getBeskrivelse()))
                                    .doOnNext(dollyPerson -> counterCustomRegistry.invoke(bestKriterier))
                                    .flatMap(dollyPerson -> Flux.concat(
                                            gjenopprettKlienter(dollyPerson, bestKriterier,
                                                    fase1Klienter(),
                                                    progress, true),
                                            personServiceClient.syncPerson(dollyPerson, progress)
                                                    .map(ClientFuture::get)
                                                    .filter(BestillingProgress::isPdlSync)
                                                    .flatMap(pdlSync ->
                                                            Flux.concat(
                                                                    tpsPersonService.syncPerson(dollyPerson, bestKriterier,
                                                                                    progress)
                                                                            .map(ClientFuture::get),
                                                                    gjenopprettKlienter(dollyPerson, bestKriterier,
                                                                            fase2Klienter(),
                                                                            progress, true),
                                                                    gjenopprettKlienter(dollyPerson, bestKriterier,
                                                                            fase3Klienter(),
                                                                            progress, true)))))
                                    .doOnError(throwable -> {
                                        var error = errorStatusDecoder.getErrorText(
                                                WebClientFilter.getStatus(throwable), WebClientFilter.getMessage(throwable));
                                        log.error("Feil oppsto ved utføring av bestilling, progressId {} {}",
                                                progress.getId(), error, throwable);
                                        transactionHelperService.persister(progress, BestillingProgress::setFeil, error);
                                    })
                                    .doOnNext(status -> oppdaterStatus(progress))))
                    .takeWhile(test -> !bestillingService.isStoppet(bestilling.getId()))
                    .collectList()
                    .doFinally(done -> {
                        doFerdig(bestilling);
                        saveBestillingToElasticServer(bestKriterier, bestilling);
                    })
                    .subscribe();

        } else {
            bestilling.setFeil("Feil: kunne ikke mappe JSON request, se logg!");
            doFerdig(bestilling);
        }
    }

    private void oppdaterStatus(BestillingProgress progress) {

        transactionHelperService.persister(progress, BestillingProgress::setPdlImportStatus, "OK");
    }
}
