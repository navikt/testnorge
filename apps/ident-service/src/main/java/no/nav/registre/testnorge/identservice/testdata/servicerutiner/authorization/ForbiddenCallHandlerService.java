package no.nav.registre.testnorge.identservice.testdata.servicerutiner.authorization;


import no.nav.registre.testnorge.identservice.testdata.servicerutiner.definition.DBRequestMeldingDefinition;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.definition.TpsRequestMeldingDefinition;

public interface ForbiddenCallHandlerService {

    void authoriseRestCall(DBRequestMeldingDefinition serviceRoutine);

    void authorisePersonSearch(DBRequestMeldingDefinition serviceRoutine, String fnr);

    boolean isAuthorisedToUseServiceRutine(TpsRequestMeldingDefinition serviceRoutine);

    boolean isAuthorisedToFetchPersonInfo(TpsRequestMeldingDefinition serviceRoutine, String fnr);

}
