package no.nav.dolly.bestilling.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.tpsf.TpsfResponseHandler;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.consumer.pdlperson.PdlPersonConsumer;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Testident;
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

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.jpa.Testident.Master.PDL;

@Slf4j
@Service
public class OpprettPersonerByKriterierService extends DollyBestillingService {

    private BestillingService bestillingService;
    private ErrorStatusDecoder errorStatusDecoder;
    private MapperFacade mapperFacade;
    private IdentService identService;
    private TpsfService tpsfService;
    private ExecutorService dollyForkJoinPool;
    private PdlDataConsumer pdlDataConsumer;

    public OpprettPersonerByKriterierService(TpsfResponseHandler tpsfResponseHandler, TpsfService tpsfService,
                                             DollyPersonCache dollyPersonCache, IdentService identService,
                                             BestillingProgressService bestillingProgressService,
                                             BestillingService bestillingService, MapperFacade mapperFacade,
                                             CacheManager cacheManager, ObjectMapper objectMapper,
                                             List<ClientRegister> clientRegisters, CounterCustomRegistry counterCustomRegistry,
                                             ErrorStatusDecoder errorStatusDecoder, ExecutorService dollyForkJoinPool,
                                             PdlPersonConsumer pdlPersonConsumer, PdlDataConsumer pdlDataConsumer) {
        super(tpsfResponseHandler, tpsfService, dollyPersonCache, identService, bestillingProgressService,
                bestillingService, mapperFacade, cacheManager, objectMapper, clientRegisters, counterCustomRegistry,
                pdlPersonConsumer, pdlDataConsumer);

        this.bestillingService = bestillingService;
        this.errorStatusDecoder = errorStatusDecoder;
        this.mapperFacade = mapperFacade;
        this.identService = identService;
        this.tpsfService = tpsfService;
        this.dollyForkJoinPool = dollyForkJoinPool;
        this.pdlDataConsumer = pdlDataConsumer;
    }

    private static BestillingProgress buildProgress(Bestilling bestilling, Testident.Master master, String error) {

        return BestillingProgress.builder()
                .bestilling(bestilling)
                .ident("?")
                .feil("NA:" + error)
                .master(master)
                .build();
    }

    @Async
    public void executeAsync(Bestilling bestilling) {

        RsDollyBestillingRequest bestKriterier = getDollyBestillingRequest(bestilling);

        if (nonNull(bestKriterier)) {

            var originator = new OriginatorCommand(bestKriterier, mapperFacade).call();

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
                                        .build();

                                progress = new BestillingProgress(bestilling, dollyPerson.getHovedperson(), originator.getMaster());

                                if (originator.isTpsf()) {
                                    sendIdenterTilTPS(List.of(bestilling.getMiljoer().split(",")),
                                            opprettedeIdenter, bestilling.getGruppe(), progress, bestKriterier.getBeskrivelse());

                                } else {
                                    identService.saveIdentTilGruppe(dollyPerson.getHovedperson(), bestilling.getGruppe(),
                                            PDL, bestKriterier.getBeskrivelse());
                                }

                                gjenopprettNonTpsf(dollyPerson, bestKriterier, progress, true);

                            } catch (RuntimeException e) {
                                progress = buildProgress(bestilling, originator.getMaster(),
                                        errorStatusDecoder.decodeRuntimeException(e));

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

    private List<String> getOpprettedeIdenter(OriginatorCommand.Originator originator) {

        return originator.isTpsf() ?
                tpsfService.opprettIdenterTpsf(originator.getTpsfBestilling()) :
                List.of(pdlDataConsumer.opprettPdl(originator.getPdlBestilling()));
    }
}
