package no.nav.registre.tss.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;
import no.nav.registre.tss.consumer.rs.TssSyntetisererenConsumer;
import no.nav.registre.tss.consumer.rs.responses.Response910;
import no.nav.registre.tss.consumer.rs.responses.TssSyntMessage;
import no.nav.registre.tss.domain.Person;
import no.nav.registre.tss.provider.rs.requests.SyntetiserTssRequest;
import no.nav.registre.tss.utils.Response910Util;
import no.nav.registre.tss.utils.Rutine910Util;
import no.nav.registre.tss.utils.RutineUtil;

@Slf4j
@Service
public class TssService {

    private static final int MIN_ALDER = 25;
    private static final int MAX_ALDER = 60;

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
        Map<String, List<TssSyntMessage>> syntetiskeTssRutiner = hentSyntetiskeTssRutiner(identer);
        List<String> flatfiler = new ArrayList<>();

        for (List<TssSyntMessage> rutiner : syntetiskeTssRutiner.values()) {
            flatfiler.add(RutineUtil.opprettFlatfil(rutiner));
        }

        return flatfiler;
    }

    public void sendTilTss(List<String> meldinger) {
        for (String melding : meldinger) {
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

    private Map<String, List<TssSyntMessage>> hentSyntetiskeTssRutiner(List<Person> personer) {
        return tssSyntetisererenConsumer.hentSyntetiskeTssRutiner(personer);
    }
}
