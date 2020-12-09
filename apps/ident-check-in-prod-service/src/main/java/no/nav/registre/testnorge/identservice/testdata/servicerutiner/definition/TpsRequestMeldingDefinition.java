package no.nav.registre.testnorge.identservice.testdata.servicerutiner.definition;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import no.nav.registre.testnorge.identservice.testdata.config.SkdParametersCreator;
import no.nav.registre.testnorge.identservice.testdata.config.TpsRequestConfig;

@Getter
@Setter
public class TpsRequestMeldingDefinition {

    private String name;

    @JsonIgnore
    private TpsRequestConfig config;

    private SkdParametersCreator skdParametersCreator;
}
