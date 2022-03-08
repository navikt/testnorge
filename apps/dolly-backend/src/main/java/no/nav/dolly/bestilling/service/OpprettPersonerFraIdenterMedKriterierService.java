package no.nav.dolly.bestilling.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
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
import java.util.concurrent.ExecutorService;

import static java.util.Objects.nonNull;

@Service
public class OpprettPersonerFraIdenterMedKriterierService extends DollyBestillingService {

    private BestillingService bestillingService;
    private ErrorStatusDecoder errorStatusDecoder;
    private MapperFacade mapperFacade;
    private TpsfService tpsfService;
    private ExecutorService dollyForkJoinPool;
    private PdlDataConsumer pdlDataConsumer;
    private IdentService identService;

    public OpprettPersonerFraIdenterMedKriterierService(TpsfResponseHandler tpsfResponseHandler, TpsfService tpsfService,
                                                        DollyPersonCache dollyPersonCache, IdentService identService,
                                                        BestillingProgressService bestillingProgressService,
                                                        BestillingService bestillingService, MapperFacade mapperFacade,
                                                        CacheManager cacheManager, ObjectMapper objectMapper,
                                                        List<ClientRegister> clientRegisters, CounterCustomRegistry counterCustomRegistry,
                                                        ErrorStatusDecoder errorStatusDecoder, ExecutorService dollyForkJoinPool,
                                                        PdlPersonConsumer pdlPersonConsumer, PdlDataConsumer pdlDataConsumer) {
        super(tpsfResponseHandler, tpsfService, dollyPersonCache, identService, bestillingProgressService, bestillingService,
                mapperFacade, cacheManager, objectMapper, clientRegisters, counterCustomRegistry, pdlPersonConsumer, pdlDataConsumer);

        this.bestillingService = bestillingService;
        this.errorStatusDecoder = errorStatusDecoder;
        this.mapperFacade = mapperFacade;
        this.tpsfService = tpsfService;
        this.dollyForkJoinPool = dollyForkJoinPool;
        this.pdlDataConsumer = pdlDataConsumer;
        this.identService = identService;
    }

    @Async
    public void executeAsync(Bestilling bestilling) {

        RsDollyBestillingRequest bestKriterier = getDollyBestillingRequest(bestilling);

        if (nonNull(bestKriterier)) {

            var tilgjengeligeIdenter = new AvailCheckCommand(bestilling.getOpprettFraIdenter(),
                    bestKriterier.getPdldata(), tpsfService, pdlDataConsumer).call();

            dollyForkJoinPool.submit(() -> {
                tilgjengeligeIdenter.parallelStream()
                        .filter(ident -> !bestillingService.isStoppet(bestilling.getId()))
                        .forEach(identStatus -> {

                            BestillingProgress progress = new BestillingProgress(bestilling, identStatus.getIdent(), identStatus.getMaster());

                            try {
                                if (identStatus.isAvailable()) {

                                    var leverteIdenter = new OpprettCommand(identStatus, bestKriterier, tpsfService,
                                            pdlDataConsumer, mapperFacade).call();

                                    if (identStatus.isTpsf()) {
                                        sendIdenterTilTPS(List.of(bestilling.getMiljoer().split(",")),
                                                leverteIdenter, bestilling.getGruppe(), progress, bestilling.getBeskrivelse());

                                    } else {
                                        identService.saveIdentTilGruppe(identStatus.getIdent(), bestilling.getGruppe(),
                                                identStatus.getMaster(), bestKriterier.getBeskrivelse());
                                    }

                                    DollyPerson dollyPerson = DollyPerson.builder()
                                            .hovedperson(leverteIdenter.get(0))
                                            .master(identStatus.getMaster())
                                            .tags(bestKriterier.getTags())
                                            .build();

                                    gjenopprettNonTpsf(dollyPerson, bestKriterier, progress, true);
                                } else {
                                    progress.setFeil("NA:Feil= Ident er ikke tilgjengelig; " + identStatus.getMessage());
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
