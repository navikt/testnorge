package no.nav.dolly.bestilling.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.tpsf.TpsfResponseHandler;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.consumer.pdlperson.PdlPersonConsumer;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.tpsf.CheckStatusResponse;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.domain.resultset.tpsf.TpsfBestilling;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.metrics.CounterCustomRegistry;
import no.nav.dolly.service.BestillingProgressService;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.DollyPersonCache;
import no.nav.dolly.service.IdentService;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static java.util.Objects.nonNull;

@Service
public class OpprettPersonerFraIdenterMedKriterierService extends DollyBestillingService {

    private BestillingService bestillingService;
    private ErrorStatusDecoder errorStatusDecoder;
    private MapperFacade mapperFacade;
    private TpsfService tpsfService;
    private ExecutorService dollyForkJoinPool;

    public OpprettPersonerFraIdenterMedKriterierService(TpsfResponseHandler tpsfResponseHandler, TpsfService tpsfService,
                                                        DollyPersonCache dollyPersonCache, IdentService identService,
                                                        BestillingProgressService bestillingProgressService,
                                                        BestillingService bestillingService, MapperFacade mapperFacade,
                                                        CacheManager cacheManager, ObjectMapper objectMapper,
                                                        List<ClientRegister> clientRegisters, CounterCustomRegistry counterCustomRegistry,
                                                        ErrorStatusDecoder errorStatusDecoder, ExecutorService dollyForkJoinPool,
                                                        PdlPersonConsumer pdlPersonConsumer) {
        super(tpsfResponseHandler, tpsfService, dollyPersonCache, identService, bestillingProgressService, bestillingService,
                mapperFacade, cacheManager, objectMapper, clientRegisters, counterCustomRegistry, pdlPersonConsumer);

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
                        .forEach(identStatus -> {

                            BestillingProgress progress = new BestillingProgress(bestilling, identStatus.getIdent(), Testident.Master.TPSF);
                            try {
                                if (identStatus.isAvailable()) {

                                    TpsfBestilling tpsfBestilling = nonNull(bestKriterier.getTpsf()) ?
                                            mapperFacade.map(bestKriterier.getTpsf(), TpsfBestilling.class) : new TpsfBestilling();
                                    tpsfBestilling.setOpprettFraIdenter(new ArrayList<>(List.of(identStatus.getIdent())));
                                    tpsfBestilling.setAntall(tpsfBestilling.getOpprettFraIdenter().size());
                                    List<String> leverteIdenter = tpsfService.opprettIdenterTpsf(tpsfBestilling);

                                    sendIdenterTilTPS(new ArrayList<>(List.of(bestilling.getMiljoer().split(","))),
                                            leverteIdenter, bestilling.getGruppe(), progress);

                                    DollyPerson dollyPerson = DollyPerson.builder()
                                            .hovedperson(leverteIdenter.get(0))
                                            .master(Testident.Master.TPSF)
                                            .build();


                                    gjenopprettNonTpsf(dollyPerson, bestKriterier, progress, false);
                                } else {
                                    progress.setFeil("NA:Feil= Ident er ikke tilgjengelig; " + identStatus.getStatus());
                                }
                            } catch (RuntimeException e) {
                                progress.setFeil("NA:" + errorStatusDecoder.decodeRuntimeException(e));
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
