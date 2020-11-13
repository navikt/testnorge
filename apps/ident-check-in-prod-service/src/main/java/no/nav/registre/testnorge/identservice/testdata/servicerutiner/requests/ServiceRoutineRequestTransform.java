package no.nav.registre.testnorge.identservice.testdata.servicerutiner.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.transformers.RequestTransformer;

@JsonIgnoreType
public class ServiceRoutineRequestTransform implements RequestTransformer {

    public static RequestTransformer serviceRoutineXmlWrappingAppender() {
        return new ServiceRoutineRequestTransform();
    }
}