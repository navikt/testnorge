package no.nav.registre.endringsmeldinger.service.utils;

import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import no.nav.registre.endringsmeldinger.consumer.rs.responses.RsPureXmlMessageResponse;
import no.nav.registre.endringsmeldinger.provider.rs.responses.StatusFraFeiledeMeldingerTpsResponse;

@Slf4j
public final class StatusFraTpsUtil {

    public static StatusFraFeiledeMeldingerTpsResponse trekkUtStatusFraTps(List<RsPureXmlMessageResponse> responses) throws ParserConfigurationException {
        StatusFraFeiledeMeldingerTpsResponse statusFraFeiledeMeldinger = StatusFraFeiledeMeldingerTpsResponse.builder().offentligIdentMedUtfyllendeMelding(new ArrayList<>()).build();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        DocumentBuilder builder;
        for (RsPureXmlMessageResponse rsPureXmlMessageResponse : responses) {
            try {
                builder = factory.newDocumentBuilder();
                Document document = builder.parse(new InputSource(new StringReader(rsPureXmlMessageResponse.getXml())));
                if (!"00".equals(document.getElementsByTagName("returStatus").item(0).getTextContent())) {
                    String offentligIdent = document.getElementsByTagName("offentligIdent").item(0).getTextContent();
                    String utfyllendeMelding = document.getElementsByTagName("utfyllendeMelding").item(0).getTextContent();
                    statusFraFeiledeMeldinger.getOffentligIdentMedUtfyllendeMelding().add(String.format("%s - %s", offentligIdent, utfyllendeMelding));
                }
            } catch (ParserConfigurationException | SAXException | IOException e) {
                log.warn("Kunne ikke hente status på sendt melding.", e);
            }
        }

        return statusFraFeiledeMeldinger;
    }
}
