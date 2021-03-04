package no.nav.dolly.bestilling.pensjonforvalter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pensjonforvalter.domain.LagreInntektRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.OpprettPersonRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.pensjon.PensjonData;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.service.TpsfPersonCache;
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

    private final PensjonforvalterConsumer pensjonforvalterConsumer;
    private final TpsfPersonCache tpsfPersonCache;
    private final MapperFacade mapperFacade;
    private final ErrorStatusDecoder errorStatusDecoder;

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, TpsPerson tpsPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getPensjonforvalter())) {

            Set<String> bestilteMiljoer = new HashSet<>(bestilling.getEnvironments());
            Set<String> tilgjengeligeMiljoer = pensjonforvalterConsumer.getMiljoer();
            bestilteMiljoer.retainAll(tilgjengeligeMiljoer);

            StringBuilder status = new StringBuilder();

            if (!bestilteMiljoer.isEmpty()) {

                opprettPerson(tpsPerson, bestilteMiljoer, status);
                lagreInntekt(bestilling.getPensjonforvalter(), tpsPerson, bestilteMiljoer, status);

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
    }

    @Override
    public void release(List<String> identer) {
        // Pensjonforvalter / POPP støtter pt ikke sletting
    }

    private void opprettPerson(TpsPerson tpsPerson, Set<String> miljoer, StringBuilder status) {

        status.append('$').append(PENSJON_FORVALTER).append('#');

        try {
            tpsfPersonCache.fetchIfEmpty(tpsPerson);
            tpsPerson.getPersondetaljer().forEach(person -> {
                OpprettPersonRequest opprettPersonRequest =
                        mapperFacade.map(person, OpprettPersonRequest.class);
                opprettPersonRequest.setMiljoer(new ArrayList<>(miljoer));
                PensjonforvalterResponse response = pensjonforvalterConsumer.opprettPerson(opprettPersonRequest);
                if (tpsPerson.getHovedperson().equals(person.getIdent())) {
                    decodeStatus(response, status);
                }
            });
        } catch (RuntimeException e) {

            status.append(errorStatusDecoder.decodeRuntimeException(e));
        }
    }

    private void lagreInntekt(PensjonData pensjonData, TpsPerson tpsPerson, Set<String> miljoer, StringBuilder status) {

        status.append('$').append(POPP_INNTEKTSREGISTER).append('#');

        try {
            LagreInntektRequest lagreInntektRequest = mapperFacade.map(pensjonData.getInntekt(), LagreInntektRequest.class);
            lagreInntektRequest.setFnr(tpsPerson.getHovedperson());
            lagreInntektRequest.setMiljoer(new ArrayList<>(miljoer));

            decodeStatus(pensjonforvalterConsumer.lagreInntekt(lagreInntektRequest), status);

        } catch (RuntimeException e) {

            status.append(errorStatusDecoder.decodeRuntimeException(e));
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