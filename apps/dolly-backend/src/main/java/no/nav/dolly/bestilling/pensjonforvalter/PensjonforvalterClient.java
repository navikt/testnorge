package no.nav.dolly.bestilling.pensjonforvalter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pensjonforvalter.domain.LagreInntektRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.LagreTpForholdRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.LagreTpYtelseRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.OpprettPersonRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.dolly.bestilling.personservice.PersonServiceConsumer;
import no.nav.dolly.consumer.pdlperson.PdlPersonConsumer;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.pensjon.PensjonData;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.encodeStatus;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getInfoVenter;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getVarselSlutt;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getVenterTekst;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@Order(7)
@RequiredArgsConstructor
public class PensjonforvalterClient implements ClientRegister {

    private static final String SYSTEM = "PESYS";
    private static final String PENSJON_FORVALTER = "PensjonForvalter#";
    private static final String POPP_INNTEKTSREGISTER = "PoppInntekt#";
    private static final String TP_FORHOLD = "TpForhold#";

    private final PensjonforvalterConsumer pensjonforvalterConsumer;
    private final MapperFacade mapperFacade;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final TransactionHelperService transactionHelperService;
    private final PersonServiceConsumer personServiceConsumer;
    private final PdlPersonConsumer pdlPersonConsumer;

    public static PensjonforvalterResponse mergePensjonforvalterResponses(List<PensjonforvalterResponse> responser) {

        var status = new HashMap<String, PensjonforvalterResponse.Response>();
        responser.stream()
                .forEach(respons -> respons.getStatus().stream()
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

    @Override
    public Flux<Void> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        var bestilteMiljoer = new HashSet<>(bestilling.getEnvironments());
        var tilgjengeligeMiljoer = pensjonforvalterConsumer.getMiljoer();
        bestilteMiljoer.retainAll(tilgjengeligeMiljoer);

        progress.setPensjonforvalterStatus(PENSJON_FORVALTER +
                tilgjengeligeMiljoer.stream()
                        .map(miljo -> String.format("%s:%s", miljo, encodeStatus(getInfoVenter(SYSTEM))))
                        .collect(Collectors.joining(",")));
        transactionHelperService.persister(progress);

        personServiceConsumer.getPdlSyncReady(dollyPerson.getHovedperson())
                .flatMap(isPresent -> {
                    if (isPresent) {
                        return pensjonforvalterConsumer.getAccessToken()
                                .flatMapMany(token -> getIdenterFamilie(dollyPerson.getHovedperson())
                                        .map(this::getPersonData)
                                        .flatMap(Flux::from)
                                        .map(person -> Flux.concat(pensjonforvalterConsumer.opprettPerson(
                                                                mapperFacade.map(person, OpprettPersonRequest.class), tilgjengeligeMiljoer, token)
                                                        .filter(response -> dollyPerson.getHovedperson().equals(person.getIdent()))
                                                        .map(response -> PENSJON_FORVALTER + decodeStatus(response, person.getIdent())),
                                                (dollyPerson.getHovedperson().equals(person.getIdent()) ?
                                                        lagreInntekt(bestilling.getPensjonforvalter(), dollyPerson, bestilteMiljoer, token)
                                                                .map(response -> POPP_INNTEKTSREGISTER + decodeStatus(response, person.getIdent())) :
                                                        Flux.just("")),
                                                (dollyPerson.getHovedperson().equals(person.getIdent()) ?
                                                        lagreTpForhold(bestilling.getPensjonforvalter(), dollyPerson, bestilteMiljoer, token)
                                                                .map(response -> TP_FORHOLD + decodeStatus(response, person.getIdent())) :
                                                        Flux.just(""))))
                                        .flatMap(Flux::from))
                                .filter(StringUtils::isNotBlank)
                                .collect(Collectors.joining("$"));
                    } else {
                        return Mono.just(tilgjengeligeMiljoer.stream()
                                .map(miljo -> String.format("%s:%s", miljo, encodeStatus(getVarselSlutt(SYSTEM))))
                                .collect(Collectors.joining(",")));
                    }
                })
                .subscribe(response -> {
                    progress.setPensjonforvalterStatus(response);
                    transactionHelperService.persister(progress);
                });

        return Flux.just();
    }

    private Flux<List<String>> getIdenterFamilie(String ident) {

        return getPersonData(List.of(ident))
                .map(person -> Stream.of(List.of(person.getIdent()),
                                person.getPerson().getSivilstand().stream()
                                        .map(PdlPerson.Sivilstand::getRelatertVedSivilstand)
                                        .filter(Objects::nonNull)
                                        .toList(),
                                person.getPerson().getForelderBarnRelasjon().stream()
                                        .map(PdlPerson.ForelderBarnRelasjon::getRelatertPersonsIdent)
                                        .filter(Objects::nonNull)
                                        .toList())
                        .flatMap(Collection::stream)
                        .distinct()
                        .toList());
    }

    private Flux<PdlPersonBolk.PersonBolk> getPersonData(List<String> identer) {

        return pdlPersonConsumer.getPdlPersoner(identer)
                .filter(pdlPersonBolk -> nonNull(pdlPersonBolk.getData()))
                .map(PdlPersonBolk::getData)
                .map(PdlPersonBolk.Data::getHentPersonBolk)
                .flatMap(Flux::fromIterable)
                .filter(personBolk -> nonNull(personBolk.getPerson()));
    }

    @Override
    public void release(List<String> identer) {

        // Pensjonforvalter / POPP støtter pt ikke sletting

        pensjonforvalterConsumer.sletteTpForhold(identer);
    }

    @Override
    public boolean isDone(RsDollyBestilling kriterier, Bestilling bestilling) {

        return bestilling.getProgresser().stream()
                .allMatch(entry -> isNotBlank(entry.getPensjonforvalterStatus()) &&
                        !entry.getPensjonforvalterStatus().contains(getVenterTekst()));
    }

    private Flux<PensjonforvalterResponse> lagreInntekt(PensjonData pensjonData, DollyPerson dollyPerson, Set<String> miljoer, AccessToken token) {

        if (nonNull(pensjonData) && nonNull(pensjonData.getInntekt())) {
            LagreInntektRequest lagreInntektRequest = mapperFacade.map(pensjonData.getInntekt(), LagreInntektRequest.class);
            lagreInntektRequest.setFnr(dollyPerson.getHovedperson());
            lagreInntektRequest.setMiljoer(new ArrayList<>(miljoer));

            return pensjonforvalterConsumer.lagreInntekt(lagreInntektRequest, token);

        } else {
            return Flux.empty();
        }
    }

    private Mono<PensjonforvalterResponse> lagreTpForhold(PensjonData pensjonData, DollyPerson dollyPerson, Set<String> miljoer, AccessToken token) {

        return nonNull(pensjonData) && !pensjonData.getTp().isEmpty() ?
                Flux.merge(
                                Flux.fromIterable(pensjonData.getTp())
                                        .map(tp -> {
                                            var lagreTpForholdRequest = mapperFacade.map(tp, LagreTpForholdRequest.class);
                                            lagreTpForholdRequest.setFnr(dollyPerson.getHovedperson());
                                            lagreTpForholdRequest.setMiljoer(miljoer);
                                            return pensjonforvalterConsumer.lagreTpForhold(lagreTpForholdRequest, token);
                                        }),
                                Flux.fromIterable(pensjonData.getTp())
                                        .map(tp -> Flux.fromIterable(tp.getYtelser())
                                                .flatMap(ytelse -> {
                                                    LagreTpYtelseRequest lagreTpYtelseRequest = mapperFacade.map(ytelse, LagreTpYtelseRequest.class);
                                                    lagreTpYtelseRequest.setYtelseType(ytelse.getType());
                                                    lagreTpYtelseRequest.setOrdning(tp.getOrdning());
                                                    lagreTpYtelseRequest.setFnr(dollyPerson.getHovedperson());
                                                    lagreTpYtelseRequest.setMiljoer(miljoer);
                                                    return pensjonforvalterConsumer.lagreTpYtelse(lagreTpYtelseRequest, token);
                                                })))
                        .flatMap(Flux::from)
                        .collectList()
                        .map(PensjonforvalterClient::mergePensjonforvalterResponses) :

                Mono.empty();
    }

    private String decodeStatus(PensjonforvalterResponse response, String ident) {

        log.info("Mottatt status på {} fra Pensjon-Testdata-Facade: {}", ident, response);

        return response.getStatus().stream()
                .map(entry -> String.format("%s:%s", entry.getMiljo(),
                        entry.getResponse().getHttpStatus().getStatus() == 200 ? "OK" :
                                encodeStatus(errorStatusDecoder.getErrorText(
                                        HttpStatus.valueOf(entry.getResponse().getHttpStatus().getStatus()),
                                        entry.getResponse().getMessage()))))
                .collect(Collectors.joining(","));
    }
}
