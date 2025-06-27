package no.nav.dolly.bestilling.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.personservice.PersonServiceClient;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUpdateRequest;
import no.nav.dolly.elastic.BestillingElasticRepository;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.metrics.CounterCustomRegistry;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.repository.TestgruppeRepository;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.util.TransactionHelperService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
public class OppdaterPersonService extends DollyBestillingService {

    private final PersonServiceClient personServiceClient;

    public OppdaterPersonService(
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
            TransactionHelperService transactionHelperService
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
                transactionHelperService
        );
        this.personServiceClient = personServiceClient;
    }

    @Async
    public void oppdaterPersonAsync(RsDollyUpdateRequest request, Bestilling bestilling) {

        log.info("Bestilling med id=#{} med type={} er startet ...", bestilling.getId(), getBestillingType(bestilling));
        request.setId(bestilling.getId());

        identService.getTestIdent(bestilling.getIdent())
                .flatMap(testident -> Mono.just(OriginatorUtility.prepOriginator(request, testident, mapperFacade))
                        .zipWith(Mono.just(testident))
                        .flatMap(tuple -> opprettProgress(bestilling, tuple.getT1().getMaster(), testident.getIdent())
                                .flatMap(progress ->
                                        oppdaterPdlPerson(tuple.getT1(), progress)
                                                .flatMap(pdlResponse -> sendOrdrePerson(progress, pdlResponse))
                                                .filter(StringUtils::isNotBlank)
                                                .flatMap(ident -> opprettDollyPerson(ident, progress, bestilling.getBruker())
                                                        .flatMap(dollyPerson -> (!dollyPerson.getIdent().equals(bestilling.getIdent()) ?
                                                                updateIdent(dollyPerson, progress) : Mono.just(ident))
                                                                .doOnNext(nyident -> counterCustomRegistry.invoke(request))
                                                                .flatMap(nyIdent ->
                                                                        gjenopprettKlienter(dollyPerson, request,
                                                                                fase1Klienter(),
                                                                                progress, true)
                                                                                .then(personServiceClient.syncPerson(dollyPerson, progress)
                                                                                        .filter(BestillingProgress::isPdlSync)
                                                                                        .flatMap(sync ->
                                                                                                gjenopprettKlienter(dollyPerson, request,
                                                                                                        fase2Klienter(),
                                                                                                        progress, true)
                                                                                                        .then(gjenopprettKlienter(dollyPerson, request,
                                                                                                                fase3Klienter(),
                                                                                                                progress, true)))))
                                                        )))))
                .then(doFerdig(bestilling))
                .doFinally(done -> clearCache())
                .subscribe();
    }
}
