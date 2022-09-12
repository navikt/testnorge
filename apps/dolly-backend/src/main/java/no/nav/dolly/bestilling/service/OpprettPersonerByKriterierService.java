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
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.exceptions.DollyFunctionalException;
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
import java.util.stream.IntStream;

import static java.util.Objects.nonNull;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static no.nav.dolly.domain.jpa.Testident.Master.PDLF;
import static no.nav.dolly.domain.jpa.Testident.Master.TPSF;
import static no.nav.dolly.util.MdcUtil.MDC_KEY_BESTILLING;

@Slf4j
@Service
public class OpprettPersonerByKriterierService extends DollyBestillingService {

    private final BestillingService bestillingService;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final MapperFacade mapperFacade;
    private final IdentService identService;
    private final TpsfService tpsfService;
    private final ExecutorService dollyForkJoinPool;
    private final PdlDataConsumer pdlDataConsumer;
    private final TransactionHelperService transactionHelperService;

    public OpprettPersonerByKriterierService(TpsfService tpsfService,
                                             DollyPersonCache dollyPersonCache, IdentService identService,
                                             BestillingProgressService bestillingProgressService,
                                             BestillingService bestillingService, MapperFacade mapperFacade,
                                             CacheManager cacheManager, ObjectMapper objectMapper,
                                             List<ClientRegister> clientRegisters, CounterCustomRegistry counterCustomRegistry,
                                             ErrorStatusDecoder errorStatusDecoder, ExecutorService dollyForkJoinPool,
                                             PdlPersonConsumer pdlPersonConsumer, PdlDataConsumer pdlDataConsumer, TransactionHelperService transactionHelperService) {
        super(tpsfService, dollyPersonCache, identService, bestillingProgressService,
                bestillingService, mapperFacade, cacheManager, objectMapper, clientRegisters, counterCustomRegistry,
                pdlPersonConsumer, pdlDataConsumer, errorStatusDecoder);

        this.bestillingService = bestillingService;
        this.errorStatusDecoder = errorStatusDecoder;
        this.mapperFacade = mapperFacade;
        this.identService = identService;
        this.tpsfService = tpsfService;
        this.dollyForkJoinPool = dollyForkJoinPool;
        this.pdlDataConsumer = pdlDataConsumer;
        this.transactionHelperService = transactionHelperService;
    }

    private static BestillingProgress buildProgress(Bestilling bestilling, Testident.Master master, String error) {

        return BestillingProgress.builder()
                .bestilling(bestilling)
                .ident("?")
                .feil(TPSF == master ? ("NA:" + error) : null)
                .pdlDataStatus(PDLF == master ? error : null)
                .master(master)
                .build();
    }

    @Async
    public void executeAsync(Bestilling bestilling) {

        log.info("Bestilling med id=#{} er startet ...", bestilling.getId());
        MDC.put(MDC_KEY_BESTILLING, bestilling.getId().toString());
        Hooks.onEachOperator(Operators.lift(new ThreadLocalContextLifter<>()));

        var bestKriterier = getDollyBestillingRequest(bestilling);

        if (nonNull(bestKriterier)) {

            var originator = new OriginatorCommand(bestKriterier, null, mapperFacade).call();

            var computeableFuture = IntStream.range(0, bestilling.getAntallIdenter()).boxed()
                    .map(index -> doBestilling(bestilling, bestKriterier, originator))
                    .map(completable -> supplyAsync(completable, dollyForkJoinPool))
                    .toList();

            computeableFuture
                    .forEach(future -> {
                        try {
                            future.get(60, TimeUnit.MINUTES);
                        } catch (InterruptedException | ExecutionException | TimeoutException e) {
                            log.error(e.getMessage(), e);
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

    private BestillingFuture doBestilling(Bestilling bestilling, RsDollyBestillingRequest bestKriterier, OriginatorCommand.Originator originator) {

        return () -> {

            if (!bestillingService.isStoppet(bestilling.getId())) {

                BestillingProgress progress = null;
                try {
                    var opprettedeIdenter = getOpprettedeIdenter(originator);

                    var dollyPerson = DollyPerson.builder()
                            .hovedperson(opprettedeIdenter.get(0))
                            .master(originator.getMaster())
                            .tags(bestilling.getGruppe().getTags())
                            .build();

                    progress = new BestillingProgress(bestilling, dollyPerson.getHovedperson(), originator.getMaster());

                    identService.saveIdentTilGruppe(dollyPerson.getHovedperson(), bestilling.getGruppe(),
                            originator.getMaster(), bestKriterier.getBeskrivelse());

                    gjenopprettNonTpsf(dollyPerson, bestKriterier, progress, true);

                } catch (RuntimeException e) {
                    progress = buildProgress(bestilling, originator.getMaster(),
                            errorStatusDecoder.decodeRuntimeException(e));

                } finally {
                    transactionHelperService.persist(progress);
                    return progress;
                }
            }
            return null;
        };
    }

    private List<String> getOpprettedeIdenter(OriginatorCommand.Originator originator) {

        if (originator.isTpsf()) {
            return tpsfService.opprettIdenterTpsf(originator.getTpsfBestilling());

        } else if (originator.isPdlf()) {
            var ident = pdlDataConsumer.opprettPdl(originator.getPdlBestilling());
            log.info("Opprettet person med ident {} ", ident);
            return List.of(ident);

        } else {
            throw new DollyFunctionalException("Bestilling er ikke støttet.");
        }
    }
}
