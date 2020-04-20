package no.nav.brregstub.ws;

import no.nav.brregstub.service.BrregService;
import no.nav.brregstub.tjenestekontrakter.ws.HentRoller;
import no.nav.brregstub.tjenestekontrakter.ws.HentRollerResponse;
import no.nav.brregstub.tjenestekontrakter.ws.HentRolleutskrift;
import no.nav.brregstub.tjenestekontrakter.ws.HentRolleutskriftResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.io.StringWriter;


@Endpoint
public class BrregEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrregEndpoint.class);

    private static final String NAMESPACE_URI = "http://no/brreg/saksys/grunndata/ws";

    private final BrregService brregService;

    @Autowired
    public BrregEndpoint(BrregService brregService) {
        this.brregService = brregService;
    }


    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "hentRoller")
    @ResponsePayload
    public JAXBElement<HentRollerResponse> hentRollerResponse(
            @RequestPayload JAXBElement<HentRoller> request) {
        try {
            var response = new HentRollerResponse();
            response.setReturn(konverterGrunndataTilString(brregService.hentRoller(request.getValue().getOrgnr())));

            return new JAXBElement<>(new QName("hentRollerResponse"), HentRollerResponse.class, response);
        } catch (Exception e) {
            LOGGER.error("Ukjent feil ved kall av hentRoller", e);
            throw new RuntimeException("Ukjent feil ved kall av hentRoller");
        }
    }


    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "hentRolleutskrift")
    @ResponsePayload
    public JAXBElement<HentRolleutskriftResponse> hentRolleutskrift(
            @RequestPayload JAXBElement<HentRolleutskrift> request) {
        try {
            var response = new HentRolleutskriftResponse();
            response.setReturn(konverterGrunndataTilString(brregService.hentRolleutskrift(request.getValue().getRequestId())));


            return new JAXBElement<>(new QName("hentRolleutskriftResponse"), HentRolleutskriftResponse.class, response);
        } catch (Exception e) {
            LOGGER.error("Ukjent feil ved kall av hentRolleutskrift", e);
            throw new RuntimeException("Ukjent feil ved kall av hentRoller");
        }
    }

    private static String konverterGrunndataTilString(Object xml) {
        StringWriter sw = new StringWriter();
        JAXB.marshal(xml, sw);
        return sw.toString();
    }

}
