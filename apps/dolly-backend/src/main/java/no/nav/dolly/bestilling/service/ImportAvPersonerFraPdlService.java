package no.nav.dolly.bestilling.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.consumer.pdlperson.PdlPersonConsumer;
import no.nav.dolly.domain.PdlPerson;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static no.nav.dolly.domain.jpa.Testident.Master.PDL;
import static no.nav.dolly.util.MdcUtil.MDC_KEY_BESTILLING;

@Slf4j
@Service
public class ImportAvPersonerFraPdlService extends DollyBestillingService {

    private final DollyPersonCache dollyPersonCache;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final ExecutorService dollyForkJoinPool;
    private final PdlPersonConsumer pdlPersonConsumer;
    private final ObjectMapper objectMapper;
    private final IdentService identService;
    private final TransactionHelperService transactionHelperService;

    public ImportAvPersonerFraPdlService(TpsfService tpsfService, DollyPersonCache dollyPersonCache,
                                         IdentService identService, BestillingProgressService bestillingProgressService,
                                         BestillingService bestillingService, MapperFacade mapperFacade,
                                         CacheManager cacheManager, ObjectMapper objectMapper,
                                         List<ClientRegister> clientRegisters, CounterCustomRegistry counterCustomRegistry,
                                         ErrorStatusDecoder errorStatusDecoder, ExecutorService dollyForkJoinPool,
                                         PdlPersonConsumer pdlPersonConsumer, PdlDataConsumer pdlDataConsumer, TransactionHelperService transactionHelperService) {
        super(tpsfService, dollyPersonCache, identService, bestillingProgressService, bestillingService,
                mapperFacade, cacheManager, objectMapper, clientRegisters, counterCustomRegistry, pdlPersonConsumer,
                pdlDataConsumer, errorStatusDecoder);

        this.dollyPersonCache = dollyPersonCache;
        this.errorStatusDecoder = errorStatusDecoder;
        this.dollyForkJoinPool = dollyForkJoinPool;
        this.objectMapper = objectMapper;
        this.pdlPersonConsumer = pdlPersonConsumer;
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

            var completableFuture =
                    Stream.of(bestilling.getPdlImport().split(","))
                            .map(ident -> doBestilling(bestilling, bestKriterier, ident))
                            .map(computeable -> supplyAsync(computeable, dollyForkJoinPool))
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
                            log.error("Tidsavbrudd (60 s) ved import av testnorge personer", e);
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

    private BestillingFuture doBestilling(Bestilling bestilling, RsDollyBestillingRequest bestKriterier, String ident) {

        return () -> {
            if (!bestilling.isStoppet()) {
                BestillingProgress progress = new BestillingProgress(bestilling, ident, PDL);
                try {

                    PdlPerson pdlPerson = objectMapper.readValue(pdlPersonConsumer.getPdlPerson(ident).toString(), PdlPerson.class);
                    DollyPerson dollyPerson = dollyPersonCache.preparePdlPersoner(pdlPerson);
                    identService.saveIdentTilGruppe(dollyPerson.getHovedperson(), bestilling.getGruppe(),
                            PDL, bestilling.getBeskrivelse());
                    gjenopprettAlleKlienter(dollyPerson, bestKriterier, progress, true);
                    progress.setPdlImportStatus(SUCCESS);

                } catch (JsonProcessingException e) {
                    progress.setPdlImportStatus(errorStatusDecoder.decodeException(e));

                } catch (RuntimeException e) {
                    progress.setPdlImportStatus(errorStatusDecoder.decodeThrowable(e));

                } finally {
                    transactionHelperService.oppdaterProgress(progress);
                }
                return progress;
            }
            return null;
        };
    }
}
