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
import no.nav.dolly.elastic.BestillingElasticRepository;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.metrics.CounterCustomRegistry;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.repository.IdentRepository;
import no.nav.dolly.repository.TestgruppeRepository;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.util.ClearCacheUtil;
import no.nav.dolly.service.TransactionHelperService;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isFalse;

@Slf4j
@Service
public class GjenopprettGruppeService extends DollyBestillingService {

    private final PersonServiceClient personServiceClient;
    private final IdentRepository identRepository;

    public GjenopprettGruppeService(
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
            IdentRepository identRepository,
            CacheManager cacheManager) {
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
        this.identRepository = identRepository;
    }

    @Async
    public void executeAsync(Bestilling bestilling) {

        log.info("Bestilling med id=#{} og type={} er startet ...", bestilling.getId(), getBestillingType(bestilling));

        var bestKriterier = getDollyBestillingRequest(bestilling);
        if (nonNull(bestKriterier)) {
            bestKriterier.setEkskluderEksternePersoner(true);

            var counterIdentBestilling = new HashMap<String, Boolean>();
            identService.getTestidenterByGruppeId(bestilling.getGruppeId())
                    .flatMap(testident -> bestillingService.isStoppet(bestilling.getId())
                            .zipWith(Mono.just(testident)))
                    .takeWhile(tuple -> isFalse(tuple.getT1()))
                    .map(Tuple2::getT2)
                    .doOnNext(testident -> counterIdentBestilling.put(testident.getIdent(), false))
                    .flatMap(testident -> opprettProgress(bestilling, testident.getMaster(), testident.getIdent())
                            .flatMap(progress -> sendOrdrePerson(progress, PdlResponse.builder()
                                    .ident(testident.getIdent())
                                    .build())
                                    .filter(Objects::nonNull)
                                    .flatMap(ident -> opprettDollyPerson(ident, progress, bestilling.getBruker())
                                            .doOnNext(dollyPerson -> counterCustomRegistry.invoke(bestKriterier))
                                            .flatMap(dollyPerson ->
                                                    gjenopprettKlienter(dollyPerson, bestKriterier,
                                                            fase1Klienter(),
                                                            progress, true)
                                                            .then(personServiceClient.syncPerson(dollyPerson, progress)
                                                                    .filter(BestillingProgress::isPdlSync))
                                                            .map(sync -> identRepository.getBestillingerFromGruppe(bestilling.getGruppeId())
                                                                    .filter(coBestilling -> ident.equals(coBestilling.getIdent()) &&
                                                                            !"{}".equals(coBestilling.getBestkriterier()) ||
                                                                            Boolean.FALSE.equals(counterIdentBestilling.replace(testident.getIdent(), true)))
                                                                    .map(coBestilling -> createBestilling(bestilling, coBestilling))
                                                                    .doOnNext(request -> log.info("Startet gjenopprett bestilling {} for ident: {}",
                                                                            request.getId(), testident.getIdent()))
                                                                    .flatMap(bestillingRequest ->
                                                                            gjenopprettKlienter(dollyPerson, bestillingRequest,
                                                                                    fase2Klienter(),
                                                                                    progress, false)
                                                                                    .then(gjenopprettKlienter(dollyPerson, bestillingRequest,
                                                                                            fase3Klienter(),
                                                                                            progress, false))))
                                                            .flatMap(Mono::from)))))
                    .collectList()
                    .then(doFerdig(bestilling))
                    .doOnTerminate(new ClearCacheUtil(cacheManager))
                    .subscribe();
        }
    }
}
