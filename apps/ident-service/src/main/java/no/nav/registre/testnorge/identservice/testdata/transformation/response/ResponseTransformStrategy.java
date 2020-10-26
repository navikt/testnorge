package no.nav.registre.testnorge.identservice.testdata.transformation.response;


import no.nav.registre.testnorge.identservice.testdata.response.Response;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.transformers.Transformer;
import no.nav.registre.testnorge.identservice.testdata.transformation.TransformStrategy;

public interface ResponseTransformStrategy extends TransformStrategy {

    void execute(Response response, Transformer transformer);

}
