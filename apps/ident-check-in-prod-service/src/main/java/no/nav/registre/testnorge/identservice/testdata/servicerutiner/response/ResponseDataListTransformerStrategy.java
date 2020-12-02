package no.nav.registre.testnorge.identservice.testdata.servicerutiner.response;

import no.nav.registre.testnorge.identservice.testdata.response.Response;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ResponseDataListTransformerStrategy {

    public void execute(Response response) {

        String xmlElement = "personDataM201";
        String xmlElementSingleResource = "EFnr";
        String xmlElementTotalHits = "antallFM201";

        String xml = extractContentInXmlElement(response.getRawXml(), xmlElement, "");
        String totalHits = extractContentInXmlElement(xml, xmlElementTotalHits, "0");
        List<String> allResults = extractAllContentInXmlElement(xml, xmlElementSingleResource);

        response.setDataXmls(allResults);
        response.setTotalHits(Integer.parseInt(totalHits));
    }

    private List<String> extractAllContentInXmlElement(String xml, String xmlElement) {
        String pattern = "<" + xmlElement + ">(.+?)</" + xmlElement + ">";
        Matcher matcher = Pattern.compile(pattern, Pattern.DOTALL).matcher(xml);
        List<String> allResultsAsXml = new ArrayList<>();
        while (matcher.find()) {
            allResultsAsXml.add(matcher.group(1));
        }
        return allResultsAsXml;
    }

    private String extractContentInXmlElement(String xml, String xmlElement, String defaultValue) {
        String pattern = "<" + xmlElement + ">(.+?)</" + xmlElement + ">";
        Matcher matcher = Pattern.compile(pattern, Pattern.DOTALL).matcher(xml);
        return matcher.find() ? matcher.group(1) : defaultValue;
    }
}
