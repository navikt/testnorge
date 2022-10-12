package no.nav.dolly.bestilling.service;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import no.nav.dolly.service.BestillingProgressService;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.DollyPersonCache;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.util.ThreadLocalContextLifter;
import no.nav.dolly.util.TransactionHelperService;
import org.slf4j.MDC;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Operators;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.util.Objects.nonNull;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static no.nav.dolly.util.MdcUtil.MDC_KEY_BESTILLING;

@Slf4j
@Service
public class GjenopprettBestillingService extends DollyBestillingService {

    private final BestillingService bestillingService;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final BestillingProgressService bestillingProgressService;
    private final ExecutorService dollyForkJoinPool;
    private final TransactionHelperService transactionHelperService;

    public GjenopprettBestillingService(TpsfService tpsfService, DollyPersonCache dollyPersonCache,
                                        IdentService identService, BestillingProgressService bestillingProgressService,
                                        BestillingService bestillingService, MapperFacade mapperFacade, CacheManager cacheManager,
                                        ObjectMapper objectMapper, List<ClientRegister> clientRegisters, CounterCustomRegistry counterCustomRegistry,
                                        ErrorStatusDecoder errorStatusDecoder, ExecutorService dollyForkJoinPool,
                                        PdlPersonConsumer pdlPersonConsumer, PdlDataConsumer pdlDataConsumer, TransactionHelperService transactionHelperService) {
        super(tpsfService, dollyPersonCache, identService, bestillingProgressService, bestillingService,
                mapperFacade, cacheManager, objectMapper, clientRegisters, counterCustomRegistry, pdlPersonConsumer,
                pdlDataConsumer, errorStatusDecoder);

        this.bestillingService = bestillingService;
        this.errorStatusDecoder = errorStatusDecoder;
        this.bestillingProgressService = bestillingProgressService;
        this.dollyForkJoinPool = dollyForkJoinPool;
        this.transactionHelperService = transactionHelperService;
    }

    @Async
    public void executeAsync(Bestilling bestilling) {

        log.info("Bestilling med id=#{} er startet ...", bestilling.getId());
        MDC.put(MDC_KEY_BESTILLING, bestilling.getId().toString());
        Hooks.onEachOperator(Operators.lift(new ThreadLocalContextLifter<>()));
        RsDollyBestillingRequest bestKriterier = getDollyBestillingRequest(bestilling);

        if (nonNull(bestKriterier)) {
            bestKriterier.setEkskluderEksternePersoner(true);
            var computableFuture =
                    bestillingProgressService.fetchBestillingProgressByBestillingId(bestilling.getOpprettetFraId()).stream()
                            .map(progress -> doBestilling(bestilling, bestKriterier, progress))
                            .map(computable -> supplyAsync(computable, dollyForkJoinPool))
                            .toList();

            computableFuture
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
                            log.error("Tidsavbrudd (60 s) ved gjenopprett fra bestilling");
                            Thread.interrupted();
                        }
                    });
            doFerdig(bestilling);

        } else {
            bestilling.setFeil("Feil: kunne ikke mappe JSON request, se logg!");
            oppdaterBestillingFerdig(bestilling);
        }
    }

    private void doFerdig(Bestilling bestilling) {

        transactionHelperService.oppdaterBestillingFerdig(bestilling);
        MDC.remove(MDC_KEY_BESTILLING);
        log.info("Bestilling med id=#{} er ferdig", bestilling.getId());
    }

    private BestillingFuture doBestilling(Bestilling bestilling, RsDollyBestillingRequest bestKriterier, BestillingProgress gjenopprettFraProgress) {

        return () -> {
            if (!bestillingService.isStoppet(bestilling.getId())) {
                BestillingProgress progress = new BestillingProgress(bestilling, gjenopprettFraProgress.getIdent(),
                        gjenopprettFraProgress.getMaster());
                bestKriterier.setNavSyntetiskIdent(isSyntetisk(gjenopprettFraProgress.getIdent()));
                bestKriterier.setBeskrivelse(bestilling.getBeskrivelse());

                try {
                    Optional<DollyPerson> dollyPerson = prepareDollyPerson(progress);

                    if (dollyPerson.isPresent()) {

                        gjenopprettNonTpsf(dollyPerson.get(), bestKriterier, progress, false);
                    } else {
                        progress.setFeil("NA:Feil= Finner ikke personen i database");
                    }

                } catch (JsonProcessingException e) {
                    progress.setFeil(errorStatusDecoder.decodeException(e));

                } catch (RuntimeException e) {
                    progress.setFeil(errorStatusDecoder.decodeRuntimeException(e));

                } finally {
                    transactionHelperService.persist(progress);
                }
                return progress;
            }
            return null;
        };
    }
}
