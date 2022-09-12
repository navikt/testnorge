package no.nav.dolly.bestilling.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
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
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.metrics.CounterCustomRegistry;
import no.nav.dolly.repository.IdentRepository.GruppeBestillingIdent;
import no.nav.dolly.service.BestillingProgressService;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.DollyPersonCache;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.util.ThreadLocalContextLifter;
import no.nav.dolly.util.TransactionHelperService;
import org.slf4j.MDC;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Operators;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.util.Objects.nonNull;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static no.nav.dolly.util.MdcUtil.MDC_KEY_BESTILLING;

@Slf4j
@Service
public class GjenopprettGruppeService extends DollyBestillingService {

    private final BestillingService bestillingService;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final ExecutorService dollyForkJoinPool;
    private final List<ClientRegister> clientRegisters;
    private final IdentService identService;
    private final TransactionHelperService transactionHelperService;

    public GjenopprettGruppeService(TpsfService tpsfService,
                                    DollyPersonCache dollyPersonCache, IdentService identService,
                                    BestillingProgressService bestillingProgressService,
                                    BestillingService bestillingService, MapperFacade mapperFacade,
                                    CacheManager cacheManager, ObjectMapper objectMapper,
                                    List<ClientRegister> clientRegisters, CounterCustomRegistry counterCustomRegistry,
                                    ErrorStatusDecoder errorStatusDecoder, ExecutorService dollyForkJoinPool,
                                    PdlPersonConsumer pdlPersonConsumer, PdlDataConsumer pdlDataConsumer,
                                    TransactionHelperService transactionHelperService) {

        super(tpsfService, dollyPersonCache, identService, bestillingProgressService,
                bestillingService, mapperFacade, cacheManager, objectMapper, clientRegisters, counterCustomRegistry,
                pdlPersonConsumer, pdlDataConsumer, errorStatusDecoder);

        this.bestillingService = bestillingService;
        this.errorStatusDecoder = errorStatusDecoder;
        this.dollyForkJoinPool = dollyForkJoinPool;
        this.clientRegisters = clientRegisters;
        this.identService = identService;
        this.transactionHelperService = transactionHelperService;
    }

    @Async
    public void executeAsync(Bestilling bestilling) {

        log.info("Bestilling med id=#{} er startet ...", bestilling.getId());
        MDC.put(MDC_KEY_BESTILLING, bestilling.getId().toString());
        Hooks.onEachOperator(Operators.lift(new ThreadLocalContextLifter<>()));

        RsDollyBestillingRequest bestKriterier = getDollyBestillingRequest(bestilling);

        if (nonNull(bestKriterier)) {
            bestKriterier.setEkskluderEksternePersoner(true);

            List<GruppeBestillingIdent> coBestillinger = identService.getBestillingerFromGruppe(bestilling.getGruppe());

            var completableFuture = bestilling.getGruppe().getTestidenter().stream()
                    .map(testident -> doGjenopprett(bestilling, bestKriterier, coBestillinger, testident))
                    .map(completable -> supplyAsync(completable, dollyForkJoinPool))
                    .toList();

            completableFuture
                    .forEach(future -> {
                        try {
                            future.get(60, TimeUnit.SECONDS);
                        } catch (InterruptedException e) {
                            log.error(e.getMessage(), e);
                            Thread.currentThread().interrupt();
                        } catch (ExecutionException e) {
                            log.error(e.getMessage(), e);
                            Thread.interrupted();
                        } catch (TimeoutException e) {
                            log.error("Tidsavbrudd (60 s) ved gjenopprett gruppe");
                            Thread.interrupted();
                        }
                    });

            oppdaterBestillingFerdig(bestilling);
            MDC.remove(MDC_KEY_BESTILLING);
            log.info("Bestilling med id=#{} er ferdig", bestilling.getId());
        }
    }

    public BestillingFuture doGjenopprett(Bestilling bestilling, RsDollyBestillingRequest bestKriterier,
                                          List<GruppeBestillingIdent> coBestillinger, Testident testident) {

        return () -> {
            if (!bestillingService.isStoppet(bestilling.getId())) {
                BestillingProgress progress = new BestillingProgress(bestilling, testident.getIdent(),
                        testident.getMaster());
                try {
                    Optional<DollyPerson> dollyPerson = prepareDollyPerson(progress);

                    if (dollyPerson.isPresent()) {
                        gjenopprettNonTpsf(dollyPerson.get(), bestKriterier, progress, false);

                        coBestillinger.stream()
                                .filter(gruppe -> gruppe.getIdent().equals(testident.getIdent()))
                                .sorted(Comparator.comparing(GruppeBestillingIdent::getBestillingid))
                                .forEach(bestilling1 -> clientRegisters.stream()
                                        .filter(register ->
                                                !(register instanceof PdlForvalterClient ||
                                                        register instanceof AktoerIdSyncClient ||
                                                        register instanceof PensjonforvalterClient))
                                        .forEach(register ->
                                                register.gjenopprett(getDollyBestillingRequest(
                                                        Bestilling.builder()
                                                                .bestKriterier(bestilling1.getBestkriterier())
                                                                .miljoer(bestilling.getMiljoer())
                                                                .build()), dollyPerson.get(), progress, false)));

                    } else {
                        progress.setFeil("NA:Feil= Finner ikke personen i database");
                    }

                } catch (JsonProcessingException e) {
                    progress.setFeil(errorStatusDecoder.decodeException(e));

                } catch (RuntimeException e) {
                    progress.setFeil(errorStatusDecoder.decodeRuntimeException(e));

                } finally {
                    transactionHelperService.persist(progress);
                }
                return progress;
            }
            return null;
        };
    }
}