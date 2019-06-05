package no.nav.registre.endringsmeldinger.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.endringsmeldinger.consumer.rs.HodejegerenConsumer;
import no.nav.registre.endringsmeldinger.consumer.rs.NavEndringsmeldingerSyntetisererenConsumer;
import no.nav.registre.endringsmeldinger.consumer.rs.TpsfConsumer;
import no.nav.registre.endringsmeldinger.consumer.rs.requests.SendTilTpsRequest;
import no.nav.registre.endringsmeldinger.consumer.rs.responses.RsPureXmlMessageResponse;
import no.nav.registre.endringsmeldinger.provider.rs.requests.SyntetiserNavEndringsmeldingerRequest;

@Service
@Slf4j
public class EndringsmeldingService {

    private static final Long TIMEOUT_SEKUNDER = 5L;

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;

    @Autowired
    private TpsfConsumer tpsfConsumer;

    @Autowired
    private NavEndringsmeldingerSyntetisererenConsumer navEndringsmeldingerSyntetisererenConsumer;

    @Autowired
    private Random rand;

    @Value("${SFE_ENDRINGSMELDING.queueName}")
    private String queueName;

    public List<RsPureXmlMessageResponse> opprettSyntetiskeNavEndringsmeldinger(SyntetiserNavEndringsmeldingerRequest syntetiserNavEndringsmeldingerRequest) throws TransformerException {
        final Map<String, Integer> antallMeldingerPerEndringskode = syntetiserNavEndringsmeldingerRequest.getAntallMeldingerPerEndringskode();
        final List<Endringskoder> filtrerteEndringskoder = filtrerEndringskoder(antallMeldingerPerEndringskode.keySet());
        List<String> utvalgteIdenter = hentLevendeIdenter(syntetiserNavEndringsmeldingerRequest);
        List<SendTilTpsRequest> sendTilTpsRequests = new ArrayList<>(utvalgteIdenter.size());
        List<RsPureXmlMessageResponse> responses = new ArrayList<>();

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "no");

        for (Endringskoder endringskode : filtrerteEndringskoder) {
            List<Document> syntetiserteNavEndringsmeldinger = navEndringsmeldingerSyntetisererenConsumer
                    .getSyntetiserteNavEndringsmeldinger(endringskode.getEndringskode(), antallMeldingerPerEndringskode.get(endringskode.getEndringskode())).getBody();

            if (syntetiserteNavEndringsmeldinger != null) {
                for (Document document : syntetiserteNavEndringsmeldinger) {
                    Node offentligIdent = document.getElementsByTagName("offentligIdent").item(0);
                    offentligIdent.setTextContent(utvalgteIdenter.remove(rand.nextInt(utvalgteIdenter.size())));
                    StringWriter stringWriter = new StringWriter();
                    transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
                    String output = stringWriter.toString().replaceAll("\n|\r", "");
                    sendTilTpsRequests.add(SendTilTpsRequest.builder()
                            .miljoe(syntetiserNavEndringsmeldingerRequest.getMiljoe())
                            .ko(queueName)
                            .melding(output)
                            .timeout(TIMEOUT_SEKUNDER)
                            .build());
                }
            }
        }

        for (SendTilTpsRequest sendTilTpsRequest : sendTilTpsRequests) {
            responses.add(sendEndringsmeldingerTilTps(sendTilTpsRequest));
        }

        return responses;
    }

    private RsPureXmlMessageResponse sendEndringsmeldingerTilTps(SendTilTpsRequest sendTilTpsRequest) {
        RsPureXmlMessageResponse rsPureXmlMessageResponse = tpsfConsumer.sendEndringsmeldingTilTps(sendTilTpsRequest);
        log.info("Endringsmelding sendt til tps: {}", rsPureXmlMessageResponse.getXml());
        return rsPureXmlMessageResponse;
    }

    private List<String> hentLevendeIdenter(SyntetiserNavEndringsmeldingerRequest syntetiserNavEndringsmeldingerRequest) {
        int antallMeldingerTotalt = 0;
        for (Integer antallMeldinger : syntetiserNavEndringsmeldingerRequest.getAntallMeldingerPerEndringskode().values()) {
            antallMeldingerTotalt += antallMeldinger;
        }

        List<String> levendeIdenter = hodejegerenConsumer.finnLevendeIdenter(syntetiserNavEndringsmeldingerRequest.getAvspillergruppeId());
        List<String> utvalgteIdenter = new ArrayList<>(antallMeldingerTotalt);

        for (int i = 0; i < antallMeldingerTotalt; i++) {
            utvalgteIdenter.add(levendeIdenter.remove(rand.nextInt(levendeIdenter.size())));
        }

        return utvalgteIdenter;
    }

    private List<Endringskoder> filtrerEndringskoder(Set<String> endringskoder) {
        List<Endringskoder> filtrerteEndringskoder = Arrays.asList(Endringskoder.values());
        return filtrerteEndringskoder.stream().filter(kode -> endringskoder.contains(kode.getEndringskode())).collect(Collectors.toList());
    }
}
