package no.nav.dolly.bestilling.pensjonforvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pensjonforvalter.PensjonforvalterConsumer;
import no.nav.dolly.bestilling.pensjonforvalter.domain.AfpOffentligRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonPoppGenerertInntektRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonPoppInntektRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonTpForholdRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonTpYtelseRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonsavtaleRequest;
import no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterHelper;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.pensjon.PensjonData;
import no.nav.dolly.mapper.MappingContextUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils.IDENT;
import static no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils.MILJOE;
import static no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils.MILJOER;
import static no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils.PEN_AFP_OFFENTLIG;
import static no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils.PEN_PENSJONSAVTALE;
import static no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils.POPP_INNTEKTSREGISTER;
import static no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils.TP_FORHOLD;

@Slf4j
@Service
@RequiredArgsConstructor
public class PensjonPensjonsdataService {

    private final PensjonforvalterConsumer pensjonforvalterConsumer;
    private final PensjonforvalterHelper pensjonforvalterHelper;
    private final MapperFacade mapperFacade;

    public Flux<String> lagrePensjonsdata(RsDollyBestilling bestilling, String ident, Set<String> miljoer) {

        return Flux.just(bestilling)
                .filter(RsDollyBestilling::isPensjon)
                .map(RsDollyBestilling::getPensjonforvalter)
                .flatMap(pensjon -> Flux.merge(
                        lagreInntekt(pensjon,
                                        ident, miljoer)
                                .map(response -> POPP_INNTEKTSREGISTER + pensjonforvalterHelper.decodeStatus(response, ident)),

                        lagreGenerertInntekt(pensjon,
                                        ident, miljoer)
                                .map(response -> POPP_INNTEKTSREGISTER + pensjonforvalterHelper.decodeStatus(response, ident)),

                        lagreTpForhold(pensjon, ident, miljoer)
                                .map(response -> TP_FORHOLD + pensjonforvalterHelper.decodeStatus(response, ident)),

                        lagrePensjonsavtale(pensjon, ident, miljoer)
                                .map(response -> PEN_PENSJONSAVTALE + pensjonforvalterHelper.decodeStatus(response, ident)),

                        lagreAfpOffentlig(pensjon, ident, miljoer)
                                .map(response -> PEN_AFP_OFFENTLIG + pensjonforvalterHelper.decodeStatus(response, ident))
                ));
    }


    private Flux<PensjonforvalterResponse> lagreInntekt(PensjonData pensjonData, String ident,
                                                       Set<String> miljoer) {

        return Flux.just(pensjonData)
                .filter(PensjonData::hasInntekt)
                .map(PensjonData::getInntekt)
                .flatMap(inntekt -> Flux.fromIterable(miljoer)
                        .flatMap(miljoe -> {

                            var request = mapperFacade.map(inntekt, PensjonPoppInntektRequest.class);
                            request.setFnr(ident);
                            request.setMiljoer(List.of(miljoe));
                            return pensjonforvalterConsumer.lagreInntekter(request);
                        }));
    }

    private Flux<PensjonforvalterResponse> lagreGenerertInntekt(PensjonData pensjonData, String ident,
                                                               Set<String> miljoer) {

        return Flux.just(pensjonData)
                .filter(PensjonData::hasGenerertInntekt)
                .map(PensjonData::getGenerertInntekt)
                .flatMap(generertInntekt -> Flux.fromIterable(miljoer)
                        .flatMap(miljoe -> {

                            var context = MappingContextUtils.getMappingContext();
                            context.setProperty(IDENT, ident);
                            context.setProperty(MILJOE, miljoe);
                            var request = mapperFacade.map(generertInntekt, PensjonPoppGenerertInntektRequest.class, context);
                            return pensjonforvalterConsumer.lagreGenererteInntekter(request);
                        }));
    }

    public Mono<PensjonforvalterResponse> lagreTpForhold(PensjonData pensjonData, String
            ident, Set<String> miljoer) {

        return Flux.just(pensjonData)
                .filter(PensjonData::hasTp)
                .map(PensjonData::getTp)
                .flatMap(Flux::fromIterable)
                .flatMap(tp -> {

                    var context = new MappingContext.Factory().getContext();
                    context.setProperty(IDENT, ident);
                    context.setProperty(MILJOER, miljoer);

                    var tpForholdRequest = mapperFacade.map(tp, PensjonTpForholdRequest.class, context);
                    return pensjonforvalterConsumer.lagreTpForhold(tpForholdRequest)
                            .flatMap(forholdSvar -> {
                                        log.info("Lagret TP-forhold {}", forholdSvar);
                                        return Flux.fromIterable(tp.getYtelser())
                                                .flatMap(ytelse -> {
                                                    context.setProperty("ordning", tp.getOrdning());
                                                    PensjonTpYtelseRequest pensjonTpYtelseRequest = mapperFacade.map(ytelse, PensjonTpYtelseRequest.class, context);
                                                    return pensjonforvalterConsumer.lagreTpYtelse(pensjonTpYtelseRequest);
                                                });
                                    }
                            );
                })
                .collectList()
                .filter(resultat -> !resultat.isEmpty())
                .map(PensjonPensjonsdataService::mergePensjonforvalterResponses);
    }

    private Flux<PensjonforvalterResponse> lagrePensjonsavtale(PensjonData pensjon, String ident, Set<String> miljoer) {

        return Flux.just(pensjon)
                .filter(PensjonData::hasPensjonsavtale)
                .map(PensjonData::getPensjonsavtale)
                .flatMap(pensjonsavtaler -> Flux.fromIterable(pensjonsavtaler)
                        .flatMap(pensjonsavtale -> {

                            var context = MappingContextUtils.getMappingContext();
                            context.setProperty(IDENT, ident);
                            context.setProperty(MILJOER, miljoer);

                            var pensjonsavtaleRequest = mapperFacade.map(pensjonsavtale, PensjonsavtaleRequest.class, context);
                            return pensjonforvalterConsumer.lagrePensjonsavtale(pensjonsavtaleRequest);
                        }));
    }

    private Flux<PensjonforvalterResponse> lagreAfpOffentlig(PensjonData pensjonData, String ident, Set<String> miljoer) {

        return Flux.just(pensjonData)
                .filter(PensjonData::hasAfpOffentlig)
                .map(PensjonData::getAfpOffentlig)
                .flatMap(pensjon -> Flux.fromIterable(miljoer)
                        .flatMap(miljoe -> {

                            var context = MappingContextUtils.getMappingContext();
                            context.setProperty(IDENT, ident);
                            var request = mapperFacade.map(pensjon, AfpOffentligRequest.class, context);
                            return pensjonforvalterConsumer.lagreAfpOffentlig(request, ident, miljoe);
                        }));
    }

    public static PensjonforvalterResponse mergePensjonforvalterResponses(List<PensjonforvalterResponse> responser) {

        var status = new HashMap<String, PensjonforvalterResponse.Response>();
        responser.forEach(respons -> respons.getStatus()
                .forEach(detalj -> {
                    if (detalj.getResponse().isResponse2xx()) {
                        status.putIfAbsent(detalj.getMiljo(), detalj.getResponse());
                    } else {
                        status.put(detalj.getMiljo(), detalj.getResponse());
                    }
                }));

        return PensjonforvalterResponse.builder()
                .status(status.entrySet().stream()
                        .map(detalj -> PensjonforvalterResponse.ResponseEnvironment.builder()
                                .miljo(detalj.getKey())
                                .response(detalj.getValue())
                                .build())
                        .toList())
                .build();
    }
}
