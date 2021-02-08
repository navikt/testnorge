package no.nav.udistub.provider.web;

import lombok.RequiredArgsConstructor;
import no.nav.udistub.service.PersonService;
import no.nav.udistub.service.dto.UdiPerson;
import no.udi.common.v2.PingRequestType;
import no.udi.common.v2.PingResponseType;
import no.udi.mt_1067_nav_data.v1.HentPersonstatusResultat;
import no.udi.mt_1067_nav_data.v1.HentUtvidetPersonstatusResultat;
import org.springframework.core.convert.ConversionService;
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

import javax.jws.soap.SOAPBinding;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import static java.util.Objects.nonNull;
import static net.logstash.logback.encoder.org.apache.commons.lang3.BooleanUtils.isNotTrue;

@Endpoint
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
@RequiredArgsConstructor
public class PersonStatusWebservice /* implements MT1067NAVV1Interface */ {

    private static final String NAMESPACE_URI = "http://udi.no.MT_1067_NAV.v1";

    private final PersonService personService;
    private final ConversionService conversionService;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "PingRequest")
    @ResponsePayload
    public JAXBElement<PingResponseType> ping(@RequestPayload PingRequestType parameters) throws PingFault {
        var ping =
                new JAXBElement<>(new QName(NAMESPACE_URI, "PingResponse"), PingResponseType.class, null);
        ping.setNil(true);
        return ping;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "DeepPingRequest")
    @ResponsePayload
    public JAXBElement<PingResponseType> deepPing(@RequestPayload PingRequestType parameters) throws DeepPingFault {
        var deepPing =
                new JAXBElement<>(new QName(NAMESPACE_URI, "DeepPingResponse"), PingResponseType.class, null);
        deepPing.setNil(true);
        return deepPing;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "HentPersonstatusRequest")
    @ResponsePayload
    public JAXBElement<HentPersonstatusResponseType> hentPersonstatus(@RequestPayload HentPersonstatusRequestType request) throws HentPersonstatusFault {

        UdiPerson foundPerson = personService.finnPerson(request.getParameter().getFodselsnummer());
        var resultat = conversionService.convert(foundPerson, HentPersonstatusResultat.class);

        if (isNotTrue(request.getParameter().isInkluderArbeidsadgang())) {
            resultat.setArbeidsadgang(null);
        }
        if (isNotTrue(request.getParameter().isInkluderAvgjorelsehistorikk())) {
            resultat.setAvgjorelsehistorikk(null);
        }
        if (isNotTrue(request.getParameter().isInkluderFlyktningstatus())) {
            resultat.setHarFlyktningstatus(null);
            resultat.setUavklartFlyktningstatus(null);
            resultat.setHistorikkHarFlyktningstatus(null);
        }
        if (isNotTrue(request.getParameter().isInkluderSoknadOmBeskyttelseUnderBehandling())) {
            resultat.setSoknadOmBeskyttelseUnderBehandling(null);
        }
//        if (isNotTrue(request.getParameter().isManuellOppgVedUavklartArbeidsadgang())) {
//            resultat.setUavklartFlyktningstatus(null);
//        }
        var response = new HentPersonstatusResponseType();
        response.setResultat(resultat);
        var hentPersonstatusResponse =
                new JAXBElement<>(new QName(NAMESPACE_URI, "HentPersonstatusResponse"), HentPersonstatusResponseType.class, response);
        hentPersonstatusResponse.setNil(true);
        return hentPersonstatusResponse;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "HentUtvidetPersonstatusRequest")
    @ResponsePayload
    public JAXBElement<HentUtvidetPersonstatusResponseType> HentUtvidetPersonstatus(@RequestPayload HentUtvidetPersonstatusRequestType request) throws HentUtvidetPersonstatusFault {

        UdiPerson foundPerson = personService.finnPerson(request.getParameter().getFodselsnummer());
        var resultat = conversionService.convert(foundPerson, HentUtvidetPersonstatusResultat.class);

        if (isNotTrue(request.getParameter().isInkluderArbeidsadgang())) {
            resultat.setArbeidsadgang(null);
        }
        if (isNotTrue(request.getParameter().isInkluderAvgjorelsehistorikk())) {
            resultat.setAvgjorelsehistorikk(null);
        }
        if (isNotTrue(request.getParameter().isInkluderFlyktningstatus())) {
            resultat.setHarFlyktningstatus(null);
            resultat.setUavklartFlyktningstatus(null);
            resultat.setHistorikkHarFlyktningstatus(null);
        }
        if (isNotTrue(request.getParameter().isInkluderHjemmel()) && nonNull(resultat.getArbeidsadgang())) {
            resultat.getArbeidsadgang().setHjemmel(null);
        }
        if (isNotTrue(request.getParameter().isInkluderSoknadOmBeskyttelseUnderBehandling())) {
            resultat.setSoknadOmBeskyttelseUnderBehandling(null);
        }
//        if (isNotTrue(request.getParameter().isManuellOppgVedUavklartArbeidsadgang())) {
//            resultat.setUavklartFlyktningstatus(null);
//        }
        var response = new HentUtvidetPersonstatusResponseType();
        response.setResultat(resultat);
        var hentUtvidetPersonstatusResponse =
                new JAXBElement<>(new QName(NAMESPACE_URI, "HentUtvidetPersonstatusResponse"), HentUtvidetPersonstatusResponseType.class, response);
        hentUtvidetPersonstatusResponse.setNil(true);
        return hentUtvidetPersonstatusResponse;
    }
}
