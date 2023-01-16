package no.nav.dolly.bestilling.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.pdldata.dto.PdlResponse;
import no.nav.dolly.bestilling.personservice.PersonServiceClient;
import no.nav.dolly.consumer.pdlperson.PdlPersonConsumer;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.metrics.CounterCustomRegistry;
import no.nav.dolly.service.BestillingProgressService;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.DollyPersonCache;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.util.ThreadLocalContextLifter;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.dolly.util.WebClientFilter;
import org.slf4j.MDC;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Operators;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.jpa.Testident.Master.PDLF;
import static no.nav.dolly.util.MdcUtil.MDC_KEY_BESTILLING;

@Slf4j
@Service
public class OpprettPersonerByKriterierService extends DollyBestillingService {

    private final PersonServiceClient personServiceClient;

    public OpprettPersonerByKriterierService(DollyPersonCache dollyPersonCache, IdentService identService,
                                             BestillingProgressService bestillingProgressService,
                                             BestillingService bestillingService, MapperFacade mapperFacade,
                                             CacheManager cacheManager, ObjectMapper objectMapper,
                                             List<ClientRegister> clientRegisters, CounterCustomRegistry counterCustomRegistry,
                                             ErrorStatusDecoder errorStatusDecoder,
                                             PdlPersonConsumer pdlPersonConsumer, PdlDataConsumer pdlDataConsumer,
                                             TransactionHelperService transactionHelperService,
                                             PersonServiceClient personServiceClient) {
        super(dollyPersonCache, identService, bestillingProgressService,
                bestillingService, mapperFacade, cacheManager, objectMapper, clientRegisters, counterCustomRegistry,
                pdlPersonConsumer, pdlDataConsumer, errorStatusDecoder, transactionHelperService);

        this.personServiceClient = personServiceClient;
    }

    @Async
    public void executeAsync(Bestilling bestilling) {

        log.info("Bestilling med id=#{} og type={} er startet ...", bestilling.getId(), getBestillingType(bestilling));
        MDC.put(MDC_KEY_BESTILLING, bestilling.getId().toString());
        Hooks.onEachOperator(Operators.lift(new ThreadLocalContextLifter<>()));

        var bestKriterier = getDollyBestillingRequest(bestilling);

        if (nonNull(bestKriterier)) {

            var originator = new OriginatorCommand(bestKriterier, null, mapperFacade).call();

            Flux.range(0, bestilling.getAntallIdenter())
                    .filter(index -> !bestillingService.isStoppet(bestilling.getId()))
                    .flatMap(index -> opprettProgress(bestilling, originator)
                            .flatMap(progress -> opprettPerson(originator)
                                    .flatMap(pdlResponse -> sendOrdrePerson(progress, pdlResponse))
                                    .filter(Objects::nonNull)
                                    .flatMap(ident -> leggIdentTilGruppe(ident,
                                            progress.getBestilling().getGruppe(), bestKriterier.getBeskrivelse())
                                            .flatMap(dollyPerson -> Flux.concat(
                                                    personServiceClient.gjenopprett(null,
                                                    dollyPerson, null, true),
                                                    gjenopprettAlleKlienter(dollyPerson, bestKriterier,
                                                    progress, true))))))
                    .collectList()
                    .doOnError(throwable -> {
                        log.error("Feil oppsto ved utføring av bestilling #{}: {}",
                                bestilling.getId(), WebClientFilter.getMessage(throwable));
                        bestilling.setFeil(errorStatusDecoder.getErrorText(
                                WebClientFilter.getStatus(throwable), WebClientFilter.getMessage(throwable)));
                        doFerdig(bestilling);
                    })
                    .subscribe(done -> doFerdig(bestilling));

        } else {
            bestilling.setFeil("Feil: kunne ikke mappe JSON request, se logg!");
            doFerdig(bestilling);

        }
    }

    private Flux<DollyPerson> leggIdentTilGruppe(String ident, Testgruppe gruppe, String beskrivelse) {

        identService.saveIdentTilGruppe(ident, gruppe, PDLF, beskrivelse);
        log.info("Ident {} lagt til gruppe {}", ident, gruppe.getId());

        return Flux.just(DollyPerson.builder()
                .hovedperson(ident)
                .master(PDLF)
                .tags(gruppe.getTags())
                .build());
    }

    private void doFerdig(Bestilling bestilling) {

        transactionHelperService.oppdaterBestillingFerdig(bestilling);
        MDC.remove(MDC_KEY_BESTILLING);
        log.info("Bestilling med id=#{} er ferdig", bestilling.getId());
    }

    private Flux<BestillingProgress> opprettProgress(Bestilling bestilling, OriginatorCommand.Originator originator) {

        return Flux.just(transactionHelperService.oppdaterProgress(BestillingProgress.builder()
                .bestilling(bestilling)
                .master(originator.getMaster())
                .build()));
    }

    private Flux<PdlResponse> opprettPerson(OriginatorCommand.Originator originator) {

        return pdlDataConsumer.opprettPdl(originator.getPdlBestilling())
                .map(response -> {

                    log.info("Opprettet person med ident {} ", response);
                    return response;
                });
    }

    private Flux<String> sendOrdrePerson(BestillingProgress progress, PdlResponse response) {

        if (response.getStatus().is2xxSuccessful()) {

            progress.setIdent(response.getIdent());
            return pdlDataConsumer.sendOrdre(response.getIdent(), true)
                    .map(resultat -> {
                        progress.setPdlDataStatus(resultat.getStatus().is2xxSuccessful() ?
                                resultat.getJsonNode() :
                                errorStatusDecoder.getErrorText(resultat.getStatus(), resultat.getFeilmelding()));
                        transactionHelperService.persister(progress);
                        log.info("Sendt ordre til PDL for ident {} ", response.getIdent());
                        return response.getIdent();
                    });

        } else {
            progress.setPdlDataStatus(errorStatusDecoder.getErrorText(response.getStatus(), response.getFeilmelding()));
            transactionHelperService.persister(progress);
            return Flux.empty();
        }
    }
}
