package no.nav.registre.aareg.consumer.ws;

import static java.util.Objects.nonNull;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.ws.soap.SOAPFaultException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import no.nav.registre.aareg.consumer.ws.request.RsAaregOppdaterRequest;
import no.nav.registre.aareg.consumer.ws.request.RsAaregOpprettRequest;
import no.nav.registre.aareg.exception.TestnorgeAaregFunctionalException;
import no.nav.registre.aareg.provider.rs.response.RsAaregResponse;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.OppdaterArbeidsforholdArbeidsforholdIkkeFunnet;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.OppdaterArbeidsforholdSikkerhetsbegrensning;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.OppdaterArbeidsforholdUgyldigInput;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.OpprettArbeidsforholdSikkerhetsbegrensning;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.OpprettArbeidsforholdUgyldigInput;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.informasjon.Arbeidsforhold;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.meldinger.OppdaterArbeidsforholdRequest;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.meldinger.OpprettArbeidsforholdRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class AaregWsConsumer {

    public static final String STATUS_OK = "OK";
    private static final String TEKNISK_FEIL_OPPRETTING = "Feil: Teknisk feil ved oppretting mot AAREG";
    private static final String TEKNISK_FEIL_OPPDATERING = "Feil: Teknisk feil ved oppdatering mot AAREG";
    private static final String SOAP_FAULT_EXCEPTION = "Feil: AAREG teknisk feil/unntak: ";

    private final BehandleArbeidsforholdV1Proxy behandleArbeidsforholdV1Proxy;
    private final MapperFacade mapperFacade;

    private static String getUuid(
            String referanse
    ) {
        return nonNull(referanse) ? referanse : "testnorge-aareg: " + UUID.randomUUID().toString();
    }

    public RsAaregResponse opprettArbeidsforhold(
            RsAaregOpprettRequest request
    ) {
        var arbeidsforholdRequest = new OpprettArbeidsforholdRequest();
        arbeidsforholdRequest.setArbeidsforhold(mapperFacade.map(request.getArbeidsforhold(), Arbeidsforhold.class));
        arbeidsforholdRequest.setArkivreferanse(getUuid(request.getArkivreferanse()));

        Map<String, String> status = new HashMap<>(request.getEnvironments().size());

        log(arbeidsforholdRequest);

        request.getEnvironments().forEach(env -> {
            try {
                behandleArbeidsforholdV1Proxy.getServiceByEnvironment(env).opprettArbeidsforhold(arbeidsforholdRequest);
                status.put(env, STATUS_OK);
            } catch (OpprettArbeidsforholdSikkerhetsbegrensning | OpprettArbeidsforholdUgyldigInput | TestnorgeAaregFunctionalException error) {
                status.put(env, AaregResponseHandler.extractError(error));
            } catch (SOAPFaultException sfe) {
                status.put(env, SOAP_FAULT_EXCEPTION + sfe.getMessage());
                log.error(TEKNISK_FEIL_OPPRETTING, sfe);
            } catch (RuntimeException re) {
                status.put(env, TEKNISK_FEIL_OPPRETTING + ", se logg!");
                log.error(TEKNISK_FEIL_OPPRETTING, re);
            }
        });

        return RsAaregResponse.builder().statusPerMiljoe(status).build();
    }

    public Map<String, String> oppdaterArbeidsforhold(
            RsAaregOppdaterRequest request
    ) {
        var arbeidsforholdRequest = new OppdaterArbeidsforholdRequest();
        arbeidsforholdRequest.setArbeidsforhold(mapperFacade.map(request.getArbeidsforhold(), Arbeidsforhold.class));
        arbeidsforholdRequest.setArkivreferanse(getUuid(request.getArkivreferanse()));
        arbeidsforholdRequest.setRapporteringsperiode(mapperFacade.map(request.getRapporteringsperiode(), XMLGregorianCalendar.class));

        log(arbeidsforholdRequest);

        Map<String, String> status = new HashMap<>(request.getEnvironments().size());
        request.getEnvironments().forEach(env -> {
            try {
                behandleArbeidsforholdV1Proxy.getServiceByEnvironment(env).oppdaterArbeidsforhold(arbeidsforholdRequest);
                status.put(env, STATUS_OK);
            } catch (OppdaterArbeidsforholdArbeidsforholdIkkeFunnet | OppdaterArbeidsforholdSikkerhetsbegrensning | OppdaterArbeidsforholdUgyldigInput | TestnorgeAaregFunctionalException error) {
                status.put(env, AaregResponseHandler.extractError(error));
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


    @SneakyThrows
    private void log(OpprettArbeidsforholdRequest request) {
        try {
            var element = new JAXBElement<>(new QName("", "opprettArbeidsforhold"), OpprettArbeidsforholdRequest.class, request);

            JAXBContext jaxbContext = JAXBContext.newInstance(OpprettArbeidsforholdRequest.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            StringWriter sw = new StringWriter();
            jaxbMarshaller.marshal(element, sw);

            log.info(sw.toString());
        } catch (Exception e) {
            log.error("Feil ved logging av opprett arbeidsforhold request.", e);
        }
    }

    @SneakyThrows
    private void log(OppdaterArbeidsforholdRequest request) {
        try {

            var element = new JAXBElement<>(new QName("", "oppdaterArbeidsforhold"), OppdaterArbeidsforholdRequest.class, request);

            JAXBContext jaxbContext = JAXBContext.newInstance(OppdaterArbeidsforholdRequest.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            StringWriter sw = new StringWriter();
            jaxbMarshaller.marshal(element, sw);

            log.info(sw.toString());
        } catch (Exception e) {
            log.error("Feil ved logging av opprett arbeidsforhold request.", e);
        }
    }

}
