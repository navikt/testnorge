package no.nav.dolly.bestilling.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.tpsf.TpsfResponseHandler;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.consumer.pdlperson.PdlPersonConsumer;
import no.nav.dolly.domain.PdlPerson;
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

import static java.util.Arrays.asList;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.jpa.Testident.Master.PDL;

@Service
public class ImportAvPersonerFraPdlService extends DollyBestillingService {

    private DollyPersonCache dollyPersonCache;
    private ErrorStatusDecoder errorStatusDecoder;
    private ExecutorService dollyForkJoinPool;
    private PdlPersonConsumer pdlPersonConsumer;
    private ObjectMapper objectMapper;
    private IdentService identService;

    public ImportAvPersonerFraPdlService(TpsfResponseHandler tpsfResponseHandler, TpsfService tpsfService, DollyPersonCache dollyPersonCache,
                                         IdentService identService, BestillingProgressService bestillingProgressService,
                                         BestillingService bestillingService, MapperFacade mapperFacade, CacheManager cacheManager,
                                         ObjectMapper objectMapper, List<ClientRegister> clientRegisters, CounterCustomRegistry counterCustomRegistry,
                                         ErrorStatusDecoder errorStatusDecoder, ExecutorService dollyForkJoinPool,
                                         PdlPersonConsumer pdlPersonConsumer, PdlDataConsumer pdlDataConsumer) {
        super(tpsfResponseHandler, tpsfService, dollyPersonCache, identService, bestillingProgressService, bestillingService,
                mapperFacade, cacheManager, objectMapper, clientRegisters, counterCustomRegistry, pdlPersonConsumer, pdlDataConsumer);

        this.dollyPersonCache = dollyPersonCache;
        this.errorStatusDecoder = errorStatusDecoder;
        this.dollyForkJoinPool = dollyForkJoinPool;
        this.objectMapper = objectMapper;
        this.pdlPersonConsumer = pdlPersonConsumer;
        this.identService = identService;
    }

    @Async
    public void executeAsync(Bestilling bestilling) {

        RsDollyBestillingRequest bestKriterier = getDollyBestillingRequest(bestilling);

        if (nonNull(bestKriterier)) {

            dollyForkJoinPool.submit(() -> {
                asList(bestilling.getPdlImport().split(",")).parallelStream()
                        .filter(ident -> !bestilling.isStoppet())
                        .forEach(ident -> {
                            BestillingProgress progress = new BestillingProgress(bestilling, ident, PDL);
                            try {

                                PdlPerson pdlPerson = objectMapper.readValue(pdlPersonConsumer.getPdlPerson(ident).toString(), PdlPerson.class);
                                DollyPerson dollyPerson = dollyPersonCache.preparePdlPersoner(pdlPerson);
                                identService.saveIdentTilGruppe(dollyPerson.getHovedperson(), bestilling.getGruppe(),
                                        PDL, bestilling.getBeskrivelse());
                                gjenopprettNonTpsf(dollyPerson, bestKriterier, progress, true);
                                progress.setPdlImportStatus(SUCCESS);

                            } catch (JsonProcessingException e){
                                progress.setPdlImportStatus(errorStatusDecoder.decodeException(e));

                            } catch (RuntimeException e) {
                                progress.setPdlImportStatus(errorStatusDecoder.decodeRuntimeException(e));

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
