package no.nav.dolly.bestilling.pensjonforvalter;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.joining;

import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;

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
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.service.TpsfPersonCache;

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

    @Timed(name = "providers", tags = { "operation", "gjenopprettPensjon" })
    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, TpsPerson tpsPerson, BestillingProgress progress, boolean isOpprettEndre) {

        StringBuilder status = new StringBuilder();

        Set bestilteMiljoer = newHashSet(bestilling.getEnvironments());
        Set tilgjengeligeMiljoer = pensjonforvalterConsumer.getMiljoer();
        bestilteMiljoer.retainAll(tilgjengeligeMiljoer);
        if (!bestilteMiljoer.isEmpty()) {

            opprettPerson(tpsPerson, bestilteMiljoer, status);

            if (nonNull(bestilling.getPensjonforvalter())) {
                lagreInntekt(bestilling.getPensjonforvalter(), tpsPerson, bestilteMiljoer, status);
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

    private void opprettPerson(TpsPerson tpsPerson, Set<String> miljoer, StringBuilder status) {

        status.append('$').append(PENSJON_FORVALTER).append('#');

        try {
            tpsfPersonCache.fetchIfEmpty(tpsPerson);
            OpprettPersonRequest opprettPersonRequest =
                    mapperFacade.map(tpsPerson.getPerson(tpsPerson.getHovedperson()), OpprettPersonRequest.class);
            opprettPersonRequest.setFnr(tpsPerson.getHovedperson());
            opprettPersonRequest.setMiljoer(newArrayList(miljoer));

            decodeStatus(pensjonforvalterConsumer.opprettPerson(opprettPersonRequest), status);

        } catch (RuntimeException e) {

            status.append(errorStatusDecoder.decodeRuntimeException(e));
        }
    }

    private void lagreInntekt(PensjonData pensjonData, TpsPerson tpsPerson, Set<String> miljoer, StringBuilder status) {

        status.append('$').append(POPP_INNTEKTSREGISTER).append('#');

        try {
            LagreInntektRequest lagreInntektRequest = mapperFacade.map(pensjonData.getInntekt(), LagreInntektRequest.class);
            lagreInntektRequest.setFnr(tpsPerson.getHovedperson());
            lagreInntektRequest.setMiljoer(newArrayList(miljoer));

            decodeStatus(pensjonforvalterConsumer.lagreInntekt(lagreInntektRequest), status);

        } catch (RuntimeException e) {

            status.append(errorStatusDecoder.decodeRuntimeException(e));
        }
    }

    private void decodeStatus(PensjonforvalterResponse response, StringBuilder pensjonStatus) {
        response.getStatus().forEach(status ->
                pensjonStatus.append(status.getMiljo()).append(':')
                        .append(status.getResponse().getHttpStatus().getStatus() == 200 ? "OK" :
                                "Feil= " + status.getResponse().getMessage())
                        .append(',')
        );
    }
}