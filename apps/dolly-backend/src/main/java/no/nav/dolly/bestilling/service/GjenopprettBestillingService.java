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
public class GjenopprettBestillingService extends DollyBestillingService {

    private final PersonServiceClient personServiceClient;

    public GjenopprettBestillingService(
            BestillingProgressRepository bestillingProgressRepository,
            BestillingRepository bestillingRepository,
            BestillingService bestillingService,
            CacheManager cacheManager,
            CounterCustomRegistry counterCustomRegistry,
            ErrorStatusDecoder errorStatusDecoder,
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
    }

    @Async
    public void executeAsync(Bestilling bestilling) {

        log.info("Bestilling med id=#{} og type={} er startet ...", bestilling.getId(), getBestillingType(bestilling));

        bestillingRepository.findById(bestilling.getOpprettetFraId())
                .flatMap(this::getDollyBestillingRequest)
                .map(bestKriterier -> {
                    bestKriterier.setEkskluderEksternePersoner(true);
                    return bestKriterier;
                })
                .filter(request -> isBlank(request.getFeil()))
                .flatMapMany(bestKriterier ->
                        bestillingProgressRepository.findAllByBestillingId(bestilling.getOpprettetFraId())
                                .filter(BestillingProgress::isIdentGyldig)
                                .flatMap(progress -> gjenopprettBestilling(bestilling.getId(), bestKriterier,
                                        progress.getIdent()), 3))
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
                        () -> doFerdig(bestilling).subscribe(null, e -> log.error("Kunne ikke sette bestilling {} ferdig: {}",
                                bestilling.getId(), e.getMessage(), e)));
    }

    private Mono<BestillingProgress> gjenopprettBestilling(Long bestillingId, RsDollyBestillingRequest bestKriterier,
                                                           String ident) {

        var bestProgress = new AtomicReference<BestillingProgress>();
        return bestillingService.isStoppet(bestillingId)
                .filter(BooleanUtils::isFalse)
                .flatMap(_ -> gjenopprettBestillingIdent(bestillingId, bestKriterier, bestProgress, ident)
                        .switchIfEmpty(Mono.error(new DollyFunctionalException("Gjenopprett bestilling ikke fullført; annen feil")))
                        .doOnNext(_ -> log.info("Fullført gjenoppretting for ident {}", ident))
                        .onErrorResume(e -> setOpprettIdentFeilet(bestillingId, bestProgress.get(), ident, e)));
    }

    private Mono<BestillingProgress> gjenopprettBestillingIdent(Long bestillingId, RsDollyBestillingRequest bestKriterier,
                                                                AtomicReference<BestillingProgress> bestProgress,
                                                                String ident) {

        return opprettProgress(bestillingId, ident)
                .doOnNext(bestProgress::set)
                .flatMap(this::sendOrdrePerson)
                .doOnNext(bestProgress::set)
                .filter(BestillingProgress::isIdentGyldig)
                .flatMap(this::opprettDollyPersonForGjenopprett)
                .flatMap(dollyPerson ->
                        gjenopprettKlienterStart(dollyPerson, bestKriterier, bestProgress.get())
                                .thenReturn(dollyPerson))
                .flatMap(dollyPerson -> personServiceClient.syncPerson(dollyPerson, bestProgress.get())
                        .zipWith(Mono.just(dollyPerson)))
                .doOnNext(progress1 -> log.info("Status på progress {}", progress1))
                .filter(tuple -> tuple.getT1().isPdlSync())
                .flatMap(tuple -> gjenopprettKlienterFerdigstill(tuple.getT2(), bestKriterier,
                        tuple.getT1()))
                .timeout(Duration.ofSeconds(opprettPersonTimeout));
    }
}
