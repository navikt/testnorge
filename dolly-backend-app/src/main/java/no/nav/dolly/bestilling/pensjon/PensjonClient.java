package no.nav.dolly.bestilling.pensjon;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pensjon.domain.OpprettPersonRequest;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.RsDollyUpdateRequest;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.metrics.Timed;

@Slf4j
@Service
@RequiredArgsConstructor
public class PensjonClient implements ClientRegister {

    private final PensjonConsumer pensjonConsumer;
    private final MapperFacade mapperFacade;
    private final ErrorStatusDecoder errorStatusDecoder;

    @Timed(name = "providers", tags = { "operation", "gjenopprettPensjon" })
    @Override
    public void gjenopprett(RsDollyBestillingRequest bestilling, TpsPerson tpsPerson, BestillingProgress progress) {

        Set miljoer = newHashSet(bestilling.getEnvironments());
        miljoer.retainAll(pensjonConsumer.getMiljoer());

        if (!miljoer.isEmpty()) {

            StringBuilder status = new StringBuilder();
            sendOpprettPerson(tpsPerson, newArrayList(miljoer), status);


            progress.setPensjonStatus(status.toString());
        }
    }

    @Override
    public void release(List<String> identer) {

    }

    @Override
    public void opprettEndre(RsDollyUpdateRequest bestilling, BestillingProgress progress) {

    }

    private void sendOpprettPerson(TpsPerson tpsPerson, List<String> miljoer, StringBuilder status) {

        try {
            OpprettPersonRequest opprettPersonRequest = mapperFacade.map(tpsPerson.getPersondetalj(), OpprettPersonRequest.class);
            opprettPersonRequest.setFnr(tpsPerson.getHovedperson());
            opprettPersonRequest.setMiljo(miljoer);
            pensjonConsumer.opprettPerson(opprettPersonRequest);
            tpsPerson.getPersondetalj().getRelasjoner().forEach(relasjon -> {
                OpprettPersonRequest personRelasjon = mapperFacade.map(relasjon.getPersonRelasjonMed(), OpprettPersonRequest.class);
                personRelasjon.setFnr(relasjon.getPersonRelasjonMed().getIdent());
                opprettPersonRequest.setMiljo(miljoer);
                pensjonConsumer.opprettPerson(personRelasjon);
            });

            status.append("OK");

        } catch (RuntimeException e) {

            status.append(errorStatusDecoder.decodeRuntimeException(e));
        }
    }

    private void lagreInntekt(TpsPerson tpsPerson, List<String> miljoer, StringBuilder status) {

        try {
            OpprettPersonRequest opprettPersonRequest = mapperFacade.map(tpsPerson.getPersondetalj(), OpprettPersonRequest.class);
            opprettPersonRequest.setFnr(tpsPerson.getHovedperson());
            opprettPersonRequest.setMiljo(miljoer);
            pensjonConsumer.opprettPerson(opprettPersonRequest);
            tpsPerson.getPersondetalj().getRelasjoner().forEach(relasjon -> {
                OpprettPersonRequest personRelasjon = mapperFacade.map(relasjon.getPersonRelasjonMed(), OpprettPersonRequest.class);
                personRelasjon.setFnr(relasjon.getPersonRelasjonMed().getIdent());
                opprettPersonRequest.setMiljo(miljoer);
                pensjonConsumer.opprettPerson(personRelasjon);
            });

            status.append("OK");

        } catch (RuntimeException e) {

            status.append(errorStatusDecoder.decodeRuntimeException(e));
        }
    }
}