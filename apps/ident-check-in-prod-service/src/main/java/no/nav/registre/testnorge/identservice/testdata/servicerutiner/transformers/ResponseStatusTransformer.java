package no.nav.registre.testnorge.identservice.testdata.servicerutiner.transformers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseStatusTransformer implements ResponseTransformer {

    private String xmlElement;

    public static ResponseTransformer extractStatusFromXmlElement(String xmlElement) {
        return new ResponseStatusTransformer(xmlElement);
    }
}
