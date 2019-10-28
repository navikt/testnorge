package no.nav.dolly.consumer.aareg;

import static java.util.Objects.nonNull;
import static no.nav.dolly.consumer.aareg.AaregResponseHandler.extractError;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.soap.SOAPFaultException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.resultset.aareg.RsAaregOppdaterRequest;
import no.nav.dolly.domain.resultset.aareg.RsAaregOpprettRequest;
import no.nav.dolly.exceptions.DollyFunctionalException;
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
public class AaregWsConsumer {

    private static final String STATUS_OK = "OK";
    private static final String TEKNISK_FEIL_OPPRETTING = "Feil: Teknisk feil ved oppretting mot AAREG";
    private static final String TEKNISK_FEIL_OPPDATERING = "Feil: Teknisk feil ved oppdatering mot AAREG";
    private static final String SOAP_FAULT_EXCEPTION = "Feil: AAREG teknisk feil/unntak: ";

    @Autowired
    private BehandleArbeidsforholdV1Proxy behandleArbeidsforholdV1Proxy;

    @Autowired
    private MapperFacade mapperFacade;

    public Map<String, String> opprettArbeidsforhold(RsAaregOpprettRequest request) {

        OpprettArbeidsforholdRequest arbeidsforholdRequest = new OpprettArbeidsforholdRequest();
        arbeidsforholdRequest.setArbeidsforhold(mapperFacade.map(request.getArbeidsforhold(), Arbeidsforhold.class));
        arbeidsforholdRequest.setArkivreferanse(getUuid(request.getArkivreferanse()));

        Map<String, String> status = new HashMap(request.getEnvironments().size());
        request.getEnvironments().forEach(env -> {
            try {
                behandleArbeidsforholdV1Proxy.getServiceByEnvironment(env).opprettArbeidsforhold(arbeidsforholdRequest);
                status.put(env, STATUS_OK);
            } catch (OpprettArbeidsforholdSikkerhetsbegrensning | OpprettArbeidsforholdUgyldigInput | DollyFunctionalException error) {
                status.put(env, extractError(error));
            } catch (SOAPFaultException sfe) {
                status.put(env, SOAP_FAULT_EXCEPTION + sfe.getMessage());
                log.error(TEKNISK_FEIL_OPPRETTING, sfe);
            } catch (RuntimeException re) {
                status.put(env, TEKNISK_FEIL_OPPRETTING + ", se logg!");
                log.error(TEKNISK_FEIL_OPPRETTING, re);
            }
        });

        return status;
    }

    public Map<String, String> oppdaterArbeidsforhold(RsAaregOppdaterRequest request) {

        OppdaterArbeidsforholdRequest arbeidsforholdRequest = new OppdaterArbeidsforholdRequest();
        arbeidsforholdRequest.setArbeidsforhold(mapperFacade.map(request.getArbeidsforhold(), Arbeidsforhold.class));
        arbeidsforholdRequest.setArkivreferanse(getUuid(request.getArkivreferanse()));
        arbeidsforholdRequest.setRapporteringsperiode(mapperFacade.map(request.getRapporteringsperiode(), XMLGregorianCalendar.class));

        Map<String, String> status = new HashMap(request.getEnvironments().size());
        request.getEnvironments().forEach(env -> {
            try {
                behandleArbeidsforholdV1Proxy.getServiceByEnvironment(env).oppdaterArbeidsforhold(arbeidsforholdRequest);
                status.put(env, STATUS_OK);
            } catch (OppdaterArbeidsforholdArbeidsforholdIkkeFunnet | OppdaterArbeidsforholdSikkerhetsbegrensning | OppdaterArbeidsforholdUgyldigInput | DollyFunctionalException error) {
                status.put(env, extractError(error));
            } catch (SOAPFaultException sfe) {
                status.put(env, SOAP_FAULT_EXCEPTION + sfe.getMessage());
                log.error(TEKNISK_FEIL_OPPDATERING, sfe);
            } catch (RuntimeException re) {
                status.put(env, TEKNISK_FEIL_OPPDATERING + ", se logg!");
                log.error(TEKNISK_FEIL_OPPDATERING, re);
            }
        });

        return status;
    }

    private static String getUuid(String referanse) {

        return nonNull(referanse) ? referanse : "Dolly: " + UUID.randomUUID().toString();
    }
}
