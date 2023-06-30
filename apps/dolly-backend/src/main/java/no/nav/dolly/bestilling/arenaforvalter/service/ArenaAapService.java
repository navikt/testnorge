package no.nav.dolly.bestilling.arenaforvalter.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.arenaforvalter.ArenaForvalterConsumer;
import no.nav.dolly.bestilling.arenaforvalter.ArenaUtils;
import no.nav.dolly.bestilling.arenaforvalter.dto.Aap;
import no.nav.dolly.bestilling.arenaforvalter.dto.AapRequest;
import no.nav.dolly.bestilling.arenaforvalter.dto.AapResponse;
import no.nav.dolly.bestilling.arenaforvalter.dto.ArenaStatusResponse;
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
public class ArenaAapService {

    private final ArenaForvalterConsumer arenaForvalterConsumer;
    private final MapperFacade mapperFacade;
    private final ErrorStatusDecoder errorStatusDecoder;

    public Flux<String> sendAap(Arenadata arenadata, ArenaStatusResponse status, String ident, String miljoe) {

        return Flux.just(arenadata)
                .filter(arenadata1 -> !arenadata1.getAap().isEmpty())
                .filter(arenadata1 -> isNull(arenadata1.getInaktiveringDato()))
                .map(arenadata1 -> {
                    var context = new MappingContext.Factory().getContext();
                    context.setProperty(ArenaUtils.IDENT, ident);
                    context.setProperty(ArenaUtils.MILJOE, miljoe);
                    return mapperFacade.map(arenadata1, AapRequest.class, context);
                })
                .flatMap(request -> Flux.fromIterable(arenadata.getAap())
                        .flatMap(aap -> Flux.concat(Flux.fromIterable(status.getVedtakListe())
                                        .filter(vedtak -> "AAP".equals(vedtak.getRettighet().getKode()))
                                        .filter(vedtak -> "Aktiv".equals(vedtak.getSak().getStatus()))
                                        .flatMap(vedtak -> {
                                            var opphoerRequest = mapperFacade.map(request, AapRequest.class);
                                            opphoerRequest.getNyeAap()
                                                    .forEach(opphoer -> {
                                                        opphoer.setVedtaktype(Aap.VedtakType.S);
                                                        opphoer.setFraDato(vedtak.getFraDato());
                                                        opphoer.setTilDato(ArenaUtils.toDate(aap.getFraDato()));
                                                    });
                                            return arenaForvalterConsumer.postAap(opphoerRequest)
                                                    .flatMap(this::getAapStatus)
                                                    .map(response -> ArenaUtils.STANSET + response);
                                        }),
                                arenaForvalterConsumer.postAap(request)
                                        .flatMap(this::getAapStatus)
                                        .map(response -> ArenaUtils.OPPRETTET + response)
                        )));
    }

    private Mono<String> getAapStatus(AapResponse response) {

        if (response.getStatus().is2xxSuccessful() && response.getNyeAap().isEmpty() && response.getNyeAapFeilList().isEmpty()) {

            return Mono.just("OK");
        } else {

            return Flux.concat(Flux.just(response.getStatus())
                                    .filter(status -> !status.is2xxSuccessful())
                                    .map(status -> errorStatusDecoder.getErrorText(response.getStatus(), response.getFeilmelding())),
                            Flux.fromIterable(response.getNyeAap())
                                    .map(nyAap -> "JA".equals(nyAap.getUtfall()) ?
                                            "OK" :
                                            encodeStatus(ArenaUtils.AVSLAG + nyAap.getBegrunnelse()))
                                    .collect(Collectors.joining()),
                            Flux.fromIterable(response.getNyeAapFeilList())
                                    .map(aapFeil ->
                                            encodeStatus(String.format(ArenaUtils.STATUS_FMT, aapFeil.getNyAapFeilstatus(), aapFeil.getMelding())))
                                    .collect(Collectors.joining()))

                    .collect(Collectors.joining());
        }
    }
}
