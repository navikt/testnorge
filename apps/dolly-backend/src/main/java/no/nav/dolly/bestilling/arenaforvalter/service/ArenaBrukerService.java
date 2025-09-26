package no.nav.dolly.bestilling.arenaforvalter.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.arenaforvalter.ArenaForvalterConsumer;
import no.nav.dolly.bestilling.arenaforvalter.ArenaUtils;
import no.nav.dolly.bestilling.arenaforvalter.dto.ArenaVedtakOperasjoner;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaBruker;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaBrukerUtenServicebehov;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyBruker;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukere;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukereResponse;
import no.nav.dolly.domain.resultset.arenaforvalter.Arenadata;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.bestilling.arenaforvalter.ArenaUtils.fixFormatUserDefinedError;
import static no.nav.dolly.bestilling.arenaforvalter.ArenaUtils.toLocalDate;
import static no.nav.dolly.bestilling.arenaforvalter.utils.ArenaStatusUtil.getMessage;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.encodeStatus;
import static no.nav.dolly.util.DateZoneUtil.CET;

@Service
@RequiredArgsConstructor
public class ArenaBrukerService {

    private final MapperFacade mapperFacade;
    private final ArenaForvalterConsumer arenaForvalterConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;

    public Flux<String> sendBruker(Arenadata arenadata, ArenaVedtakOperasjoner arbeidssoker,
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

                    if (arenaNyeBrukere.getNyeBrukere().stream().anyMatch(ArenaNyBruker::hasServicebehov) ||
                            isNull(arbeidssoker.getRegistrertDato())) {

                        arenaNyeBrukere.getNyeBrukere().stream()
                                .findFirst()
                                .ifPresent(nyBruker -> {
                                    if (!nyBruker.hasKvalifiseringsgruppe()) {
                                        nyBruker.setKvalifiseringsgruppe(arbeidssoker.getKvalifiseringsgruppe());
                                    }
                                    if (nonNull(arenadata.getInaktiveringDato())) {
                                        nyBruker.setUtenServicebehov(ArenaBrukerUtenServicebehov.builder()
                                                .stansDato(toLocalDate(arenadata.getInaktiveringDato()))
                                                .build());
                                    }
                                });

                        return arenaForvalterConsumer.postArenaBruker(arenaNyeBrukere)
                                .flatMap(this::getBrukerStatus)
                                .map(response -> ArenaUtils.OPPRETTET + response);

                    } else {

                        return Flux.from(arenaForvalterConsumer.inaktiverBruker(ident, toLocalDate(arenadata.getInaktiveringDato()), miljoe)
                                        .map(respons -> respons.getStatus().is2xxSuccessful() ?
                                                "OK" : errorStatusDecoder.getErrorText(respons.getStatus(), respons.getFeilmelding())))
                                .map(response -> ArenaUtils.INAKTIVERT + response);
                    }
                });
    }

    Mono<String> getBrukerStatus(ArenaNyeBrukereResponse response) {

        return Flux.concat(Flux.just(response.getStatus())
                                .filter(status -> !status.is2xxSuccessful())
                                .map(status -> errorStatusDecoder.getErrorText(response.getStatus(), getMessage(response.getFeilmelding()))),
                        Flux.fromIterable(response.getArbeidsokerList())
                                .map(nyBruker -> nyBruker.getStatus() == ArenaBruker.BrukerStatus.OK ?
                                        "OK" :
                                        encodeStatus(ArenaUtils.AVSLAG + nyBruker.getStatus()))
                                .collect(Collectors.joining()),
                        Flux.fromIterable(response.getNyBrukerFeilList())
                                .map(brukerFeil ->
                                        encodeStatus(String.format(ArenaUtils.STATUS_FMT, brukerFeil.getNyBrukerFeilstatus(), brukerFeil.getMelding())))
                                .map(decoded -> {
                                    if (decoded.contains("404 Not Found")) {
                                        return "404 Not Found";
                                    } else if (decoded.contains("FINNES_ALLEREDE_PAA_VALGT_MILJO") ||
                                            decoded.contains("kan ikke reaktiveres siden denne er under behandling for " +
                                                    "aktivering eller har status som aktivert ved forsøk på aktivering")) {
                                        return "OK";
                                    } else {
                                        return fixFormatUserDefinedError(decoded);
                                    }
                                })
                                .collect(Collectors.joining()))

                .collect(Collectors.joining());
    }

    private static LocalDate oppdaterAktiveringsdato(ArenaNyBruker bruker, ArenaVedtakOperasjoner arbeidssoker) {

        return Stream.of(bruker.getAktiveringsDato(), arbeidssoker.getRegistrertDato())
                .filter(Objects::nonNull)
                .max(LocalDate::compareTo)
                .orElse(LocalDate.now(CET));
    }
}
