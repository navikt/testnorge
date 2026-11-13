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

@Slf4j
@Service
public class GjenopprettIdentService extends DollyBestillingService {

    private final PersonServiceClient personServiceClient;
    private final IdentRepository identRepository;

    public GjenopprettIdentService(
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

        gjenopprettIdent(bestilling, bestilling.getGjenopprettetFraIdent())
                    .subscribe(null,
                        e -> {
                            var melding = getIfNull(e.getMessage(), e.getClass().getSimpleName());
                            log.error("Feil ved utføring av {}, bestId: {} melding: {}",
                                    getBestillingType(bestilling), bestilling.getId(), melding, e);
                            transactionHelperService.oppdaterBestillingFerdig(bestilling.getId(),
                                            StringUtils.left("Feil ved utføring av : " + melding, 1000))
                                    .subscribe(null, ex -> log.error("Kunne ikke sette bestilling {} ferdig: {}",
                                            bestilling.getId(), ex.getMessage(), ex));
                        },
                        () -> doFerdig(bestilling)
                                .subscribe());
    }

    private Mono<BestillingProgress> gjenopprettIdent(Bestilling bestilling, String ident) {

        var bestProgress = new AtomicReference<BestillingProgress>();
        var isVisited = new AtomicBoolean(false);

        return opprettProgress(bestilling.getId(), ident)
                .doOnNext(bestProgress::set)
                .flatMap(this::sendOrdrePerson)
                .doOnNext(bestProgress::set)
                .filter(BestillingProgress::isIdentGyldig)
                .flatMap(this::opprettDollyPersonForGjenopprett)
                .flatMap(dollyPerson ->
                        gjenopprettKlienterStart(dollyPerson, new RsDollyBestillingRequest(), bestProgress.get())
                                .zipWith(Mono.just(dollyPerson)))
                .flatMap(tuple -> personServiceClient.syncPerson(tuple.getT2(), tuple.getT1())
                        .zipWith(Mono.just(tuple.getT2())))
                .filter(tuple -> tuple.getT1().isPdlSync())
                .flatMapMany(tuple ->
                        identRepository.getBestillingerByIdent(ident)
                                .filter(coBestilling ->
                                        !isVisited.getAndSet(true) ||
                                        !"{}".equals(coBestilling.getBestkriterier()))
                                .flatMap(coBestilling -> createBestilling(bestilling, coBestilling)
                                        .zipWith(Mono.just(tuple))))
                .doOnNext(tuple ->
                        log.info("Startet gjenopprett bestilling {} for ident: {}",
                                tuple.getT1().getId(), tuple.getT2().getT2().getIdent()))
                .concatMap(bestillingRequest ->
                        gjenopprettKlienterFerdigstill(bestillingRequest.getT2().getT2(), bestillingRequest.getT1(),
                                bestillingRequest.getT2().getT1()))
                .collectList()
                .map(_ -> bestProgress.get())
                .timeout(Duration.ofSeconds(opprettPersonTimeout))
                .switchIfEmpty(Mono.error(new DollyFunctionalException("Oppretting ikke fullført; annen feil")))
                .doOnNext(_ -> log.info("Fullført gjenoppretting av ident: {}", ident))
                .onErrorResume(e -> setOpprettIdentFeilet(bestilling.getId(), bestProgress.get(), ident, e));
    }
}