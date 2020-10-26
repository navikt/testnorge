package no.nav.registre.testnorge.identservice.testdata.servicerutiner.definition;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.authorization.ServiceRutineAuthorisationStrategy;

import java.util.List;

@Getter
@Setter
public class DBRequestMeldingDefinition {

    @JsonIgnore
    private List<ServiceRutineAuthorisationStrategy> requiredSecurityServiceStrategies;
}
