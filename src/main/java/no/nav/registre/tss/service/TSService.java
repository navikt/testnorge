package no.nav.registre.tss.service;

import static no.nav.registre.tss.utils.Rutine110Util.fiksPosisjoner;
import static no.nav.registre.tss.utils.Rutine110Util.leggTilHeader;
import static no.nav.registre.tss.utils.Rutine110Util.setOppdater;
import static no.nav.registre.tss.utils.RutineUtil.TOTAL_LENGTH;
import static no.nav.registre.tss.utils.RutineUtil.padTilLengde;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.registre.testnorge.consumers.HodejegerenConsumer;
import no.nav.registre.tss.consumer.rs.response.Response910;
import no.nav.registre.tss.consumer.rs.response.TssSyntetisererenConsumer;
import no.nav.registre.tss.domain.Person;
import no.nav.registre.tss.provider.rs.requests.SyntetiserTssRequest;
import no.nav.registre.tss.utils.Response910Util;
import no.nav.registre.tss.utils.Rutine910Util;

@Slf4j
@Service
public class TSService {

    private static int MIN_ALDER = 25;
    private static int MAX_ALDER = 60;

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;

    @Autowired
    private TssSyntetisererenConsumer tssSyntetisererenConsumer;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Value("${queue.queueNameAjourhold}")
    private String mqQueueNameAjourhold;

    @Value("${queue.queueNameSamhandlerService}")
    private String mqQueueNameSamhandlerService;

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

    public List<String> opprettSyntetiskeTssRutiner(List<Person> identer) {
        List<String> syntetiskeTssRutiner = hentSyntetiskeTssRutiner(identer);

        for (int i = 0; i < syntetiskeTssRutiner.size(); i++) {
            List<String> rutiner = new ArrayList<>(Arrays.asList(syntetiskeTssRutiner.get(i).split("\n")));
            StringBuilder s = new StringBuilder();
            for (int j = 0; j < rutiner.size(); j++) {
                rutiner.set(j, padTilLengde(rutiner.get(j)));
                if (rutiner.get(j).length() != TOTAL_LENGTH) {
                    throw new RuntimeException("Feil lengde på rutine");
                }
                if (rutiner.get(j).startsWith("110")) {
                    rutiner.set(j, fiksPosisjoner(rutiner.get(j)));
                    rutiner.set(j, setOppdater(rutiner.get(j)));
                    rutiner.set(j, leggTilHeader(rutiner.get(j)));
                }
                s.append(rutiner.get(j));
            }
            syntetiskeTssRutiner.set(i, s.toString());
        }

        return syntetiskeTssRutiner;
    }

    public void sendTilTss(List<String> meldinger) {
        for (String melding : meldinger) {
            log.info("Sender melding til TSS: {}", melding);
            jmsTemplate.convertAndSend("queue:///" + mqQueueNameAjourhold + "?targetClient=1", melding);
        }
    }

    public Map<String, Response910> sendOgMotta910RutineFraTss(Long avspillergruppeId, Integer antallLeger) throws JMSException {
        List<String> alleLeger = hodejegerenConsumer.getLevende(avspillergruppeId);
        List<String> utvalgteLeger = new ArrayList<>();
        if (antallLeger != null) {
            Collections.shuffle(alleLeger);
            utvalgteLeger.addAll(alleLeger.subList(0, antallLeger));
        } else {
            utvalgteLeger.addAll(alleLeger);
        }

        Map<String, Response910> legerMedRespons = new HashMap<>(utvalgteLeger.size());

        for (String lege : utvalgteLeger) {
            String rutine910 = Rutine910Util.opprettRutine(lege);

            Message mottattMelding = jmsTemplate.sendAndReceive("queue:///" + mqQueueNameSamhandlerService + "?targetClient=1", session -> session.createTextMessage(rutine910));

            if (mottattMelding != null) {
                legerMedRespons.put(lege, Response910Util.parseResponse(mottattMelding.getBody(String.class)));
            } else {
                log.warn("Fikk ikke svar fra TSS for lege {}", lege);
            }
        }

        return legerMedRespons;
    }

    public Response910 sendOgMotta910RutineFraTss(String lege) throws JMSException {
        String rutine910 = Rutine910Util.opprettRutine(lege);

        Message mottattMelding = jmsTemplate.sendAndReceive("queue:///" + mqQueueNameSamhandlerService + "?targetClient=1", session -> session.createTextMessage(rutine910));

        if (mottattMelding != null) {
            return Response910Util.parseResponse(mottattMelding.getBody(String.class));
        } else {
            log.warn("Fikk ikke svar fra TSS for lege {}", lege);
        }
        return null;
    }

    private List<String> hentSyntetiskeTssRutiner(List<Person> personer) {
        return tssSyntetisererenConsumer.hentSyntetiskeTssRutiner(personer);
    }
}
