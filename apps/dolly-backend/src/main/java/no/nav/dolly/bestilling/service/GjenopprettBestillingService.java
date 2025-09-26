package no.nav.dolly.bestilling.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.pdldata.dto.PdlResponse;
import no.nav.dolly.bestilling.personservice.PersonServiceClient;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
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

import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
public class GjenopprettBestillingService extends DollyBestillingService {

    private final PersonServiceClient personServiceClient;

    public GjenopprettBestillingService(
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
            PdlDataConsumer pdlDataConsumer,
            PersonServiceClient personServiceClient,
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

        bestillingRepository.findById(bestilling.getOpprettetFraId())
                .map(this::getDollyBestillingRequest)
                .map(bestKriterier -> {
                    bestKriterier.setEkskluderEksternePersoner(true);
                    return bestKriterier;
                })
                .filter(request -> isBlank(request.getFeil()))
                .flatMapMany(bestKriterier ->
                        bestillingProgressRepository.findAllByBestillingId(bestilling.getOpprettetFraId())
                                .filter(BestillingProgress::isIdentGyldig)
                                .flatMap(progress -> gjenopprettBestilling(bestilling, bestKriterier,
                                        progress.getMaster(), progress.getIdent()), 3))
                .subscribe(progress -> log.info("Fullført oppretting av ident: {}", progress.getIdent()),
                        error -> doFerdig(bestilling).subscribe(),
                        () -> doFerdig(bestilling).subscribe());
    }

    private Flux<BestillingProgress> gjenopprettBestilling(Bestilling bestilling, RsDollyBestillingRequest bestKriterier,
                                                           Testident.Master master, String ident) {

        return Flux.from(bestillingService.isStoppet(bestilling.getId()))
                .takeWhile(BooleanUtils::isFalse)
                .concatMap(tuple -> opprettProgress(bestilling, master, ident))
                .concatMap(progress -> sendOrdrePerson(progress,
                        PdlResponse.builder().ident(progress.getIdent()).build())
                        .thenReturn(progress))
                .concatMap(progress -> opprettDollyPerson(ident, progress, bestilling.getBruker())
                        .zipWith(Mono.just(progress)))
                .doOnNext(tuple -> counterCustomRegistry.invoke(bestKriterier))
                .concatMap(tuple ->
                        gjenopprettKlienter(tuple.getT1(), bestKriterier, fase1Klienter(), tuple.getT2(), false)
                                .then(personServiceClient.syncPerson(tuple.getT1(), tuple.getT2())
                                        .doOnNext(progress1 -> log.info("Status på progress {}", progress1))
                                        .filter(BestillingProgress::isPdlSync)
                                        .then(gjenopprettKlienter(tuple.getT1(), bestKriterier,
                                                fase2Klienter(), tuple.getT2(), false)
                                                .then(gjenopprettKlienter(tuple.getT1(), bestKriterier,
                                                        fase3Klienter(), tuple.getT2(), false)))));
    }
}