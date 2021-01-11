package no.nav.dolly.bestilling.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.tpsf.TpsfResponseHandler;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.metrics.CounterCustomRegistry;
import no.nav.dolly.service.BestillingProgressService;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.service.TpsfPersonCache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Service
public class GjenopprettGruppeService extends DollyBestillingService {

    private BestillingService bestillingService;
    private ErrorStatusDecoder errorStatusDecoder;
    private TpsfService tpsfService;
    private ForkJoinPool dollyForkJoinPool;

    public GjenopprettGruppeService(TpsfResponseHandler tpsfResponseHandler, TpsfService tpsfService, TpsfPersonCache tpsfPersonCache,
                                    IdentService identService, BestillingProgressService bestillingProgressService,
                                    BestillingService bestillingService, MapperFacade mapperFacade, CacheManager cacheManager,
                                    ObjectMapper objectMapper, List<ClientRegister> clientRegisters, CounterCustomRegistry counterCustomRegistry,
                                    ErrorStatusDecoder errorStatusDecoder, ForkJoinPool dollyForkJoinPool) {
        super(tpsfResponseHandler, tpsfService, tpsfPersonCache, identService, bestillingProgressService, bestillingService,
                mapperFacade, cacheManager, objectMapper, clientRegisters, counterCustomRegistry);

        this.bestillingService = bestillingService;
        this.errorStatusDecoder = errorStatusDecoder;
        this.tpsfService = tpsfService;
        this.dollyForkJoinPool = dollyForkJoinPool;
    }

    @Async
    public void executeAsync(Bestilling bestilling) {

        RsDollyBestillingRequest bestKriterier = getDollyBestillingRequest(bestilling);

        if (nonNull(bestKriterier)) {
            dollyForkJoinPool.submit(() -> {
                bestilling.getGruppe().getTestidenter().parallelStream()
                        .filter(testident -> !bestillingService.isStoppet(bestilling.getId()))
                        .map(testident -> {

                            BestillingProgress progress = new BestillingProgress(bestilling.getId(), testident.getIdent());
                            try {
                                List<Person> personer = tpsfService.hentTestpersoner(List.of(testident.getIdent()));

                                if (!personer.isEmpty()) {
                                    sendTestidenter(bestilling, bestKriterier, progress, personer.get(0));
                                } else {
                                    progress.setFeil("NA:Feil= Finner ikke personen i database");
                                }

                            } catch (RuntimeException e) {
                                progress.setFeil(errorStatusDecoder.decodeRuntimeException(e));

                            } finally {
                                oppdaterProgress(bestilling, progress);
                            }
                            return null;
                        })
                        .collect(Collectors.toList());
                oppdaterBestillingFerdig(bestilling);
            });
        }
    }
}
