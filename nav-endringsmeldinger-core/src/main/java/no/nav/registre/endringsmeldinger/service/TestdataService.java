package no.nav.registre.endringsmeldinger.service;

import static no.nav.registre.endringsmeldinger.service.utils.NyttKontonummerUtil.opprettDokumentForNyttKontonummer;
import static no.nav.registre.endringsmeldinger.service.utils.StatusFraTpsUtil.trekkUtStatusFraTps;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import no.nav.registre.endringsmeldinger.consumer.rs.TpsfConsumer;
import no.nav.registre.endringsmeldinger.consumer.rs.requests.SendTilTpsRequest;
import no.nav.registre.endringsmeldinger.consumer.rs.responses.RsPureXmlMessageResponse;
import no.nav.registre.endringsmeldinger.provider.rs.requests.GenererKontonummerRequest;

@Service
@Slf4j
public class TestdataService {

    private static final Long TIMEOUT_SEKUNDER = 5L;

    @Autowired
    private TpsfConsumer tpsfConsumer;

    public List<RsPureXmlMessageResponse> genererKontonummerOgSendTilTps(String koeNavn, GenererKontonummerRequest genererKontonummerRequest) throws TransformerException {
        List<String> identer = genererKontonummerRequest.getIdenter();
        String kontonummer = genererKontonummerRequest.getKontonummer();

        List<Document> kontonummerEndringsmeldinger = new ArrayList<>();

        for (String ident : identer) {
            try {
                kontonummerEndringsmeldinger.add(opprettDokumentForNyttKontonummer(ident, kontonummer));
            } catch (ParserConfigurationException e) {
                log.warn("Kunne ikke opprette endringsmelding for ident {} med kontonummer {}.", ident, kontonummer, e);
            }
        }

        List<RsPureXmlMessageResponse> responsFraTps = new ArrayList<>(kontonummerEndringsmeldinger.size());

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "no");
        transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");

        for (Document endringsmelding : kontonummerEndringsmeldinger) {
            StringWriter stringWriter = new StringWriter();
            transformer.transform(new DOMSource(endringsmelding), new StreamResult(stringWriter));
            String output = stringWriter.toString().replaceAll("\n|\r", "");
            SendTilTpsRequest sendTilTpsRequest = SendTilTpsRequest.builder()
                    .miljoe(genererKontonummerRequest.getMiljoe())
                    .ko(koeNavn)
                    .melding(output)
                    .timeout(TIMEOUT_SEKUNDER)
                    .build();

            responsFraTps.add(tpsfConsumer.sendEndringsmeldingTilTps(sendTilTpsRequest));
        }

        try {
            List<String> statusFraFeiledeMeldinger = trekkUtStatusFraTps(responsFraTps).getOffentligIdentMedUtfyllendeMelding();
            log.info("Antall meldinger sendt til TPS: {}. Antall feilede meldinger: {}. Status på feilede meldinger: ", responsFraTps.size(), statusFraFeiledeMeldinger.size());
            StringBuilder sb = new StringBuilder("\r\n");
            for (String status : statusFraFeiledeMeldinger) {
                sb.append(status).append("\r\n");
            }
            log.info(sb.toString());
        } catch (ParserConfigurationException e) {
            log.warn("Kunne ikke opprette XML-parser", e);
        }

        return responsFraTps;
    }
}
