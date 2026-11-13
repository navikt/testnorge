package no.nav.dolly.bestilling.service;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.personservice.PersonServiceClient;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.apache.commons.lang3.ObjectUtils.getIfNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
public class ImportAvPersonerFraPdlService extends DollyBestillingService {

    private final PersonServiceClient personServiceClient;

    public ImportAvPersonerFraPdlService(
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

        var request = new AtomicReference<RsDollyUtvidetBestilling>();
        getDollyBestillingRequest(bestilling)
                .doOnNext(request::set)
                .filter(bestKriterier -> isBlank(bestKriterier.getFeil()))
                .flatMapMany(_ ->
                        Flux.fromArray(bestilling.getPdlImport().split(","))
                                .flatMap(testnorgeIdent -> opprettPerson(bestilling.getId(), request.get(), testnorgeIdent), 3))
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
                        () -> saveBestillingToOpenSearchServer(request.get(), bestilling)
                                .onErrorResume(e -> {
                                    log.warn("Feil ved lagring til OpenSearch for bestilling {}: {}", bestilling.getId(), e.getMessage());
                                    return Mono.empty();
                                })
                                .then(doFerdig(bestilling))
                                .subscribe());
    }

    private Mono<BestillingProgress> opprettPerson(Long bestillingId,
                                                   RsDollyUtvidetBestilling bestKriterier,
                                                   String ident) {

        var progress = new AtomicReference<BestillingProgress>();
        return bestillingService.isStoppet(bestillingId)
                .filter(BooleanUtils::isFalse)
                .flatMap(_ -> prosesserPerson(bestillingId, bestKriterier, progress, ident)
                        .switchIfEmpty(Mono.error(new DollyFunctionalException("Oppretting ikke fullført; annen feil")))
                        .doOnNext(_ -> log.info("Fullført oppretting av ident: {}", progress.get().getIdent()))
                        .onErrorResume(e -> setOpprettIdentFeilet(bestillingId, progress.get(), ident, e)));
    }

    private Mono<BestillingProgress> prosesserPerson(Long bestillingId, RsDollyUtvidetBestilling bestKriterier,
                                                     AtomicReference<BestillingProgress> bestProgress,String ident) {

        return opprettProgress(bestillingId, ident)
                .doOnNext(bestProgress::set)
                .flatMap(progress -> oppdaterPerson(bestKriterier, progress, ident))
                .flatMap(progress -> leggIdentTilGruppe(progress, bestKriterier.getBeskrivelse()))
                .flatMap(this::sendOrdrePerson)
                .doOnNext(bestProgress::set)
                .filter(BestillingProgress::isIdentGyldig)
                .flatMap(progress -> opprettDollyPersonNyoppretting(progress, bestKriterier)
                        .zipWith(Mono.just(progress)))
                .doOnNext(_ -> counterCustomRegistry.invoke(bestKriterier))
                .flatMap(tuple ->
                        gjenopprettKlienterStart(tuple.getT1(), bestKriterier, tuple.getT2())
                                .zipWith(Mono.just(tuple.getT1())))
                .flatMap(tuple ->
                        personServiceClient.syncPerson(tuple.getT2(), tuple.getT1())
                                .zipWith(Mono.just(tuple.getT2())))
                .filter(tuple -> tuple.getT1().isPdlSync())
                .flatMap(tuple -> gjenopprettKlienterFerdigstill(tuple.getT2(), bestKriterier,
                        tuple.getT1()))
                .timeout(Duration.ofSeconds(opprettPersonTimeout));
    }
}