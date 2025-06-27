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
import no.nav.dolly.util.TransactionHelperService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isFalse;

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
    public void executeAsync(Bestilling bestilling) {

        log.info("Bestilling med id=#{} og type={} er startet ...", bestilling.getId(), getBestillingType(bestilling));

        var bestKriterier = getDollyBestillingRequest(bestilling);
        if (nonNull(bestKriterier)) {
            bestKriterier.setEkskluderEksternePersoner(true);

            bestillingProgressRepository.findByBestillingId(bestilling.getOpprettetFraId())
                    .flatMap(progress -> bestillingService.isStoppet(progress.getBestillingId())
                            .zipWith(Mono.just(progress)))
                    .takeWhile(tuple -> isFalse(tuple.getT1()))
                    .flatMap(tuple -> opprettProgress(bestilling, tuple.getT2().getMaster(), tuple.getT2().getIdent())
                            .flatMap(progress -> sendOrdrePerson(progress,
                                    PdlResponse.builder()
                                            .ident(tuple.getT2().getIdent()).build())
                                    .flatMap(ident -> opprettDollyPerson(ident, progress, bestilling.getBruker()))
                                    .doOnNext(dollyPerson -> counterCustomRegistry.invoke(bestKriterier))
                                    .flatMap(dollyPerson ->
                                            gjenopprettKlienter(dollyPerson, bestKriterier, fase1Klienter(), progress, false)
                                                    .then(personServiceClient.syncPerson(dollyPerson, progress)
                                                            .filter(BestillingProgress::isPdlSync)
                                                            .flatMap(pdlSync -> createBestilling(bestilling, tuple.getT2().getBestillingId()))
                                                            .flatMap(cobestilling ->
                                                                    gjenopprettKlienter(dollyPerson, cobestilling,
                                                                            fase2Klienter(), progress, false)
                                                                            .then(gjenopprettKlienter(dollyPerson, cobestilling,
                                                                                    fase3Klienter(), progress, false)))))
                            ))
                    .then(doFerdig(bestilling))
                    .doFinally(done -> clearCache())
                    .subscribe();

        } else {
            bestilling.setFeil("Feil: kunne ikke mappe JSON request, se logg!");
            doFerdig(bestilling)
                    .subscribe();
        }
    }
}