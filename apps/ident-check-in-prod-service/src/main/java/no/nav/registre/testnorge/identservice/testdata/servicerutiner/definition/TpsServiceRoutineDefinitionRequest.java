package no.nav.registre.testnorge.identservice.testdata.servicerutiner.definition;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.TpsParameter;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.transformers.Transformer;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class TpsServiceRoutineDefinitionRequest extends TpsRequestMeldingDefinition {

    private static final String REQUIRED = "required";

    private String internalName;    // (DisplayName)

    @JsonIgnore
    private Class<?> javaClass;

    private List<TpsParameter> parameters;

    @JsonIgnore
    private List<Transformer> transformers;

    @JsonIgnore
    public List<String> getRequiredParameterNameList() {
        return parameters.stream().filter(parameter -> REQUIRED.equals(parameter.getUse())).map(TpsParameter::getName).collect(Collectors.toList());
    }
}
