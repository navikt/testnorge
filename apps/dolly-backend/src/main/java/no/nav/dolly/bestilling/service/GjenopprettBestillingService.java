package no.nav.dolly.bestilling.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.slf4j.MDC;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Operators;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

import static java.time.LocalDateTime.now;
import static java.util.Objects.nonNull;
import static no.nav.dolly.util.MdcUtil.MDC_KEY_BESTILLING;

@Service
public class GjenopprettBestillingService extends DollyBestillingService {

    private final BestillingService bestillingService;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final BestillingProgressService bestillingProgressService;
    private final ExecutorService dollyForkJoinPool;
    private final TransactionTemplate transactionTemplate;
    private final EntityManager entityManager;

    public GjenopprettBestillingService(TpsfService tpsfService, DollyPersonCache dollyPersonCache,
                                        IdentService identService, BestillingProgressService bestillingProgressService,
                                        BestillingService bestillingService, MapperFacade mapperFacade, CacheManager cacheManager,
                                        ObjectMapper objectMapper, List<ClientRegister> clientRegisters, CounterCustomRegistry counterCustomRegistry,
                                        ErrorStatusDecoder errorStatusDecoder, ExecutorService dollyForkJoinPool,
                                        PdlPersonConsumer pdlPersonConsumer, PdlDataConsumer pdlDataConsumer,
                                        PlatformTransactionManager transactionManager, EntityManager entityManager) {
        super(tpsfService, dollyPersonCache, identService, bestillingProgressService, bestillingService,
                mapperFacade, cacheManager, objectMapper, clientRegisters, counterCustomRegistry, pdlPersonConsumer,
                pdlDataConsumer, errorStatusDecoder);

        this.bestillingService = bestillingService;
        this.errorStatusDecoder = errorStatusDecoder;
        this.bestillingProgressService = bestillingProgressService;
        this.dollyForkJoinPool = dollyForkJoinPool;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.entityManager = entityManager;
    }

    @Async
    @SuppressWarnings("java:S1143")
    public void executeAsync(Bestilling bestilling) {

        MDC.put(MDC_KEY_BESTILLING, bestilling.getId().toString());
        Hooks.onEachOperator(Operators.lift(new ThreadLocalContextLifter<>()));
        RsDollyBestillingRequest bestKriterier = getDollyBestillingRequest(bestilling);

        if (nonNull(bestKriterier)) {
            bestKriterier.setEkskluderEksternePersoner(true);
            dollyForkJoinPool.submit(() -> {
                bestillingProgressService.fetchBestillingProgressByBestillingId(bestilling.getOpprettetFraId()).parallelStream()
                        .filter(ident -> !bestillingService.isStoppet(bestilling.getId()))
                        .forEach(gjenopprettFraProgress -> {

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
                                persist(progress);
                            }
                        });
                oppdaterBestillingFerdig(bestilling);
                MDC.remove(MDC_KEY_BESTILLING);
            });
        } else {
            bestilling.setFeil("Feil: kunne ikke mappe JSON request, se logg!");
            oppdaterBestillingFerdig(bestilling);
        }
    }

    private void persist(BestillingProgress progress) {

        transactionTemplate.execute(status -> {
            var best = entityManager.find(Bestilling.class, progress.getBestilling().getId());
            entityManager.persist(progress);
            best.setSistOppdatert(now());
            entityManager.merge(best);
            clearCache();
            return null;
        });
    }
}
