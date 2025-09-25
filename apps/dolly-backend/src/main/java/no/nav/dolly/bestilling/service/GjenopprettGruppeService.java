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
import no.nav.dolly.repository.IdentRepository;
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

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;

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

            identService.getTestidenterByGruppeId(bestilling.getGruppeId())
                    .flatMap(testident -> utfoergjenoppretting(bestKriterier, bestilling, testident), 3)
                    .subscribe(progress -> log.info("Fullført gjenoppretting av ident: {}", progress.getIdent()),
                            error -> doFerdig(bestilling).subscribe(),
                            () -> doFerdig(bestilling).subscribe());
        }
    }

    private static PdlResponse buildPdlRequest(String ident) {
        return PdlResponse.builder().ident(ident).build();
    }

    private Flux<BestillingProgress> utfoergjenoppretting(RsDollyBestillingRequest bestKriterier, Bestilling bestilling, Testident testident) {

        var counterIdentBestilling = new HashMap<String, Boolean>();

        return Flux.from(bestillingService.isStoppet(bestilling.getId()))
                .filter(BooleanUtils::isFalse)
                .doOnNext(ignore -> counterIdentBestilling.put(testident.getIdent(), false))
                .concatMap(ignore -> opprettProgress(bestilling, testident.getMaster(), testident.getIdent())
                        .zipWith(Mono.just(testident)))
                .concatMap(tuple ->
                        sendOrdrePerson(tuple.getT1(), buildPdlRequest(tuple.getT2().getIdent()))
                                .filter(Objects::nonNull)
                                .zipWith(Mono.just(tuple.getT1())))
                .concatMap(tuple -> opprettDollyPerson(tuple.getT1(), tuple.getT2(), bestilling.getBruker())
                        .zipWith(Mono.just(tuple.getT2())))
                .doOnNext(tuple -> counterCustomRegistry.invoke(bestKriterier))
                .concatMap(tuple ->
                        gjenopprettKlienter(tuple.getT1(), bestKriterier, fase1Klienter(), tuple.getT2(), true)
                                .zipWith(Mono.just(tuple.getT1())))
                .concatMap(tuple -> personServiceClient.syncPerson(tuple.getT2(), tuple.getT1())
                        .zipWith(Mono.just(tuple.getT2())))
                .doOnNext(tuple -> log.info("Fullført fase 1 for ident: {}", tuple.getT1()))
                .filter(tuple -> tuple.getT1().isPdlSync())
                .concatMap(tuple ->
                        identRepository.getBestillingerFromGruppe(bestilling.getGruppeId())
                                .filter(coBestilling -> tuple.getT2().getIdent().equals(coBestilling.getIdent()) &&
                                        !"{}".equals(coBestilling.getBestkriterier()) ||
                                        Boolean.FALSE.equals(counterIdentBestilling.replace(tuple.getT1().getIdent(), true)))
                                .map(coBestilling -> createBestilling(bestilling, coBestilling))
                                .doOnNext(request -> log.info("Startet gjenopprett bestilling {} for ident: {}",
                                        request.getId(), tuple.getT1().getIdent()))
                                .zipWith(Mono.just(tuple)))
                .concatMap(tuple ->
                        gjenopprettKlienter(tuple.getT2().getT2(), tuple.getT1(),
                                fase2Klienter(), tuple.getT2().getT1(), false)
                                .then(gjenopprettKlienter(tuple.getT2().getT2(), tuple.getT1(),
                                        fase3Klienter(),
                                        tuple.getT2().getT1(), false)));
    }
}