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
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.pensjon.PensjonData;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.service.DollyPersonCache;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.joining;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.encodeStatus;

@Slf4j
@Service
@RequiredArgsConstructor
public class PensjonforvalterClient implements ClientRegister {

    public static final String PENSJON_FORVALTER = "PensjonForvalter";
    public static final String POPP_INNTEKTSREGISTER = "PoppInntekt";
    public static final String TP_FORHOLD = "TpForhold";

    private final PensjonforvalterConsumer pensjonforvalterConsumer;
    private final DollyPersonCache dollyPersonCache;
    private final MapperFacade mapperFacade;
    private final ErrorStatusDecoder errorStatusDecoder;

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

            Set<String> bestilteMiljoer = new HashSet<>(bestilling.getEnvironments());
            Set<String> tilgjengeligeMiljoer = pensjonforvalterConsumer.getMiljoer();
            bestilteMiljoer.retainAll(tilgjengeligeMiljoer);

            StringBuilder status = new StringBuilder();

            if (!bestilteMiljoer.isEmpty()) {
                opprettPerson(dollyPerson, bestilteMiljoer, status);

                if (nonNull(bestilling.getPensjonforvalter()) && nonNull(bestilling.getPensjonforvalter().getInntekt())) {
                    lagreInntekt(bestilling.getPensjonforvalter(), dollyPerson, bestilteMiljoer, status);
                }

                if (nonNull(bestilling.getPensjonforvalter()) && nonNull(bestilling.getPensjonforvalter().getTp())) {
                    lagreTpForhold(bestilling.getPensjonforvalter(), dollyPerson, bestilteMiljoer, status);
                }

            } else if (nonNull(bestilling.getPensjonforvalter())) {
                status.append('$')
                        .append(PENSJON_FORVALTER)
                        .append("#Feil= Bestilling ble ikke sendt til Pensjonsforvalter (PEN) da tilgjengelig(e) miljø(er) [")
                        .append(tilgjengeligeMiljoer.stream().collect(joining(",")))
                        .append("] ikke er valgt");
            }
            if (status.length() > 1) {
                progress.setPensjonforvalterStatus(status.substring(1));
            }
    }

    @Override
    public void release(List<String> identer) {

        // Pensjonforvalter / POPP støtter pt ikke sletting
    }

    private void opprettPerson(DollyPerson dollyPerson, Set<String> miljoer, StringBuilder status) {

        status.append('$').append(PENSJON_FORVALTER).append('#');

        try {
            dollyPersonCache.fetchIfEmpty(dollyPerson);
            dollyPerson.getPersondetaljer().forEach(person -> {
                var opprettPersonRequest =
                        mapperFacade.map(person, OpprettPersonRequest.class);
                opprettPersonRequest.setMiljoer(new ArrayList<>(miljoer));
                log.info("Persondata til pensjon: " + opprettPersonRequest.toString());
                var response = pensjonforvalterConsumer.opprettPerson(opprettPersonRequest);
                if (dollyPerson.getHovedperson().equals(person.getIdent())) {
                    decodeStatus(response, status);
                }
            });
        } catch (RuntimeException e) {

            status.append(errorStatusDecoder.decodeRuntimeException(e));
        }
    }

    private void lagreInntekt(PensjonData pensjonData, DollyPerson dollyPerson, Set<String> miljoer, StringBuilder status) {

        status.append('$').append(POPP_INNTEKTSREGISTER).append('#');

        try {
            LagreInntektRequest lagreInntektRequest = mapperFacade.map(pensjonData.getInntekt(), LagreInntektRequest.class);
            lagreInntektRequest.setFnr(dollyPerson.getHovedperson());
            lagreInntektRequest.setMiljoer(new ArrayList<>(miljoer));

            decodeStatus(pensjonforvalterConsumer.lagreInntekt(lagreInntektRequest), status);

        } catch (RuntimeException e) {

            status.append(errorStatusDecoder.decodeRuntimeException(e));
        }
    }

    private void lagreTpForhold(PensjonData pensjonData, DollyPerson dollyPerson, Set<String> miljoer, StringBuilder status) {
        List<String> exceptions = new ArrayList<>();
        status.append('$').append(TP_FORHOLD).append('#');
        PensjonforvalterResponse response = new PensjonforvalterResponse();

            pensjonData.getTp()
                    .stream()
                    .forEach(tp -> {
                        try {
                            LagreTpForholdRequest lagreTpForholdRequest = mapperFacade.map(tp, LagreTpForholdRequest.class);
                            lagreTpForholdRequest.setFnr(dollyPerson.getHovedperson());
                            lagreTpForholdRequest.setMiljoer(new ArrayList<>(miljoer));

                            var forholdResponse = pensjonforvalterConsumer.lagreTpForhold(lagreTpForholdRequest);
                            mergePensjonforvalterResponses(forholdResponse, response);

                            if (nonNull(tp.getYtelser()) && !tp.getYtelser().isEmpty()) {
                                var ytelseResponse = lagreTpYtelse(dollyPerson.getHovedperson(), tp.getOrdning(), tp.getYtelser(), miljoer);
                                mergePensjonforvalterResponses(ytelseResponse, response);
                            }
                        } catch (RuntimeException e) {
                            exceptions.add(errorStatusDecoder.decodeRuntimeException(e));
                        }
                    });

            if (exceptions.isEmpty()) {
                decodeStatus(response, status);
            } else {
                status.append(exceptions.get(0));
            }

    }

    private PensjonforvalterResponse lagreTpYtelse(String person, String ordning, List<PensjonData.TpYtelse> ytelser, Set<String> miljoer) {
        PensjonforvalterResponse response = new PensjonforvalterResponse();

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

    private static boolean isResponse2xx(PensjonforvalterResponse.Response status) {
        return status.getHttpStatus().getStatus() >= 200 && status.getHttpStatus().getStatus() < 300;
    }

    public static void mergePensjonforvalterResponses(PensjonforvalterResponse response, PensjonforvalterResponse responseTil) {
        if (response.getStatus() != null) {
            response.getStatus().forEach(status -> {
                var miljo = status.getMiljo();
                var miljoResponse = status.getResponse();

                if (responseTil.getStatus() == null) {
                    responseTil.setStatus(new ArrayList<>());
                }

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
    }

    private void decodeStatus(PensjonforvalterResponse response, StringBuilder pensjonStatus) {

        response.getStatus().forEach(status ->
                pensjonStatus.append(status.getMiljo()).append(':')
                        .append(status.getResponse().getHttpStatus().getStatus() == 200 ? "OK" :
                                "Feil= " + encodeStatus(status.getResponse().getMessage()))
                        .append(',')
        );
    }
}
