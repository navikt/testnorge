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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static no.nav.dolly.domain.jpa.Testident.Master.PDLF;
import static org.apache.commons.lang3.ObjectUtils.getIfNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
public class OpprettPersonerByKriterierService extends DollyBestillingService {

    private final PersonServiceClient personServiceClient;

    public OpprettPersonerByKriterierService(
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

        log.info("Bestilling med id=#{} og type={} er startet i miljøer {} ...", bestilling.getId(), getBestillingType(bestilling), bestilling.getMiljoer());

        var bestRequest = new AtomicReference<RsDollyBestillingRequest>();
        getDollyBestillingRequest(bestilling)
                .doOnNext(bestRequest::set)
                .filter(bestKriterier-> isBlank(bestKriterier.getFeil()))
                .flatMapMany(request -> Flux.range(0, bestilling.getAntallIdenter())
                        .flatMap(_ -> opprettPerson(bestilling.getId(), request), 3))
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
                        () -> saveBestillingToOpenSearchServer(bestRequest.get(), bestilling)
                                .onErrorResume(e -> {
                                    log.warn("Feil ved lagring til OpenSearch for bestilling {}: {}", bestilling.getId(), e.getMessage());
                                    return Mono.empty();
                                })
                                .then(doFerdig(bestilling))
                                .subscribe());
    }

    private Mono<BestillingProgress> opprettPerson(Long bestillingId,
                                                   RsDollyBestillingRequest bestKriterier) {

        var progress = new AtomicReference<BestillingProgress>();
        return bestillingService.isStoppet(bestillingId)
                .filter(BooleanUtils::isFalse)
                .flatMap(_ -> prosesserPerson(bestillingId, bestKriterier, progress)
                        .switchIfEmpty(Mono.error(new DollyFunctionalException("Oppretting ikke fullført; annen feil")))
                        .doOnNext(_ -> log.info("Fullført oppretting av ident: {}", progress.get().getIdent()))
                        .onErrorResume(e -> setOpprettIdentFeilet(bestillingId, progress.get(), progress.get().getIdent(), e)));
    }

    private Mono<BestillingProgress> prosesserPerson(Long bestillingId, RsDollyBestillingRequest bestKriterier,
                                                     AtomicReference<BestillingProgress> bestProgress) {

        return opprettProgress(bestillingId, PDLF)
                .flatMap(progress -> opprettPerson(bestKriterier, progress))
                .flatMap(progress -> leggIdentTilGruppe(progress, bestKriterier.getBeskrivelse()))
                .flatMap(this::sendOrdrePerson)
                .doOnNext(bestProgress::set)
                .filter(BestillingProgress::isIdentGyldig)
                .flatMap(progress -> opprettDollyPersonNyPerson(progress, bestKriterier))
                .doOnNext(_ -> counterCustomRegistry.invoke(bestKriterier))
                .flatMap(dollyPerson -> gjenopprettKlienterStart(dollyPerson, bestKriterier, bestProgress.get(), OPPRETT_ENDRE_TRUE)
                        .thenReturn(dollyPerson))
                .flatMap(dollyPerson -> personServiceClient.syncPerson(dollyPerson, bestProgress.get())
                        .zipWith(Mono.just(dollyPerson)))
                .filter(tuple -> tuple.getT1().isPdlSync())
                .flatMap(tuple -> gjenopprettKlienterFerdigstill(tuple.getT2(), bestKriterier,
                        bestProgress.get(), OPPRETT_ENDRE_TRUE))
                .timeout(Duration.ofSeconds(opprettPersonTimeout));
    }
}
