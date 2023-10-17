package no.nav.dolly.bestilling.arenaforvalter.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.arenaforvalter.ArenaForvalterConsumer;
import no.nav.dolly.bestilling.arenaforvalter.ArenaUtils;
import no.nav.dolly.bestilling.arenaforvalter.dto.ArenaVedtakOperasjoner;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaDagpenger;
import no.nav.dolly.domain.resultset.arenaforvalter.Arenadata;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.bestilling.arenaforvalter.utils.ArenaStatusUtil.getDagpengerStatus;

@Service
@RequiredArgsConstructor
public class ArenaDagpengerService {

    private final ArenaForvalterConsumer arenaForvalterConsumer;
    private final MapperFacade mapperFacade;
    private final ErrorStatusDecoder errorStatusDecoder;

    public Flux<String> sendDagpenger(Arenadata arenadata, ArenaVedtakOperasjoner operasjoner, String ident, String miljoe) {

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
                        .flatMap(dagp -> Flux.concat(
                                Flux.just(operasjoner.getDagpengeVedtak())
                                        .filter(vedtak -> nonNull(vedtak.getNyttVedtak()))
                                        .flatMap(vedtak -> {
                                            request.getNyeDagp().forEach(dagp1 ->
                                                    dagp1.getVedtaksperiode().setTom(vedtak.getNyttVedtak().getTom()));
                                            return arenaForvalterConsumer.postArenaDagpenger(request)
                                                    .flatMap(response -> getDagpengerStatus(response, errorStatusDecoder))
                                                    .map(response -> ArenaUtils.OPPRETTET + response);
                                        }),
                                Flux.just(operasjoner.getDagpengeVedtak())
                                        .filter(ArenaVedtakOperasjoner.Operasjon::isEksisterendeVedtak)
                                        .map(vedtak -> ArenaUtils.OPPRETTET + "OK")
                        )));
    }
}
