package no.nav.dolly.bestilling.service;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.personservice.PersonServiceClient;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.metrics.CounterCustomRegistry;
import no.nav.dolly.opensearch.service.OpenSearchService;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.repository.IdentRepository;
import no.nav.dolly.repository.TestgruppeRepository;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.service.TransactionHelperService;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.apache.commons.lang3.ObjectUtils.getIfNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
public class GjenopprettGruppeService extends DollyBestillingService {

    private final PersonServiceClient personServiceClient;
    private final IdentRepository identRepository;

    public GjenopprettGruppeService(
            BestillingProgressRepository bestillingProgressRepository,
            BestillingRepository bestillingRepository,
            BestillingService bestillingService,
            CacheManager cacheManager,
            CounterCustomRegistry counterCustomRegistry,
            ErrorStatusDecoder errorStatusDecoder,
            IdentRepository identRepository,
            IdentService identService,
            JsonMapper jsonMapper,
            List<ClientRegister> clientRegisters,
            MapperFacade mapperFacade,
            OpenSearchService openSearchService,
            PdlDataConsumer pdlDataConsumer,
            PersonServiceClient personServiceClient,
            TestgruppeRepository testgruppeRepository,
            TransactionHelperService transactionHelperService
    ) {
        super(
                bestillingProgressRepository,
                bestillingRepository,
                bestillingService,
                cacheManager,
                counterCustomRegistry,
                errorStatusDecoder,
                identService,
                jsonMapper,
                clientRegisters,
                mapperFacade,
                openSearchService,
                pdlDataConsumer,
                testgruppeRepository,
                transactionHelperService
        );
        this.personServiceClient = personServiceClient;
        this.identRepository = identRepository;
    }

    @Async
    public void executeAsync(Bestilling bestilling) {

        log.info("Bestilling med id=#{} og type={} er startet ...", bestilling.getId(), getBestillingType(bestilling));

        getDollyBestillingRequest(bestilling)
                .map(bestKriterier -> {
                    bestKriterier.setEkskluderEksternePersoner(true);
                    return bestKriterier;
                })
                .filter(request -> isBlank(request.getFeil()))
                .flatMapMany(bestKriterier ->
                        identService.getTestidenterByGruppeId(bestilling.getGruppeId())
                                .flatMap(testident -> gjenopprettPerson(bestKriterier, bestilling, testident.getIdent()), 3))
                .subscribe(null, e -> {
                            var melding = getIfNull(e.getMessage(), e.getClass().getSimpleName());
                            transactionHelperService.oppdaterBestillingFerdig(bestilling.getId(),
                                            StringUtils.left("Feil ved utføring av : " + melding, 1000))
                                    .subscribe(null, ex -> log.error("Kunne ikke sette bestilling {} ferdig: {}",
                                            bestilling.getId(), ex.getMessage(), ex));
                        },
                        () -> doFerdig(bestilling).subscribe());
    }

    private Mono<BestillingProgress> gjenopprettPerson(RsDollyBestillingRequest bestKriterier, Bestilling bestilling, String ident) {

        var bestProgress = new AtomicReference<BestillingProgress>();

        return bestillingService.isStoppet(bestilling.getId())
                .filter(BooleanUtils::isFalse)
                .flatMap(_ -> gjenopprettIdent(bestKriterier, bestilling, ident, bestProgress)
                        .switchIfEmpty(Mono.error(new DollyFunctionalException("Oppretting ikke fullført; annen feil")))
                        .doOnNext(_ -> log.info("Fullført gjenoppretting av ident: {}", ident))
                        .onErrorResume(e -> setOpprettIdentFeilet(bestilling.getId(), bestProgress.get(), ident, e)));
    }

    private Mono<BestillingProgress> gjenopprettIdent(RsDollyBestillingRequest bestKriterier, Bestilling bestilling,
                                                      String ident, AtomicReference<BestillingProgress> progress) {

        var isVisited = new AtomicBoolean(false);
        return opprettProgress(bestilling.getId(), ident)
                .doOnNext(progress::set)
                .flatMap(this::sendOrdrePerson)
                .doOnNext(progress::set)
                .filter(BestillingProgress::isIdentGyldig)
                .flatMap(this::opprettDollyPersonForGjenopprett)
                .doOnNext(_ -> counterCustomRegistry.invoke(bestKriterier))
                .flatMap(dollyPerson ->
                        gjenopprettKlienterStart(dollyPerson, bestKriterier, progress.get())
                                .thenReturn(dollyPerson))
                .flatMap(dollyPerson -> personServiceClient.syncPerson(dollyPerson, progress.get())
                        .zipWith(Mono.just(dollyPerson)))
                .filter(tuple -> tuple.getT1().isPdlSync())
                .flatMapMany(tuple ->
                        identRepository.getBestillingerFromGruppe(bestilling.getGruppeId())
                                .filter(coBestilling -> ident.equals(coBestilling.getIdent()) &&
                                                        (!isVisited.getAndSet(true) ||
                                                         !"{}".equals(coBestilling.getBestkriterier())))
                                .concatMap(coBestilling -> createBestilling(bestilling, coBestilling)
                                        .zipWith(Mono.just(tuple.getT2()))))
                .doOnNext(tuple -> log.info("Startet gjenopprett bestilling {} for ident: {}",
                        tuple.getT1().getId(), ident))
                .concatMap(tuple ->
                        gjenopprettKlienterFerdigstill(tuple.getT2(), tuple.getT1(),
                                progress.get()))
                .then(Mono.defer(() -> Mono.just(progress.get())))
                .timeout(Duration.ofSeconds(opprettPersonTimeout));
    }
}