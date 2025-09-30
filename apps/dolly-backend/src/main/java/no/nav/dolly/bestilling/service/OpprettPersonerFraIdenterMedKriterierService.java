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
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static no.nav.dolly.domain.jpa.Testident.Master.PDLF;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
public class OpprettPersonerFraIdenterMedKriterierService extends DollyBestillingService {

    private final AvailCheckService availCheckService;
    private final PersonServiceClient personServiceClient;

    public OpprettPersonerFraIdenterMedKriterierService(
            AvailCheckService availCheckService,
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
        this.availCheckService = availCheckService;
        this.personServiceClient = personServiceClient;
    }

    @Async
    public void executeAsync(Bestilling bestilling) {

        log.info("Bestilling med id=#{} og type={} er startet ...", bestilling.getId(), getBestillingType(bestilling));

        var request = getDollyBestillingRequest(bestilling);

        Mono.just(request)
                .filter(bestKriterier -> isBlank(bestKriterier.getFeil()))
                .flatMapMany(bestKriterier ->
                        availCheckService.checkAvailable(bestilling.getOpprettFraIdenter())
                                .flatMap(avail -> opprettPerson(bestilling, bestKriterier, avail), 3))

                .subscribe(progress -> log.info("Fullført oppretting av ident: {}", progress.getIdent()),
                        error -> doFerdig(bestilling).subscribe(),
                        () -> saveBestillingToElasticServer(request, bestilling)
                                .then(doFerdig(bestilling))
                                .subscribe());

    }

    private Flux<BestillingProgress> opprettPerson(Bestilling bestilling,
                                                   RsDollyUtvidetBestilling bestKriterier,
                                                   AvailCheckService.AvailStatus availStatus) {

        return Flux.from(bestillingService.isStoppet(bestilling.getId()))
                .takeWhile(BooleanUtils::isFalse)
                .concatMap(ok -> Mono.just(availStatus))
                .filter(AvailCheckService.AvailStatus::isAvailable)
                .map(AvailCheckService.AvailStatus::getIdent)
                .map(availIdent -> OriginatorUtility.prepOriginator(bestKriterier,
                        availIdent, mapperFacade))
                .concatMap(originator -> opprettProgress(bestilling, PDLF, originator.getIdent())
                        .zipWith(Mono.just(originator)))
                .concatMap(tuple -> opprettPerson(tuple.getT2(), tuple.getT1())
                        .zipWith(Mono.just(tuple.getT1())))
                .concatMap(tuple -> sendOrdrePerson(tuple.getT2(), tuple.getT1())
                        .zipWith(Mono.just(tuple.getT2())))
                .filter(tuple -> StringUtils.isNotBlank(tuple.getT1()))
                .concatMap(tuple -> opprettDollyPerson(tuple.getT1(), tuple.getT2(), bestilling.getBruker())
                        .zipWith(Mono.just(tuple.getT2())))
                .concatMap(tuple -> leggIdentTilGruppe(tuple.getT1().getIdent(),
                        tuple.getT2(), bestKriterier.getBeskrivelse())
                        .thenReturn(tuple))
                .doOnNext(tuple -> counterCustomRegistry.invoke(bestKriterier))
                .concatMap(tuple ->
                        gjenopprettKlienterStart(tuple.getT1(), bestKriterier, tuple.getT2(), true)
                                .then(personServiceClient.syncPerson(tuple.getT1(), tuple.getT2())
                                        .filter(BestillingProgress::isPdlSync)
                                        .then(gjenopprettKlienterFerdigstill(tuple.getT1(), bestKriterier,
                                                tuple.getT2(), true))));
    }
}
