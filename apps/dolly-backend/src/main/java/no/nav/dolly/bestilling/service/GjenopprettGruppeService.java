package no.nav.dolly.bestilling.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.aktoeridsyncservice.AktoerIdSyncClient;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.pdlforvalter.PdlForvalterClient;
import no.nav.dolly.bestilling.pensjonforvalter.PensjonforvalterClient;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.consumer.pdlperson.PdlPersonConsumer;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.metrics.CounterCustomRegistry;
import no.nav.dolly.repository.IdentRepository.GruppeBestillingIdent;
import no.nav.dolly.service.BestillingProgressService;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.DollyPersonCache;
import no.nav.dolly.service.IdentService;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

import static java.util.Objects.nonNull;

@Service
public class GjenopprettGruppeService extends DollyBestillingService {

    private BestillingService bestillingService;
    private ErrorStatusDecoder errorStatusDecoder;
    private ExecutorService dollyForkJoinPool;
    private List<ClientRegister> clientRegisters;
    private IdentService identService;

    public GjenopprettGruppeService(TpsfService tpsfService,
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

        this.bestillingService = bestillingService;
        this.errorStatusDecoder = errorStatusDecoder;
        this.dollyForkJoinPool = dollyForkJoinPool;
        this.clientRegisters = clientRegisters;
        this.identService = identService;
    }

    @Async
    public void executeAsync(Bestilling bestilling) {

        RsDollyBestillingRequest bestKriterier = getDollyBestillingRequest(bestilling);
        bestKriterier.setEkskluderEksternePersoner(true);

        if (nonNull(bestKriterier)) {

            List<GruppeBestillingIdent> coBestillinger = identService.getBestillingerFromGruppe(bestilling.getGruppe());

            dollyForkJoinPool.submit(() -> {
                bestilling.getGruppe().getTestidenter().parallelStream()
                        .filter(testident -> !bestillingService.isStoppet(bestilling.getId()))
                        .forEach(testident -> {

                            BestillingProgress progress = new BestillingProgress(bestilling, testident.getIdent(),
                                    testident.getMaster());
                            try {
                                Optional<DollyPerson> dollyPerson = prepareDollyPerson(progress);

                                if (dollyPerson.isPresent()) {
                                    gjenopprettNonTpsf(dollyPerson.get(), bestKriterier, progress, false);

                                    coBestillinger.stream()
                                            .filter(gruppe -> gruppe.getIdent().equals(testident.getIdent()))
                                            .sorted(Comparator.comparing(GruppeBestillingIdent::getBestillingid))
                                            .map(bestilling1 -> clientRegisters.stream()
                                                    .filter(register ->
                                                            !(register instanceof PdlForvalterClient ||
                                                                    register instanceof AktoerIdSyncClient ||
                                                                    register instanceof PensjonforvalterClient))
                                                    .map(register -> {
                                                        register.gjenopprett(getDollyBestillingRequest(
                                                                Bestilling.builder()
                                                                        .bestKriterier(bestilling1.getBestkriterier())
                                                                        .miljoer(bestilling.getMiljoer())
                                                                        .build()), dollyPerson.get(), progress, false);
                                                        return register;
                                                    })
                                                    .toList())
                                            .toList();

                                } else {
                                    progress.setFeil("NA:Feil= Finner ikke personen i database");
                                }

                            } catch (JsonProcessingException e) {
                                progress.setFeil(errorStatusDecoder.decodeException(e));

                            } catch (RuntimeException e) {
                                progress.setFeil(errorStatusDecoder.decodeRuntimeException(e));

                            } finally {
                                oppdaterProgress(bestilling, progress);
                            }
                        });

                oppdaterBestillingFerdig(bestilling);
            });
        }
    }
}
