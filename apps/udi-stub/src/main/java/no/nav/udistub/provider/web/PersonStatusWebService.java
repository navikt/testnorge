package no.nav.udistub.provider.web;

import lombok.RequiredArgsConstructor;
import no.udi.common.v2.PingRequestType;
import no.udi.common.v2.PingResponseType;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import v1.mt_1067_nav.no.udi.DeepPingFault;
import v1.mt_1067_nav.no.udi.HentPersonstatusFault;
import v1.mt_1067_nav.no.udi.HentPersonstatusRequestType;
import v1.mt_1067_nav.no.udi.HentPersonstatusResponseType;
import v1.mt_1067_nav.no.udi.HentUtvidetPersonstatusFault;
import v1.mt_1067_nav.no.udi.HentUtvidetPersonstatusRequestType;
import v1.mt_1067_nav.no.udi.HentUtvidetPersonstatusResponseType;
import v1.mt_1067_nav.no.udi.PingFault;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

@Endpoint
@RequiredArgsConstructor
public class PersonStatusWebService {

    private static final String NAMESPACE_URI = "http://udi.no.MT_1067_NAV.v1";

    private final PersonStatusService personStatusService;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "PingRequest")
    @ResponsePayload
    public JAXBElement<PingResponseType> ping(@RequestPayload PingRequestType parameters) throws PingFault {
        var ping = new JAXBElement<>(new QName(NAMESPACE_URI, "PingResponse"), PingResponseType.class,
                personStatusService.ping(parameters));
        ping.setNil(true);
        return ping;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "DeepPingRequest")
    @ResponsePayload
    public JAXBElement<PingResponseType> deepPing(@RequestPayload PingRequestType parameters) throws DeepPingFault {
        var deepPing =
                new JAXBElement<>(new QName(NAMESPACE_URI, "DeepPingResponse"), PingResponseType.class,
                        personStatusService.deepPing(parameters));
        deepPing.setNil(true);
        return deepPing;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "HentPersonstatusRequest")
    @ResponsePayload
    public JAXBElement<HentPersonstatusResponseType> hentPersonstatus(@RequestPayload HentPersonstatusRequestType request)
            throws HentPersonstatusFault {

        var hentPersonstatusResponse =
                new JAXBElement<>(new QName(NAMESPACE_URI, "HentPersonstatusResponse"),
                        HentPersonstatusResponseType.class, personStatusService.hentPersonstatus(request));
        hentPersonstatusResponse.setNil(true);
        return hentPersonstatusResponse;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "HentUtvidetPersonstatusRequest")
    @ResponsePayload
    public JAXBElement<HentUtvidetPersonstatusResponseType> hentUtvidetPersonstatus(@RequestPayload HentUtvidetPersonstatusRequestType request)
            throws HentUtvidetPersonstatusFault {

        var hentUtvidetPersonstatusResponse =
                new JAXBElement<>(new QName(NAMESPACE_URI, "HentUtvidetPersonstatusResponse"),
                        HentUtvidetPersonstatusResponseType.class, personStatusService.hentUtvidetPersonstatus(request));
        hentUtvidetPersonstatusResponse.setNil(true);
        return hentUtvidetPersonstatusResponse;
    }
}
