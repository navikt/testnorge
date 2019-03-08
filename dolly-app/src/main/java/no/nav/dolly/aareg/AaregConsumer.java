package no.nav.dolly.aareg;

import java.util.UUID;
import javax.xml.datatype.XMLGregorianCalendar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.resultset.aareg.RsAaregRequest;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.OppdaterArbeidsforholdArbeidsforholdIkkeFunnet;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.OppdaterArbeidsforholdSikkerhetsbegrensning;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.OppdaterArbeidsforholdUgyldigInput;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.OpprettArbeidsforholdSikkerhetsbegrensning;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.OpprettArbeidsforholdUgyldigInput;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.informasjon.Arbeidsforhold;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.meldinger.OppdaterArbeidsforholdRequest;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.meldinger.OpprettArbeidsforholdRequest;

@Service
@Slf4j
public class AaregConsumer {

    @Autowired
    private BehandleArbeidsforholdV1Proxy behandleArbeidsforholdV1Proxy;

    @Autowired
    private MapperFacade mapperFacade;

    public void opprettArbeidsforhold(RsAaregRequest request) throws OpprettArbeidsforholdSikkerhetsbegrensning, OpprettArbeidsforholdUgyldigInput {

        OpprettArbeidsforholdRequest arbeidsforholdRequest = new OpprettArbeidsforholdRequest();
        arbeidsforholdRequest.setArbeidsforhold(mapperFacade.map(request.getArbeidsforhold(), Arbeidsforhold.class));
        arbeidsforholdRequest.setArkivreferanse(getUuid());

        for (String environment : request.getEnvironments()){
            behandleArbeidsforholdV1Proxy.getServiceByEnvironment(environment).opprettArbeidsforhold(arbeidsforholdRequest);
        }
    }

    public void oppdaterArbeidsforhold(RsAaregRequest request)
            throws OppdaterArbeidsforholdArbeidsforholdIkkeFunnet, OppdaterArbeidsforholdSikkerhetsbegrensning, OppdaterArbeidsforholdUgyldigInput {

        OppdaterArbeidsforholdRequest arbeidsforholdRequest = new OppdaterArbeidsforholdRequest();
        arbeidsforholdRequest.setArbeidsforhold(mapperFacade.map(request.getArbeidsforhold(), Arbeidsforhold.class));
        arbeidsforholdRequest.setArkivreferanse(getUuid());
        arbeidsforholdRequest.setRapporteringsperiode(mapperFacade.map(request.getRapporteringsperiode(), XMLGregorianCalendar.class));

        for (String environment : request.getEnvironments()) {
            behandleArbeidsforholdV1Proxy.getServiceByEnvironment(environment).oppdaterArbeidsforhold(arbeidsforholdRequest);
        }
    }

    private static String getUuid() {
        return "Dolly: " + UUID.randomUUID().toString();
    }
}
