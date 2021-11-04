package no.nav.dolly.bestilling.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.tpsf.TpsfImportPersonRequest;
import no.nav.dolly.bestilling.tpsf.TpsfResponseHandler;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.consumer.pdlperson.PdlPersonConsumer;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.domain.resultset.tpsf.Person;
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

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static no.nav.dolly.domain.jpa.Testident.Master.TPSF;

@Service
public class ImportAvPersonerFraTpsService extends DollyBestillingService {

    private TpsfService tpsfService;
    private DollyPersonCache dollyPersonCache;
    private ErrorStatusDecoder errorStatusDecoder;
    private ExecutorService dollyForkJoinPool;

    public ImportAvPersonerFraTpsService(TpsfResponseHandler tpsfResponseHandler, TpsfService tpsfService,
                                         DollyPersonCache dollyPersonCache, IdentService identService,
                                         BestillingProgressService bestillingProgressService,
                                         BestillingService bestillingService, MapperFacade mapperFacade,
                                         CacheManager cacheManager, ObjectMapper objectMapper,
                                         List<ClientRegister> clientRegisters, CounterCustomRegistry counterCustomRegistry,
                                         ErrorStatusDecoder errorStatusDecoder, ExecutorService dollyForkJoinPool,
                                         PdlPersonConsumer pdlPersonConsumer) {
        super(tpsfResponseHandler, tpsfService, dollyPersonCache, identService, bestillingProgressService, bestillingService,
                mapperFacade, cacheManager, objectMapper, clientRegisters, counterCustomRegistry, pdlPersonConsumer);

        this.tpsfService = tpsfService;
        this.dollyPersonCache = dollyPersonCache;
        this.errorStatusDecoder = errorStatusDecoder;
        this.dollyForkJoinPool = dollyForkJoinPool;
    }

    @Async
    public void executeAsync(Bestilling bestilling) {

        RsDollyBestillingRequest bestKriterier = getDollyBestillingRequest(bestilling);

        if (nonNull(bestKriterier)) {

            dollyForkJoinPool.submit(() -> {
                asList(bestilling.getTpsImport().split(",")).parallelStream()
                        .filter(ident -> !bestilling.isStoppet())
                        .forEach(ident -> {
                            BestillingProgress progress = new BestillingProgress(bestilling, ident, TPSF);
                            try {
                                Person person = tpsfService.importerPersonFraTps(TpsfImportPersonRequest.builder()
                                        .miljoe(bestilling.getKildeMiljoe())
                                        .ident(ident)
                                        .build());

                                progress.setTpsImportStatus(SUCCESS);

                                sendIdenterTilTPS(List.of(bestilling.getMiljoer().split(","))
                                        .stream().filter(miljoe -> !bestilling.getKildeMiljoe().equalsIgnoreCase(miljoe))
                                        .collect(toList()), singletonList(ident), bestilling.getGruppe(), progress,
                                        bestilling.getBeskrivelse());

                                DollyPerson dollyPerson = dollyPersonCache.prepareTpsPersoner(person);
                                gjenopprettNonTpsf(dollyPerson, bestKriterier, progress, false);

                            } catch (RuntimeException e) {
                                progress.setTpsImportStatus(errorStatusDecoder.decodeRuntimeException(e));

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
