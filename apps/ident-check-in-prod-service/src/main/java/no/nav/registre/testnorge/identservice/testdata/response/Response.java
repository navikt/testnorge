package no.nav.registre.testnorge.identservice.testdata.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

    public Response(String rawXml, TpsRequestContext context) {
        this.rawXml = rawXml;
        this.context = context;
    }

    public void addDataXml(String xml) {
        if (dataXmls == null) {
            dataXmls = new ArrayList<>();
        }
        dataXmls.add(xml);
    }

}
