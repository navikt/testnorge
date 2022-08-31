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
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.exceptions.DollyFunctionalException;
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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static java.time.LocalDateTime.now;
import static java.util.Objects.nonNull;
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
    private final TransactionTemplate transactionTemplate;
    private final EntityManager entityManager;

    public OpprettPersonerByKriterierService(TpsfService tpsfService,
                                             DollyPersonCache dollyPersonCache, IdentService identService,
                                             BestillingProgressService bestillingProgressService,
                                             BestillingService bestillingService, MapperFacade mapperFacade,
                                             CacheManager cacheManager, ObjectMapper objectMapper,
                                             List<ClientRegister> clientRegisters, CounterCustomRegistry counterCustomRegistry,
                                             ErrorStatusDecoder errorStatusDecoder, ExecutorService dollyForkJoinPool,
                                             PdlPersonConsumer pdlPersonConsumer, PdlDataConsumer pdlDataConsumer,
                                             PlatformTransactionManager transactionManager, EntityManager entityManager) {
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
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.entityManager = entityManager;
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
    @SuppressWarnings("java:S1143")
    public void executeAsync(Bestilling bestilling) {

        MDC.put(MDC_KEY_BESTILLING, bestilling.getId().toString());
        Hooks.onEachOperator(Operators.lift(new ThreadLocalContextLifter<>()));

        var bestKriterier = getDollyBestillingRequest(bestilling);

        if (nonNull(bestKriterier)) {

            var originator = new OriginatorCommand(bestKriterier, null, mapperFacade).call();

            dollyForkJoinPool.submit(() -> {
                Collections.nCopies(bestilling.getAntallIdenter(), true).parallelStream()
                        .filter(ident -> !bestillingService.isStoppet(bestilling.getId()))
                        .forEach(ident -> {

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
