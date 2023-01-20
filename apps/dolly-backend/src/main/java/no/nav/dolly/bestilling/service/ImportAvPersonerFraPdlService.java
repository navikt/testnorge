package no.nav.dolly.bestilling.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.personservice.PersonServiceClient;
import no.nav.dolly.consumer.pdlperson.PdlPersonConsumer;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.metrics.CounterCustomRegistry;
import no.nav.dolly.service.BestillingProgressService;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.DollyPersonCache;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.util.ThreadLocalContextLifter;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.dolly.util.WebClientFilter;
import org.slf4j.MDC;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Operators;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.jpa.Testident.Master.PDL;
import static no.nav.dolly.util.MdcUtil.MDC_KEY_BESTILLING;

@Slf4j
@Service
public class ImportAvPersonerFraPdlService extends DollyBestillingService {

    private static final String FEIL = "FEIL: Feilet å importere identer fra Testnorge";

    public ImportAvPersonerFraPdlService(DollyPersonCache dollyPersonCache,
                                         IdentService identService, BestillingProgressService bestillingProgressService,
                                         BestillingService bestillingService, MapperFacade mapperFacade,
                                         CacheManager cacheManager, ObjectMapper objectMapper,
                                         List<ClientRegister> clientRegisters, CounterCustomRegistry counterCustomRegistry,
                                         ErrorStatusDecoder errorStatusDecoder,
                                         PdlPersonConsumer pdlPersonConsumer, PdlDataConsumer pdlDataConsumer,
                                         TransactionHelperService transactionHelperService,
                                         PersonServiceClient personServiceClient) {
        super(dollyPersonCache, identService, bestillingProgressService, bestillingService,
                mapperFacade, cacheManager, objectMapper, clientRegisters, counterCustomRegistry, pdlPersonConsumer,
                pdlDataConsumer, errorStatusDecoder, transactionHelperService, personServiceClient);
    }

    @Async
    public void executeAsync(Bestilling bestilling) {

        log.info("Bestilling med id=#{} og type={} er startet ...", bestilling.getId(), getBestillingType(bestilling));
        MDC.put(MDC_KEY_BESTILLING, bestilling.getId().toString());
        Hooks.onEachOperator(Operators.lift(new ThreadLocalContextLifter<>()));

        RsDollyBestillingRequest bestKriterier = getDollyBestillingRequest(bestilling);

        if (nonNull(bestKriterier)) {

            var test = Flux.fromArray(bestilling.getPdlImport().split(","))
                    .filter(testnorgeIdent -> !bestillingService.isStoppet(bestilling.getId()))
                    .flatMap(testnorgeIdent -> opprettProgress(bestilling, PDL)
                            .flatMap(progress -> leggIdentTilGruppe(testnorgeIdent,
                                    progress, bestKriterier.getBeskrivelse())
                                    .flatMap(dollyPerson -> Flux.concat(
                                            gjenopprettKlienter(dollyPerson, bestKriterier,
                                                    fase1Klienter(),
                                                    progress, true),
                                            personServiceClient.gjenopprett(null,
                                                            dollyPerson, progress, true)
                                                    .map(ClientFuture::get)
                                                    .map(BestillingProgress::isPdlSync)
                                                    .flatMap(pdlSync -> pdlSync ?
                                                            Flux.concat(
                                                                    gjenopprettKlienter(dollyPerson, bestKriterier,
                                                                            fase2Klienter(),
                                                                            progress, true),
                                                                    gjenopprettKlienter(dollyPerson, bestKriterier,
                                                                            fase3Klienter(),
                                                                            progress, true)) :
                                                            Flux.empty())
                                                    .filter(Objects::nonNull)))
                                    .collectList()
                                    .doOnNext(status -> oppdaterStatus(progress))
                                    .doOnError(throwable -> {
                                        var error = errorStatusDecoder.getErrorText(
                                                WebClientFilter.getStatus(throwable), WebClientFilter.getMessage(throwable));
                                        log.error("Feil oppsto ved utføring av bestilling, progressId {} {}",
                                                progress.getId(), error);
                                        bestilling.setFeil(error);
                                    })))
                    .collectList()
                    .subscribe(done -> doFerdig(bestilling));

        } else {
            bestilling.setFeil("Feil: kunne ikke mappe JSON request, se logg!");
            doFerdig(bestilling);
        }
    }

    private void oppdaterStatus(BestillingProgress progress) {

        progress.setPdlImportStatus(progress.isPdlSync() ? SUCCESS : FEIL);
        transactionHelperService.persister(progress);
    }
}
