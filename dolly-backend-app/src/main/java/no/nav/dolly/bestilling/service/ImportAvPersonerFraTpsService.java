package no.nav.dolly.bestilling.service;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.tpsf.TpsfImportPersonRequest;
import no.nav.dolly.bestilling.tpsf.TpsfResponseHandler;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.domain.jpa.postgres.Bestilling;
import no.nav.dolly.domain.jpa.postgres.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.metrics.CounterCustomRegistry;
import no.nav.dolly.service.BestillingProgressService;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.service.TpsfPersonCache;

@Service
public class ImportAvPersonerFraTpsService extends DollyBestillingService {

    private TpsfService tpsfService;
    private TpsfPersonCache tpsfPersonCache;
    private ErrorStatusDecoder errorStatusDecoder;
    private ForkJoinPool dollyForkJoinPool;

    public ImportAvPersonerFraTpsService(TpsfResponseHandler tpsfResponseHandler, TpsfService tpsfService, TpsfPersonCache tpsfPersonCache,
            IdentService identService, BestillingProgressService bestillingProgressService,
            BestillingService bestillingService, MapperFacade mapperFacade, CacheManager cacheManager,
            ObjectMapper objectMapper, List<ClientRegister> clientRegisters, CounterCustomRegistry counterCustomRegistry,
            ErrorStatusDecoder errorStatusDecoder, ForkJoinPool dollyForkJoinPool) {
        super(tpsfResponseHandler, tpsfService, tpsfPersonCache, identService, bestillingProgressService, bestillingService,
                mapperFacade, cacheManager, objectMapper, clientRegisters, counterCustomRegistry);

        this.tpsfService = tpsfService;
        this.tpsfPersonCache = tpsfPersonCache;
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
                        .map(ident -> {
                            BestillingProgress progress = new BestillingProgress(bestilling.getId(), ident);
                            try {
                                Person person = tpsfService.importerPersonFraTps(TpsfImportPersonRequest.builder()
                                        .miljoe(bestilling.getKildeMiljoe())
                                        .ident(ident)
                                        .build());

                                progress.setTpsImportStatus(SUCCESS);

                                sendIdenterTilTPS(List.of(bestilling.getMiljoer().split(","))
                                        .stream().filter(miljoe -> !bestilling.getKildeMiljoe().equalsIgnoreCase(miljoe))
                                        .collect(toList()), singletonList(ident), bestilling.getGruppe(), progress);

                                TpsPerson tpsPerson = tpsfPersonCache.prepareTpsPersoner(person);
                                gjenopprettNonTpsf(tpsPerson, bestKriterier, progress, false);

                            } catch (RuntimeException e) {
                                progress.setTpsImportStatus(errorStatusDecoder.decodeRuntimeException(e));

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
