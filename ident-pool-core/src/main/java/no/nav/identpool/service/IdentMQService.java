package no.nav.identpool.service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.jms.JMSException;
import javax.xml.bind.JAXB;

import no.nav.tps.ctg.m201.domain.StatusFraTPSType;
import org.springframework.stereotype.Service;
import com.google.common.collect.Lists;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.identpool.ajourhold.mq.QueueContext;
import no.nav.identpool.ajourhold.mq.consumer.MessageQueue;
import no.nav.identpool.ajourhold.mq.factory.MessageQueueFactory;
import no.nav.identpool.ajourhold.tps.xml.service.NavnOpplysning;
import no.nav.tps.ctg.m201.domain.TpsPersonData;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentMQService {

    private static final int MAX_SIZE_TPS_QUEUE = 80;
    private static final String TPS_I_BRUK = "00";
    private static final String TPS_ENDRET_I_BRUK = "04";

    private final MessageQueueFactory messageQueueFactory;

    private MessageQueue messageQueue;


    public Map<String, Boolean> checkInTps(List<String> fnr) {
        return checkInTps(QueueContext.getIncluded(), fnr);
    }

    public Map<String, Boolean> checkInTps(List<String> environments, List<String> fnrs) {
        //TODO Skurrer noe at denne sendes videre for manipulering. Burde heller bruke putAll med response fra filterTpsQueue
        Map<String, Boolean> identer = populateDefaults(fnrs);

        for (String environment : environments) {
            try {
                filterTpsQueue(environment, identer);
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
        }
        return identer;
    }

    private Map<String, Boolean> populateDefaults(List<String> fnrs) {
        return fnrs.stream().collect(Collectors.toMap(fnr -> fnr, fnr -> Boolean.FALSE));
    }

    private void filterTpsQueue(String environment, Map<String, Boolean> identer) throws JMSException {
        //TODO Denne vil alltid være lik som List<String> fnrs som kommer inn i checkInTps
        List<String> finnesIkke = filterNonExisting(identer);

        if (finnesIkke.isEmpty()) {
            return;
        }
        initMq(environment);

        for (List<String> list : Lists.partition(new ArrayList<>(finnesIkke), MAX_SIZE_TPS_QUEUE)) {
            TpsPersonData data = getFromTps(list);
            if (data.getTpsSvar().getIngenReturData() != null) {
                return;
            }
            updateIdents(data, identer);
        }
    }

    private void initMq(String environment) throws JMSException {
        try {
            messageQueue = messageQueueFactory.createMessageQueue(environment);
        } catch (JMSException e) {
            log.warn(e.getMessage());
            messageQueue = messageQueueFactory.createMessageQueueIgnoreCache(environment);
            messageQueue.ping();
        }
    }

    //TODO Denne burde ikke være nødvendig..
    private List<String> filterNonExisting(Map<String, Boolean> identer) {
        return identer.entrySet()
                    .stream()
                    .filter(entry -> !entry.getValue())
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
    }

    private TpsPersonData getFromTps(List<String> list) throws JMSException {
        String message = new NavnOpplysning(list).toXml();
        String response = messageQueue.sendMessage(message);
        return JAXB.unmarshal(new StringReader(response), TpsPersonData.class);
    }

    private void updateIdents(TpsPersonData data, Map<String, Boolean> identer) {
        data.getTpsSvar().getPersonDataM201().getAFnr().getEFnr()
                .forEach(personData -> {
                    StatusFraTPSType svarStatus = personData.getSvarStatus();
                    if (svarStatus == null || TPS_I_BRUK.equals(svarStatus.getReturStatus())) {
                        identer.put(personData.getFnr(), Boolean.TRUE);
                    } else if (TPS_ENDRET_I_BRUK.equals(svarStatus.getReturStatus())) {
                        identer.put(personData.getForespurtFnr(), Boolean.TRUE);
                    }
                    //TODO Får vi tilbake de som det ikke er treff og heller ikke i bruk så vi kan gjøre
                    // identer.put(personData.getForespurtFnr(), Boolean.FALSE); for å støtte endringer tidligere i koden?
                });
    }
}

