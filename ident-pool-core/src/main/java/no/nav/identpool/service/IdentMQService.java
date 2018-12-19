package no.nav.identpool.service;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.jms.JMSException;
import javax.xml.bind.JAXB;

import no.nav.identpool.service.support.QueueContext;
import no.nav.tps.ctg.m201.domain.StatusFraTPSType;
import org.springframework.stereotype.Service;
import com.google.common.collect.Lists;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private Map<String, Boolean> idents = new HashMap<>();

    public Map<String, Boolean> checkIdentsInTps(List<String> fnrs) {
        List<String> environments = QueueContext.getSuccessfulEnvs();
        idents = populateDefaults(fnrs);

        for (String environment : environments) {
            List<String> nonExisting = filterNonExisting();
            if (nonExisting.isEmpty()) {
                break;
            }
            checkInEnvironment(environment, nonExisting);
        }
        return idents;
    }

    private void checkInEnvironment(String environment, List<String> nonExisting) {
        try {
            initMq(environment);
            for (List<String> list : Lists.partition(nonExisting, MAX_SIZE_TPS_QUEUE)) {
                String response = messageQueue.sendMessage(new NavnOpplysning(list).toXml());
                TpsPersonData data = JAXB.unmarshal(new StringReader(response), TpsPersonData.class);
                if (data.getTpsSvar().getIngenReturData() == null) {
                    updateIdents(data);
                }
            }
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    private void initMq(String environment) throws JMSException {
        try {
            messageQueue = messageQueueFactory.createMessageQueue(environment);
        } catch (JMSException e) {
            log.warn(e.getMessage());
            messageQueue = messageQueueFactory.createMessageQueueIgnoreCache(environment);
            messageQueue.ping(); //Make sure it's alive
        }
    }

    private void updateIdents(TpsPersonData data) {
        data.getTpsSvar().getPersonDataM201().getAFnr().getEFnr()
                .forEach(personData -> {
                    StatusFraTPSType svarStatus = personData.getSvarStatus();
                    if (svarStatus == null || TPS_I_BRUK.equals(svarStatus.getReturStatus())) {
                        idents.put(personData.getFnr(), Boolean.TRUE);
                    } else if (TPS_ENDRET_I_BRUK.equals(svarStatus.getReturStatus())) {
                        idents.put(personData.getForespurtFnr(), Boolean.TRUE);
                    }
                });
    }

    private Map<String, Boolean> populateDefaults(List<String> fnrs) {
        return fnrs.stream().collect(Collectors.toMap(fnr -> fnr, fnr -> Boolean.FALSE));
    }

    private List<String> filterNonExisting() {
        return idents.entrySet().stream()
                .filter(entry -> !entry.getValue())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

}

