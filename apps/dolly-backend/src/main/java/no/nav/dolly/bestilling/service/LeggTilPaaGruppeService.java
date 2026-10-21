package no.nav.dolly.bestilling.service;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.personservice.PersonServiceClient;
import no.nav.dolly.config.ApplicationConfig;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.metrics.CounterCustomRegistry;
import no.nav.dolly.opensearch.service.OpenSearchService;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.repository.BestillingRepository;
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
import java.util.concurrent.atomic.AtomicReference;

import static org.apache.commons.lang3.ObjectUtils.getIfNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
public class LeggTilPaaGruppeService extends DollyBestillingService {

    private final ApplicationConfig applicationConfig;
    private final PersonServiceClient personServiceClient;

    public LeggTilPaaGruppeService(
            ApplicationConfig applicationConfig,
            BestillingProgressRepository bestillingProgressRepository,
            BestillingRepository bestillingRepository,
            BestillingService bestillingService,
            CounterCustomRegistry counterCustomRegistry,
            ErrorStatusDecoder errorStatusDecoder,
            IdentService identService,
            List<ClientRegister> clientRegisters,
            MapperFacade mapperFacade,
            JsonMapper jsonMapper,
            OpenSearchService openSearchService,
            PdlDataConsumer pdlDataConsumer,
            PersonServiceClient personServiceClient,
            TestgruppeRepository testgruppeRepository,
            TransactionHelperService transactionHelperService,
            CacheManager cacheManager
    ) {
        super(
                bestillingProgressRepository,
                bestillingRepository,
                bestillingService,
                cacheManager,
                counterCustomRegistry,
                errorStatusDecoder,
                identService,
                clientRegisters,
                mapperFacade,
                jsonMapper,
                openSearchService,
                pdlDataConsumer,
                testgruppeRepository,
                transactionHelperService
        );
        this.applicationConfig = applicationConfig;
        this.personServiceClient = personServiceClient;
    }

    @Async
    public void executeAsync(Bestilling bestilling) {

        var bestKriterier = getDollyBestillingRequest(bestilling);

        Mono.just(bestKriterier)
                .filter(request -> isBlank(request.getFeil()))
                .flatMapMany(_ ->
                        identService.getTestidenterByGruppeId(bestilling.getGruppeId()))
                .filter(testident -> bestilling.getGruppeId() != 4457 || !testident.isIdentSyntetisk())
                .flatMap(testident -> leggTilPaaPerson(bestilling, bestKriterier, testident), 3)
                .subscribe(null, e -> {
                            var melding = getIfNull(e.getMessage(), e.getClass().getSimpleName());
                            log.error("Feil ved utføring av legg-til på gruppe, bestId: {} melding: {}",
                                    bestilling.getId(), melding, e);
                            transactionHelperService.oppdaterBestillingFerdig(bestilling.getId(),
                                            StringUtils.left("Feil ved utføring av legg-til på gruppe: " + melding, 1000))
                                    .subscribe(null, ex -> log.error("Kunne ikke sette bestilling {} ferdig: {}",
                                            bestilling.getId(), ex.getMessage(), ex));
                        },
                        () -> saveToOpenSearchUnlessStoppet(bestKriterier, bestilling)
                                .then(Mono.defer(() -> doFerdig(bestilling)))
                                .subscribe(null, e -> log.error("Kunne ikke sette bestilling {} ferdig: {}",
                                        bestilling.getId(), e.getMessage(), e)));
    }

    private Mono<Void> saveToOpenSearchUnlessStoppet(RsDollyUtvidetBestilling bestKriterier, Bestilling bestilling) {

        return bestillingService.isStoppet(bestilling.getId())
                .filter(BooleanUtils::isFalse)
                .flatMap(_ -> saveBestillingToOpenSearchServer(bestKriterier, bestilling))
                .onErrorResume(e -> {
                    log.warn("Feil ved lagring til OpenSearch for bestilling {}: {}", bestilling.getId(), e.getMessage());
                    return Mono.empty();
                });
    }

    private Mono<BestillingProgress> leggTilPaaPerson(Bestilling bestilling, RsDollyUtvidetBestilling bestKriterier, Testident testident) {

        var progress = new AtomicReference<BestillingProgress>();
        return bestillingService.isStoppet(bestilling.getId())
                .filter(BooleanUtils::isFalse)
                .flatMap(_ -> prossesserIdent(bestilling, bestKriterier, testident, progress)
                        .switchIfEmpty(Mono.error(new DollyFunctionalException("Oppretting ikke fullført; annen feil")))
                        .doOnNext(_ -> log.info("Fullført oppretting av ident: {}", testident.getIdent()))
                        .onErrorResume(e -> setOpprettIdentFeilet(bestilling.getId(), progress.get(), testident.getIdent(), e)));
    }

    private Mono<BestillingProgress> prossesserIdent(Bestilling bestilling, RsDollyUtvidetBestilling bestKriterier, Testident testident,
                                                       AtomicReference<BestillingProgress> progress) {

        return Mono.just(OriginatorUtility.prepOriginator(bestKriterier, testident, mapperFacade))
                .flatMap(originator -> opprettProgress(bestilling, originator.getMaster(), originator.getIdent())
                        .doOnNext(progress::set)
                        .thenReturn(originator))
                .flatMap(originator -> oppdaterPerson(originator, progress.get()))
                .flatMap(this::sendOrdrePerson)
                .doOnNext(progress::set)
                .filter(BestillingProgress::isIdentGyldig)
                .flatMap(_ -> opprettDollyPersonLeggTil(progress.get(), bestilling.getBruker()))
                .flatMap(dollyPerson -> updateIdent(dollyPerson, progress.get()))
                .doOnNext(_ -> counterCustomRegistry.invoke(bestKriterier))
                .flatMap(dollyPerson -> gjenopprettKlienterStart(dollyPerson, bestKriterier, progress.get(), true)
                        .thenReturn(dollyPerson))
                .flatMap(dollyPerson ->
                        personServiceClient.syncPerson(dollyPerson, progress.get())
                        .zipWith(Mono.just(dollyPerson)))
                .filter(tuple -> tuple.getT1().isPdlSync())
                .flatMap(tuple -> gjenopprettKlienterFerdigstill(tuple.getT2(), bestKriterier,
                        tuple.getT1(), true))
                .timeout(Duration.ofSeconds(applicationConfig.getOpprettPersonTimeout()));
    }
}
