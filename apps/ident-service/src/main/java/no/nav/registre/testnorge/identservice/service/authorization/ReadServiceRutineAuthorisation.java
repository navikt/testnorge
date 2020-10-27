package no.nav.registre.testnorge.identservice.service.authorization;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.authorization.ServiceRutineAuthorisationStrategy;

@JsonIgnoreType
public class ReadServiceRutineAuthorisation implements ServiceRutineAuthorisationStrategy {
    public static ReadServiceRutineAuthorisation readAuthorisation(){
        return new ReadServiceRutineAuthorisation();
    }
}
