package no.nav.dolly.bestilling.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.consumer.pdlperson.PdlPersonConsumer;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.metrics.CounterCustomRegistry;
import no.nav.dolly.service.*;
import no.nav.dolly.util.ThreadLocalContextLifter;
import no.nav.dolly.util.TransactionHelperService;
import org.slf4j.MDC;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Operators;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.util.Objects.nonNull;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static no.nav.dolly.util.MdcUtil.MDC_KEY_BESTILLING;

@Slf4j
@Service
public class OpprettPersonerFraIdenterMedKriterierService extends DollyBestillingService {

    private final BestillingService bestillingService;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final MapperFacade mapperFacade;
    private final ExecutorService dollyForkJoinPool;
    private final PdlDataConsumer pdlDataConsumer;
    private final IdentService identService;
    private final TransactionHelperService transactionHelperService;

    public OpprettPersonerFraIdenterMedKriterierService(TpsfService tpsfService,
                                                        DollyPersonCache dollyPersonCache, IdentService identService,
                                                        BestillingProgressService bestillingProgressService,
                                                        BestillingService bestillingService, MapperFacade mapperFacade,
                                                        CacheManager cacheManager, ObjectMapper objectMapper,
                                                        List<ClientRegister> clientRegisters,
                                                        CounterCustomRegistry counterCustomRegistry,
                                                        ErrorStatusDecoder errorStatusDecoder,
                                                        ExecutorService dollyForkJoinPool,
                                                        PdlPersonConsumer pdlPersonConsumer,
                                                        PdlDataConsumer pdlDataConsumer,
                                                        TransactionHelperService transactionHelperService,
                                                        AutentisertBrukerService bruker) {
        super(tpsfService, dollyPersonCache, identService, bestillingProgressService, bestillingService,
                mapperFacade, cacheManager, objectMapper, clientRegisters, counterCustomRegistry, pdlPersonConsumer,
                pdlDataConsumer, errorStatusDecoder, bruker);

        this.bestillingService = bestillingService;
        this.errorStatusDecoder = errorStatusDecoder;
        this.mapperFacade = mapperFacade;
        this.dollyForkJoinPool = dollyForkJoinPool;
        this.pdlDataConsumer = pdlDataConsumer;
        this.identService = identService;
        this.transactionHelperService = transactionHelperService;
    }

    @Async
    public void executeAsync(Bestilling bestilling) {

        log.info("Bestilling med id=#{} og type={} er startet ...", bestilling.getId(), getBestillingType(bestilling));
        MDC.put(MDC_KEY_BESTILLING, bestilling.getId().toString());
        Hooks.onEachOperator(Operators.lift(new ThreadLocalContextLifter<>()));

        RsDollyBestillingRequest bestKriterier = getDollyBestillingRequest(bestilling);

        if (nonNull(bestKriterier)) {

            var tilgjengeligeIdenter = new AvailCheckCommand(
                    bestilling.getOpprettFraIdenter(),
                    pdlDataConsumer).call();

            var completableFuture = tilgjengeligeIdenter.stream()
                    .map(availStatus -> completeableFuture(bestilling, bestKriterier, availStatus))
                    .map(completeable -> supplyAsync(completeable, dollyForkJoinPool))
                    .toList();

            completableFuture
                    .forEach(future -> {
                        try {
                            future.get(60, TimeUnit.SECONDS);
                        } catch (InterruptedException e) {
                            log.error(e.getMessage(), e);
                            Thread.currentThread().interrupt();
                        } catch (ExecutionException e) {
                            log.error(e.getMessage(), e);
                            Thread.interrupted();
                        } catch (TimeoutException e) {
                            log.error("Tidsavbrudd (60 s) ved opprett personer fra identer", e);
                            Thread.interrupted();
                        }
                    });

            doFerdig(bestilling);

        } else {

            bestilling.setFeil("Feil: kunne ikke mappe JSON request, se logg!");
            doFerdig(bestilling);
        }
    }

    private void doFerdig(Bestilling bestilling) {

        transactionHelperService.oppdaterBestillingFerdig(bestilling);
        MDC.remove(MDC_KEY_BESTILLING);
        log.info("Bestilling med id=#{} er ferdig", bestilling.getId());
    }

    private BestillingFuture completeableFuture(Bestilling bestilling, RsDollyBestillingRequest bestKriterier, AvailCheckCommand.AvailStatus identStatus) {

        return () -> {
            if (!bestillingService.isStoppet(bestilling.getId())) {
                BestillingProgress progress = new BestillingProgress(bestilling, identStatus.getIdent(), identStatus.getMaster());

                try {
                    if (identStatus.isAvailable()) {

                        var opprettetIdent = new OpprettCommand(identStatus, bestKriterier,
                                pdlDataConsumer, mapperFacade).call();

                        identService.saveIdentTilGruppe(identStatus.getIdent(), bestilling.getGruppe(),
                                identStatus.getMaster(), bestKriterier.getBeskrivelse());

                        DollyPerson dollyPerson = DollyPerson.builder()
                                .hovedperson(opprettetIdent)
                                .master(identStatus.getMaster())
                                .tags(bestilling.getGruppe().getTags())
                                .build();
                        gjenopprettNonTpsf(getAutentisertBruker(), dollyPerson, bestKriterier, progress, true);
                    } else {
                        progress.setFeil("NA:Feil= Ident er ikke tilgjengelig; " + identStatus.getMessage());
                    }
                } catch (RuntimeException e) {
                    progress.setFeil("NA:" + errorStatusDecoder.decodeThrowable(e));
                } finally {
                    transactionHelperService.persist(progress);
                }
                return progress;
            }
            return null;
        };
    }
}
