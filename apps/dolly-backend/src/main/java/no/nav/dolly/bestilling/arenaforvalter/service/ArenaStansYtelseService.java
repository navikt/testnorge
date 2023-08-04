package no.nav.dolly.bestilling.arenaforvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.arenaforvalter.ArenaForvalterConsumer;
import no.nav.dolly.bestilling.arenaforvalter.ArenaUtils;
import no.nav.dolly.bestilling.arenaforvalter.dto.Aap;
import no.nav.dolly.bestilling.arenaforvalter.dto.AapRequest;
import no.nav.dolly.bestilling.arenaforvalter.dto.ArenaVedtakOperasjoner;
import no.nav.dolly.bestilling.arenaforvalter.utils.ArenaStatusUtil;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaDagpenger;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

import static java.util.Objects.nonNull;
import static no.nav.dolly.bestilling.arenaforvalter.utils.ArenaStatusUtil.AAP;
import static no.nav.dolly.bestilling.arenaforvalter.utils.ArenaStatusUtil.DAGPENGER;
import static no.nav.dolly.bestilling.arenaforvalter.utils.ArenaStatusUtil.fmtResponse;
import static no.nav.dolly.bestilling.arenaforvalter.utils.ArenaStatusUtil.getDagpengerStatus;

@Service
@RequiredArgsConstructor
public class ArenaStansYtelseService {

    private final ArenaForvalterConsumer arenaForvalterConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;

    public Flux<String> stopYtelse(ArenaVedtakOperasjoner arenaVedtakOperasjoner, String ident, String miljoe) {

        return Flux.concat(
                Flux.just(arenaVedtakOperasjoner.getAapVedtak())
                        .filter(aap -> nonNull(aap.getAvslutteVedtak()))
                        .flatMap(aap -> arenaForvalterConsumer.postAap(AapRequest.builder()
                                        .personident(ident)
                                        .miljoe(miljoe)
                                        .nyeAap(List.of(Aap.builder()
                                                .vedtaktype(Aap.VedtakType.S)
                                                .fraDato(aap.getAvslutteVedtak().getFom())
                                                .tilDato(aap.getAvslutteVedtak().getStansFra())
                                                .build()))
                                        .build())
                                .flatMap(response -> ArenaStatusUtil.getAapStatus(response, errorStatusDecoder))
                                .map(response -> ArenaUtils.STANSET + response)
                                .map(aapStatus -> fmtResponse(miljoe, AAP, aapStatus))),

                Flux.just(arenaVedtakOperasjoner.getDagpengeVedtak())
                        .filter(dagp -> nonNull(dagp.getAvslutteVedtak()))
                        .flatMap(dagp -> arenaForvalterConsumer.postArenaDagpenger(ArenaDagpenger.builder()
                                        .personident(ident)
                                        .miljoe(miljoe)
                                        .nyeDagp(List.of(ArenaDagpenger.NyeDagp.builder()
                                                .vedtaktype(ArenaDagpenger.VedtaksType.S)
                                                .vilkaar(ArenaDagpenger.DAGPENGER_VILKAAR)
                                                .taptArbeidstid(ArenaDagpenger.TaptArbeidstid.builder()
                                                        .anvendtRegelKode("GJSNITT12MND")
                                                        .fastsattArbeidstid(35)
                                                        .naavaerendeArbeidstid(6)
                                                        .build())
                                                .vedtaksperiode(ArenaDagpenger.Vedtaksperiode.builder()
                                                        .fom(arenaVedtakOperasjoner.getDagpengeVedtak().getAvslutteVedtak().getFom())
                                                        .tom(arenaVedtakOperasjoner.getDagpengeVedtak().getAvslutteVedtak().getTom())
                                                        .build())
                                                .stansFomDato(arenaVedtakOperasjoner.getDagpengeVedtak().getAvslutteVedtak().getStansFra())
                                                .build()))
                                        .build())
                                .flatMap(response -> getDagpengerStatus(response, errorStatusDecoder))
                                .map(response -> ArenaUtils.STANSET + response)
                                .map(dagpStatus -> fmtResponse(miljoe, DAGPENGER, dagpStatus))));
    }
}
