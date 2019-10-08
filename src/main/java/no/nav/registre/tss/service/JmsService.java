package no.nav.registre.tss.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import no.nav.registre.tss.consumer.rs.responses.Response910;
import no.nav.registre.tss.utils.Response910Util;
import no.nav.registre.tss.utils.Rutine910Util;

@Service
@Slf4j
public class JmsService {

    private static final Set<String> STOETTEDE_MILJOER = new HashSet<>(Arrays.asList("q1", "q2"));

    @Autowired
    private JmsTemplate jmsTemplate;

    @Value("${queue.queueNameAjourhold.Q1}")
    private String ajourholdQ1;

    @Value("${queue.queueNameAjourhold.Q2}")
    private String ajourholdQ2;

    @Value("${queue.queueNameSamhandlerService.Q1}")
    private String samhandlerQ1;

    @Value("${queue.queueNameSamhandlerService.Q2}")
    private String samhandlerQ2;

    public void sendTilTss(List<String> meldinger, String koeNavn) {
        try {
            for (String melding : meldinger) {
                jmsTemplate.convertAndSend("queue:///" + koeNavn + "?targetClient=1", melding);
            }
        } catch (Exception e) {
            log.error("Kunne ikke sende til kø", e);
            throw e;
        }
    }

    public Map<String, Response910> sendOgMotta910RutineFraTss(List<String> utvalgteLeger, String koeNavn) throws JMSException {
        Map<String, Response910> legerMedRespons = new HashMap<>(utvalgteLeger.size());

        for (String lege : utvalgteLeger) {
            String rutine910 = Rutine910Util.opprettRutine(lege);

            Message mottattMelding = jmsTemplate.sendAndReceive("queue:///" + koeNavn + "?targetClient=1", session -> session.createTextMessage(rutine910));

            if (mottattMelding != null) {
                Response910 response = Response910Util.parseResponse(mottattMelding.getBody(String.class));
                if (!response.getResponse110().isEmpty() || !response.getResponse111().isEmpty() || !response.getResponse125().isEmpty()) {
                    legerMedRespons.put(lege, Response910Util.parseResponse(mottattMelding.getBody(String.class)));
                }
            } else {
                log.warn("Fikk ikke svar fra TSS for lege {}", lege.replaceAll("[\r\n]", ""));
            }
        }

        return legerMedRespons;
    }

    public Response910 sendOgMotta910RutineFraTss(String lege, String koeNavn) throws JMSException {
        String rutine910 = Rutine910Util.opprettRutine(lege);

        Message mottattMelding = jmsTemplate.sendAndReceive("queue:///" + koeNavn + "?targetClient=1", session -> session.createTextMessage(rutine910));

        if (mottattMelding != null) {
            return Response910Util.parseResponse(mottattMelding.getBody(String.class));
        } else {
            log.warn("Fikk ikke svar fra TSS for lege {}", lege.replaceAll("[\r\n]", ""));
        }
        return null;
    }

    public Object sendOgMotta990RutineFraTss(String message, String koeNavn) throws JMSException {
        Message mottattMelding = jmsTemplate.sendAndReceive("queue:///" + koeNavn + "?targetClient=1", session -> session.createTextMessage(message));

        if (mottattMelding != null) {
            return mottattMelding.getBody(String.class);
        } else {
            log.warn("Fikk ikke svar fra TSS for rutine 990");
        }
        return null;
    }

    public String hentKoeNavnAjour(String miljoe) {
        validerMiljoe(miljoe);
        if ("q1".equals(miljoe)) {
            return ajourholdQ1;
        } else if ("q2".equals(miljoe)) {
            return ajourholdQ2;
        }
        return "";
    }

    public String hentKoeNavnSamhandler(String miljoe) {
        validerMiljoe(miljoe);
        if ("q1".equals(miljoe)) {
            return samhandlerQ1;
        } else if ("q2".equals(miljoe)) {
            return samhandlerQ2;
        }
        return "";
    }

    private void validerMiljoe(String miljoe) {
        if (!STOETTEDE_MILJOER.contains(miljoe.toLowerCase())) {
            throw new IllegalArgumentException("Miljø " + miljoe + " er ikke støttet. Støttede miljøer: " + STOETTEDE_MILJOER);
        }
    }
}
