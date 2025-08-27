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
import no.nav.dolly.repository.TestgruppeRepository;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.util.ClearCacheUtil;
import no.nav.dolly.service.TransactionHelperService;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonUpdateRequestDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.List;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isFalse;

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
        if (nonNull(bestKriterier)) {

            identService.getTestidenterByGruppeId(bestilling.getGruppeId())
                    .flatMap(testident -> bestillingService.isStoppet(bestilling.getId())
                            .zipWith(Mono.just(testident)))
                    .takeWhile(tuple -> isFalse(tuple.getT1()))
                    .map(Tuple2::getT2)
                    .flatMap(testident -> Mono.just(OriginatorUtility.prepOriginator(bestKriterier, testident, mapperFacade))
                            .flatMap(originator -> opprettProgress(bestilling, originator.getMaster(), testident.getIdent())
                                    .flatMap(progress -> oppdaterPdlPerson(originator, progress, testident.getIdent())
                                            .flatMap(pdlResponse -> sendOrdrePerson(progress, pdlResponse))
                                            .filter(StringUtils::isNotBlank)
                                            .flatMap(ident -> opprettDollyPerson(ident, progress, bestilling.getBruker())
                                                    .flatMap(dollyPerson -> (!dollyPerson.getIdent().equals(progress.getIdent()) ?
                                                            updateIdent(dollyPerson, progress) : Mono.just(ident))
                                                            .doOnNext(nyident -> counterCustomRegistry.invoke(bestKriterier))
                                                            .flatMap(nyIdent ->
                                                                    gjenopprettKlienter(dollyPerson, bestKriterier,
                                                                            fase1Klienter(),
                                                                            progress, true)
                                                                            .then(personServiceClient.syncPerson(dollyPerson, progress)
                                                                                    .filter(BestillingProgress::isPdlSync)
                                                                                    .flatMap(pdlSync ->
                                                                                            gjenopprettKlienter(dollyPerson, bestKriterier,
                                                                                                    fase2Klienter(),
                                                                                                    progress, true)
                                                                                                    .then(gjenopprettKlienter(dollyPerson, bestKriterier,
                                                                                                            fase3Klienter(),
                                                                                                            progress, true))))
                                                            ))))))
                    .collectList()
                    .then(doFerdig(bestilling))
                    .then(saveBestillingToElasticServer(bestKriterier, bestilling))
                    .doOnTerminate(new ClearCacheUtil(cacheManager))
                    .subscribe();
        }
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
