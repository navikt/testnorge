package no.nav.brregstub.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import no.nav.brregstub.tjenestekontrakter.hentroller.Grunndata;
import no.nav.brregstub.tjenestekontrakter.ws.HentRoller;
import no.nav.brregstub.tjenestekontrakter.ws.HentRollerResponse;
import no.nav.brregstub.tjenestekontrakter.ws.HentRolleutskrift;
import no.nav.brregstub.tjenestekontrakter.ws.HentRolleutskriftResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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


    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "hentRoller")
    @ResponsePayload
    public JAXBElement<HentRollerResponse> hentRollerResponse(
            @RequestPayload JAXBElement<HentRoller> request) throws JsonProcessingException {
        LOGGER.debug("Received request object: {}");

        var response = new HentRollerResponse();
        var grunndata = new Grunndata();
        var melding = new Grunndata.Melding();
        var organisasjonsnummer = new Grunndata.Melding.Organisasjonsnummer();
        organisasjonsnummer.setValue("123");
        melding.setOrganisasjonsnummer(organisasjonsnummer);
        grunndata.setMelding(melding);
        StringWriter sw = new StringWriter();
        JAXB.marshal(grunndata, sw);
        String xmlString = sw.toString();
        response.setReturn(xmlString);

        return new JAXBElement<>(new QName("hentRollerResponse"), HentRollerResponse.class, response);
    }


    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "hentRolleutskrift")
    @ResponsePayload
    public JAXBElement<HentRolleutskriftResponse> hentRolleutskrift(
            @RequestPayload JAXBElement<HentRolleutskrift> request) throws JsonProcessingException {
        LOGGER.debug("Received request object: {}");

        var response = new HentRolleutskriftResponse();


        return new JAXBElement<>(new QName("hentRolleutskriftResponse"), HentRolleutskriftResponse.class, response);
    }

}
