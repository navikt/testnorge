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
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.opensearch.service.OpenSearchService;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.metrics.CounterCustomRegistry;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.repository.IdentRepository;
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

import java.util.HashMap;
import java.util.List;

import static java.lang.Boolean.FALSE;
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
            List<ClientRegister> clientRegisters,
            MapperFacade mapperFacade,
            ObjectMapper objectMapper,
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
                clientRegisters,
                mapperFacade,
                objectMapper,
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

        Mono.just(getDollyBestillingRequest(bestilling))
                .map(bestKriterier -> {
                    bestKriterier.setEkskluderEksternePersoner(true);
                    return bestKriterier;
                })
                .filter(request -> isBlank(request.getFeil()))
                .flatMapMany(bestKriterier ->
                        identService.getTestidenterByGruppeId(bestilling.getGruppeId())
                                .flatMap(testident -> utfoergjenoppretting(bestKriterier, bestilling, testident), 3))
                .subscribe(progress -> log.info("Fullført gjenoppretting av ident: {}", progress.getIdent()),
                        error -> doFerdig(bestilling).subscribe(),
                        () -> doFerdig(bestilling).subscribe());
    }

    private Flux<BestillingProgress> utfoergjenoppretting(RsDollyBestillingRequest bestKriterier, Bestilling bestilling, Testident testident) {

        var counterIdentBestilling = new HashMap<String, Boolean>();

        return Flux.from(bestillingService.isStoppet(bestilling.getId()))
                .filter(BooleanUtils::isFalse)
                .doOnNext(ok -> counterIdentBestilling.put(testident.getIdent(), false))
                .concatMap(ok -> opprettProgress(bestilling, testident.getMaster(), testident.getIdent()))
                .concatMap(this::sendOrdrePerson)
                .filter(BestillingProgress::isIdentGyldig)
                .concatMap(progress -> opprettDollyPerson(progress, bestilling.getBruker())
                        .zipWith(Mono.just(progress)))
                .doOnNext(tuple -> counterCustomRegistry.invoke(bestKriterier))
                .concatMap(tuple ->
                        gjenopprettKlienterStart(tuple.getT1(), bestKriterier, tuple.getT2(), true)
                                .zipWith(Mono.just(tuple.getT1())))
                .concatMap(tuple -> personServiceClient.syncPerson(tuple.getT2(), tuple.getT1())
                        .zipWith(Mono.just(tuple.getT2())))
                .filter(tuple -> tuple.getT1().isPdlSync())
                .concatMap(tuple ->
                        identRepository.getBestillingerFromGruppe(bestilling.getGruppeId())
                                .filter(coBestilling -> tuple.getT2().getIdent().equals(coBestilling.getIdent()) &&
                                        (!"{}".equals(coBestilling.getBestkriterier()) ||
                                                FALSE.equals(counterIdentBestilling.replace(tuple.getT1().getIdent(), true))))
                                .map(coBestilling -> createBestilling(bestilling, coBestilling))
                                .doOnNext(request -> log.info("Startet gjenopprett bestilling {} for ident: {}",
                                        request.getId(), tuple.getT1().getIdent()))
                                .zipWith(Mono.just(tuple)))
                .concatMap(tuple ->
                        gjenopprettKlienterFerdigstill(tuple.getT2().getT2(), tuple.getT1(),
                                tuple.getT2().getT1(), false));
    }
}