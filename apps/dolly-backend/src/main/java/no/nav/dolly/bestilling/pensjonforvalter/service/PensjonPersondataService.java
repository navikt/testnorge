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
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterHelper;
import no.nav.dolly.domain.PdlPersonBolk;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Set;

import static no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils.IDENT;
import static no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils.PENSJON_FORVALTER;
import static no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils.SAMBOER_REGISTER;
import static no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils.getPeriodeId;

@Slf4j
@Service
@RequiredArgsConstructor
public class PensjonPersondataService {

    private final PensjonforvalterConsumer pensjonforvalterConsumer;
    private final PdlDataConsumer pdlDataConsumer;
    private final PensjonforvalterHelper pensjonforvalterHelper;
    private final MapperFacade mapperFacade;

    public Flux<String> lagrePersondata(String ident, List<PdlPersonBolk.PersonBolk> persondata, Set<String> miljoer) {

        return Flux.merge(
                opprettPersoner(ident, miljoer, persondata)
                        .map(response -> PENSJON_FORVALTER + pensjonforvalterHelper.decodeStatus(response, ident)),
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
}
