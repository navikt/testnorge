package no.nav.dolly.bestilling.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.personservice.PersonServiceClient;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.elastic.BestillingElasticRepository;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.metrics.CounterCustomRegistry;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.repository.TestgruppeRepository;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.util.ClearCacheUtil;
import no.nav.dolly.service.TransactionHelperService;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.List;

import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.jpa.Testident.Master.PDL;
import static org.apache.commons.lang3.BooleanUtils.isFalse;

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
                    .flatMap(testnorgeIdent -> bestillingService.isStoppet(bestilling.getId())
                            .zipWith(Mono.just(testnorgeIdent)))
                    .takeWhile(tuple -> isFalse(tuple.getT1()))
                    .map(Tuple2::getT2)
                    .flatMap(testnorgeIdent -> Mono.just(OriginatorUtility.prepOriginator(bestKriterier, testnorgeIdent, mapperFacade)))
                    .flatMap(originator -> opprettProgress(bestilling, PDL, originator.getIdent())
                            .flatMap(progress -> oppdaterPdlPerson(originator, progress)
                                    .flatMap(pdlResponse -> sendOrdrePerson(progress, pdlResponse)
                                            .flatMap(testnorgeIdent -> opprettDollyPerson(progress, bestilling.getBruker())
                                                    .flatMap(dollyPerson -> leggIdentTilGruppe(progress, bestKriterier.getBeskrivelse())
                                                            .thenReturn(dollyPerson))
                                                    .doOnNext(dollyPerson -> counterCustomRegistry.invoke(bestKriterier))
                                                    .flatMap(dollyPerson -> gjenopprettKlienter(dollyPerson, bestKriterier,
                                                            fase1Klienter(), progress, true)
                                                            .then(personServiceClient.syncPerson(dollyPerson, progress)
                                                                    .filter(BestillingProgress::isPdlSync)
                                                                    .flatMap(sync -> gjenopprettKlienter(dollyPerson, bestKriterier,
                                                                            fase2Klienter(),
                                                                            progress, true)
                                                                            .then(gjenopprettKlienter(dollyPerson, bestKriterier,
                                                                                    fase3Klienter(),
                                                                                    progress, true)))
                                                                    .then(oppdaterStatus(progress))))
                                            ))))
                    .collectList()
                    .then(doFerdig(bestilling))
                    .then(saveBestillingToElasticServer(bestKriterier, bestilling))
                    .doOnTerminate(new ClearCacheUtil(cacheManager))
                    .subscribe();

        } else {
            bestilling.setFeil("Feil: kunne ikke mappe JSON request, se logg!");
            doFerdig(bestilling)
                    .subscribe();
        }
    }

    private Mono<BestillingProgress> oppdaterStatus(BestillingProgress progress) {

        return transactionHelperService.persister(progress, BestillingProgress::setPdlImportStatus, "OK");
    }
}
