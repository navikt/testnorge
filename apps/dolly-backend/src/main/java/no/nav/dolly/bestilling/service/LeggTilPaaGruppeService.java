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
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.elastic.BestillingElasticRepository;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.metrics.CounterCustomRegistry;
import no.nav.dolly.service.BestillingProgressService;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.util.ThreadLocalContextLifter;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonUpdateRequestDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Operators;

import java.util.List;

import static java.util.Objects.nonNull;
import static no.nav.dolly.util.MdcUtil.MDC_KEY_BESTILLING;

@Slf4j
@Service
public class LeggTilPaaGruppeService extends DollyBestillingService {

    private final PersonServiceClient personServiceClient;
    private final BestillingProgressService bestillingProgressService;

    public LeggTilPaaGruppeService(
            IdentService identService,
            BestillingProgressService bestillingProgressService,
            BestillingService bestillingService,
            ObjectMapper objectMapper,
            MapperFacade mapperFacade,
            List<ClientRegister> clientRegisters,
            CounterCustomRegistry counterCustomRegistry,
            ErrorStatusDecoder errorStatusDecoder,
            PdlDataConsumer pdlDataConsumer,
            TransactionHelperService transactionHelperService,
            PersonServiceClient personServiceClient,
            BestillingElasticRepository bestillingElasticRepository) {
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
        this.bestillingProgressService = bestillingProgressService;
    }

    @Async
    public void executeAsync(Bestilling bestilling) {

        MDC.put(MDC_KEY_BESTILLING, bestilling.getId().toString());
        Hooks.onEachOperator(Operators.lift(new ThreadLocalContextLifter<>()));

        RsDollyBestillingRequest bestKriterier = getDollyBestillingRequest(bestilling);
        if (nonNull(bestKriterier)) {

            var testidenter = identService.getTestidenterByGruppe(bestilling.getGruppe().getId());
            Flux.fromIterable(testidenter)
                    .flatMap(testident -> Flux.just(OriginatorUtility.prepOriginator(bestKriterier, testident, mapperFacade))
                            .flatMap(originator -> opprettProgress(bestilling, originator.getMaster(), testident.getIdent())
                                    .flatMap(progress ->
                                            oppdaterPdlPerson(originator, progress, testident.getIdent())
                                                    .flatMap(pdlResponse -> sendOrdrePerson(progress, pdlResponse))
                                            .filter(StringUtils::isNotBlank)
                                            .flatMap(ident -> opprettDollyPerson(ident, progress, bestilling.getBruker())
                                                    .flatMap(dollyPerson -> (!dollyPerson.getIdent().equals(bestilling.getIdent()) ?
                                                            updateIdent(dollyPerson, progress) : Flux.just(ident))
                                                            .doOnNext(nyident -> counterCustomRegistry.invoke(bestKriterier))
                                                            .flatMap(nyIdent -> Flux.concat(
                                                                    gjenopprettKlienter(dollyPerson, bestKriterier,
                                                                            fase1Klienter(),
                                                                            progress, true),
                                                                    personServiceClient.syncPerson(dollyPerson, progress)
                                                                            .map(ClientFuture::get)
                                                                            .filter(BestillingProgress::isPdlSync)
                                                                            .flatMap(pdlSync ->
                                                                                    Flux.concat(
                                                                                            gjenopprettKlienter(dollyPerson, bestKriterier,
                                                                                                    fase2Klienter(),
                                                                                                    progress, true),
                                                                                            gjenopprettKlienter(dollyPerson, bestKriterier,
                                                                                                    fase3Klienter(),
                                                                                                    progress, true))))))
                                                    .onErrorResume(throwable -> {
                                                        var error = errorStatusDecoder.getErrorText(
                                                                WebClientError.describe(throwable).getStatus(), WebClientError.describe(throwable).getMessage());
                                                        log.error("Feil oppsto ved utføring av bestilling, progressId {} {}",
                                                                progress.getId(), error, throwable);
                                                        saveFeil(progress, error);
                                                        return Flux.just(progress);
                                                    })))))
                    .takeWhile(test -> !bestillingService.isStoppet(bestilling.getId()))
                    .collectList()
                    .doFinally(done -> {
                        doFerdig(bestilling);
                        saveBestillingToElasticServer(bestKriterier, bestilling);
                        clearCache();
                    })
                    .subscribe();
        }
    }

    private Flux<PdlResponse> oppdaterPdlPerson(OriginatorUtility.Originator originator, BestillingProgress progress, String ident) {

        if (nonNull(originator.getPdlBestilling()) && nonNull(originator.getPdlBestilling().getPerson())) {

            transactionHelperService.persister(progress, BestillingProgress::setPdlForvalterStatus,
                    "Info: Oppdatering av person startet ...");
            return pdlDataConsumer.oppdaterPdl(ident,
                            PersonUpdateRequestDTO.builder()
                                    .person(originator.getPdlBestilling().getPerson())
                                    .build())
                    .doOnNext(response -> log.info("Oppdatert person til PDL-forvalter med response {}", response));

        } else {
            return Flux.just(PdlResponse.builder()
                    .ident(ident)
                    .build());
        }
    }

    private Flux<String> updateIdent(DollyPerson dollyPerson, BestillingProgress progress) {

        transactionHelperService.persister(progress, BestillingProgress::setIdent, dollyPerson.getIdent());
        identService.swapIdent(progress.getBestilling().getIdent(), dollyPerson.getIdent());
        bestillingProgressService.swapIdent(progress.getBestilling().getIdent(), dollyPerson.getIdent());
        bestillingService.swapIdent(progress.getBestilling().getIdent(), dollyPerson.getIdent());

        return Flux.just(dollyPerson.getIdent());
    }
}
