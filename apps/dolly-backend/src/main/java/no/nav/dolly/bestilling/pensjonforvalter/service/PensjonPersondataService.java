package no.nav.dolly.bestilling.pensjonforvalter.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.pensjonforvalter.PensjonforvalterConsumer;
import no.nav.dolly.bestilling.pensjonforvalter.domain.AlderspensjonVedtakRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonPersonRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonSamboerRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonSamboerResponse;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonSivilstandWrapper;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.dolly.bestilling.pensjonforvalter.domain.RevurderingVedtakRequest;
import no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterHelper;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.resultset.SystemTyper;
import no.nav.dolly.domain.resultset.pdldata.PdlPersondata;
import no.nav.dolly.service.TransaksjonMappingService;
import no.nav.testnav.libs.data.pdlforvalter.v1.SivilstandDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils.IDENT;
import static no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils.PENSJON_FORVALTER;
import static no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils.PEN_REVURDERING_AP;
import static no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils.SAMBOER_REGISTER;
import static no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils.getPeriodeId;

@Slf4j
@Service
@RequiredArgsConstructor
public class PensjonPersondataService {

    private static final String NAV_ENHET = "navEnhetId";
    private static final String SIVILSTAND = "sivilstand";

    private final PensjonforvalterConsumer pensjonforvalterConsumer;
    private final PdlDataConsumer pdlDataConsumer;
    private final PensjonforvalterHelper pensjonforvalterHelper;
    private final MapperFacade mapperFacade;
    private final TransaksjonMappingService transaksjonMappingService;
    private final ObjectMapper objectMapper;

    public Flux<String> lagrePersondata(String ident, List<PdlPersonBolk.PersonBolk> persondata,
                                        PdlPersondata pdlData, String navEnhet, Set<String> miljoer) {

        return Flux.merge(
                Flux.concat(
                        opprettPersoner(ident, miljoer, persondata)
                                .map(response -> PENSJON_FORVALTER + pensjonforvalterHelper.decodeStatus(response, ident)),
                        revurderingVedNySivilstand(ident, miljoer, pdlData, navEnhet)
                                .map(response -> PEN_REVURDERING_AP + pensjonforvalterHelper.decodeStatus(response, ident))),
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
                                                                      PdlPersondata pdlPersondata, String navEnhetId) {

        if (isNull(pdlPersondata) || isNull(pdlPersondata.getPerson()) ||
                pdlPersondata.getPerson().getSivilstand().stream().noneMatch(SivilstandDTO::isGift)) {
            return Flux.empty();
        }

        return transaksjonMappingService.getTransaksjonMapping(SystemTyper.PEN_AP.name(), ident)
                .map(mapping -> {
                    try {
                        return objectMapper.readValue(mapping.getTransaksjonId(), AlderspensjonVedtakRequest.class);
                    } catch (JsonProcessingException e) {
                        log.error("Feil ved deserialisering av transaksjonId", e);
                        return basicAlderspensjonRequest(ident, miljoer);
                    }
                })
                .map(transaksjonMapping -> {

                    var context = new MappingContext.Factory().getContext();
                    context.setProperty(NAV_ENHET, navEnhetId);
                    context.setProperty(SIVILSTAND, pdlPersondata.getPerson().getSivilstand());
                    return mapperFacade.map(transaksjonMapping, RevurderingVedtakRequest.class, context);
                })
                .flatMap(pensjonforvalterConsumer::lagreRevurderingVedtak);
    }

    private AlderspensjonVedtakRequest basicAlderspensjonRequest(String ident, Set<String> miljoer) {

        return AlderspensjonVedtakRequest.builder()
                .fnr(ident)
                .miljoer(miljoer)
                .build();
    }
}
