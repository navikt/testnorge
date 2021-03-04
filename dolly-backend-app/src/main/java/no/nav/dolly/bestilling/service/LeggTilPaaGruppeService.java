package no.nav.dolly.bestilling.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.tpsf.TpsfResponseHandler;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.tpsf.RsOppdaterPersonResponse;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;
import no.nav.dolly.domain.resultset.tpsf.TpsfBestilling;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.metrics.CounterCustomRegistry;
import no.nav.dolly.service.BestillingProgressService;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.service.TpsfPersonCache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

@Service
public class LeggTilPaaGruppeService extends DollyBestillingService {

    private MapperFacade mapperFacade;
    private BestillingService bestillingService;
    private TpsfService tpsfService;
    private TpsfPersonCache tpsfPersonCache;
    private ErrorStatusDecoder errorStatusDecoder;
    private ExecutorService dollyForkJoinPool;

    public LeggTilPaaGruppeService(TpsfResponseHandler tpsfResponseHandler, TpsfService tpsfService, TpsfPersonCache tpsfPersonCache,
                                   IdentService identService, BestillingProgressService bestillingProgressService,
                                   BestillingService bestillingService, MapperFacade mapperFacade, CacheManager cacheManager,
                                   ObjectMapper objectMapper, List<ClientRegister> clientRegisters, CounterCustomRegistry counterCustomRegistry,
                                   ErrorStatusDecoder errorStatusDecoder, ExecutorService dollyForkJoinPool) {
        super(tpsfResponseHandler, tpsfService, tpsfPersonCache, identService, bestillingProgressService,
                bestillingService, mapperFacade, cacheManager, objectMapper, clientRegisters, counterCustomRegistry);

        this.mapperFacade = mapperFacade;
        this.bestillingService = bestillingService;
        this.tpsfService = tpsfService;
        this.tpsfPersonCache = tpsfPersonCache;
        this.errorStatusDecoder = errorStatusDecoder;
        this.dollyForkJoinPool = dollyForkJoinPool;
    }

    @Async
    public void executeAsync(Bestilling bestilling) {

        RsDollyBestillingRequest bestKriterier = getDollyBestillingRequest(bestilling);

        if (nonNull(bestKriterier)) {

            TpsfBestilling tpsfBestilling = nonNull(bestKriterier.getTpsf()) ?
                    mapperFacade.map(bestKriterier.getTpsf(), TpsfBestilling.class) : new TpsfBestilling();
            tpsfBestilling.setNavSyntetiskIdent(bestilling.getNavSyntetiskIdent());

            dollyForkJoinPool.submit(() -> {
                bestilling.getGruppe().getTestidenter().parallelStream()
                        .filter(testident -> !bestillingService.isStoppet(bestilling.getId()))
                        .map(testident -> {
                            BestillingProgress progress = new BestillingProgress(bestilling.getId(), testident.getIdent());
                            try {
                                RsOppdaterPersonResponse oppdaterPersonResponse = tpsfService.endreLeggTilPaaPerson(testident.getIdent(), tpsfBestilling);
                                if (!oppdaterPersonResponse.getIdentTupler().isEmpty()) {

                                    sendIdenterTilTPS(new ArrayList<>(List.of(bestilling.getMiljoer().split(","))),
                                            oppdaterPersonResponse.getIdentTupler().stream()
                                                    .map(RsOppdaterPersonResponse.IdentTuple::getIdent).collect(toList()), null, progress);

                                    TpsPerson tpsPerson = tpsfPersonCache.prepareTpsPersoner(oppdaterPersonResponse);
                                    gjenopprettNonTpsf(tpsPerson, bestKriterier, progress, true);

                                } else {
                                    progress.setFeil("NA:Feil= Ident finnes ikke i database");
                                }

                            } catch (RuntimeException e) {
                                progress.setFeil("NA:" + errorStatusDecoder.decodeRuntimeException(e));

                            } finally {
                                oppdaterProgress(bestilling, progress);
                            }
                            return null;
                        }).collect(toList());
                oppdaterBestillingFerdig(bestilling);
            });

        } else {
            bestilling.setFeil("Feil: kunne ikke mappe JSON request, se logg!");
            oppdaterBestillingFerdig(bestilling);
        }
    }
}
