package no.nav.registre.testnorge.identservice.service.authorization;

import no.nav.registre.testnorge.identservice.testdata.servicerutiner.authorization.ForbiddenCallHandlerService;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.definition.DBRequestMeldingDefinition;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.definition.TpsRequestMeldingDefinition;
import org.springframework.stereotype.Service;

@Service
public class DefaultForbiddenCallHandlerService implements ForbiddenCallHandlerService {


    @Override
    public void authoriseRestCall(DBRequestMeldingDefinition serviceRoutine) {

    }

    @Override
    public void authorisePersonSearch(DBRequestMeldingDefinition serviceRoutine, String fnr) {

    }

    @Override
    public boolean isAuthorisedToUseServiceRutine(TpsRequestMeldingDefinition serviceRoutine) {
        return false;
    }

    @Override
    public boolean isAuthorisedToFetchPersonInfo(TpsRequestMeldingDefinition serviceRoutine, String fnr) {
        return false;
    }
}
