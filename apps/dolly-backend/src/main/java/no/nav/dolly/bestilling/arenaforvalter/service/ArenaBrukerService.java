package no.nav.dolly.bestilling.arenaforvalter.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.arenaforvalter.ArenaForvalterConsumer;
import no.nav.dolly.bestilling.arenaforvalter.ArenaUtils;
import no.nav.dolly.bestilling.arenaforvalter.dto.ArenaInnsatsbehov;
import no.nav.dolly.bestilling.arenaforvalter.dto.ArenaInnsatsbehovResponse;
import no.nav.dolly.bestilling.arenaforvalter.dto.ArenaVedtakOperasjoner;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaBruker;
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
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.encodeStatus;

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

                    if (arenaNyeBrukere.getNyeBrukere().stream()
                            .anyMatch(ArenaNyBruker::hasKvalifiseringsgruppe)) {

                        if (isNull(arbeidssoker.getRegistrertDato())) {
                            return arenaForvalterConsumer.postArenaBruker(arenaNyeBrukere)
                                    .flatMap(this::getBrukerStatus)
                                    .map(response -> ArenaUtils.OPPRETTET + response);

                        } else {
                            return arenaForvalterConsumer.postInnsatsbehov(buildInnsatsbehov(ident, miljoe, arenaNyeBrukere.getNyeBrukere()))
                                    .flatMap(this::getInnsatsStatus)
                                    .map(response -> ArenaUtils.OPPRETTET + response);
                        }

                    } else if (nonNull(arenadata.getInaktiveringDato())) {

                        return Flux.from(arenaForvalterConsumer.inaktiverBruker(ident, miljoe)
                                        .map(respons -> respons.getStatus().is2xxSuccessful() ?
                                                "OK" : errorStatusDecoder.getErrorText(respons.getStatus(), respons.getFeilmelding())))
                                .map(response -> ArenaUtils.INAKTIVERT + response);
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

    private static LocalDate oppdaterAktiveringsdato(ArenaNyBruker bruker, ArenaVedtakOperasjoner arbeidssoker) {

        return Stream.of(bruker.getAktiveringsDato(), arbeidssoker.getRegistrertDato())
                .filter(Objects::nonNull)
                .max(LocalDate::compareTo)
                .orElse(LocalDate.now());
    }

    private Mono<String> getInnsatsStatus(ArenaInnsatsbehovResponse response) {

        if (response.getStatus().is2xxSuccessful() && response.getNyeEndreInnsatsbehovFeilList().isEmpty()) {

            return Mono.just("OK");
        } else if (!response.getStatus().is2xxSuccessful()) {

            return Mono.just(errorStatusDecoder.getErrorText(response.getStatus(), response.getFeilmelding()));
        } else {

            return Flux.fromIterable(response.getNyeEndreInnsatsbehovFeilList())
                    .map(feil ->
                            encodeStatus(String.format(ArenaUtils.STATUS_FMT, feil.getNyEndreInnsatsbehovFeilstatus(), feil.getMelding())))
                    .collect(Collectors.joining());
        }
    }

    private Mono<String> getBrukerStatus(ArenaNyeBrukereResponse response) {

        return Flux.concat(Flux.just(response.getStatus())
                                .filter(status -> !status.is2xxSuccessful())
                                .map(status -> errorStatusDecoder.getErrorText(response.getStatus(), response.getFeilmelding())),
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
                                    } else {
                                        return decoded;
                                    }
                                })
                                .collect(Collectors.joining()))

                .collect(Collectors.joining());
    }
}
