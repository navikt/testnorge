package no.nav.registre.tss.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;
import no.nav.registre.tss.consumer.rs.TssSyntetisererenConsumer;
import no.nav.registre.tss.consumer.rs.response.TssMessage;
import no.nav.registre.tss.domain.Person;
import no.nav.registre.tss.domain.TssType;
import no.nav.registre.tss.provider.rs.request.SyntetiserTssRequest;
import no.nav.registre.tss.utils.RutineUtil;

@Slf4j
@Service
public class SyntetiseringService {

    private static final int MIN_ALDER = 25;
    private static final int MAX_ALDER = 70;

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;

    @Autowired
    private TssSyntetisererenConsumer tssSyntetisererenConsumer;

    @Autowired
    private JmsService jmsService;

    public List<Person> hentIdenter(SyntetiserTssRequest syntetiserTssRequest) {
        Map<String, JsonNode> identerMedStatusQuo = hodejegerenConsumer.getStatusQuo(
                syntetiserTssRequest.getAvspillergruppeId(),
                syntetiserTssRequest.getMiljoe(),
                syntetiserTssRequest.getAntallNyeIdenter(),
                MIN_ALDER,
                MAX_ALDER);

        List<Person> identer = new ArrayList<>(identerMedStatusQuo.size());
        for (Map.Entry<String, JsonNode> entry : identerMedStatusQuo.entrySet()) {
            JsonNode jsonNode = entry.getValue();
            identer.add(new Person(
                    entry.getKey(),
                    jsonNode.findValue("gjeldendePersonnavn").asText()));
        }
        return identer;
    }

    public List<String> opprettSyntetiskeTssRutiner(List<Person> identer, TssType type) {
        Map<String, List<TssMessage>> syntetiskeTssRutiner = tssSyntetisererenConsumer.hentSyntetiskeTssRutiner(identer);
        List<String> flatfiler = new ArrayList<>(syntetiskeTssRutiner.values().size());

        for (List<TssMessage> rutiner : syntetiskeTssRutiner.values()) {
            flatfiler.add(RutineUtil.opprettFlatfil(rutiner, type));
        }

        return flatfiler;
    }

    public void sendTilTss(List<String> tssRutiner, String miljoe) {
        String koeNavn = jmsService.hentKoeNavnAjour(miljoe);
        jmsService.sendTilTss(tssRutiner, koeNavn);
    }
}
