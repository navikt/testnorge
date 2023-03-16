package no.nav.udistub.provider.ws;

import lombok.RequiredArgsConstructor;
import no.nav.udistub.service.PersonService;
import no.nav.udistub.service.dto.UdiPerson;
import no.udi.common.v2.PingRequestType;
import no.udi.common.v2.PingResponseType;
import no.udi.mt_1067_nav_data.v1.HentPersonstatusRequestType;
import no.udi.mt_1067_nav_data.v1.HentPersonstatusResponseType;
import no.udi.mt_1067_nav_data.v1.HentPersonstatusResultat;
import no.udi.mt_1067_nav_data.v1.HentUtvidetPersonstatusRequestType;
import no.udi.mt_1067_nav_data.v1.HentUtvidetPersonstatusResponseType;
import no.udi.mt_1067_nav_data.v1.HentUtvidetPersonstatusResultat;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;

@Service
@RequiredArgsConstructor
public class PersonStatusService {

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

    public PingResponseType ping(PingRequestType parameters) {

        return new PingResponseType();
    }

    public PingResponseType deepPing(PingRequestType parameters) {

        return new PingResponseType();
    }

    public HentPersonstatusResponseType hentPersonstatus(HentPersonstatusRequestType parameters) {

        UdiPerson foundPerson = personService.finnPerson(parameters.getParameter().getFodselsnummer());
        var resultat = conversionService.convert(foundPerson, HentPersonstatusResultat.class);

        var response = new HentPersonstatusResponseType();
        response.setResultat(filtrerResultat(parameters, resultat));
        return response;
    }

    public HentUtvidetPersonstatusResponseType hentUtvidetPersonstatus(HentUtvidetPersonstatusRequestType parameters) {

        UdiPerson foundPerson = personService.finnPerson(parameters.getParameter().getFodselsnummer());
        var resultat = conversionService.convert(foundPerson, HentUtvidetPersonstatusResultat.class);

        var response = new HentUtvidetPersonstatusResponseType();
        response.setResultat(filtererResultat(parameters, resultat));
        return response;
    }
}
