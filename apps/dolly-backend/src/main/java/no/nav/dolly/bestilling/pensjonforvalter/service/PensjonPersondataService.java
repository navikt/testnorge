package no.nav.dolly.bestilling.pensjonforvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pensjonforvalter.PensjonforvalterConsumer;
import no.nav.dolly.bestilling.pensjonforvalter.domain.AlderspensjonVedtakDTO;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonPersonRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonSamboerRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonSamboerResponse;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonSivilstandWrapper;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.dolly.bestilling.pensjonforvalter.domain.RevurderingVedtakRequest;
import no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterHelper;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.SystemTyper;
import no.nav.dolly.service.TransaksjonMappingService;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullPersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;

import java.time.LocalDate;
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
import static org.apache.commons.lang3.BooleanUtils.isFalse;

@Slf4j
@Service
@RequiredArgsConstructor
public class PensjonPersondataService {

    private static final String SIVILSTAND = "sivilstand";

    private final PensjonforvalterConsumer pensjonforvalterConsumer;
    private final PensjonforvalterHelper pensjonforvalterHelper;
    private final MapperFacade mapperFacade;
    private final TransaksjonMappingService transaksjonMappingService;

    public Flux<String> lagrePersondata(String ident, Tuple3<List<PdlPersonBolk.PersonBolk>,
                                                List<FullPersonDTO>, String> utvidetPersondata,
                                        RsDollyBestilling bestilling, Set<String> miljoer,
                                        boolean isUpdateEndre) {

        return Flux.merge(
                Flux.concat(
                        opprettPersoner(ident, miljoer, utvidetPersondata.getT1())
                                .map(response -> PENSJON_FORVALTER + pensjonforvalterHelper.decodeStatus(response, ident)),
                        revurderingVedNySivilstand(ident, miljoer, utvidetPersondata.getT1(), bestilling, utvidetPersondata.getT3(), isUpdateEndre)
                                .map(response -> PEN_REVURDERING_AP + pensjonforvalterHelper.decodeStatus(response, ident))),
                lagreSamboer(ident, miljoer, utvidetPersondata.getT2())
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

    private Flux<PensjonforvalterResponse> lagreSamboer(String ident, Set<String> tilgjengeligeMiljoer, List<FullPersonDTO> persondata) {

        return Flux.concat(annulerAlleSamboere(ident, tilgjengeligeMiljoer),
                Flux.fromIterable(persondata)
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
                                                                      RsDollyBestilling bestilling, String navEnhetId,
                                                                      boolean isUpdateEndre) {

        if (isNull(bestilling.getPdldata()) || isNull(bestilling.getPdldata().getPerson()) ||
                bestilling.getPdldata().getPerson().getSivilstand().stream().noneMatch(SivilstandDTO::isGift)) {
            return Flux.empty();
        }

        return Flux.fromIterable(miljoer)
                .flatMap(miljoe -> transaksjonMappingService.existsByIdentAndMiljoe(ident, miljoe)
                        .zipWith(Mono.just(miljoe)))
                .flatMap(tuple -> isFalse(tuple.getT1()) ? Mono.empty() : Mono.just(tuple.getT2()))
                .flatMap(miljoe ->
                        pensjonforvalterHelper.hentForrigeVedtakAP(ident, miljoe, getSivilstandDato(bestilling))
                                .flatMap(vedtak -> {
                                    var context = new MappingContext.Factory().getContext();
                                    context.setProperty(NAV_ENHET, navEnhetId);
                                    context.setProperty(SIVILSTAND, persondata.stream()
                                            .filter(personBolk -> personBolk.getIdent().equals(ident))
                                            .map(PdlPersonBolk.PersonBolk::getPerson)
                                            .map(PdlPerson.Person::getSivilstand)
                                            .findFirst().orElse(List.of(new PdlPerson.Sivilstand())));
                                    return Mono.just(mapperFacade.map(vedtak, RevurderingVedtakRequest.class, context))
                                            .zipWith(Mono.just(vedtak));
                                })
                                .flatMapMany(tuple -> {
                                    if (isRevurderingValid(tuple.getT1(), tuple.getT2())) {
                                        return pensjonforvalterConsumer.lagreRevurderingVedtak(tuple.getT1())
                                                .flatMap(response -> isNoMatch(tuple.getT2(), tuple.getT1()) ?
                                                        pensjonforvalterHelper.saveTransaksjonId(ident, miljoe,
                                                                        bestilling.getId(), SystemTyper.PEN_AP_REVURDERING, tuple.getT1())
                                                                .thenReturn(response) :
                                                        Mono.just(response));
                                    } else {

                                        return miscRevurderingResponse(tuple.getT1(), tuple.getT2(), miljoe, isUpdateEndre);
                                    }
                                }));
    }

    private static LocalDate getSivilstandDato(RsDollyBestilling bestilling) {

        var sivilstand = bestilling.getPdldata().getPerson().getSivilstand().getFirst();
        if (nonNull(sivilstand.getSivilstandsdato())) {
            return sivilstand.getSivilstandsdato().toLocalDate();
        } else if (nonNull(sivilstand.getBekreftelsesdato())) {
            return sivilstand.getBekreftelsesdato().toLocalDate();
        } else {
            return null;
        }
    }

    private static boolean isNoMatch(AlderspensjonVedtakDTO response, RevurderingVedtakRequest request) {

        return !response.getFom().equals(request.getFom()) &&
                response.getHistorikk().stream()
                        .noneMatch(historikk ->
                                historikk.getFom().equals(request.getFom()));
    }

    private static boolean isRevurderingValid(RevurderingVedtakRequest request, AlderspensjonVedtakDTO response) {

        return nonNull(response.getFom()) && nonNull(request.getFom()) &&
                request.getFom().isAfter(response.getFom());
    }

    private static Mono<PensjonforvalterResponse> miscRevurderingResponse(RevurderingVedtakRequest request,
                                                                          AlderspensjonVedtakDTO response, String miljoe,
                                                                          boolean isUpdateEndre) {

        String message;
        if (isUpdateEndre && isNull(request.getFom())) {
            message = "Automatisk revurderingsvedtak ikke mulig når dato for sivilstandsendring mangler.";

        } else if (isUpdateEndre && nonNull(response.getFom()) && request.getFom().isBefore(response.getFom())) {
            message = "Automatisk revurderingsvedtak ikke mulig når dato for sivilstandsendring er før dato på forrige vedtak.";

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
