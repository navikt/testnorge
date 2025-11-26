package no.nav.dolly.bestilling.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.personservice.PersonServiceClient;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.elastic.service.OpenSearchService;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.metrics.CounterCustomRegistry;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.repository.IdentRepository;
import no.nav.dolly.repository.TestgruppeRepository;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.service.TransactionHelperService;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.apache.commons.lang3.StringUtils.isBlank;

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

        var bestKriterier = getDollyBestillingRequest(bestilling);
        var countBestillinger = new AtomicInteger(0);

        Mono.just(bestKriterier)
                .filter(request -> isBlank(request.getFeil()))
                .flatMap(request -> identService.getTestIdent(bestilling.getIdent()))
                .flatMap(testident -> opprettProgress(bestilling, testident.getMaster(), testident.getIdent()))
                .flatMap(this::sendOrdrePerson)
                .filter(BestillingProgress::isIdentGyldig)
                .flatMap(progress -> opprettDollyPerson(progress, bestilling.getBruker())
                        .zipWith(Mono.just(progress)))
                .doOnNext(tuple -> counterCustomRegistry.invoke(bestKriterier))
                .flatMap(tuple ->
                        gjenopprettKlienterStart(tuple.getT1(), bestKriterier, tuple.getT2(), true)
                                .then(personServiceClient.syncPerson(tuple.getT1(), tuple.getT2())
                                        .filter(BestillingProgress::isPdlSync)
                                        .flatMapMany(pdlSync ->
                                                identRepository.getBestillingerByIdent(bestilling.getIdent())
                                                        .filter(coBestilling -> !"{}".equals(coBestilling.getBestkriterier()) ||
                                                                countBestillinger.getAndIncrement() == 0))
                                        .map(coBestilling -> createBestilling(bestilling, coBestilling))
                                        .doOnNext(request ->
                                                log.info("Startet gjenopprett bestilling {} for ident: {}",
                                                        request.getId(), tuple.getT1().getIdent()))
                                        .concatMap(bestillingRequest ->
                                                gjenopprettKlienterFerdigstill(tuple.getT1(), bestillingRequest,
                                                        tuple.getT2(), false))
                                        .collectList()))
                .subscribe(progress -> log.info("Fullført oppretting av ident: {}", bestilling.getIdent()),
                        error -> doFerdig(bestilling).subscribe(),
                        () -> doFerdig(bestilling)
                                .subscribe());
    }
}