package no.nav.dolly.bestilling.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.personservice.PersonServiceClient;
import no.nav.dolly.consumer.pdlperson.PdlPersonConsumer;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.domain.resultset.tpsf.TpsfBestilling;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.util.Objects.nonNull;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static no.nav.dolly.util.MdcUtil.MDC_KEY_BESTILLING;

@Slf4j
@Service
public class LeggTilPaaGruppeService extends DollyBestillingService {

    private final ExecutorService dollyForkJoinPool;

    public LeggTilPaaGruppeService(DollyPersonCache dollyPersonCache, IdentService identService,
                                   BestillingProgressService bestillingProgressService,
                                   BestillingService bestillingService, MapperFacade mapperFacade,
                                   CacheManager cacheManager, ObjectMapper objectMapper,
                                   List<ClientRegister> clientRegisters, CounterCustomRegistry counterCustomRegistry,
                                   ErrorStatusDecoder errorStatusDecoder, ExecutorService dollyForkJoinPool,
                                   PdlPersonConsumer pdlPersonConsumer, PdlDataConsumer pdlDataConsumer,
                                   TransactionHelperService transactionHelperService,
                                   PersonServiceClient personServiceClient) {
        super(dollyPersonCache, identService, bestillingProgressService,
                bestillingService, mapperFacade, cacheManager, objectMapper, clientRegisters, counterCustomRegistry,
                pdlPersonConsumer, pdlDataConsumer, errorStatusDecoder, transactionHelperService,
                personServiceClient);

        this.dollyForkJoinPool = dollyForkJoinPool;
    }

    @Async
    public void executeAsync(Bestilling bestilling) {

        MDC.put(MDC_KEY_BESTILLING, bestilling.getId().toString());
        Hooks.onEachOperator(Operators.lift(new ThreadLocalContextLifter<>()));

        RsDollyBestillingRequest bestKriterier = getDollyBestillingRequest(bestilling);

        if (nonNull(bestKriterier)) {

            TpsfBestilling tpsfBestilling = nonNull(bestKriterier.getTpsf()) ?
                    mapperFacade.map(bestKriterier.getTpsf(), TpsfBestilling.class) : new TpsfBestilling();
            tpsfBestilling.setNavSyntetiskIdent(bestilling.getNavSyntetiskIdent());

            var completableFuture =
                    bestilling.getGruppe().getTestidenter().stream()
                            .map(testident -> doBestilling(bestilling, bestKriterier, tpsfBestilling, testident))
                            .map(computable -> supplyAsync(computable, dollyForkJoinPool))
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
                            log.error("Tidsavbrudd (60 s) ved legg til på gruppe", e);
                            Thread.interrupted();
                        }
                    });

            doFerdig(bestilling);

        } else {
            bestilling.setFeil("Feil: kunne ikke mappe JSON request, se logg!");
            doFerdig(bestilling);
        }
    }

    private BestillingFuture doBestilling(Bestilling bestilling, RsDollyBestillingRequest bestKriterier, TpsfBestilling tpsfBestilling, Testident testident) {

        return () -> {
            if (!bestillingService.isStoppet(bestilling.getId())) {
                BestillingProgress progress = new BestillingProgress(bestilling, testident.getIdent(), testident.getMaster());
                try {
                    DollyPerson dollyPerson = prepareDollyPerson(bestilling, tpsfBestilling, testident, progress);
                    gjenopprettAlleKlienter(dollyPerson, bestKriterier, progress, false);

                } catch (JsonProcessingException e) {
                    progress.setFeil(errorStatusDecoder.decodeException(e));
                } catch (RuntimeException e) {
                    progress.setFeil("NA:" + errorStatusDecoder.decodeThrowable(e));
                } finally {
                    transactionHelperService.oppdaterProgress(progress);
                }
                return progress;
            }
            return null;
        };
    }

    private DollyPerson prepareDollyPerson(Bestilling bestilling, TpsfBestilling tpsfBestilling, no.nav.dolly.domain.jpa.Testident testident, BestillingProgress progress) throws JsonProcessingException {

        DollyPerson dollyPerson = null;
        PdlPerson pdlPerson = objectMapper.readValue(
                pdlPersonConsumer.getPdlPerson(testident.getIdent()).toString(), PdlPerson.class);
//        dollyPerson = dollyPersonCache.preparePdlPersoner(pdlPerson);
        return dollyPerson;
    }
}
