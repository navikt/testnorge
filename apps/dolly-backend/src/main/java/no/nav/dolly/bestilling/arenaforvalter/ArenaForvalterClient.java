package no.nav.dolly.bestilling.arenaforvalter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaDagpenger;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyBruker;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukere;
import no.nav.dolly.domain.resultset.arenaforvalter.Arenadata;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.encodeStatus;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getInfoVenter;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArenaForvalterClient implements ClientRegister {

    private static final String STATUS_FMT = "%s$%s";
    private static final String SYSTEM = "Arena";

    private final ArenaForvalterConsumer arenaForvalterConsumer;
    private final MapperFacade mapperFacade;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final TransactionHelperService transactionHelperService;

    @Override
    public Flux<ClientFuture> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        return Flux.just(bestilling)
                .filter(best -> nonNull(best.getArenaforvalter()))
                .map(RsDollyUtvidetBestilling::getArenaforvalter)
                .flatMap(ordre -> arenaForvalterConsumer.getToken()
                        .flatMapMany(token -> arenaForvalterConsumer.getEnvironments(token)
                                .filter(env -> bestilling.getEnvironments().contains(env))
                                .collectList()
                                .doOnNext(miljoer -> {
                                    var initStatus = miljoer.stream()
                                            .map(miljo -> String.format(STATUS_FMT, miljo, getInfoVenter(SYSTEM)))
                                            .collect(Collectors.joining(","));
                                    transactionHelperService.persister(progress, BestillingProgress::setArenaforvalterStatus, initStatus);
                                })
                                .flatMap(miljoer -> doArenaOpprett(ordre, dollyPerson.getIdent(), miljoer, token)
                                        .map(status -> futurePersist(progress, status)))));
    }

    private Mono<String> doArenaOpprett(Arenadata arenadata, String ident, List<String> miljoer, AccessToken token) {

        return Flux.fromIterable(miljoer)
                .flatMap(miljoe -> arenaForvalterConsumer.getBruker(ident, miljoe, token)
                        .flatMap(response -> response.getArbeidsokerList().isEmpty() ?
                                sendArenaBruker(arenadata, ident, miljoe, token) :
                                Flux.just(String.format(STATUS_FMT, miljoe, "OK")))
                        .flatMap(brukerStatus -> brukerStatus.contains("OK") &&
                                !arenadata.getDagpenger().isEmpty() ?
                                sendArenadagpenger(arenadata, ident, miljoe, token) :
                                Flux.just(brukerStatus)))
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(","));
    }

    private ClientFuture futurePersist(BestillingProgress progress, String status) {

        return () -> {
            transactionHelperService.persister(progress, BestillingProgress::setArenaforvalterStatus, status);
            return progress;
        };
    }

    @Override
    public void release(List<String> identer) {

        arenaForvalterConsumer.deleteIdenter(identer)
                .collectList()
                .subscribe(response -> log.info("Sletting utført mot Arena-forvalteren"));
    }

    private Flux<String> sendArenaBruker(Arenadata arenadata, String ident, String miljoe, AccessToken token) {

        return Flux.just(arenadata)
                .map(arenadata1 -> {
                    var arenaNyeBrukere = ArenaNyeBrukere.builder()
                            .nyeBrukere(List.of(mapperFacade.map(arenadata, ArenaNyBruker.class)))
                            .build();
                    arenaNyeBrukere.getNyeBrukere().get(0).setPersonident(ident);
                    arenaNyeBrukere.getNyeBrukere().get(0).setMiljoe(miljoe);
                    return arenaNyeBrukere;
                })
                .flatMap(arenaNyeBrukere -> arenaForvalterConsumer.postArenaBruker(arenaNyeBrukere, token)
                        .map(respons -> {
                            if (!respons.getStatus().is2xxSuccessful()) {
                                return String.format(STATUS_FMT, miljoe,
                                        errorStatusDecoder.getErrorText(respons.getStatus(), respons.getFeilmelding()));
                            } else if (respons.getNyBrukerFeilList().isEmpty()) {
                                return respons.getArbeidsokerList().stream()
                                        .map(bruker -> String.format(STATUS_FMT, bruker.getMiljoe(), encodeStatus(bruker.getStatus())))
                                        .collect(Collectors.joining(","));
                            } else {
                                return respons.getNyBrukerFeilList().stream()
                                        .map(brukerfeil -> String.format(STATUS_FMT, brukerfeil.getMiljoe(),
                                                "Feil: " + brukerfeil.getNyBrukerFeilstatus() + ": " + encodeStatus(brukerfeil.getMelding())))
                                        .collect(Collectors.joining(","));
                            }
                        }));
    }

    private Flux<String> sendArenadagpenger(Arenadata arenadata, String ident, String miljoe, AccessToken token) {

        return Flux.just(arenadata)
                .map(arenadata1 -> {
                    var context = new MappingContext.Factory().getContext();
                    context.setProperty("ident", ident);
                    context.setProperty("miljoe", miljoe);
                    return mapperFacade.map(arenadata1, ArenaDagpenger.class);
                })
                .flatMap(dagpenger -> arenaForvalterConsumer.postArenaDagpenger(dagpenger, token))
                .map(response -> {
                    if (!response.getStatus().is2xxSuccessful()) {
                        return String.format(STATUS_FMT, miljoe,
                                errorStatusDecoder.getErrorText(response.getStatus(), response.getFeilmelding()));
                    } else if (response.getNyeDagpFeilList().isEmpty()) {
                        return response.getNyeDagpResponse().stream()
                                .map(dagpResponse -> String.format(STATUS_FMT, miljoe, dagpResponse.getUtfall() +
                                        ": " + dagpResponse.getBegrunnelse()))
                                .collect(Collectors.joining(","));
                    } else {
                        return response.getNyeDagpFeilList().stream()
                                .map(dagpFeil -> String.format(STATUS_FMT, miljoe,
                                        "Feil: " + dagpFeil.getNyDagpFeilstatus() + ": " + dagpFeil.getMelding()))
                                .collect(Collectors.joining(","));
                    }
                });
    }
}
