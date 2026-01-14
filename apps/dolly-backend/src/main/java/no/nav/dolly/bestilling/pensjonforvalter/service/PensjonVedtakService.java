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
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullPersonDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;

import java.time.Period;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils.IDENT;
import static no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils.MILJOER;
import static no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils.NAV_ENHET;
import static no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils.PEN_ALDERSPENSJON;
import static no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils.PEN_NY_UTTAKSGRAD_AP;
import static no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils.PEN_UFORETRYGD;
import static no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils.getStatus;
import static no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils.hasVedtak;
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
                                           Tuple3<List<PdlPersonBolk.PersonBolk>, List<FullPersonDTO>, String> utvidetPersondata,
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
                                utvidetPersondata.getT3(), bestilling, isUpdateEndre)
                                .map(response -> PEN_NY_UTTAKSGRAD_AP + pensjonforvalterHelper.decodeStatus(response, ident)),

                        lagreUforetrygd(
                                pensjon,
                                utvidetPersondata.getT3(),
                                ident,
                                miljoer,
                                bestilling.getId())
                                .map(response -> PEN_UFORETRYGD + pensjonforvalterHelper.decodeStatus(response, ident))
                ));
    }

    private Flux<PensjonforvalterResponse> lagreAlderspensjon(PensjonData pensjonData,
                                                              Tuple3<List<PdlPersonBolk.PersonBolk>,
                                                                      List<FullPersonDTO>,String> utvidetPersondata,
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
                                            context.setProperty("pdlRelasjoner", utvidetPersondata.getT1());
                                            context.setProperty("dollyRelasjoner", utvidetPersondata.getT2());
                                            pensjonRequest = mapperFacade.map(alderspensjon, AlderspensjonSoknadRequest.class, context);

                                        } else {
                                            context.setProperty(NAV_ENHET, utvidetPersondata.getT3());
                                            pensjonRequest = mapperFacade.map(alderspensjon, AlderspensjonVedtakRequest.class, context);
                                        }

                                        return pensjonforvalterConsumer.lagreAlderspensjon(pensjonRequest)
                                                .flatMap(response ->
                                                        Flux.fromIterable(response.getStatus())
                                                                .filter(status -> status.getResponse().isResponse2xx())
                                                                .flatMap(status ->
                                                                        pensjonforvalterHelper.saveAPTransaksjonId(ident,
                                                                                status.getMiljo(), bestillingId,
                                                                                pensjonRequest))
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
                                                                        pensjonforvalterHelper.saveUTTransaksjonId(ident,
                                                                                status.getMiljo(), bestillingId,
                                                                                request))
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
                .flatMap(miljoe -> pensjonforvalterHelper.hentForrigeVedtakAP(ident, miljoe,
                                pensjondata.getAlderspensjonNyUtaksgrad().getFom())
                        .flatMap(vedtak -> {
                            var context = new MappingContext.Factory().getContext();
                            context.setProperty(ALDERSPENSJON_VEDTAK, vedtak);
                            context.setProperty(NAV_ENHET, navEnhetId);
                            return Mono.just(mapperFacade.map(pensjondata.getAlderspensjonNyUtaksgrad(),
                                            AlderspensjonNyUtaksgradRequest.class, context))
                                    .zipWith(Mono.just(vedtak));
                        })
                        .flatMapMany(tuple -> {
                            if (isValid(tuple.getT1(), tuple.getT2())) {
                                return pensjonforvalterConsumer.lagreAPNyUttaksgrad(tuple.getT1())
                                        .flatMap(response -> Flux.fromIterable(response.getStatus())
                                                .filter(status -> status.getResponse().isResponse2xx())
                                                .flatMap(status ->
                                                        isNoMatch(tuple.getT2(), tuple.getT1()) ?
                                                                pensjonforvalterHelper.saveTransaksjonId(ident, status.getMiljo(), bestilling.getId(),
                                                                                SystemTyper.PEN_AP_NY_UTTAKSGRAD, tuple.getT1())
                                                                        .thenReturn(response) :
                                                                Mono.just(response)));
                            } else {
                                return miscRevurderingResponse(tuple.getT1(), tuple.getT2(), miljoe, isUpdateEndre);
                            }
                        }));
    }

    private static boolean isNoMatch(AlderspensjonVedtakDTO response, AlderspensjonNyUtaksgradRequest request) {

        return !(response.getFom().equals(request.getFom()) &&
                response.getUttaksgrad().equals(request.getNyUttaksgrad())) &&
                response.getHistorikk().stream()
                        .noneMatch(historikk ->
                                historikk.getFom().equals(request.getFom()) &&
                                        historikk.getUttaksgrad().equals(request.getNyUttaksgrad()));
    }

    private static boolean isValid(AlderspensjonNyUtaksgradRequest request, AlderspensjonVedtakDTO response) {

        return nonNull(response.getFom()) &&
                request.getFom().isAfter(response.getFom()) &&
                (request.getNyUttaksgrad().equals(0) || request.getNyUttaksgrad().equals(100) ||
                        Period.between(response.getDatoForrigeGraderteUttak(), request.getFom()).getYears() >= 1);
    }

    private static Mono<PensjonforvalterResponse> miscRevurderingResponse(AlderspensjonNyUtaksgradRequest request,
                                                                          AlderspensjonVedtakDTO response, String miljoe,
                                                                          boolean isUpdateEndre) {

        String message;
        if (isNull(response.getFom())) {
            message = "Uttaksgrad kan ikke endres da vedtak for alderspensjon mangler.";

        } else if (isUpdateEndre && request.getFom().isBefore(response.getFom())) {
            message = "Automatisk vedtak av ny uttaksgrad ikke mulig for dato tidligere enn dato på forrige vedtak.";

        } else if (isUpdateEndre && !request.getNyUttaksgrad().equals(0) && !request.getNyUttaksgrad().equals(100) &&
                Period.between(response.getDatoForrigeGraderteUttak(), request.getFom()).getYears() < 1) {
            message = "Automatisk vedtak med gradert uttak ikke mulig oftere enn hver 12 måned.";

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
