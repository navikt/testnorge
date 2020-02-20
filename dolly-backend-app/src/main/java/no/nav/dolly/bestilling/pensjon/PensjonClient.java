package no.nav.dolly.bestilling.pensjon;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.lang.String.*;
import static java.util.Objects.nonNull;

import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pensjon.domain.LagreInntektRequest;
import no.nav.dolly.bestilling.pensjon.domain.OpprettPersonRequest;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.RsDollyUpdateRequest;
import no.nav.dolly.domain.resultset.pensjon.PensjonData;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.metrics.Timed;

@Slf4j
@Service
@RequiredArgsConstructor
public class PensjonClient implements ClientRegister {

    public static final String PENSJON_FORVALTER = "PensjonForvalter";
    public static final String POPP_INNTEKTSREGISTER = "PoppInntekts";

    private final PensjonConsumer pensjonConsumer;
    private final MapperFacade mapperFacade;
    private final ErrorStatusDecoder errorStatusDecoder;

    @Timed(name = "providers", tags = { "operation", "gjenopprettPensjon" })
    @Override
    public void gjenopprett(RsDollyBestillingRequest bestilling, TpsPerson tpsPerson, BestillingProgress progress) {

        StringBuilder status = new StringBuilder();

        Set bestilteMiljoer = newHashSet(bestilling.getEnvironments());
        Set tilgjengeligeMiljoer = pensjonConsumer.getMiljoer();
        bestilteMiljoer.retainAll(tilgjengeligeMiljoer);
        if (!bestilteMiljoer.isEmpty()) {

            opprettPerson(tpsPerson, bestilteMiljoer, status);

            if (nonNull(bestilling.getPensjonforvalter())) {
                lagreInntekt(bestilling.getPensjonforvalter(), tpsPerson, bestilteMiljoer, status);
            }

        } else if (nonNull(bestilling.getPensjonforvalter())) {
            status.append('$')
                    .append(PENSJON_FORVALTER)
                    .append("&Feil: Bestilling ble ikke sendt til Pensjonsforvalter (PEN) da tilgjengelig(e) miljø(er) ['")
                    .append(join(",", (String[]) tilgjengeligeMiljoer.toArray()))
                    .append("]' ikke er valgt");
        }

        if (status.length() > 1) {
            progress.setPensjonforvalterStatus(status.substring(1));
        }
    }

    @Override
    public void release(List<String> identer) {

    }

    @Override
    public void opprettEndre(RsDollyUpdateRequest bestilling, BestillingProgress progress) {

    }

    private void opprettPerson(TpsPerson tpsPerson, Set<String> miljoer, StringBuilder status) {

        status.append('$').append(PENSJON_FORVALTER);

        try {
            sendOpprettPerson(tpsPerson.getPersondetalj(), miljoer);
            tpsPerson.getPersondetalj().getRelasjoner().forEach(relasjon ->
                sendOpprettPerson(relasjon.getPersonRelasjonMed(), miljoer)
            );

            status.append("&OK");

        } catch (RuntimeException e) {

            status.append('&').append(errorStatusDecoder.decodeRuntimeException(e));
        }
    }

    private void sendOpprettPerson(Person person, Set<String> miljoer) {

        OpprettPersonRequest opprettPersonRequest = mapperFacade.map(person, OpprettPersonRequest.class);
        opprettPersonRequest.setFnr(person.getIdent());
        opprettPersonRequest.setMiljo(newArrayList(miljoer));
        pensjonConsumer.opprettPerson(opprettPersonRequest);
    }

    private void lagreInntekt(PensjonData pensjonData, TpsPerson tpsPerson, Set<String> miljoer, StringBuilder status) {

        status.append('$').append(POPP_INNTEKTSREGISTER);

        try {
            LagreInntektRequest lagreInntektRequest = mapperFacade.map(pensjonData.getInntekt(), LagreInntektRequest.class);
            lagreInntektRequest.setFnr(tpsPerson.getHovedperson());
            lagreInntektRequest.setMiljo(newArrayList(miljoer));
            pensjonConsumer.lagreInntekt(lagreInntektRequest);

            status.append("&OK");

        } catch (RuntimeException e) {

            status.append('&').append(errorStatusDecoder.decodeRuntimeException(e));
        }
    }
}