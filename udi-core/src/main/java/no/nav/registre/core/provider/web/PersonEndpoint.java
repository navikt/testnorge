package no.nav.registre.core.provider.web;

import lombok.RequiredArgsConstructor;
import no.udi.common.v2.PingRequestType;
import no.udi.common.v2.PingResponseType;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.server.endpoint.annotation.SoapHeader;
import v1.mt_1067_nav.no.udi.HentPersonstatusRequestType;
import v1.mt_1067_nav.no.udi.HentPersonstatusResponseType;

import no.nav.registre.core.database.repository.PersonRepository;

@Endpoint
@RequiredArgsConstructor
public class PersonEndpoint {

    private static final String NAMESPACE_URI = "http://udi.no.MT_1067_NAV.v1";

    private final PersonRepository personRepository;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "HentPersonStatus")
    @ResponsePayload
    public HentPersonstatusResponseType HentPersonstatus(@RequestPayload HentPersonstatusRequestType request, @SoapHeader(
            value = "{http://udi.no.MT_1067_NAV.v1}listFlightsSoapHeaders") SoapHeaderElement soapHeaderElement) {
        return new HentPersonstatusResponseType();
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "Ping")
    @ResponsePayload
    public PingResponseType ping(@RequestPayload PingRequestType request) {
        PingResponseType pingResponseType = new PingResponseType();
        pingResponseType.setResponse(":+1");
        return pingResponseType;
    }
}
