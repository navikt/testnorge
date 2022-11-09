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
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

    public static final String PENSJON_FORVALTER = "PensjonForvalter";
    public static final String POPP_INNTEKTSREGISTER = "PoppInntekt";
    public static final String TP_FORHOLD = "TpForhold";

    private final PensjonforvalterConsumer pensjonforvalterConsumer;
    private final DollyPersonCache dollyPersonCache;
    private final MapperFacade mapperFacade;

    private static boolean isResponse2xx(PensjonforvalterResponse.Response status) {
        return status.getHttpStatus().getStatus() >= 200 && status.getHttpStatus().getStatus() < 300;
    }

    public static void mergePensjonforvalterResponses(PensjonforvalterResponse response, PensjonforvalterResponse responseTil) {

        response.getStatus().forEach(status -> {

            var miljo = status.getMiljo();
            var miljoResponse = status.getResponse();

            var mergingTilMiljo = responseTil.getStatus().stream()
                    .filter(s -> s.getMiljo().equalsIgnoreCase(miljo))
                    .findFirst();

            if (mergingTilMiljo.isPresent()) {
                if (isResponse2xx(mergingTilMiljo.get().getResponse())) {
                    mergingTilMiljo.get().setResponse(miljoResponse);
                }
            } else {
                // det var ingen miljø response tidligere
                responseTil.getStatus().add(status);
            }
        });
    }

    @Override
    public Flux<Void> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        var bestilteMiljoer = new HashSet<>(bestilling.getEnvironments());
        var tilgjengeligeMiljoer = pensjonforvalterConsumer.getMiljoer();
        bestilteMiljoer.retainAll(tilgjengeligeMiljoer);

        var status = new StringBuilder()
                .append('$').append(PENSJON_FORVALTER).append('#');

        if (!dollyPerson.isOpprettetIPDL()) {
            status.append(bestilling.getEnvironments().stream()
                    .map(miljo -> String.format("%s:%s", miljo, encodeStatus(getVarsel("PESYS"))))
                    .collect(Collectors.joining(",")));

        } else {
            opprettPerson(dollyPerson, tilgjengeligeMiljoer, status);
            if (!bestilteMiljoer.isEmpty() && nonNull(bestilling.getPensjonforvalter())) {

                if (nonNull(bestilling.getPensjonforvalter().getInntekt())) {
                    lagreInntekt(bestilling.getPensjonforvalter(), dollyPerson, bestilteMiljoer, status);
                }

                if (nonNull(bestilling.getPensjonforvalter().getTp())) {
                    lagreTpForhold(bestilling.getPensjonforvalter(), dollyPerson, bestilteMiljoer, status);
                }

            } else if (nonNull(bestilling.getPensjonforvalter())) {
                status.append('$')
                        .append(PENSJON_FORVALTER)
                        .append("#Feil= Bestilling ble ikke sendt til Pensjonsforvalter (PEN) da tilgjengelig(e) miljø(er) [")
                        .append(tilgjengeligeMiljoer.stream().collect(joining(",")))
                        .append("] ikke er valgt");
            }
        }

        progress.setPensjonforvalterStatus(status.substring(1));
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

    private void opprettPerson(DollyPerson dollyPerson, Set<String> miljoer, StringBuilder status) {

        dollyPersonCache.fetchIfEmpty(dollyPerson);
        dollyPerson.getPersondetaljer().forEach(person -> {
            var opprettPersonRequest =
                    mapperFacade.map(person, OpprettPersonRequest.class);
            opprettPersonRequest.setMiljoer(new ArrayList<>(miljoer));
            log.info("Persondata til pensjon: " + opprettPersonRequest.toString());
            var response = pensjonforvalterConsumer.opprettPerson(opprettPersonRequest);
            if (dollyPerson.getHovedperson().equals(person.getIdent())) {
                decodeStatus(response, person.getIdent(), status);
            }
        });
    }

    private void lagreInntekt(PensjonData pensjonData, DollyPerson dollyPerson, Set<String> miljoer, StringBuilder status) {

        status.append('$').append(POPP_INNTEKTSREGISTER).append('#');

        LagreInntektRequest lagreInntektRequest = mapperFacade.map(pensjonData.getInntekt(), LagreInntektRequest.class);
        lagreInntektRequest.setFnr(dollyPerson.getHovedperson());
        lagreInntektRequest.setMiljoer(new ArrayList<>(miljoer));

        decodeStatus(pensjonforvalterConsumer.lagreInntekt(lagreInntektRequest), dollyPerson.getHovedperson(), status);
    }

    private void lagreTpForhold(PensjonData pensjonData, DollyPerson dollyPerson, Set<String> miljoer, StringBuilder status) {

        status.append('$').append(TP_FORHOLD).append('#');
        var response = new PensjonforvalterResponse();

        pensjonData.getTp()
                .forEach(tp -> {
                    LagreTpForholdRequest lagreTpForholdRequest = mapperFacade.map(tp, LagreTpForholdRequest.class);
                    lagreTpForholdRequest.setFnr(dollyPerson.getHovedperson());
                    lagreTpForholdRequest.setMiljoer(new ArrayList<>(miljoer));

                    var forholdResponse = pensjonforvalterConsumer.lagreTpForhold(lagreTpForholdRequest);
                    mergePensjonforvalterResponses(forholdResponse, response);

                    if (!tp.getYtelser().isEmpty()) {
                        var ytelseResponse = lagreTpYtelse(dollyPerson.getHovedperson(), tp.getOrdning(), tp.getYtelser(), miljoer);
                        mergePensjonforvalterResponses(ytelseResponse, response);
                    }
                });


        decodeStatus(response, dollyPerson.getHovedperson(), status);
    }

    private PensjonforvalterResponse lagreTpYtelse(String person, String ordning, List<PensjonData.TpYtelse> ytelser, Set<String> miljoer) {

        var response = new PensjonforvalterResponse();

        ytelser.stream().forEach(ytelse -> {
            LagreTpYtelseRequest lagreTpYtelseRequest = mapperFacade.map(ytelse, LagreTpYtelseRequest.class);
            lagreTpYtelseRequest.setYtelseType(ytelse.getType());
            lagreTpYtelseRequest.setOrdning(ordning);
            lagreTpYtelseRequest.setFnr(person);
            lagreTpYtelseRequest.setMiljoer(new ArrayList<>(miljoer));

            var ytelseResponse = pensjonforvalterConsumer.lagreTpYtelse(lagreTpYtelseRequest);
            mergePensjonforvalterResponses(ytelseResponse, response);
        });

        return response;
    }

    private void decodeStatus(PensjonforvalterResponse response, String ident, StringBuilder pensjonStatus) {

        log.info("Mottatt status på {} fra Pensjon-Testdata-Facade: {}", ident, response);
        response.getStatus().forEach(status ->
                pensjonStatus.append(status.getMiljo()).append(':')
                        .append(status.getResponse().getHttpStatus().getStatus() == 200 ? "OK" :
                                "Feil= " + encodeStatus(status.getResponse().getMessage()))
                        .append(',')
        );
    }
}
