package no.nav.dolly.bestilling.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.tpsf.TpsfResponseHandler;
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
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

import static java.util.Objects.nonNull;

@Service
public class GjenopprettBestillingService extends DollyBestillingService {

    private BestillingService bestillingService;
    private ErrorStatusDecoder errorStatusDecoder;
    private BestillingProgressService bestillingProgressService;
    private ExecutorService dollyForkJoinPool;

    public GjenopprettBestillingService(TpsfResponseHandler tpsfResponseHandler, TpsfService tpsfService, DollyPersonCache dollyPersonCache,
                                        IdentService identService, BestillingProgressService bestillingProgressService,
                                        BestillingService bestillingService, MapperFacade mapperFacade, CacheManager cacheManager,
                                        ObjectMapper objectMapper, List<ClientRegister> clientRegisters, CounterCustomRegistry counterCustomRegistry,
                                        ErrorStatusDecoder errorStatusDecoder, ExecutorService dollyForkJoinPool,
                                        PdlPersonConsumer pdlPersonConsumer) {
        super(tpsfResponseHandler, tpsfService, dollyPersonCache, identService, bestillingProgressService, bestillingService,
                mapperFacade, cacheManager, objectMapper, clientRegisters, counterCustomRegistry, pdlPersonConsumer);

        this.bestillingService = bestillingService;
        this.errorStatusDecoder = errorStatusDecoder;
        this.bestillingProgressService = bestillingProgressService;
        this.dollyForkJoinPool = dollyForkJoinPool;
    }

    @Async
    public void executeAsync(Bestilling bestilling) {

        RsDollyBestillingRequest bestKriterier = getDollyBestillingRequest(bestilling);

        if (nonNull(bestKriterier)) {
            dollyForkJoinPool.submit(() -> {
                bestillingProgressService.fetchBestillingProgressByBestillingId(bestilling.getOpprettetFraId()).parallelStream()
                        .filter(ident -> !bestillingService.isStoppet(bestilling.getId()))
                        .forEach(gjenopprettFraProgress -> {

                            BestillingProgress progress = new BestillingProgress(bestilling, gjenopprettFraProgress.getIdent(),
                                    gjenopprettFraProgress.getMaster());
                            bestKriterier.setNavSyntetiskIdent(isSyntetisk(gjenopprettFraProgress.getIdent()));
                            bestKriterier.setBeskrivelse(bestilling.getBeskrivelse());

                            try {
                                Optional<DollyPerson> dollyPerson = prepareDollyPersonTpsf(bestilling, progress);

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
                                oppdaterProgress(bestilling, progress);
                            }
                        });
                oppdaterBestillingFerdig(bestilling);
            });
        } else {
            bestilling.setFeil("Feil: kunne ikke mappe JSON request, se logg!");
            oppdaterBestillingFerdig(bestilling);
        }
    }
}
