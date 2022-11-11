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
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.pensjon.PensjonData;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.service.DollyPersonCache;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.joining;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.encodeStatus;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getVarsel;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@Order(7)
@RequiredArgsConstructor
public class PensjonforvalterClient implements ClientRegister {

    private static final String PENSJON_FORVALTER = "PensjonForvalter#";
    private static final String POPP_INNTEKTSREGISTER = "PoppInntekt#";
    private static final String TP_FORHOLD = "TpForhold#";

    private final PensjonforvalterConsumer pensjonforvalterConsumer;
    private final DollyPersonCache dollyPersonCache;
    private final MapperFacade mapperFacade;

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

        var status = new StringBuilder()
                .append(PENSJON_FORVALTER);

        if (!dollyPerson.isOpprettetIPDL()) {
            status.append(bestilling.getEnvironments().stream()
                    .map(miljo -> String.format("%s:%s", miljo, encodeStatus(getVarsel("PESYS"))))
                    .collect(Collectors.joining(",")));

        } else if (nonNull(bestilling.getPensjonforvalter()) && bestilteMiljoer.isEmpty()) {
            status.append("Feil= Bestilling ble ikke sendt til Pensjonsforvalter (PEN) da tilgjengelig(e) miljø(er) [")
                    .append(tilgjengeligeMiljoer.stream().collect(joining(",")))
                    .append("] ikke er valgt");

        } else {
            dollyPersonCache.fetchIfEmpty(dollyPerson);
            status.append(String.join("$",
                    Objects.requireNonNull(pensjonforvalterConsumer.getAccessToken()
                            .flatMapMany(token -> Flux.fromIterable(dollyPerson.getPersondetaljer())
                                    .flatMap(person -> Flux.concat(pensjonforvalterConsumer.opprettPerson(mapperFacade.map(person, OpprettPersonRequest.class), tilgjengeligeMiljoer, token)
                                                    .filter(response -> dollyPerson.getHovedperson().equals(person.getIdent()))
                                                    .map(response -> decodeStatus(response, person.getIdent())),
                                            lagreInntekt(bestilling.getPensjonforvalter(), dollyPerson, bestilteMiljoer, token)
                                                    .map(response -> POPP_INNTEKTSREGISTER + decodeStatus(response, person.getIdent())),
                                            lagreTpForhold(bestilling.getPensjonforvalter(), dollyPerson, bestilteMiljoer, token)
                                                    .map(response -> TP_FORHOLD + decodeStatus(response, person.getIdent())))))
                            .collectList()
                            .block())));
        }

        progress.setPensjonforvalterStatus(status.toString());
        return Flux.just();
    }

    @Override
    public void release(List<String> identer) {

        // Pensjonforvalter / POPP støtter pt ikke sletting

        pensjonforvalterConsumer.sletteTpForhold(identer);
    }

    @Override
    public boolean isDone(RsDollyBestilling kriterier, Bestilling bestilling) {

        return isNull(kriterier.getPensjonforvalter()) ||
                bestilling.getProgresser().stream()
                        .allMatch(entry -> isNotBlank(entry.getPensjonforvalterStatus()));
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

        return nonNull(pensjonData) ?
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

        var pensjonStatus = new StringBuilder();
        response.getStatus().forEach(status ->
                pensjonStatus.append(status.getMiljo()).append(':')
                        .append(status.getResponse().getHttpStatus().getStatus() == 200 ? "OK" :
                                "Feil= " + encodeStatus(status.getResponse().getMessage()))
                        .append(',')
        );

        return pensjonStatus.toString();
    }
}
