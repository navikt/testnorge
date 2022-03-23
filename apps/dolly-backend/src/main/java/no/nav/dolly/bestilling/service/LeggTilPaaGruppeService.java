package no.nav.dolly.bestilling.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.consumer.pdlperson.PdlPersonConsumer;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.domain.resultset.tpsf.RsOppdaterPersonResponse;
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

import java.util.List;
import java.util.concurrent.ExecutorService;

import static java.util.Objects.nonNull;

@Service
public class LeggTilPaaGruppeService extends DollyBestillingService {

    private MapperFacade mapperFacade;
    private BestillingService bestillingService;
    private TpsfService tpsfService;
    private DollyPersonCache dollyPersonCache;
    private ErrorStatusDecoder errorStatusDecoder;
    private ExecutorService dollyForkJoinPool;
    private ObjectMapper objectMapper;
    private PdlPersonConsumer pdlPersonConsumer;

    public LeggTilPaaGruppeService(TpsfService tpsfService,
                                   DollyPersonCache dollyPersonCache, IdentService identService,
                                   BestillingProgressService bestillingProgressService,
                                   BestillingService bestillingService, MapperFacade mapperFacade,
                                   CacheManager cacheManager, ObjectMapper objectMapper,
                                   List<ClientRegister> clientRegisters, CounterCustomRegistry counterCustomRegistry,
                                   ErrorStatusDecoder errorStatusDecoder, ExecutorService dollyForkJoinPool,
                                   PdlPersonConsumer pdlPersonConsumer, PdlDataConsumer pdlDataConsumer) {
        super(tpsfService, dollyPersonCache, identService, bestillingProgressService,
                bestillingService, mapperFacade, cacheManager, objectMapper, clientRegisters, counterCustomRegistry,
                pdlPersonConsumer, pdlDataConsumer);

        this.mapperFacade = mapperFacade;
        this.bestillingService = bestillingService;
        this.tpsfService = tpsfService;
        this.dollyPersonCache = dollyPersonCache;
        this.errorStatusDecoder = errorStatusDecoder;
        this.dollyForkJoinPool = dollyForkJoinPool;
        this.objectMapper = objectMapper;
        this.pdlPersonConsumer = pdlPersonConsumer;
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
                        .forEach(testident -> {
                            BestillingProgress progress = new BestillingProgress(bestilling, testident.getIdent(), testident.getMaster());
                            try {
                                DollyPerson dollyPerson = prepareDollyPerson(bestilling, tpsfBestilling, testident, progress);
                                gjenopprettNonTpsf(dollyPerson, bestKriterier, progress, true);

                            } catch (JsonProcessingException e) {
                                progress.setFeil(errorStatusDecoder.decodeException(e));
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

    private DollyPerson prepareDollyPerson(Bestilling bestilling, TpsfBestilling tpsfBestilling, no.nav.dolly.domain.jpa.Testident testident, BestillingProgress progress) throws JsonProcessingException {

        DollyPerson dollyPerson = null;
        if (testident.isTpsf()) {
            RsOppdaterPersonResponse oppdaterPersonResponse = tpsfService.endreLeggTilPaaPerson(testident.getIdent(), tpsfBestilling);
            if (!oppdaterPersonResponse.getIdentTupler().isEmpty()) {

                dollyPerson = dollyPersonCache.prepareTpsPerson(bestilling.getIdent());

            } else {
                progress.setFeil("NA:Feil= Ident finnes ikke i database");
            }

        } else {
            PdlPerson pdlPerson = objectMapper.readValue(
                    pdlPersonConsumer.getPdlPerson(testident.getIdent()).toString(), PdlPerson.class);
            dollyPerson = dollyPersonCache.preparePdlPersoner(pdlPerson);
        }
        return dollyPerson;
    }
}
