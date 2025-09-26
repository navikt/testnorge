package no.nav.dolly.bestilling.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.personservice.PersonServiceClient;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.elastic.BestillingElasticRepository;
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

import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.jpa.Testident.Master.PDL;

@Slf4j
@Service
public class ImportAvPersonerFraPdlService extends DollyBestillingService {

    private final PersonServiceClient personServiceClient;

    public ImportAvPersonerFraPdlService(
            BestillingElasticRepository bestillingElasticRepository,
            BestillingProgressRepository bestillingProgressRepository,
            BestillingRepository bestillingRepository,
            BestillingService bestillingService,
            CounterCustomRegistry counterCustomRegistry,
            ErrorStatusDecoder errorStatusDecoder,
            IdentService identService,
            List<ClientRegister> clientRegisters,
            MapperFacade mapperFacade,
            ObjectMapper objectMapper,
            PersonServiceClient personServiceClient,
            PdlDataConsumer pdlDataConsumer,
            TestgruppeRepository testgruppeRepository,
            TransactionHelperService transactionHelperService,
            CacheManager cacheManager
    ) {
        super(
                bestillingElasticRepository,
                bestillingProgressRepository,
                bestillingRepository,
                bestillingService,
                counterCustomRegistry,
                errorStatusDecoder,
                identService,
                clientRegisters,
                mapperFacade,
                objectMapper,
                pdlDataConsumer,
                testgruppeRepository,
                transactionHelperService,
                cacheManager
        );
        this.personServiceClient = personServiceClient;
    }

    @Async
    public void executeAsync(Bestilling bestilling) {

        log.info("Bestilling med id=#{} og type={} er startet ...", bestilling.getId(), getBestillingType(bestilling));

        var bestKriterier = getDollyBestillingRequest(bestilling);
        if (nonNull(bestKriterier)) {

            Flux.fromArray(bestilling.getPdlImport().split(","))
                    .flatMap(testnorgeIdent -> opprettPerson(bestilling, bestKriterier, testnorgeIdent), 3)
                    .subscribe(progress -> log.info("Fullført oppretting av ident: {}", progress.getIdent()),
                            error -> doFerdig(bestilling).subscribe(),
                            () -> saveBestillingToElasticServer(bestKriterier, bestilling)
                                    .then(doFerdig(bestilling))
                                    .subscribe());

        } else {
            bestilling.setFeil("Feil: kunne ikke mappe JSON request, se logg!");
            doFerdig(bestilling)
                    .subscribe();
        }
    }

    private Mono<BestillingProgress> oppdaterStatus(BestillingProgress progress) {

        return transactionHelperService.persister(progress, BestillingProgress::setPdlImportStatus, "OK");
    }

    private Flux<BestillingProgress> opprettPerson(Bestilling bestilling, RsDollyUtvidetBestilling bestKriterier, String ident) {

        return Flux.from(bestillingService.isStoppet(bestilling.getId()))
                .takeWhile(BooleanUtils::isFalse)
                .concatMap(ok -> Mono.just(ident))
                .concatMap(testnorgeIdent -> Mono.just(OriginatorUtility.prepOriginator(bestKriterier, testnorgeIdent, mapperFacade)))
                .concatMap(originator -> opprettProgress(bestilling, PDL, originator.getIdent())
                        .zipWith(Mono.just(originator)))
                .concatMap(tuple -> oppdaterPdlPerson(tuple.getT2(), tuple.getT1())
                        .zipWith(Mono.just(tuple.getT1())))
                .concatMap(tuple -> sendOrdrePerson(tuple.getT2(), tuple.getT1())
                        .thenReturn(tuple.getT2()))
                .concatMap(progress -> opprettDollyPerson(progress, bestilling.getBruker())
                        .zipWith(Mono.just(progress)))
                .concatMap(tuple -> leggIdentTilGruppe(tuple.getT2(), bestKriterier.getBeskrivelse())
                        .thenReturn(tuple))
                .doOnNext(tuple -> counterCustomRegistry.invoke(bestKriterier))
                .concatMap(tuple -> gjenopprettKlienter(tuple.getT1(), bestKriterier,
                        fase1Klienter(), tuple.getT2(), true)
                        .then(personServiceClient.syncPerson(tuple.getT1(), tuple.getT2())
                                .filter(BestillingProgress::isPdlSync)
                                .then(gjenopprettKlienter(tuple.getT1(), bestKriterier,
                                        fase2Klienter(),
                                        tuple.getT2(), true)
                                        .then(gjenopprettKlienter(tuple.getT1(), bestKriterier,
                                                fase3Klienter(),
                                                tuple.getT2(), true)))
                                .then(oppdaterStatus(tuple.getT2()))));
    }
}