package no.nav.udistub.provider.web;

import lombok.RequiredArgsConstructor;
import no.nav.udistub.service.PersonService;
import no.nav.udistub.service.dto.UdiPerson;
import no.udi.common.v2.PingRequestType;
import no.udi.common.v2.PingResponseType;
import no.udi.mt_1067_nav_data.v1.HentPersonstatusResultat;
import no.udi.mt_1067_nav_data.v1.HentUtvidetPersonstatusResultat;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import v1.mt_1067_nav.no.udi.DeepPingFault;
import v1.mt_1067_nav.no.udi.HentPersonstatusFault;
import v1.mt_1067_nav.no.udi.HentPersonstatusRequestType;
import v1.mt_1067_nav.no.udi.HentPersonstatusResponseType;
import v1.mt_1067_nav.no.udi.HentUtvidetPersonstatusFault;
import v1.mt_1067_nav.no.udi.HentUtvidetPersonstatusRequestType;
import v1.mt_1067_nav.no.udi.HentUtvidetPersonstatusResponseType;
import v1.mt_1067_nav.no.udi.MT1067NAVV1Interface;
import v1.mt_1067_nav.no.udi.PingFault;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;

@Service
@RequiredArgsConstructor
public class PersonStatusService implements MT1067NAVV1Interface {

    private final PersonService personService;
    private final ConversionService conversionService;

    private static HentPersonstatusResultat filtrerResultat(HentPersonstatusRequestType request, HentPersonstatusResultat resultat) {

        if (nonNull(resultat)) {
            if (isNotTrue(request.getParameter().isInkluderArbeidsadgang())) {
                resultat.setArbeidsadgang(null);
            }
            if (isNotTrue(request.getParameter().isInkluderFlyktningstatus())) {
                resultat.setHarFlyktningstatus(null);
                resultat.setUavklartFlyktningstatus(null);
                resultat.setHistorikkHarFlyktningstatus(null);
            }
            if (isNotTrue(request.getParameter().isInkluderSoknadOmBeskyttelseUnderBehandling())) {
                resultat.setSoknadOmBeskyttelseUnderBehandling(null);
            }
        }
        return resultat;
    }

    private static HentUtvidetPersonstatusResultat filtererResultat(HentUtvidetPersonstatusRequestType request, HentUtvidetPersonstatusResultat resultat) {

        if (nonNull(resultat)) {

            if (isNotTrue(request.getParameter().isInkluderArbeidsadgang())) {
                resultat.setArbeidsadgang(null);

                if (nonNull(resultat.getArbeidsadgang()) && isNotTrue(request.getParameter().isInkluderHjemmel())) {
                    resultat.getArbeidsadgang().setHjemmel(null);
                    resultat.getArbeidsadgang().setForklaring(null);
                }
            }
            if (isNotTrue(request.getParameter().isInkluderFlyktningstatus())) {
                resultat.setHarFlyktningstatus(null);
                resultat.setUavklartFlyktningstatus(null);
                resultat.setHistorikkHarFlyktningstatus(null);
            }
            if (isNotTrue(request.getParameter().isInkluderSoknadOmBeskyttelseUnderBehandling())) {
                resultat.setSoknadOmBeskyttelseUnderBehandling(null);
            }
        }
        return resultat;
    }

    @Override
    public PingResponseType ping(PingRequestType parameters) throws PingFault {

        return new PingResponseType();
    }

    @Override
    public PingResponseType deepPing(PingRequestType parameters) throws DeepPingFault {

        return new PingResponseType();
    }

    @Override
    public HentPersonstatusResponseType hentPersonstatus(HentPersonstatusRequestType parameters) throws HentPersonstatusFault {

        UdiPerson foundPerson = personService.finnPerson(parameters.getParameter().getFodselsnummer());
        var resultat = conversionService.convert(foundPerson, HentPersonstatusResultat.class);

        var response = new HentPersonstatusResponseType();
        response.setResultat(filtrerResultat(parameters, resultat));
        return response;
    }

    @Override
    public HentUtvidetPersonstatusResponseType hentUtvidetPersonstatus(HentUtvidetPersonstatusRequestType parameters) throws HentUtvidetPersonstatusFault {

        UdiPerson foundPerson = personService.finnPerson(parameters.getParameter().getFodselsnummer());
        var resultat = conversionService.convert(foundPerson, HentUtvidetPersonstatusResultat.class);

        var response = new HentUtvidetPersonstatusResponseType();
        response.setResultat(filtererResultat(parameters, resultat));
        return response;
    }
}
