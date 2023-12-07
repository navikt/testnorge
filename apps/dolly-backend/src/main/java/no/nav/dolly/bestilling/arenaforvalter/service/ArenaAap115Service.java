package no.nav.dolly.bestilling.arenaforvalter.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.arenaforvalter.ArenaForvalterConsumer;
import no.nav.dolly.bestilling.arenaforvalter.ArenaUtils;
import no.nav.dolly.bestilling.arenaforvalter.dto.Aap115Request;
import no.nav.dolly.bestilling.arenaforvalter.dto.Aap115Response;
import no.nav.dolly.bestilling.arenaforvalter.dto.ArenaVedtakOperasjoner;
import no.nav.dolly.domain.resultset.arenaforvalter.Arenadata;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.bestilling.arenaforvalter.ArenaUtils.fixFormatUserDefinedError;
import static no.nav.dolly.bestilling.arenaforvalter.utils.ArenaStatusUtil.getMessage;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.encodeStatus;

@Service
@RequiredArgsConstructor
public class ArenaAap115Service {

    private final MapperFacade mapperFacade;
    private final ArenaForvalterConsumer arenaForvalterConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;

    public Flux<String> sendAap115(Arenadata arenadata, ArenaVedtakOperasjoner operasjoner, String ident, String miljoe) {

        return Flux.just(arenadata)
                .filter(arenadata1 -> !arenadata1.getAap115().isEmpty())
                .filter(arenadata1 -> isNull(arenadata1.getInaktiveringDato()))
                .map(arenadata1 -> {
                    var context = new MappingContext.Factory().getContext();
                    context.setProperty(ArenaUtils.IDENT, ident);
                    context.setProperty(ArenaUtils.MILJOE, miljoe);
                    return mapperFacade.map(arenadata1, Aap115Request.class, context);
                })
                .flatMap(request -> Flux.fromIterable(arenadata.getAap115())
                        .flatMap(aap115 -> Flux.concat(
                                Flux.just(operasjoner.getAa115())
                                        .filter(operasjon -> nonNull(operasjon.getNyttVedtak()))
                                        .flatMap(operasjon -> arenaForvalterConsumer.postAap115(request)
                                                .flatMap(this::getAap115Status)
                                                .map(response -> ArenaUtils.OPPRETTET + response)),
                                Flux.just(operasjoner.getAa115())
                                        .filter(ArenaVedtakOperasjoner.Operasjon::isEksisterendeVedtak)
                                        .map(operasjon -> ArenaUtils.OPPRETTET + "OK")
                        )));
    }

    private Mono<String> getAap115Status(Aap115Response response) {

        return Flux.concat(Flux.just(response.getStatus())
                                .filter(status -> !status.is2xxSuccessful())
                                .map(status -> errorStatusDecoder.getErrorText(response.getStatus(), getMessage(response.getFeilmelding()))),
                        Flux.fromIterable(response.getNyeAap115())
                                .map(nyAap115 -> "JA" .equals(nyAap115.getUtfall()) ?
                                        "OK" :
                                        encodeStatus(ArenaUtils.AVSLAG + nyAap115.getBegrunnelse()))
                                .collect(Collectors.joining()),
                        Flux.fromIterable(response.getNyeAap115FeilList())
                                .map(aap115Feil ->
                                        fixFormatUserDefinedError(encodeStatus(String.format(ArenaUtils.STATUS_FMT, aap115Feil.getNyAap115Feilstatus(), aap115Feil.getMelding()))))
                                .collect(Collectors.joining()))

                .collect(Collectors.joining());
    }
}
