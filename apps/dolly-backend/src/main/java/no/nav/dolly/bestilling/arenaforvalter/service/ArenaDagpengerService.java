package no.nav.dolly.bestilling.arenaforvalter.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.arenaforvalter.ArenaForvalterConsumer;
import no.nav.dolly.bestilling.arenaforvalter.ArenaUtils;
import no.nav.dolly.bestilling.arenaforvalter.dto.ArenaStatusResponse;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaDagpenger;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeDagpengerResponse;
import no.nav.dolly.domain.resultset.arenaforvalter.Arenadata;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.encodeStatus;

@Service
@RequiredArgsConstructor
public class ArenaDagpengerService {

    private final ArenaForvalterConsumer arenaForvalterConsumer;
    private final MapperFacade mapperFacade;
    private final ErrorStatusDecoder errorStatusDecoder;

    public Flux<String> sendDagpenger(Arenadata arenadata, ArenaStatusResponse status, String ident, String miljoe) {

        return Flux.just(arenadata)
                .filter(arenadata1 -> !arenadata1.getDagpenger().isEmpty())
                .filter(arenadata1 -> isNull(arenadata1.getInaktiveringDato()))
                .map(arenadata1 -> {
                    var context = new MappingContext.Factory().getContext();
                    context.setProperty(ArenaUtils.IDENT, ident);
                    context.setProperty(ArenaUtils.MILJOE, miljoe);
                    return mapperFacade.map(arenadata1, ArenaDagpenger.class, context);
                })
                .flatMap(request -> Flux.fromIterable(arenadata.getDagpenger())
                        .flatMap(dagp -> Flux.concat(Flux.fromIterable(status.getVedtakListe())
                                        .filter(vedtak -> "DAGP".equals(vedtak.getRettighet().getKode()))
                                        .filter(vedtak -> "Aktiv".equals(vedtak.getSak().getStatus()))
                                        .flatMap(vedtak -> {
                                            var opphoerRequest = mapperFacade.map(request, ArenaDagpenger.class);
                                            opphoerRequest.getNyeDagp()
                                                    .forEach(opphoer -> {
                                                        opphoer.setVedtaktype(ArenaDagpenger.VedtaksType.S);
                                                        opphoer.setVedtaksperiode(ArenaDagpenger.Vedtaksperiode.builder()
                                                                .fom(vedtak.getFraDato())
                                                                .tom(vedtak.getTilDato())
                                                                .build());
                                                        opphoer.setStansFomDato(ArenaUtils.toDate(dagp.getFraDato()));
                                                    });
                                            return arenaForvalterConsumer.postArenaDagpenger(opphoerRequest)
                                                    .flatMap(this::getDagpengerStatus)
                                                    .map(response -> ArenaUtils.STANSET + response);

                                        }),
                                arenaForvalterConsumer.postArenaDagpenger(request)
                                        .flatMap(this::getDagpengerStatus)
                                        .map(response -> ArenaUtils.OPPRETTET + response)
                        )));
    }

    private Mono<String> getDagpengerStatus(ArenaNyeDagpengerResponse response) {

        return Flux.concat(Flux.just(response.getStatus())
                                .filter(status -> !status.is2xxSuccessful())
                                .map(status -> errorStatusDecoder.getErrorText(response.getStatus(), response.getFeilmelding())),
                        Flux.fromIterable(response.getNyeDagp())
                                .map(nyDagP -> "JA".equals(nyDagP.getNyeDagpResponse().getUtfall()) ?
                                        "OK" :
                                        encodeStatus(ArenaUtils.AVSLAG + nyDagP.getNyeDagpResponse().getBegrunnelse()))
                                .collect(Collectors.joining()),
                        Flux.fromIterable(response.getNyeDagpResponse())
                                .map(nyDagP -> "JA".equals(nyDagP.getUtfall()) ?
                                        "OK" :
                                        encodeStatus(ArenaUtils.AVSLAG + nyDagP.getBegrunnelse()))
                                .collect(Collectors.joining()),
                        Flux.fromIterable(response.getNyeDagpFeilList())
                                .map(dagpFeil ->
                                        encodeStatus(String.format(ArenaUtils.STATUS_FMT, dagpFeil.getNyDagpFeilstatus(), dagpFeil.getMelding())))
                                .collect(Collectors.joining()))

                .collect(Collectors.joining());
    }
}
