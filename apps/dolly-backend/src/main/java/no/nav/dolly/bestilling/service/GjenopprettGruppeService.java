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
import no.nav.dolly.repository.IdentRepository.GruppeBestillingIdent;
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

import static java.util.Objects.nonNull;

@Slf4j
@Service
public class GjenopprettGruppeService extends DollyBestillingService {

    private final PersonServiceClient personServiceClient;

    public GjenopprettGruppeService(
            IdentService identService,
            BestillingService bestillingService,
            ObjectMapper objectMapper,
            MapperFacade mapperFacade,
            List<ClientRegister> clientRegisters,
            CounterCustomRegistry counterCustomRegistry,
            ErrorStatusDecoder errorStatusDecoder,
            PdlDataConsumer pdlDataConsumer,
            TransactionHelperService transactionHelperService,
            PersonServiceClient personServiceClient,
            BestillingElasticRepository bestillingElasticRepository
    ) {
        super(
                identService,
                bestillingService,
                objectMapper,
                mapperFacade,
                clientRegisters,
                counterCustomRegistry,
                pdlDataConsumer,
                errorStatusDecoder,
                transactionHelperService,
                bestillingElasticRepository
        );
        this.personServiceClient = personServiceClient;
    }

    @Async
    public void executeAsync(Bestilling bestilling) {

        log.info("Bestilling med id=#{} og type={} er startet ...", bestilling.getId(), getBestillingType(bestilling));

        var bestKriterier = getDollyBestillingRequest(bestilling);
        if (nonNull(bestKriterier)) {
            bestKriterier.setEkskluderEksternePersoner(true);

            var coBestillinger = identService.getBestillingerFromGruppe(bestilling.getGruppe()).stream()
                    .sorted(Comparator.comparing(GruppeBestillingIdent::getBestillingId))
                    .toList();

            Flux.fromIterable(bestilling.getGruppe().getTestidenter())
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
                                                            .flatMap(pdlSync -> Flux.fromIterable(coBestillinger)
                                                                    .concatMap(bestilling1 -> Flux.just(bestilling1)
                                                                            .filter(cobestilling -> ident.equals(cobestilling.getIdent()))
                                                                            .flatMapSequential(cobestilling -> createBestilling(bestilling, cobestilling)
                                                                                    .filter(bestillingRequest -> bestillingRequest.getId() ==
                                                                                            isFirstBestilling(coBestillinger, cobestilling.getIdent()) ||
                                                                                            bestillingRequest.isNonEmpty())
                                                                                    .doOnNext(request -> log.info("Startet gjenopprett bestilling {} for ident: {}",
                                                                                            request.getId(), testident.getIdent()))
                                                                                    .flatMapSequential(bestillingRequest -> Flux.concat(
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

    private long isFirstBestilling(List<GruppeBestillingIdent> coBestillinger, String ident) {

        return coBestillinger.stream()
                .filter(bestilling -> ident.equals(bestilling.getIdent()))
                .min(Comparator.comparing(GruppeBestillingIdent::getBestillingId))
                .map(GruppeBestillingIdent::getBestillingId)
                .orElse(0L);
    }
}
