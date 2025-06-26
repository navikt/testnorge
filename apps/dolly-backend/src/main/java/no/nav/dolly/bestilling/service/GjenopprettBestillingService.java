package no.nav.dolly.bestilling.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientFuture;
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
import no.nav.dolly.service.BestillingProgressService;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;

@Slf4j
@Service
public class GjenopprettBestillingService extends DollyBestillingService {

    private final BestillingProgressService bestillingProgressService;
    private final PersonServiceClient personServiceClient;

    public GjenopprettBestillingService(
            BestillingElasticRepository bestillingElasticRepository,
            BestillingProgressRepository bestillingProgressRepository,
            BestillingProgressService bestillingProgressService,
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
        this.bestillingProgressService = bestillingProgressService;
        this.personServiceClient = personServiceClient;
    }

    @Async
    public void executeAsync(Bestilling bestilling) {

        log.info("Bestilling med id=#{} og type={} er startet ...", bestilling.getId(), getBestillingType(bestilling));

        var bestKriterier = getDollyBestillingRequest(bestilling);
        if (nonNull(bestKriterier)) {
            bestKriterier.setEkskluderEksternePersoner(true);

            var gamleProgresser = bestillingProgressService.fetchBestillingProgressByBestillingId(bestilling.getOpprettetFraId());

          var test =   Flux.fromIterable(gamleProgresser)
//                    .takeWhile(test -> !bestillingService.isStoppet(bestilling.getId()))
                    .flatMap(gmlProgress -> opprettProgress(bestilling, gmlProgress.getMaster(), gmlProgress.getIdent())
                            .flatMap(progress -> sendOrdrePerson(progress, PdlResponse.builder()
                                    .ident(gmlProgress.getIdent())
                                    .build())
                                    .filter(Objects::nonNull)
                                    .flatMap(ident -> opprettDollyPerson(ident, progress, bestilling.getBruker())
                                            .doOnNext(dollyPerson -> counterCustomRegistry.invoke(bestKriterier))
                                            .flatMap(dollyPerson -> Flux.concat(
                                                            gjenopprettKlienter(dollyPerson, bestKriterier,
                                                                    fase1Klienter(),
                                                                    progress, false),
                                                                    Flux.from(personServiceClient.syncPerson(dollyPerson, progress))
                                                                            .filter(BestillingProgress::isPdlSync)
                                                                    .flatMap(pdlSync -> createBestilling(bestilling, gmlProgress.getBestillingId()))
                                                                    .flatMap(cobestilling -> Flux.concat(
                                                                            gjenopprettKlienter(dollyPerson, cobestilling,
                                                                                    fase2Klienter(),
                                                                                    progress, false),
                                                                            gjenopprettKlienter(dollyPerson, cobestilling,
                                                                                    fase3Klienter(),
                                                                                    progress, false))))
                                                                    .map(t -> )
//                                                                    .onErrorResume(throwable -> {
//                                                                        var description = WebClientError.describe(throwable);
//                                                                        var error = errorStatusDecoder.getErrorText(description.getStatus(), description.getMessage());
//                                                                        log.error("Feil oppsto ved utføring av bestilling, progressId {} {}",
//                                                                                progress.getId(), error, throwable);
//                                                                        progress.setFeil(error);
//                                                                        return saveFeil(progress, error).flux();
//                                                                    })


//                    .next()
//                    .flatMap(progresser -> doFerdig(bestilling))
//                    .doFinally(signal -> clearCache())
//                    .subscribe();

        } else {
            bestilling.setFeil("Feil: kunne ikke mappe JSON request, se logg!");
            doFerdig(bestilling);
        }
    }
}
