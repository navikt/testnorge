package no.nav.dolly.bestilling.arenaforvalter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.arenaforvalter.dto.Aap;
import no.nav.dolly.bestilling.arenaforvalter.dto.Aap115;
import no.nav.dolly.bestilling.arenaforvalter.dto.Aap115Request;
import no.nav.dolly.bestilling.arenaforvalter.dto.Aap115Response;
import no.nav.dolly.bestilling.arenaforvalter.dto.AapRequest;
import no.nav.dolly.bestilling.arenaforvalter.dto.AapResponse;
import no.nav.dolly.bestilling.arenaforvalter.dto.ArenaInnsatsbehov;
import no.nav.dolly.bestilling.arenaforvalter.dto.ArenaInnsatsbehovResponse;
import no.nav.dolly.bestilling.arenaforvalter.dto.ArenaStatusResponse;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaBruker;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaDagpenger;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyBruker;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukere;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukereResponse;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeDagpengerResponse;
import no.nav.dolly.domain.resultset.arenaforvalter.Arenadata;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.util.TransactionHelperService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.encodeStatus;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getInfoVenter;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArenaForvalterClient implements ClientRegister {

    private static final String MILJOE_FMT = "%s$%s";
    private static final String MILJOE2_FMT = "%s$%s %s";
    private static final String STATUS_FMT = "%s:%s";
    private static final String SYSTEM = "Arena";
    private static final String IDENT = "ident";
    private static final String MILJOE = "miljoe";
    private static final String BRUKER = "BRUKER";
    private static final String AAP115 = "AAP115";
    private static final String AAP = "AAP";
    private static final String DAGPENGER = "DAGP";
    private static final String STANSET = "Stanset forrige vedtak: ";
    private static final String OPPRETTET = "Oppretting: ";
    private static final String AVSLAG = "Avslag: ";
    private static final String INAKTIVERT = "Inaktivert: ";

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
                                    .map(miljo -> String.format(MILJOE_FMT, miljo, getInfoVenter(SYSTEM)))
                                    .collect(Collectors.joining(","));
                            transactionHelperService.persister(progress, BestillingProgress::setArenaforvalterStatus, initStatus);
                        })
                        .flatMap(miljoer -> doArenaOpprett(ordre, dollyPerson.getIdent(), miljoer)
                                .map(status -> futurePersist(progress, status))));
    }

    private Mono<String> doArenaOpprett(Arenadata arenadata, String ident, List<String> miljoer) {

        return Flux.fromIterable(miljoer)
                .flatMap(miljoe -> arenaForvalterConsumer.getArenaBruker(ident, miljoe)
                        .flatMapMany(arenaArbeidsokerStatus -> Flux.concat(

                                sendArenaBruker(arenadata, arenaArbeidsokerStatus, ident, miljoe)
                                        .map(brukerStatus -> fmtResponse(miljoe, BRUKER, brukerStatus)),

                                sendAap115(arenadata, arenaArbeidsokerStatus, ident, miljoe)
                                        .map(aap115tstaus -> fmtResponse(miljoe, AAP115, aap115tstaus)),

                                sendAap(arenadata, arenaArbeidsokerStatus, ident, miljoe)
                                        .map(aapStataus -> fmtResponse(miljoe, AAP, aapStataus)),

                                sendArenadagpenger(arenadata, arenaArbeidsokerStatus, ident, miljoe)
                                        .map(dagpengerStatus -> fmtResponse(miljoe, DAGPENGER, dagpengerStatus))
                        )))
                .collect(Collectors.joining(","));
    }

    private static String fmtResponse(String miljoe, String system, String status) {

        return encodeStatus(String.format(MILJOE2_FMT, miljoe, system, status));
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

    private Flux<String> sendArenaBruker(Arenadata arenadata, ArenaStatusResponse arbeidssoker,
                                         String ident, String miljoe) {

        return Flux.just(arenadata)
                .map(arenadata1 -> {
                    var arenaNyeBrukere = ArenaNyeBrukere.builder()
                            .nyeBrukere(List.of(mapperFacade.map(arenadata, ArenaNyBruker.class)))
                            .build();
                    arenaNyeBrukere.getNyeBrukere()
                            .forEach(bruker -> {
                                bruker.setPersonident(ident);
                                bruker.setMiljoe(miljoe);
                                bruker.setAktiveringsDato(oppdaterAktiveringsdato(bruker, arbeidssoker));
                            });
                    return arenaNyeBrukere;
                })
                .flatMap(arenaNyeBrukere -> {

                    if (arenaNyeBrukere.getNyeBrukere().stream()
                            .anyMatch(ArenaNyBruker::hasKvalifiseringsgruppe)) {

                        if (isNull(arbeidssoker.getRegistrertDato())) {
                            return arenaForvalterConsumer.postArenaBruker(arenaNyeBrukere)
                                    .flatMap(this::getBrukerStatus)
                                    .map(response -> OPPRETTET + response);

                        } else {
                            return arenaForvalterConsumer.postInnsatsbehov(buildInnsatsbehov(ident, miljoe, arenaNyeBrukere.getNyeBrukere()))
                                    .flatMap(this::getInnsatsStatus)
                                    .map(response -> OPPRETTET + response);
                        }

                    } else if (nonNull(arenadata.getInaktiveringDato())) {

                        return Flux.from(arenaForvalterConsumer.inaktiverBruker(ident, miljoe)
                                        .map(respons -> respons.getStatus().is2xxSuccessful() ?
                                                "OK" : errorStatusDecoder.getErrorText(respons.getStatus(), respons.getFeilmelding())))
                                .map(response -> INAKTIVERT + response);
                    } else {

                        return Flux.empty();
                    }
                });
    }

    private static ArenaInnsatsbehov buildInnsatsbehov(String ident, String miljoe, List<ArenaNyBruker> arenaNyBruker) {

        return ArenaInnsatsbehov.builder()
                .personident(ident)
                .miljoe(miljoe)
                .nyeEndreInnsatsbehov(List.of(ArenaInnsatsbehov.EndreInnsatsbehov.builder()
                        .kvalifiseringsgruppe(arenaNyBruker.get(0).getKvalifiseringsgruppe())
                        .build()))
                .build();
    }

    private static LocalDate oppdaterAktiveringsdato(ArenaNyBruker bruker, ArenaStatusResponse arbeidssoker) {

        return Stream.of(bruker.getAktiveringsDato(), arbeidssoker.getRegistrertDato())
                .filter(Objects::nonNull)
                .max(LocalDate::compareTo)
                .orElse(LocalDate.now());
    }

    private Flux<String> sendAap115(Arenadata arenadata, ArenaStatusResponse status, String ident, String miljoe) {

        return Flux.just(arenadata)
                .filter(arenadata1 -> !arenadata1.getAap115().isEmpty())
                .filter(arenadata1 -> isNull(arenadata1.getInaktiveringDato()))
                .map(arenadata1 -> {
                    var context = new MappingContext.Factory().getContext();
                    context.setProperty(IDENT, ident);
                    context.setProperty(MILJOE, miljoe);
                    return mapperFacade.map(arenadata1, Aap115Request.class, context);
                })
                .flatMap(request -> Flux.fromIterable(arenadata.getAap115())
                        .flatMap(aap115 -> Flux.concat(Flux.fromIterable(status.getVedtakListe())
                                        .filter(vedtak -> "AA115".equals(vedtak.getRettighet().getNavn()))
                                        .flatMap(vedtak -> {
                                            var opphoerRequest = mapperFacade.map(request, Aap115Request.class);
                                            opphoerRequest.getNyeAap115()
                                                    .forEach(opphoer -> {
                                                        opphoer.setVedtaktype(Aap115.VedtaksType.S);
                                                        opphoer.setTilDato(toDate(aap115.getFraDato()));
                                                    });
                                            return arenaForvalterConsumer.postAap115(opphoerRequest)
                                                    .flatMap(this::getAap115Status)
                                                    .map(response -> STANSET + response);
                                        }),
                                arenaForvalterConsumer.postAap115(request)
                                        .flatMap(this::getAap115Status)
                                        .map(response -> OPPRETTET + response)
                        )));
    }

    private Flux<String> sendAap(Arenadata arenadata, ArenaStatusResponse status, String ident, String miljoe) {

        return Flux.just(arenadata)
                .filter(arenadata1 -> !arenadata1.getAap().isEmpty())
                .filter(arenadata1 -> isNull(arenadata1.getInaktiveringDato()))
                .map(arenadata1 -> {
                    var context = new MappingContext.Factory().getContext();
                    context.setProperty(IDENT, ident);
                    context.setProperty(MILJOE, miljoe);
                    return mapperFacade.map(arenadata1, AapRequest.class, context);
                })
                .flatMap(request -> Flux.fromIterable(arenadata.getAap())
                        .flatMap(aap -> Flux.concat(Flux.fromIterable(status.getVedtakListe())
                                        .filter(vedtak -> "AAP".equals(vedtak.getRettighet().getNavn()))
                                        .flatMap(vedtak -> {
                                            var opphoerRequest = mapperFacade.map(request, AapRequest.class);
                                            opphoerRequest.getNyeAap()
                                                    .forEach(opphoer -> {
                                                        opphoer.setVedtaktype(Aap.VedtakType.S);
                                                        opphoer.setTilDato(toDate(aap.getFraDato()));
                                                    });
                                            return arenaForvalterConsumer.postAap(opphoerRequest)
                                                    .flatMap(this::getAapStatus)
                                                    .map(response -> STANSET + response);
                                        }),
                                arenaForvalterConsumer.postAap(request)
                                        .flatMap(this::getAapStatus)
                                        .map(response -> OPPRETTET + response)
                        )));
    }

    private Flux<String> sendArenadagpenger(Arenadata arenadata, ArenaStatusResponse status, String ident, String miljoe) {

        return Flux.just(arenadata)
                .filter(arenadata1 -> !arenadata1.getDagpenger().isEmpty())
                .filter(arenadata1 -> isNull(arenadata1.getInaktiveringDato()))
                .map(arenadata1 -> {
                    var context = new MappingContext.Factory().getContext();
                    context.setProperty(IDENT, ident);
                    context.setProperty(MILJOE, miljoe);
                    return mapperFacade.map(arenadata1, ArenaDagpenger.class, context);
                })
                .flatMap(request -> Flux.fromIterable(arenadata.getDagpenger())
                        .flatMap(dagp -> Flux.concat(Flux.fromIterable(status.getVedtakListe())
                                        .filter(vedtak -> "DAGP".equals(vedtak.getRettighet().getNavn()))
                                        .flatMap(vedtak -> {
                                            var opphoerRequest = mapperFacade.map(request, ArenaDagpenger.class);
                                            opphoerRequest.getNyeDagp()
                                                    .forEach(opphoer -> {
                                                        opphoer.setVedtaktype(ArenaDagpenger.VedtaksType.S);
                                                        opphoer.setStansFomDato(toDate(dagp.getFraDato()));
                                                    });
                                            return arenaForvalterConsumer.postArenaDagpenger(opphoerRequest)
                                                    .flatMap(this::getDagpengerStatus)
                                                    .map(response -> STANSET + response);

                                        }),
                                arenaForvalterConsumer.postArenaDagpenger(request)
                                        .flatMap(this::getDagpengerStatus)
                                        .map(response -> OPPRETTET + response)
                        )));
    }

    private Mono<String> getBrukerStatus(ArenaNyeBrukereResponse response) {

        return Flux.concat(Flux.just(response.getStatus())
                                .filter(status -> !status.is2xxSuccessful())
                                .map(status -> errorStatusDecoder.getErrorText(response.getStatus(), response.getFeilmelding())),
                        Flux.fromIterable(response.getArbeidsokerList())
                                .map(nyBruker -> nyBruker.getStatus() == ArenaBruker.BrukerStatus.OK ?
                                        "OK" :
                                        encodeStatus(AVSLAG + nyBruker.getStatus()))
                                .collect(Collectors.joining()),
                        Flux.fromIterable(response.getNyBrukerFeilList())
                                .map(brukerFeil ->
                                        encodeStatus(String.format(STATUS_FMT, brukerFeil.getNyBrukerFeilstatus(), brukerFeil.getMelding())))
                                .collect(Collectors.joining()))

                .collect(Collectors.joining());
    }

    private Mono<String> getInnsatsStatus(ArenaInnsatsbehovResponse response) {

        if (response.getStatus().is2xxSuccessful() && response.getNyeEndreInnsatsbehovFeilList().isEmpty()) {

            return Mono.just("OK");
        } else if (!response.getStatus().is2xxSuccessful()) {

            return Mono.just(errorStatusDecoder.getErrorText(response.getStatus(), response.getFeilmelding()));
        } else {

            return Flux.fromIterable(response.getNyeEndreInnsatsbehovFeilList())
                    .map(feil ->
                            encodeStatus(String.format(STATUS_FMT, feil.getNyEndreInnsatsbehovFeilstatus(), feil.getMelding())))
                    .collect(Collectors.joining());
        }
    }

    private Mono<String> getAapStatus(AapResponse response) {

        return Flux.concat(Flux.just(response.getStatus())
                                .filter(status -> !status.is2xxSuccessful())
                                .map(status -> errorStatusDecoder.getErrorText(response.getStatus(), response.getFeilmelding())),
                        Flux.fromIterable(response.getNyeAap())
                                .map(nyAap -> "JA".equals(nyAap.getUtfall()) ?
                                        "OK" :
                                        encodeStatus(AVSLAG + nyAap.getBegrunnelse()))
                                .collect(Collectors.joining()),
                        Flux.fromIterable(response.getNyeAapFeilList())
                                .map(aapFeil ->
                                        encodeStatus(String.format(STATUS_FMT, aapFeil.getNyAapFeilstatus(), aapFeil.getMelding())))
                                .collect(Collectors.joining()))

                .collect(Collectors.joining());
    }

    private Mono<String> getAap115Status(Aap115Response response) {

        return Flux.concat(Flux.just(response.getStatus())
                                .filter(status -> !status.is2xxSuccessful())
                                .map(status -> errorStatusDecoder.getErrorText(response.getStatus(), response.getFeilmelding())),
                        Flux.fromIterable(response.getNyeAap115())
                                .map(nyAap115 -> "JA".equals(nyAap115.getUtfall()) ?
                                        "OK" :
                                        encodeStatus(AVSLAG + nyAap115.getBegrunnelse()))
                                .collect(Collectors.joining()),
                        Flux.fromIterable(response.getNyeAapFeilList())
                                .map(aap115Feil ->
                                        encodeStatus(String.format(STATUS_FMT, aap115Feil.getNyAapFeilstatus(), aap115Feil.getMelding())))
                                .collect(Collectors.joining()))

                .collect(Collectors.joining());
    }

    private Mono<String> getDagpengerStatus(ArenaNyeDagpengerResponse response) {

        return Flux.concat(Flux.just(response.getStatus())
                                .filter(status -> !status.is2xxSuccessful())
                                .map(status -> errorStatusDecoder.getErrorText(response.getStatus(), response.getFeilmelding())),
                        Flux.fromIterable(response.getNyeDagp())
                                .map(nyDagP -> "JA".equals(nyDagP.getNyeDagpResponse().getUtfall()) ?
                                        "OK" :
                                        encodeStatus(AVSLAG + nyDagP.getNyeDagpResponse().getBegrunnelse()))
                                .collect(Collectors.joining()),
                        Flux.fromIterable(response.getNyeDagpResponse())
                                .map(nyDagP -> "JA".equals(nyDagP.getUtfall()) ?
                                        "OK" :
                                        encodeStatus(AVSLAG + nyDagP.getBegrunnelse()))
                                .collect(Collectors.joining()),
                        Flux.fromIterable(response.getNyeDagpFeilList())
                                .map(dagpFeil ->
                                        encodeStatus(String.format(STATUS_FMT, dagpFeil.getNyDagpFeilstatus(), dagpFeil.getMelding())))
                                .collect(Collectors.joining()))

                .collect(Collectors.joining());
    }

    private static LocalDate toDate(LocalDateTime localDateTime) {

        return nonNull(localDateTime) ? localDateTime.toLocalDate() : null;
    }

//    private static void findAndStopAktivYtelse(ArenaStatusResponse arenaStatus) {
//
//        if (arenaStatus.getVedtakListe().
//    }
}
