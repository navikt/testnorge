package no.nav.dolly.bestilling.pensjon;

import java.util.List;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pensjon.domain.OpprettPerson;
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

    public static final String SYNTH_ENV = "q2";
    public static final String PENSJON = "Pensjon";

    private final PensjonConsumer pensjonConsumer;
    private final MapperFacade mapperFacade;
    private final ErrorStatusDecoder errorStatusDecoder;

    @Timed(name = "providers", tags = { "operation", "gjenopprettPensjon" })
    @Override
    public void gjenopprett(RsDollyBestillingRequest bestilling, TpsPerson tpsPerson, BestillingProgress progress) {

        if (bestilling.getEnvironments().contains(SYNTH_ENV)) {

            StringBuilder status = new StringBuilder();
            sendOpprettPerson(tpsPerson, status);

            if (status.length() > 1) {
                progress.setPensjonStatus(status.substring(1));
            }
        }
    }

    @Override
    public void release(List<String> identer) {

    }

    @Override
    public void opprettEndre(RsDollyUpdateRequest bestilling, BestillingProgress progress) {

    }

    private void sendOpprettPerson(TpsPerson tpsPerson, StringBuilder status) {

        try {
            OpprettPerson opprettPerson = mapperFacade.map(tpsPerson.getPersondetalj(), OpprettPerson.class);
            opprettPerson.setFnr(tpsPerson.getHovedperson());
            pensjonConsumer.opprettPerson(opprettPerson);
            tpsPerson.getPersondetalj().getRelasjoner().forEach(relasjon -> {
                OpprettPerson personRelasjon = mapperFacade.map(relasjon.getPersonRelasjonMed(), OpprettPerson.class);
                personRelasjon.setFnr(relasjon.getPersonRelasjonMed().getIdent());
                pensjonConsumer.opprettPerson(personRelasjon);
            });

            status.append('$').append(PENSJON).append('&').append("OK");

        } catch (RuntimeException e) {

            status.append('$').append(PENSJON).append('&').append(errorStatusDecoder.decodeRuntimeException(e));
        }
    }
}