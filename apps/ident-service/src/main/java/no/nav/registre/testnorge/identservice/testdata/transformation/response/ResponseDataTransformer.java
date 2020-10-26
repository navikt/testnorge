package no.nav.registre.testnorge.identservice.testdata.transformation.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.transformers.ResponseTransformer;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDataTransformer implements ResponseTransformer {

    private String xmlElement;

    public static ResponseTransformer extractDataFromXmlElement(String xmlElement) {
        return new ResponseDataTransformer(xmlElement);
    }
}
