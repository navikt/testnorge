package no.nav.registre.testnorge.identservice.testdata.transformation.request;


import no.nav.registre.testnorge.identservice.testdata.servicerutiner.requests.Request;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.transformers.Transformer;
import no.nav.registre.testnorge.identservice.testdata.transformation.TransformStrategy;

public interface RequestTransformStrategy extends TransformStrategy {
    void execute(Request request, Transformer transformer);
}
