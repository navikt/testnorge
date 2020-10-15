package no.nav.dolly.bestilling.service;

import static java.util.Objects.nonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.tpsf.TpsfResponseHandler;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.tpsf.CheckStatusResponse;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;
import no.nav.dolly.domain.resultset.tpsf.TpsfBestilling;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.metrics.CounterCustomRegistry;
import no.nav.dolly.service.BestillingProgressService;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.service.TpsfPersonCache;

@Service
public class OpprettPersonerFraIdenterMedKriterierService extends DollyBestillingService {

    private BestillingService bestillingService;
    private ErrorStatusDecoder errorStatusDecoder;
    private MapperFacade mapperFacade;
    private TpsfService tpsfService;
    private ForkJoinPool dollyForkJoinPool;

    public OpprettPersonerFraIdenterMedKriterierService(TpsfResponseHandler tpsfResponseHandler, TpsfService tpsfService,
            TpsfPersonCache tpsfPersonCache, IdentService identService, BestillingProgressService bestillingProgressService,
            BestillingService bestillingService, MapperFacade mapperFacade, CacheManager cacheManager,
            ObjectMapper objectMapper, List<ClientRegister> clientRegisters, CounterCustomRegistry counterCustomRegistry,
            ErrorStatusDecoder errorStatusDecoder, ForkJoinPool dollyForkJoinPool) {
        super(tpsfResponseHandler, tpsfService, tpsfPersonCache, identService, bestillingProgressService, bestillingService,
                mapperFacade, cacheManager, objectMapper, clientRegisters, counterCustomRegistry);

        this.bestillingService = bestillingService;
        this.errorStatusDecoder = errorStatusDecoder;
        this.mapperFacade = mapperFacade;
        this.tpsfService = tpsfService;
        this.dollyForkJoinPool = dollyForkJoinPool;
    }

    @Async
    public void executeAsync(Bestilling bestilling) {

        RsDollyBestillingRequest bestKriterier = getDollyBestillingRequest(bestilling);

        if (nonNull(bestKriterier)) {

            CheckStatusResponse tilgjengeligeIdenter = tpsfService.checkEksisterendeIdenter(
                    new ArrayList<>(List.of(bestilling.getOpprettFraIdenter().split(","))));

            dollyForkJoinPool.submit(() -> {
                tilgjengeligeIdenter.getStatuser().parallelStream()
                        .filter(ident -> !bestillingService.isStoppet(bestilling.getId()))
                        .map(identStatus -> {

                            BestillingProgress progress = new BestillingProgress(bestilling.getId(), identStatus.getIdent());
                            try {
                                if (identStatus.isAvailable()) {

                                    TpsfBestilling tpsfBestilling = nonNull(bestKriterier.getTpsf()) ?
                                            mapperFacade.map(bestKriterier.getTpsf(), TpsfBestilling.class) : new TpsfBestilling();
                                    tpsfBestilling.setOpprettFraIdenter(new ArrayList<>(List.of(identStatus.getIdent())));
                                    List<String> leverteIdenter = tpsfService.opprettIdenterTpsf(tpsfBestilling);

                                    sendIdenterTilTPS(new ArrayList<>(List.of(bestilling.getMiljoer().split(","))),
                                            leverteIdenter, bestilling.getGruppe(), progress);

                                    TpsPerson tpsPerson = buildTpsPerson(bestilling, leverteIdenter, null);
                                    gjenopprettNonTpsf(tpsPerson, bestKriterier, progress, false);
                                } else {
                                    progress.setFeil("NA:Feil= Ident er ikke tilgjengelig; " + identStatus.getStatus());
                                }
                            } catch (RuntimeException e) {
                                progress.setFeil("NA:" + errorStatusDecoder.decodeRuntimeException(e));
                            } finally {
                                oppdaterProgress(bestilling, progress);
                            }
                            return null;
                        })
                        .collect(Collectors.toList());
                oppdaterBestillingFerdig(bestilling);
            });
        } else {
            bestilling.setFeil("Feil: kunne ikke mappe JSON request, se logg!");
            oppdaterBestillingFerdig(bestilling);
        }
    }
}
