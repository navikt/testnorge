package no.nav.dolly.bestilling.pensjonforvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pensjonforvalter.PensjonforvalterConsumer;
import no.nav.dolly.bestilling.pensjonforvalter.domain.AlderspensjonNyUtaksgradRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.AlderspensjonRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.AlderspensjonSoknadRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.AlderspensjonVedtakDTO;
import no.nav.dolly.bestilling.pensjonforvalter.domain.AlderspensjonVedtakRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonUforetrygdRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonVedtakResponse;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterHelper;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.SystemTyper;
import no.nav.dolly.domain.resultset.pensjon.PensjonData;
import no.nav.dolly.mapper.MappingContextUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.List;
import java.util.Set;

import static java.util.Objects.nonNull;
import static no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils.IDENT;
import static no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils.MILJOER;
import static no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils.NAV_ENHET;
import static no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils.PEN_ALDERSPENSJON;
import static no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils.PEN_NY_UTTAKSGRAD_AP;
import static no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils.PEN_UFORETRYGD;
import static no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils.getStatus;
import static no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils.hasVedtak;
import static no.nav.dolly.domain.resultset.SystemTyper.PEN_AP;
import static no.nav.dolly.domain.resultset.SystemTyper.PEN_UT;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Slf4j
@Service
@RequiredArgsConstructor
public class PensjonVedtakService {

    private final MapperFacade mapperFacade;
    private final PensjonforvalterConsumer pensjonforvalterConsumer;
    private final PensjonforvalterHelper pensjonforvalterHelper;

    private static final String ALDERSPENSJON_VEDTAK = "AlderspensjonVedtak";

    public Flux<String> lagrePensjonVedtak(RsDollyBestilling bestilling, String ident,
                                           Tuple2<List<PdlPersonBolk.PersonBolk>, String> utvidetPersondata,
                                           Set<String> miljoer, boolean isUpdateEndre) {

        return Flux.just(bestilling)
                .filter(RsDollyBestilling::isPensjon)
                .map(RsDollyBestilling::getPensjonforvalter)
                .flatMap(pensjon -> Flux.merge(
                        lagreAlderspensjon(
                                pensjon,
                                utvidetPersondata,
                                ident,
                                miljoer,
                                bestilling.getId())
                                .map(response -> PEN_ALDERSPENSJON + pensjonforvalterHelper.decodeStatus(response, ident)),

                        lagreNyVurdering(
                                pensjon,
                                ident,
                                miljoer,
                                utvidetPersondata.getT2(), bestilling, isUpdateEndre)
                                .map(response -> PEN_NY_UTTAKSGRAD_AP + pensjonforvalterHelper.decodeStatus(response, ident)),

                        lagreUforetrygd(
                                pensjon,
                                utvidetPersondata.getT2(),
                                ident,
                                miljoer,
                                bestilling.getId())
                                .map(response -> PEN_UFORETRYGD + pensjonforvalterHelper.decodeStatus(response, ident))
                ));
    }

    private Flux<PensjonforvalterResponse> lagreAlderspensjon(PensjonData pensjonData,
                                                              Tuple2<List<PdlPersonBolk.PersonBolk>, String> utvidetPersondata,
                                                              String ident, Set<String> miljoer,
                                                              Long bestillingId) {

        return Mono.just(pensjonData)
                .filter(PensjonData::hasAlderspensjon)
                .map(PensjonData::getAlderspensjon)
                .flatMapMany(alderspensjon -> Flux.fromIterable(miljoer)
                        .flatMap(miljoe -> pensjonforvalterConsumer.hentVedtak(ident, miljoe)
                                .collectList()

                                .map(vedtakResponse -> !hasVedtak(vedtakResponse, PensjonVedtakResponse.SakType.AP))
                                .map(skalOpprette -> {
                                    if (isTrue(skalOpprette)) {

                                        AlderspensjonRequest pensjonRequest;
                                        var context = new MappingContext.Factory().getContext();
                                        context.setProperty(IDENT, ident);
                                        context.setProperty(MILJOER, Set.of(miljoe));

                                        if (alderspensjon.isSoknad()) {
                                            context.setProperty("relasjoner", utvidetPersondata.getT1());
                                            pensjonRequest = mapperFacade.map(alderspensjon, AlderspensjonSoknadRequest.class, context);

                                        } else {
                                            context.setProperty(NAV_ENHET, utvidetPersondata.getT2());
                                            pensjonRequest = mapperFacade.map(alderspensjon, AlderspensjonVedtakRequest.class, context);
                                        }

                                        return pensjonforvalterConsumer.lagreAlderspensjon(pensjonRequest)
                                                .flatMap(response ->
                                                        Flux.fromIterable(response.getStatus())
                                                                .filter(status -> status.getResponse().isResponse2xx())
                                                                .flatMap(status ->
                                                                        pensjonforvalterHelper.saveAPTransaksjonId(ident, status.getMiljo(), bestillingId,
                                                                                PEN_AP, pensjonRequest))
                                                                .then(Mono.just(response)));

                                    } else {
                                        return getStatus(miljoe, 200, "OK");
                                    }
                                })))
                .flatMap(response -> response);
    }

