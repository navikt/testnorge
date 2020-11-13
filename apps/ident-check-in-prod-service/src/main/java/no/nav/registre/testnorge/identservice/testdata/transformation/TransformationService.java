package no.nav.registre.testnorge.identservice.testdata.transformation;

import no.nav.registre.testnorge.identservice.testdata.response.Response;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.definition.TpsServiceRoutineDefinitionRequest;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.requests.Request;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.transformers.Transformer;
import no.nav.registre.testnorge.identservice.testdata.transformation.request.RequestTransformStrategy;
import no.nav.registre.testnorge.identservice.testdata.transformation.response.ResponseTransformStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.nonNull;


@Service
public class TransformationService {

    @Autowired
    private List<RequestTransformStrategy> requestStrategies;

    @Autowired
    private List<ResponseTransformStrategy> responseStrategies;

    public void transform(Request request, TpsServiceRoutineDefinitionRequest serviceRoutine) {
        if (nonNull(serviceRoutine.getTransformers())) {
            for (Transformer transformer : serviceRoutine.getTransformers()) {
                for (RequestTransformStrategy strategy : requestStrategies) {
                    if (strategy.isSupported(transformer)) {
                        strategy.execute(request, transformer);
                    }
                }
            }
        }
    }

    public void transform(Response response, TpsServiceRoutineDefinitionRequest serviceRoutine) {
        if (nonNull(serviceRoutine.getTransformers())) {
            for (Transformer transformer : serviceRoutine.getTransformers()) {
                for (ResponseTransformStrategy strategy : responseStrategies) {
                    if (strategy.isSupported(transformer)) {
                        strategy.execute(response, transformer);
                    }
                }
            }
        }
    }

}
