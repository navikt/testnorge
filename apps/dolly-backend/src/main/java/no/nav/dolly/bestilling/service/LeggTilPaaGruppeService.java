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
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonUpdateRequestDTO;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
public class LeggTilPaaGruppeService extends DollyBestillingService {

    private final PersonServiceClient personServiceClient;

    public LeggTilPaaGruppeService(
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

        var bestKriterier = getDollyBestillingRequest(bestilling);

        Mono.just(bestKriterier)
                .filter(request -> isBlank(request.getFeil()))
                .flatMapMany(request ->
                        identService.getTestidenterByGruppeId(bestilling.getGruppeId())
                                .flatMap(testident -> leggTilPaaPerson(bestilling, bestKriterier, testident), 3))
                .subscribe(progress -> log.info("Fullført oppretting av ident: {}", progress.getIdent()),
                        error -> doFerdig(bestilling).subscribe(),
                        () -> saveBestillingToElasticServer(bestKriterier, bestilling)
                                .then(doFerdig(bestilling))
                                .subscribe());
    }

    private Flux<BestillingProgress> leggTilPaaPerson(Bestilling bestilling, RsDollyUtvidetBestilling bestKriterier, Testident testident) {

        return Flux.from(bestillingService.isStoppet(bestilling.getId()))
                .takeWhile(BooleanUtils::isFalse)
                .map(ok -> OriginatorUtility.prepOriginator(bestKriterier, testident, mapperFacade))
                .concatMap(originator -> opprettProgress(bestilling, originator.getMaster(), originator.getIdent())
                        .zipWith(Mono.just(originator)))
                .concatMap(tuple -> oppdaterPdlPerson(tuple.getT2(), tuple.getT1(), tuple.getT2().getIdent())
                        .zipWith(Mono.just(tuple.getT1())))
                .concatMap(tuple -> sendOrdrePerson(tuple.getT2(), tuple.getT1())
                        .zipWith(Mono.just(tuple.getT2())))
                .filter(tuple -> isNotBlank(tuple.getT1()))
                .concatMap(tuple -> opprettDollyPerson(tuple.getT1(), tuple.getT2(), bestilling.getBruker())
                        .zipWith(Mono.just(tuple.getT2())))
                .concatMap(tuple -> (!tuple.getT1().getIdent().equals(tuple.getT2().getIdent()) ?
                        updateIdent(tuple.getT1(), tuple.getT2()) : Mono.just(tuple.getT1().getIdent()))
                        .doOnNext(nyident -> counterCustomRegistry.invoke(bestKriterier))
                        .then(gjenopprettKlienter(tuple.getT1(), bestKriterier,
                                fase1Klienter(),
                                tuple.getT2(), true)
                                .then(personServiceClient.syncPerson(tuple.getT1(), tuple.getT2())
                                        .filter(BestillingProgress::isPdlSync)
                                        .then(gjenopprettKlienter(tuple.getT1(), bestKriterier,
                                                fase2Klienter(),
                                                tuple.getT2(), true)
                                                .then(gjenopprettKlienter(tuple.getT1(), bestKriterier,
                                                        fase3Klienter(),
                                                        tuple.getT2(), true))))
                        ));
    }

    private Mono<PdlResponse> oppdaterPdlPerson(OriginatorUtility.Originator originator, BestillingProgress progress, String ident) {

        if (nonNull(originator.getPdlBestilling()) && nonNull(originator.getPdlBestilling().getPerson())) {

            return transactionHelperService.persister(progress, BestillingProgress::setPdlForvalterStatus,
                            "Info: Oppdatering av person startet ...")
                    .then(pdlDataConsumer.oppdaterPdl(ident,
                                    PersonUpdateRequestDTO.builder()
                                            .person(originator.getPdlBestilling().getPerson())
                                            .build())
                            .doOnNext(response -> log.info("Oppdatert person til PDL-forvalter med response {}", response)));

        } else {
            return Mono.just(PdlResponse.builder()
                    .ident(ident)
                    .build());
        }
    }
}