    private Flux<PensjonforvalterResponse> lagreUforetrygd(PensjonData pensjondata, String navEnhetNr,
                                                           String ident, Set<String> miljoer, Long bestillingId) {

        return Mono.just(pensjondata)
                .filter(PensjonData::hasUforetrygd)
                .map(PensjonData::getUforetrygd)
                .flatMapMany(uforetrygd -> Flux.fromIterable(miljoer)
                        .flatMap(miljoe -> pensjonforvalterConsumer.hentVedtak(ident, miljoe)
                                .collectList()
                                .flatMap(vedtak -> {
                                    if (!hasVedtak(vedtak, PensjonVedtakResponse.SakType.UT)) {

                                        var context = MappingContextUtils.getMappingContext();
                                        context.setProperty(IDENT, ident);
                                        context.setProperty(MILJOER, List.of(miljoe));
                                        context.setProperty(NAV_ENHET, navEnhetNr);
                                        return Mono.just(mapperFacade.map(uforetrygd, PensjonUforetrygdRequest.class, context))
                                                .flatMap(request -> pensjonforvalterConsumer.lagreUforetrygd(request)
                                                        .flatMap(response -> Flux.fromIterable(response.getStatus())
                                                                .filter(status -> status.getResponse().isResponse2xx())
                                                                .flatMap(status ->
                                                                        pensjonforvalterHelper.saveAPTransaksjonId(ident, status.getMiljo(), bestillingId,
                                                                                PEN_UT, request))
                                                                .then(Mono.just(response))));

                                    } else {
                                        return getStatus(miljoe, 200, "OK");
                                    }
                                })));
    }

    private Flux<PensjonforvalterResponse> lagreNyVurdering(PensjonData pensjondata, String ident, Set<String> miljoer,
                                                            String navEnhetId, RsDollyBestilling bestilling, boolean isUpdateEndre) {

        if (!pensjondata.hasNyUttaksgrad()) {
            return Flux.empty();
        }

        return Flux.fromIterable(miljoer)
                .flatMap(miljoe -> pensjonforvalterHelper.hentSisteVedtakAP(ident, miljoe)
                        .flatMap(vedtak -> {
                            var context = new MappingContext.Factory().getContext();
                            context.setProperty(ALDERSPENSJON_VEDTAK, vedtak);
                            context.setProperty(NAV_ENHET, navEnhetId);
                            return Mono.just(mapperFacade.map(pensjondata.getAlderspensjonNyUtaksgrad(),
                                            AlderspensjonNyUtaksgradRequest.class, context))
                                    .zipWith(Mono.just(vedtak));
                        })
                        .flatMapMany(tuple ->
                                isValid(tuple.getT1(), tuple.getT2()) ?
                                        pensjonforvalterConsumer.lagreAPNyUttaksgrad(tuple.getT1())
                                                .zipWith(Mono.just(tuple.getT1()))
                                                .flatMap(tuple2 -> Flux.fromIterable(tuple2.getT1().getStatus())
                                                        .filter(status -> status.getResponse().isResponse2xx())
                                                        .flatMap(status ->
                                                                pensjonforvalterHelper.saveTransaksjonId(ident, status.getMiljo(), bestilling.getId(),
                                                                                SystemTyper.PEN_AP_NY_UTTAKSGRAD, tuple2.getT2())
                                                                        .thenReturn(tuple2.getT1()))) :
                                        miscRevurderingResponse(tuple.getT1(), tuple.getT2(), miljoe, isUpdateEndre)));
    }

    private static boolean isValid(AlderspensjonNyUtaksgradRequest request, AlderspensjonVedtakDTO response) {

        var eksisterendeFom = nonNull(response.getFom()) ? response.getFom() : response.getIverksettelsesdato();

        return request.getFom().isAfter(eksisterendeFom) &&
                (request.getNyUttaksgrad().equals(0) || request.getNyUttaksgrad().equals(100) ||
                        (request.getFom().getMonth().getValue() == 1 && request.getFom().getDayOfMonth() == 1));
    }

    private static Mono<PensjonforvalterResponse> miscRevurderingResponse(AlderspensjonNyUtaksgradRequest request,
                                                                   AlderspensjonVedtakDTO response, String miljoe,
                                                                          boolean isUpdateEndre) {

        var eksisterendeFom = nonNull(response.getFom()) ? response.getFom() : response.getIverksettelsesdato();

        String message;
         if (isUpdateEndre && request.getFom().isBefore(eksisterendeFom)) {
             message = "Automatisk vedtak av ny uttaksgrad ikke mulig for dato tidligere enn dato på forrige vedtak.";

         } else if (isUpdateEndre && !request.getNyUttaksgrad().equals(0) && !request.getNyUttaksgrad().equals(100) &&
                 (request.getFom().getMonth().getValue() != 1 || request.getFom().getDayOfMonth() != 1)) {

             message = "Automatisk vedtak av gradert uttaksgrad ikke mulig unntatt ved nytt år (1. janauar).";

        } else {
            return Mono.empty();
        }

        return Mono.just(PensjonforvalterResponse.builder()
                .status(List.of(PensjonforvalterResponse.ResponseEnvironment.builder()
                        .response(PensjonforvalterResponse.Response.builder()
                                .httpStatus(PensjonforvalterResponse.HttpStatus.builder()
                                        .status(400)
                                        .build())
                                .message(message)
                                .build())
                                .miljo(miljoe)
                        .build()))
                .build());
    }
}
