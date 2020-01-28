package no.nav.registre.endringsmeldinger.service;

import static no.nav.registre.endringsmeldinger.service.utils.StatusFraTpsUtil.trekkUtStatusFraTps;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.parsers.ParserConfigurationException;
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
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.endringsmeldinger.consumer.rs.NavEndringsmeldingerSyntetisererenConsumer;
import no.nav.registre.endringsmeldinger.consumer.rs.TpsfConsumer;
import no.nav.registre.endringsmeldinger.consumer.rs.exceptions.SyntetiseringsException;
import no.nav.registre.endringsmeldinger.consumer.rs.requests.SendTilTpsRequest;
import no.nav.registre.endringsmeldinger.consumer.rs.responses.RsPureXmlMessageResponse;
import no.nav.registre.endringsmeldinger.provider.rs.requests.SyntetiserNavEndringsmeldingerRequest;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;

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

    public List<RsPureXmlMessageResponse> opprettSyntetiskeNavEndringsmeldinger(
            SyntetiserNavEndringsmeldingerRequest syntetiserNavEndringsmeldingerRequest
    ) throws TransformerException {
        final var antallMeldingerPerEndringskode = syntetiserNavEndringsmeldingerRequest.getAntallMeldingerPerEndringskode();
        final var filtrerteEndringskoder = filtrerEndringskoder(antallMeldingerPerEndringskode.keySet());
        var utvalgteIdenter = hentLevendeIdenter(syntetiserNavEndringsmeldingerRequest);
        List<SendTilTpsRequest> sendTilTpsRequests = new ArrayList<>(utvalgteIdenter.size());

        var tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "no");

        for (var endringskode : filtrerteEndringskoder) {
            try {
                var syntetiserteNavEndringsmeldinger = navEndringsmeldingerSyntetisererenConsumer
                        .getSyntetiserteNavEndringsmeldinger(endringskode.getEndringskode(), antallMeldingerPerEndringskode.get(endringskode.getEndringskode())).getBody();

                if (syntetiserteNavEndringsmeldinger != null) {
                    for (var document : syntetiserteNavEndringsmeldinger) {
                        var offentligIdent = document.getElementsByTagName("offentligIdent").item(0);
                        offentligIdent.setTextContent(utvalgteIdenter.remove(rand.nextInt(utvalgteIdenter.size())));
                        var stringWriter = new StringWriter();
                        transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
                        var output = stringWriter.toString().replaceAll("\n|\r", "");
                        sendTilTpsRequests.add(SendTilTpsRequest.builder()
                                .miljoe(syntetiserNavEndringsmeldingerRequest.getMiljoe())
                                .ko(queueName)
                                .melding(output)
                                .timeout(TIMEOUT_SEKUNDER)
                                .build());
                    }
                }
            } catch (SyntetiseringsException e) {
                log.warn("Kunne ikke hente syntetisert nav-endringsmelding for endringskode {}. ", endringskode.getEndringskode(), e);
            }
        }

        List<RsPureXmlMessageResponse> responsFraTps = new ArrayList<>(sendTilTpsRequests.size());

        for (var sendTilTpsRequest : sendTilTpsRequests) {
            responsFraTps.add(sendEndringsmeldingerTilTps(sendTilTpsRequest));
        }

        try {
            var statusFraFeiledeMeldinger = trekkUtStatusFraTps(responsFraTps).getOffentligIdentMedUtfyllendeMelding();
            log.info("Antall meldinger sendt til TPS: {}. Antall feilede meldinger: {}. Status på feilede meldinger: {}", responsFraTps.size(), statusFraFeiledeMeldinger.size(), statusFraFeiledeMeldinger.toString());
        } catch (ParserConfigurationException e) {
            log.warn("Kunne ikke opprette XML-parser", e);
        }

        return responsFraTps;
    }

    private RsPureXmlMessageResponse sendEndringsmeldingerTilTps(
            SendTilTpsRequest sendTilTpsRequest
    ) {
        return tpsfConsumer.sendEndringsmeldingTilTps(sendTilTpsRequest);
    }

    private List<String> hentLevendeIdenter(
            SyntetiserNavEndringsmeldingerRequest syntetiserNavEndringsmeldingerRequest
    ) {
        var antallMeldingerTotalt = 0;
        for (var antallMeldinger : syntetiserNavEndringsmeldingerRequest.getAntallMeldingerPerEndringskode().values()) {
            antallMeldingerTotalt += antallMeldinger;
        }

        var levendeIdenter = getLevendeIdenter(syntetiserNavEndringsmeldingerRequest.getAvspillergruppeId());
        List<String> utvalgteIdenter = new ArrayList<>(antallMeldingerTotalt);

        for (int i = 0; i < antallMeldingerTotalt; i++) {
            utvalgteIdenter.add(levendeIdenter.remove(rand.nextInt(levendeIdenter.size())));
        }

        return utvalgteIdenter;
    }

    private List<Endringskoder> filtrerEndringskoder(
            Set<String> endringskoder
    ) {
        var filtrerteEndringskoder = Arrays.asList(Endringskoder.values());
        return filtrerteEndringskoder.stream().filter(kode -> endringskoder.contains(kode.getEndringskode())).collect(Collectors.toList());
    }

    @Timed(value = "nav-endringsmeldinger.resource.latency", extraTags = { "operation", "hodejegeren" })
    private List<String> getLevendeIdenter(
            Long avspillergruppeId
    ) {
        return hodejegerenConsumer.getLevende(avspillergruppeId);
    }
}
