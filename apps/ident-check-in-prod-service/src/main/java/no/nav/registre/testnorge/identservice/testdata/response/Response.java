package no.nav.registre.testnorge.identservice.testdata.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.definition.TpsServiceRoutineDefinitionRequest;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.requests.TpsRequestContext;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Response {

    private ResponseStatus status;
    private String rawXml;
    private Integer totalHits;
    private List<String> dataXmls;
    private TpsRequestContext context;
    private TpsServiceRoutineDefinitionRequest serviceRoutine;

    public Response(String rawXml, TpsRequestContext context, TpsServiceRoutineDefinitionRequest serviceRoutine) {
        this.rawXml = rawXml;
        this.context = context;
        this.serviceRoutine = serviceRoutine;
    }

    public void addDataXml(String xml) {
        if (dataXmls == null) {
            dataXmls = new ArrayList<>();
        }
        dataXmls.add(xml);
    }

}
