package no.nav.registre.testnorge.identservice.testdata.servicerutiner.response;

import no.nav.registre.testnorge.identservice.testdata.response.Response;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.transformers.Transformer;
import no.nav.registre.testnorge.identservice.testdata.transformation.response.ResponseDataTransformer;
import no.nav.registre.testnorge.identservice.testdata.transformation.response.ResponseTransformStrategy;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ResponseDataTransformStrategy implements ResponseTransformStrategy {


    @Override
    public void execute(Response response, Transformer transformer) {
        String xmlElement = ((ResponseDataTransformer) transformer).getXmlElement();
        response.addDataXml(extractDataFromXmlWithWrappingXmlElement(response.getRawXml(), xmlElement));
    }

    private String extractDataFromXmlWithWrappingXmlElement(String xml, String xmlElement) {
        String pattern = "<"+xmlElement+">(.+?)</"+xmlElement+">";
        Matcher matcher = Pattern.compile(pattern, Pattern.DOTALL).matcher(xml);
        return matcher.find() ? matcher.group(1) : "";
    }

    @Override
    public boolean isSupported(Object o) {
        return o instanceof ResponseDataTransformer;
    }
}
