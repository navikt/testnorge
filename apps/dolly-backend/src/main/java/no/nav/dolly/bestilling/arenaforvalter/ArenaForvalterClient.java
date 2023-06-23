package no.nav.dolly.bestilling.arenaforvalter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.arenaforvalter.dto.Aap115Request;
import no.nav.dolly.bestilling.arenaforvalter.dto.AapRequest;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaArbeidssokerBruker;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaBruker;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaDagpenger;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyBruker;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukere;
import no.nav.dolly.domain.resultset.arenaforvalter.Arenadata;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.util.TransactionHelperService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
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
    private static final String IDENT = "ident";
    private static final String MILJOE = "miljoe";

    private static final String FEIL = "Feil: ";
    private static final String BRUKER = "bruker:";
    private static final String AAP115 = "aap115:";
    private static final String AAP = "aap:";
    private static final String DAGPENGER = "dagpenger:";

    private final ArenaForvalterConsumer arenaForvalterConsumer;
    private final MapperFacade mapperFacade;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final TransactionHelperService transactionHelperService;

    @Override
    public Flux<ClientFuture> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        return Flux.just(bestilling)
                .filter(best -> nonNull(best.getArenaforvalter()))
                .map(RsDollyUtvidetBestilling::getArenaforvalter)
                .flatMap(ordre -> arenaForvalterConsumer.getEnvironments()
                                .filter(env -> bestilling.getEnvironments().contains(env))
                                .collectList()
                                .doOnNext(miljoer -> {
                                    var initStatus = miljoer.stream()
                                            .map(miljo -> String.format(STATUS_FMT, miljo, getInfoVenter(SYSTEM)))
                                            .collect(Collectors.joining(","));
                                    transactionHelperService.persister(progress, BestillingProgress::setArenaforvalterStatus, initStatus);
                                })
                                .flatMap(miljoer -> doArenaOpprett(ordre, dollyPerson.getIdent(), miljoer)
                                        .map(this::parseStatus)
                                        .map(status -> futurePersist(progress, status))));
    }

    private Mono<List<String>> doArenaOpprett(Arenadata arenadata, String ident, List<String> miljoer) {

        return Flux.fromIterable(miljoer)
                .flatMap(miljoe -> arenaForvalterConsumer.getBruker(ident, miljoe)
                        .flatMap(arenaArbeidsokerStatus -> Flux.concat(
                                sendArenaBruker(arenadata, arenaArbeidsokerStatus, ident, miljoe)
                                        .map(brukerStatus -> BRUKER + brukerStatus),

                                sendAap115(arenadata, ident, miljoe)
                                        .map(aap115tstaus -> AAP115 + aap115tstaus),

                                sendAap(arenadata, ident, miljoe)
                                        .map(aapStataus -> AAP + aapStataus),

                                sendArenadagpenger(arenadata, ident, miljoe)
                                        .map(dagpengerStatus -> DAGPENGER + dagpengerStatus)
                        )))
                .collectList();
    }

    private String parseStatus(List<String> status) {

        return status.stream()
                .filter(entry -> !entry.contains("OK"))
                .map(entry -> FEIL + entry)
                .findFirst()
                .orElse("OK");
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

    private Flux<String> sendArenaBruker(Arenadata arenadata, ArenaArbeidssokerBruker arbeidssoker,
                                         String ident, String miljoe) {

        return Flux.just(arenadata)
                .map(arenadata1 -> {
                    var arenaNyeBrukere = ArenaNyeBrukere.builder()
                            .nyeBrukere(List.of(mapperFacade.map(arenadata, ArenaNyBruker.class)))
                            .build();
                    arenaNyeBrukere.getNyeBrukere().get(0).setPersonident(ident);
                    arenaNyeBrukere.getNyeBrukere().get(0).setMiljoe(miljoe);
                    oppdaterAktiveringsdato(arenaNyeBrukere, arbeidssoker);
                    return arenaNyeBrukere;
                })
                .flatMap(arenaNyeBrukere -> (!arbeidssoker.getArbeidsokerList().isEmpty() ?
                        arenaForvalterConsumer.inaktiverBruker(ident, miljoe) :
                        Mono.just("Ingen sletting"))
                        .map(response -> arenaNyeBrukere))
                .flatMap(arenaNyeBrukere ->
                        arenaForvalterConsumer.postArenaBruker(arenaNyeBrukere)
                                .map(respons -> {
                                    if (!respons.getStatus().is2xxSuccessful()) {
                                        return String.format(STATUS_FMT, miljoe,
                                                errorStatusDecoder.getErrorText(respons.getStatus(), respons.getFeilmelding()));
                                    } else if (!respons.getNyBrukerFeilList().isEmpty()) {
                                        return respons.getNyBrukerFeilList().stream()
                                                .map(brukerfeil -> String.format(STATUS_FMT, brukerfeil.getMiljoe(),
                                                        brukerfeil.getNyBrukerFeilstatus() + ": " + encodeStatus(brukerfeil.getMelding())))
                                                .collect(Collectors.joining(","));
                                    } else {
                                        return respons.getArbeidsokerList().stream()
                                                .map(bruker -> String.format(STATUS_FMT, bruker.getMiljoe(), encodeStatus(bruker.getStatus())))
                                                .collect(Collectors.joining(","));
                                    }
                                }));
    }

    private static void oppdaterAktiveringsdato(ArenaNyeBrukere arenaNyeBrukere, ArenaArbeidssokerBruker arbeidssoker) {

        arenaNyeBrukere.getNyeBrukere()
                .forEach(bruker -> {

                    if (nonNull(bruker.getAktiveringsDato()) && !arbeidssoker.getArbeidsokerList().isEmpty()) {
                        bruker.setAktiveringsDato(
                                arbeidssoker.getArbeidsokerList().stream()
                                        .map(ArenaBruker::getAktiveringsDato)
                                        .filter(Objects::nonNull)
                                        .findFirst()
                                        .orElse(bruker.getAktiveringsDato()));
                    }
                });
    }

    private Flux<String> sendAap115(Arenadata arenadata, String ident, String miljoe) {

        return Flux.just(arenadata)
                .filter(arenadata1 -> !arenadata1.getAap115().isEmpty())
                .map(arenadata1 -> {
                    var context = new MappingContext.Factory().getContext();
                    context.setProperty(IDENT, ident);
                    context.setProperty(MILJOE, miljoe);
                    return mapperFacade.map(arenadata1, Aap115Request.class, context);
                })
                .flatMap(arenaForvalterConsumer::postAap115)
                .map(response -> response.getStatus().is2xxSuccessful() ? "OK" :
                        errorStatusDecoder.getErrorText(response.getStatus(), response.getFeilmelding()));
    }

    private Flux<String> sendAap(Arenadata arenadata, String ident, String miljoe) {

        return Flux.just(arenadata)
                .filter(arenadata1 -> !arenadata1.getAap().isEmpty())
                .map(arenadata1 -> {
                    var context = new MappingContext.Factory().getContext();
                    context.setProperty(IDENT, ident);
                    context.setProperty(MILJOE, miljoe);
                    return mapperFacade.map(arenadata1, AapRequest.class, context);
                })
                .flatMap(arenaForvalterConsumer::postAap)
                .map(response -> response.getStatus().is2xxSuccessful() ? "OK" :
                        errorStatusDecoder.getErrorText(response.getStatus(), response.getFeilmelding()));
    }

    private Flux<String> sendArenadagpenger(Arenadata arenadata, String ident, String miljoe) {

        return Flux.just(arenadata)
                .filter(arenadata1 -> !arenadata1.getDagpenger().isEmpty())
                .map(arenadata1 -> {
                    var context = new MappingContext.Factory().getContext();
                    context.setProperty(IDENT, ident);
                    context.setProperty(MILJOE, miljoe);
                    return mapperFacade.map(arenadata1, ArenaDagpenger.class, context);
                })
                .flatMap(arenaForvalterConsumer::postArenaDagpenger)
                .map(response -> {
                    if (!response.getStatus().is2xxSuccessful()) {
                        return String.format(STATUS_FMT, miljoe,
                                errorStatusDecoder.getErrorText(response.getStatus(), response.getFeilmelding()));
                    } else if (!response.getNyeDagpFeilList().isEmpty()) {
                        return response.getNyeDagpFeilList().stream()
                                .map(dagpFeil -> String.format(STATUS_FMT, miljoe,
                                        dagpFeil.getNyDagpFeilstatus() + ": " + encodeStatus(dagpFeil.getMelding())))
                                .collect(Collectors.joining(","));
                    } else {
                        return response.getNyeDagp().stream()
                                .map(dagpResponse -> String.format(STATUS_FMT, miljoe, "JA".equals(dagpResponse.getNyeDagpResponse().getUtfall()) ?
                                        "OK" :
                                        ("Feil: " + dagpResponse.getNyeDagpResponse().getUtfall() + ": " +
                                                encodeStatus(dagpResponse.getNyeDagpResponse().getBegrunnelse()))))
                                .collect(Collectors.joining(","));
                    }
                });
    }
}
