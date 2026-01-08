package no.nav.dolly.bestilling.service;

import tools.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.personservice.PersonServiceClient;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.opensearch.service.OpenSearchService;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.metrics.CounterCustomRegistry;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.repository.TestgruppeRepository;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.service.TransactionHelperService;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
public class LeggTilPaaGruppeService extends DollyBestillingService {

    private final PersonServiceClient personServiceClient;

    public LeggTilPaaGruppeService(
            BestillingProgressRepository bestillingProgressRepository,
            BestillingRepository bestillingRepository,
            BestillingService bestillingService,
            CounterCustomRegistry counterCustomRegistry,
            ErrorStatusDecoder errorStatusDecoder,
            IdentService identService,
            List<ClientRegister> clientRegisters,
            MapperFacade mapperFacade,
            ObjectMapper objectMapper,
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
                objectMapper,
                openSearchService,
                pdlDataConsumer,
                testgruppeRepository,
                transactionHelperService
        );
        this.personServiceClient = personServiceClient;
    }

    @Async
    public void executeAsync(Bestilling bestilling) {

        var bestKriterier = getDollyBestillingRequest(bestilling);

        Mono.just(bestKriterier)
                .filter(request -> isBlank(request.getFeil()))
                .flatMapMany(request ->
                        identService.getTestidenterByGruppeId(bestilling.getGruppeId())
                                .flatMap(testident -> leggTilPaaPerson(bestilling, bestKriterier, testident), 3))
                .subscribe(progress -> log.info("Fullført oppretting av ident: {}", progress.getIdent()),
                        error -> doFerdig(bestilling).subscribe(),
                        () -> saveBestillingToElasticServer(bestKriterier, bestilling)
                                .then(doFerdig(bestilling))
                                .subscribe());
    }

    private Flux<BestillingProgress> leggTilPaaPerson(Bestilling bestilling, RsDollyUtvidetBestilling bestKriterier, Testident testident) {

        return Flux.from(bestillingService.isStoppet(bestilling.getId()))
                .takeWhile(BooleanUtils::isFalse)
                .map(ok -> OriginatorUtility.prepOriginator(bestKriterier, testident, mapperFacade))
                .concatMap(originator -> opprettProgress(bestilling, originator.getMaster(), originator.getIdent())
                        .zipWith(Mono.just(originator)))
                .concatMap(tuple -> oppdaterPerson(tuple.getT2(), tuple.getT1()))
                .concatMap(this::sendOrdrePerson)
                .filter(BestillingProgress::isIdentGyldig)
                .concatMap(progress -> opprettDollyPerson(progress, bestilling.getBruker())
                        .zipWith(Mono.just(progress)))
                .concatMap(tuple -> (!tuple.getT1().getIdent().equals(testident.getIdent()) ?
                        updateIdent(tuple.getT1(), tuple.getT2()) : Mono.just(tuple.getT1().getIdent()))
                        .doOnNext(nyident -> counterCustomRegistry.invoke(bestKriterier))
                        .then(gjenopprettKlienterStart(tuple.getT1(), bestKriterier, tuple.getT2(), true)
                                .then(personServiceClient.syncPerson(tuple.getT1(), tuple.getT2())
                                        .filter(BestillingProgress::isPdlSync)
                                        .then(gjenopprettKlienterFerdigstill(tuple.getT1(), bestKriterier,
                                                tuple.getT2(), true)))));
    }
}
