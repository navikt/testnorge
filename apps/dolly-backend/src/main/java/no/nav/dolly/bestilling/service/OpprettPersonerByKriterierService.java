package no.nav.dolly.bestilling.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.tpsf.TpsfResponseHandler;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.consumer.pdlperson.PdlPersonConsumer;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.jpa.Testident.Master.TPSF;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

@Slf4j
@Service
public class OpprettPersonerByKriterierService extends DollyBestillingService {

    private BestillingService bestillingService;
    private ErrorStatusDecoder errorStatusDecoder;
    private MapperFacade mapperFacade;
    private IdentService identService;
    private TpsfService tpsfService;
    private ExecutorService dollyForkJoinPool;

    public OpprettPersonerByKriterierService(TpsfResponseHandler tpsfResponseHandler, TpsfService tpsfService,
                                             DollyPersonCache dollyPersonCache, IdentService identService,
                                             BestillingProgressService bestillingProgressService,
                                             BestillingService bestillingService, MapperFacade mapperFacade,
                                             CacheManager cacheManager, ObjectMapper objectMapper,
                                             List<ClientRegister> clientRegisters, CounterCustomRegistry counterCustomRegistry,
                                             ErrorStatusDecoder errorStatusDecoder, ExecutorService dollyForkJoinPool,
                                             PdlPersonConsumer pdlPersonConsumer) {
        super(tpsfResponseHandler, tpsfService, dollyPersonCache, identService, bestillingProgressService,
                bestillingService, mapperFacade, cacheManager, objectMapper, clientRegisters, counterCustomRegistry,
                pdlPersonConsumer);

        this.bestillingService = bestillingService;
        this.errorStatusDecoder = errorStatusDecoder;
        this.mapperFacade = mapperFacade;
        this.identService = identService;
        this.tpsfService = tpsfService;
        this.dollyForkJoinPool = dollyForkJoinPool;
    }

    @Async
    public void executeAsync(Bestilling bestilling) {

        RsDollyBestillingRequest bestKriterier = getDollyBestillingRequest(bestilling);

        if (nonNull(bestKriterier)) {

            TpsfBestilling tpsfBestilling = nonNull(bestKriterier.getTpsf()) ?
                    mapperFacade.map(bestKriterier.getTpsf(), TpsfBestilling.class) : new TpsfBestilling();
            tpsfBestilling.setAntall(1);
            tpsfBestilling.setNavSyntetiskIdent(bestilling.getNavSyntetiskIdent());

            dollyForkJoinPool.submit(() -> {
                Collections.nCopies(bestilling.getAntallIdenter(), true).parallelStream()
                        .filter(ident -> !bestillingService.isStoppet(bestilling.getId()))
                        .forEach(ident -> {

                            BestillingProgress progress = null;
                            try {
                                List<String> leverteIdenter = tpsfService.opprettIdenterTpsf(tpsfBestilling);

                                DollyPerson dollyPerson = DollyPerson.builder()
                                        .hovedperson(leverteIdenter.get(0))
                                        .master(TPSF)
                                        .build();
                                progress = new BestillingProgress(bestilling, dollyPerson.getHovedperson(), TPSF);

                                sendIdenterTilTPS(new ArrayList<>(List.of(bestilling.getMiljoer().split(","))),
                                        leverteIdenter, bestilling.getGruppe(), progress);

                                if (isNotBlank(bestKriterier.getBeskrivelse())) {
                                    identService.saveIdentBeskrivelse(dollyPerson.getHovedperson(), bestKriterier.getBeskrivelse());
                                }

                                gjenopprettNonTpsf(dollyPerson, bestKriterier, progress, false);

                            } catch (RuntimeException e) {
                                progress = BestillingProgress.builder()
                                        .bestilling(bestilling)
                                        .ident("?")
                                        .feil("NA:" + errorStatusDecoder.decodeRuntimeException(e))
                                        .master(TPSF)
                                        .build();
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
