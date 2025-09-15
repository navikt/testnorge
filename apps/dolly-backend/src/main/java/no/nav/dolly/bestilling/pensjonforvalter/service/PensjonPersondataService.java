package no.nav.dolly.bestilling.pensjonforvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.pensjonforvalter.PensjonforvalterConsumer;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonPersonRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonSamboerRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonSamboerResponse;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonSivilstandWrapper;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonVedtakResponse;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.dolly.bestilling.pensjonforvalter.domain.RevurderingVedtakRequest;
import no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterHelper;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.SystemTyper;
import no.nav.testnav.libs.data.pdlforvalter.v1.SivilstandDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils.IDENT;
import static no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils.NAV_ENHET;
import static no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils.PENSJON_FORVALTER;
import static no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils.PEN_REVURDERING_AP;
import static no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils.SAMBOER_REGISTER;
import static no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils.getPeriodeId;

@Slf4j
@Service
@RequiredArgsConstructor
public class PensjonPersondataService {

    private static final String SIVILSTAND = "sivilstand";

    private final PensjonforvalterConsumer pensjonforvalterConsumer;
    private final PdlDataConsumer pdlDataConsumer;
    private final PensjonforvalterHelper pensjonforvalterHelper;
    private final MapperFacade mapperFacade;

    public Flux<String> lagrePersondata(String ident, List<PdlPersonBolk.PersonBolk> persondata,
                                        RsDollyBestilling bestilling, String navEnhet, Set<String> miljoer) {

        return Flux.merge(
//                Flux.concat(
                        opprettPersoner(ident, miljoer, persondata)
                                .map(response -> PENSJON_FORVALTER + pensjonforvalterHelper.decodeStatus(response, ident)),
//                        revurderingVedNySivilstand(ident, miljoer, persondata, bestilling, navEnhet)
//                                .map(response -> PEN_REVURDERING_AP + pensjonforvalterHelper.decodeStatus(response, ident))),
                lagreSamboer(ident, miljoer)
                        .map(response -> SAMBOER_REGISTER + pensjonforvalterHelper.decodeStatus(response, ident))
        );
    }

    private Flux<PensjonforvalterResponse> opprettPersoner(String hovedperson, Set<String> miljoer,
                                                           List<PdlPersonBolk.PersonBolk> personer) {

        return Flux.fromIterable(personer)
                .map(person -> mapperFacade.map(person, PensjonPersonRequest.class))
                .flatMap(request -> pensjonforvalterConsumer.opprettPerson(request, miljoer)
                        .filter(response -> hovedperson.equals(request.getFnr())));
    }

    private Flux<PensjonforvalterResponse> lagreSamboer(String ident, Set<String> tilgjengeligeMiljoer) {

        return Flux.concat(annulerAlleSamboere(ident, tilgjengeligeMiljoer),
                pdlDataConsumer.getPersoner(List.of(ident))
                        .map(hovedperson -> {
                            var context = new MappingContext.Factory().getContext();
                            context.setProperty(IDENT, ident);
                            return (List<PensjonSamboerRequest>) mapperFacade.map(PensjonSivilstandWrapper.builder()
                                    .sivilstander(hovedperson.getPerson().getSivilstand())
                                    .build(), List.class, context);
                        })
                        .flatMap(Flux::fromIterable)
                        .flatMap(request -> Flux.fromIterable(tilgjengeligeMiljoer)
                                .flatMap(miljoe -> pensjonforvalterConsumer.lagreSamboer(request, miljoe))
                                .filter(response -> request.getPidBruker().equals(ident))));
    }

    private Flux<PensjonforvalterResponse> annulerAlleSamboere(String ident, Set<String> tilgjengeligeMiljoer) {

        return Flux.fromIterable(tilgjengeligeMiljoer)
                .flatMap(miljoe -> pensjonforvalterConsumer.hentSamboer(ident, miljoe)
                        .flatMap(response -> Flux.merge(Flux.just(response), Flux.fromIterable(response.getSamboerforhold())
                                        .map(PensjonSamboerResponse.Samboerforhold::getPidSamboer)
                                        .flatMap(identSamboer -> pensjonforvalterConsumer.hentSamboer(identSamboer, miljoe)))
                                .flatMap(samboerResponse -> Flux.fromIterable(samboerResponse.getSamboerforhold())
                                        .flatMap(samboer -> pensjonforvalterConsumer.annullerSamboer(
                                                        getPeriodeId(samboer.get_links().getAnnuller().getHref()), miljoe)
                                                .filter(response1 -> samboer.getPidBruker().equals(ident) &&
                                                        response1.getStatus().stream()
                                                                .noneMatch(status -> status.getResponse().getHttpStatus().getStatus() == 200))))));
    }

    private Flux<PensjonforvalterResponse> revurderingVedNySivilstand(String ident, Set<String> miljoer,
                                                                      List<PdlPersonBolk.PersonBolk> persondata,
                                                                      RsDollyBestilling bestilling, String navEnhetId) {

        if (isNull(bestilling.getPdldata()) || isNull(bestilling.getPdldata().getPerson()) ||
                bestilling.getPdldata().getPerson().getSivilstand().stream().noneMatch(SivilstandDTO::isGift)) {
            return Flux.empty();
        }

        return Flux.empty();
//        return Flux.fromIterable(miljoer)
//                .flatMap(miljoe -> pensjonforvalterHelper.hentSisteVedtakAPHvisOK(ident, miljoe)
//                        .flatMapMany(vedtak ->
//                                pensjonforvalterHelper.hentTransaksjonMappingAP(ident, miljoe)
//                                        .map(transaksjonMapping -> {
//                                            var context = new MappingContext.Factory().getContext();
//                                            context.setProperty(NAV_ENHET, navEnhetId);
//                                            context.setProperty(SIVILSTAND, persondata.stream()
//                                                    .filter(personBolk -> personBolk.getIdent().equals(ident))
//                                                    .map(PdlPersonBolk.PersonBolk::getPerson)
//                                                    .map(PdlPerson.Person::getSivilstand)
//                                                    .findFirst().orElse(List.of(new PdlPerson.Sivilstand())));
//                                            return mapperFacade.map(transaksjonMapping, RevurderingVedtakRequest.class, context);
//                                        })
//                                        .flatMap(request -> {
//                                            if (nonNull(request.getFom()) &&
//                                                    request.getFom().isAfter(vedtak.getFom())) {
//                                                return pensjonforvalterConsumer.lagreRevurderingVedtak(request)
//                                                        .flatMap(response ->
//                                                                pensjonforvalterHelper.saveTransaksjonId(ident, miljoe,
//                                                                                bestilling.getId(), SystemTyper.PEN_AP_REVURDERING, request)
//                                                                        .thenReturn(response));
//                                            } else {
//                                                return miscRevurderingResponse(request, vedtak);
//                                            }
//                                        })
//                        ));
    }

    private Mono<PensjonforvalterResponse> miscRevurderingResponse(RevurderingVedtakRequest request, PensjonVedtakResponse response) {

        String message;
        if (isNull(request.getFom())) {
            message = "Automatisk revurderingsvedtak ikke mulig når dato for sivilstandsendring mangler.";

        } else if (request.getFom().isBefore(response.getFom())) {
            message = "Automatisk revurderingsvedtak kan ikke settes tidligere enn et eksisterende vedtak.";

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
                        .build()))
                .build());
    }
}
