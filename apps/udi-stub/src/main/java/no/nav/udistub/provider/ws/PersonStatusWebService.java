package no.nav.udistub.provider.ws;

import jakarta.xml.bind.JAXBElement;
import lombok.RequiredArgsConstructor;
import no.udi.common.v2.PingRequestType;
import no.udi.common.v2.PingResponseType;
import no.udi.mt_1067_nav_data.v1.HentPersonstatusRequestType;
import no.udi.mt_1067_nav_data.v1.HentPersonstatusResponseType;
import no.udi.mt_1067_nav_data.v1.HentUtvidetPersonstatusRequestType;
import no.udi.mt_1067_nav_data.v1.HentUtvidetPersonstatusResponseType;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.xml.namespace.QName;

@Endpoint
@RequiredArgsConstructor
public class PersonStatusWebService {

    private static final String NAMESPACE_URI = "http://udi.no.MT_1067_NAV.v1";

    private final PersonStatusService personStatusService;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "PingRequest")
    @ResponsePayload
    public JAXBElement<PingResponseType> ping(@RequestPayload PingRequestType parameters) {
        var ping = new JAXBElement<>(new QName(NAMESPACE_URI, "PingResponse"), PingResponseType.class,
                personStatusService.ping(parameters));
        ping.setNil(true);
        return ping;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "DeepPingRequest")
    @ResponsePayload
    public JAXBElement<PingResponseType> deepPing(@RequestPayload PingRequestType parameters) {
        var deepPing =
                new JAXBElement<>(new QName(NAMESPACE_URI, "DeepPingResponse"), PingResponseType.class,
                        personStatusService.deepPing(parameters));
        deepPing.setNil(true);
        return deepPing;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "HentPersonstatusRequest")
    @ResponsePayload
    public JAXBElement<HentPersonstatusResponseType> hentPersonstatus(@RequestPayload HentPersonstatusRequestType request) {

        var hentPersonstatusResponse =
                new JAXBElement<>(new QName(NAMESPACE_URI, "HentPersonstatusResponse"),
                        HentPersonstatusResponseType.class, personStatusService.hentPersonstatus(request));
        hentPersonstatusResponse.setNil(true);
        return hentPersonstatusResponse;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "HentUtvidetPersonstatusRequest")
    @ResponsePayload
    public JAXBElement<HentUtvidetPersonstatusResponseType> hentUtvidetPersonstatus(@RequestPayload HentUtvidetPersonstatusRequestType request) {

        var hentUtvidetPersonstatusResponse =
                new JAXBElement<>(new QName(NAMESPACE_URI, "HentUtvidetPersonstatusResponse"),
                        HentUtvidetPersonstatusResponseType.class, personStatusService.hentUtvidetPersonstatus(request));
        hentUtvidetPersonstatusResponse.setNil(true);
        return hentUtvidetPersonstatusResponse;
    }
}
