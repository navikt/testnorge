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
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.elastic.BestillingElasticRepository;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.metrics.CounterCustomRegistry;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.repository.IdentRepository.GruppeBestillingIdent;
import no.nav.dolly.repository.TestgruppeRepository;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Objects.nonNull;

@Slf4j
@Service
public class GjenopprettIdentService extends DollyBestillingService {

    private final PersonServiceClient personServiceClient;

    public GjenopprettIdentService(
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

        RsDollyBestillingRequest bestKriterier = getDollyBestillingRequest(bestilling);
        if (nonNull(bestKriterier)) {
            bestKriterier.setEkskluderEksternePersoner(true);

            var tIdent = identService.getTestIdent(bestilling.getIdent());

            var coBestillinger = identService.getBestillingerFromIdent(bestilling.getIdent()).stream()
                    .sorted(Comparator.comparing(GruppeBestillingIdent::getBestillingId))
                    .toList();

            var countEmptyBestillinger = new AtomicInteger(0);
            Flux.just(tIdent)
                    .flatMap(testident -> opprettProgress(bestilling, testident.getMaster(), testident.getIdent())
                            .flatMap(progress -> sendOrdrePerson(progress, PdlResponse.builder()
                                    .ident(testident.getIdent())
                                    .build())
                                    .filter(Objects::nonNull)
                                    .flatMap(ident -> opprettDollyPerson(ident, progress, bestilling.getBruker())
                                            .doOnNext(dollyPerson -> counterCustomRegistry.invoke(bestKriterier))
                                            .flatMap(dollyPerson -> Flux.concat(
                                                    gjenopprettKlienter(dollyPerson, bestKriterier,
                                                            fase1Klienter(),
                                                            progress, true),
                                                    personServiceClient.syncPerson(dollyPerson, progress)
                                                            .map(ClientFuture::get)
                                                            .filter(BestillingProgress::isPdlSync)
                                                            .flatMap(pdlSync ->
                                                                    Flux.fromIterable(coBestillinger)
                                                                            .concatMap(bestilling1 -> Flux.just(bestilling1)
                                                                                    .filter(cobestilling -> ident.equals(cobestilling.getIdent()))
                                                                                    .flatMap(cobestilling -> createBestilling(bestilling, cobestilling)
                                                                                            .filter(bestillingRequest -> countEmptyBestillinger.getAndIncrement() == 0 ||
                                                                                                    bestillingRequest.isNonEmpty())
                                                                                            .doOnNext(request ->
                                                                                                    log.info("Startet gjenopprett bestilling {} for ident: {}",
                                                                                                            request.getId(), testident.getIdent()))
                                                                                            .flatMap(bestillingRequest -> Flux.concat(
                                                                                                    gjenopprettKlienter(dollyPerson, bestillingRequest,
                                                                                                            fase2Klienter(),
                                                                                                            progress, false),
                                                                                                    gjenopprettKlienter(dollyPerson, bestillingRequest,
                                                                                                            fase3Klienter(),
                                                                                                            progress, false))))))))
                                            .onErrorResume(throwable -> {
                                                var description = WebClientError.describe(throwable);
                                                var error = errorStatusDecoder.getErrorText(description.getStatus(), description.getMessage());
                                                log.error("Feil oppsto ved utføring av bestilling, progressId {} {}",
                                                        progress.getId(), error, throwable);
                                                saveFeil(progress, error);
                                                return Flux.just(progress);
                                            }))))
                    .takeWhile(test -> !bestillingService.isStoppet(bestilling.getId()))
                    .collectList()
                    .doFinally(done -> {
                        doFerdig(bestilling);
                        clearCache();
                    })
                    .subscribe();
        }
    }
}
