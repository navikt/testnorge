package no.nav.udistub.provider.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.udistub.exception.NotFoundException;
import no.nav.udistub.service.PersonService;
import no.nav.udistub.service.dto.UdiPerson;
import no.udi.common.v2.PingRequestType;
import no.udi.mt_1067_nav_data.v1.HentPersonstatusResultat;
import org.springframework.core.convert.ConversionService;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import v1.mt_1067_nav.no.udi.HentPersonstatusRequestType;
import v1.mt_1067_nav.no.udi.HentPersonstatusResponseType;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

@Slf4j
//@Endpoint
@RequiredArgsConstructor
public class PersonEndpoint {

    private static final String NAMESPACE_URI = "http://udi.no.MT_1067_NAV.v1";

    private final PersonService personService;
    private final ConversionService conversionService;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "HentPersonstatusRequest")
    @ResponsePayload
    public JAXBElement<HentPersonstatusResponseType> hentPersonstatusRequest(@RequestPayload HentPersonstatusRequestType request) {

        UdiPerson foundPerson = personService.finnPerson(request.getParameter().getFodselsnummer());
        HentPersonstatusResultat resultat = conversionService.convert(foundPerson, HentPersonstatusResultat.class);
        HentPersonstatusResponseType hentPersonstatusResponseType = new HentPersonstatusResponseType();
        hentPersonstatusResponseType.setResultat(resultat);
        JAXBElement<HentPersonstatusResponseType> hentPersonstatusResponse = new JAXBElement<>(new QName("http://udi.no.MT_1067_NAV.v1", "HentPersonstatusResponse"), HentPersonstatusResponseType.class, hentPersonstatusResponseType);
        hentPersonstatusResponse.setNil(true);
        return hentPersonstatusResponse;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "PingRequest")
    @ResponsePayload
    public void ping(@RequestPayload PingRequestType request) {
    }
}
