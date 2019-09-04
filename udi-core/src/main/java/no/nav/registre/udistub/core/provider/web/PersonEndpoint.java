package no.nav.registre.udistub.core.provider.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.udistub.core.exception.NotFoundException;
import no.nav.registre.udistub.core.service.PersonService;
import no.nav.registre.udistub.core.service.to.UdiPerson;
import no.udi.common.v2.PingRequestType;
import no.udi.mt_1067_nav_data.v1.HentPersonstatusResultat;
import org.springframework.core.convert.ConversionService;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import v1.mt_1067_nav.no.udi.HentPersonstatusRequestType;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

@Slf4j
@Endpoint
@RequiredArgsConstructor
public class PersonEndpoint {

    private static final String NAMESPACE_URI = "http://udi.no.MT_1067_NAV.v1";

    private final PersonService personService;
    private final ConversionService conversionService;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "HentPersonstatusRequest")
    @ResponsePayload
    public JAXBElement<HentPersonstatusResultat> hentPersonstatusRequest(@RequestPayload HentPersonstatusRequestType request) {

        UdiPerson foundPerson = personService.finnPerson(request.getParameter().getFodselsnummer())
                .orElseThrow(() -> new NotFoundException(String.format("Kunne ikke finne person med fnr:%s", request.getParameter().getFodselsnummer())));
        HentPersonstatusResultat hentPersonstatusResultat = conversionService.convert(foundPerson, HentPersonstatusResultat.class);

        return new JAXBElement<>(new QName("http://udi.no.MT_1067_NAV.v1", "HentPersonstatusResultat"), HentPersonstatusResultat.class, hentPersonstatusResultat);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "PingRequest")
    @ResponsePayload
    public void ping(@RequestPayload PingRequestType request) {
    }
}
