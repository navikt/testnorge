package no.nav.dolly.bestilling.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.personservice.PersonServiceClient;
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
import no.nav.dolly.util.ThreadLocalContextLifter;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.dolly.util.WebClientFilter;
import org.slf4j.MDC;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Operators;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

import static java.util.Objects.nonNull;
import static no.nav.dolly.util.MdcUtil.MDC_KEY_BESTILLING;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Slf4j
@Service
public class GjenopprettBestillingService extends DollyBestillingService {

    private final ExecutorService dollyForkJoinPool;

    public GjenopprettBestillingService(DollyPersonCache dollyPersonCache,
                                        IdentService identService, BestillingProgressService bestillingProgressService,
                                        BestillingService bestillingService, MapperFacade mapperFacade, CacheManager cacheManager,
                                        ObjectMapper objectMapper, List<ClientRegister> clientRegisters, CounterCustomRegistry counterCustomRegistry,
                                        ErrorStatusDecoder errorStatusDecoder, ExecutorService dollyForkJoinPool,
                                        PdlPersonConsumer pdlPersonConsumer, PdlDataConsumer pdlDataConsumer,
                                        TransactionHelperService transactionHelperService,
                                        PersonServiceClient personServiceClient) {
        super(dollyPersonCache, identService, bestillingProgressService, bestillingService,
                mapperFacade, cacheManager, objectMapper, clientRegisters, counterCustomRegistry, pdlPersonConsumer,
                pdlDataConsumer, errorStatusDecoder, transactionHelperService, personServiceClient);

        this.dollyForkJoinPool = dollyForkJoinPool;
    }

    @Async
    public void executeAsync(Bestilling bestilling) {
        log.info("Bestilling med id=#{} og type={} er startet ...", bestilling.getId(), getBestillingType(bestilling));
        MDC.put(MDC_KEY_BESTILLING, bestilling.getId().toString());
        Hooks.onEachOperator(Operators.lift(new ThreadLocalContextLifter<>()));

        RsDollyBestillingRequest bestKriterier = getDollyBestillingRequest(bestilling);

        if (nonNull(bestKriterier)) {
            bestKriterier.setEkskluderEksternePersoner(true);

            var gamleProgresser = bestillingProgressService.fetchBestillingProgressByBestillingId(bestilling.getOpprettetFraId());

            Flux.fromIterable(gamleProgresser)
                    .filter(gmlProgress -> !bestillingService.isStoppet(bestilling.getId()))
                    .flatMap(gmlProgress -> opprettProgress(bestilling, gmlProgress.getMaster(), gmlProgress.getIdent())
                            .flatMap(progress -> sendOrdrePerson(progress, gmlProgress.getIdent())
                                    .flatMap(ident -> createDollyperson(progress, ident)
                                            .doOnNext(dollyPerson -> counterCustomRegistry.invoke(bestKriterier))
                                            .flatMap(dollyPerson -> Flux.concat(
                                                    gjenopprettKlienter(dollyPerson, bestKriterier,
                                                            fase1Klienter(),
                                                            progress, false),
                                                    personServiceClient.gjenopprett(null,
                                                                    dollyPerson, progress, false)
                                                            .map(ClientFuture::get)
                                                            .map(BestillingProgress::isPdlSync)
                                                            .flatMap(pdlSync -> isTrue(pdlSync) ?
                                                                    Flux.concat(
                                                                            gjenopprettKlienter(dollyPerson, bestKriterier,
                                                                                    fase2Klienter(),
                                                                                    progress, false),
                                                                            gjenopprettKlienter(dollyPerson, bestKriterier,
                                                                                    fase3Klienter(),
                                                                                    progress, false)) :
                                                                    Flux.empty())
                                                            .filter(Objects::nonNull)))
                                            .onErrorResume(throwable -> {
                                                var error = errorStatusDecoder.getErrorText(
                                                        WebClientFilter.getStatus(throwable), WebClientFilter.getMessage(throwable));
                                                log.error("Feil oppsto ved utføring av bestilling, progressId {} {}",
                                                        progress.getId(), error);
                                                progress.setFeil(error);
                                                transactionHelperService.persister(progress);
                                                return Flux.just(progress);
                                            }))))
                    .collectList()
                    .subscribe(done -> doFerdig(bestilling));

        } else {
            bestilling.setFeil("Feil: kunne ikke mappe JSON request, se logg!");
            oppdaterBestillingFerdig(bestilling);
        }
    }

    private BestillingFuture doBestilling(Bestilling bestilling, RsDollyBestillingRequest bestKriterier, BestillingProgress gjenopprettFraProgress) {

        return () -> {
            if (!bestillingService.isStoppet(bestilling.getId())) {
                var progress = new BestillingProgress(bestilling, gjenopprettFraProgress.getIdent(),
                        gjenopprettFraProgress.getMaster());
                transactionHelperService.oppdaterProgress(progress);

                bestKriterier.setNavSyntetiskIdent(isSyntetisk(gjenopprettFraProgress.getIdent()));
                bestKriterier.setBeskrivelse(bestilling.getBeskrivelse());

                try {
                    Optional<DollyPerson> dollyPerson = prepareDollyPerson(progress);

                    if (dollyPerson.isPresent()) {

                        gjenopprettAlleKlienter(dollyPerson.get(), bestKriterier, progress, false);
                    } else {
                        progress.setFeil("NA:Feil= Finner ikke personen i database");
                    }

                } catch (JsonProcessingException e) {
                    progress.setFeil(errorStatusDecoder.decodeException(e));

                } catch (RuntimeException e) {
                    progress.setFeil(errorStatusDecoder.decodeThrowable(e));

                } finally {
                    transactionHelperService.persister(progress);
                }
                return progress;
            }
            return null;
        };
    }
}
