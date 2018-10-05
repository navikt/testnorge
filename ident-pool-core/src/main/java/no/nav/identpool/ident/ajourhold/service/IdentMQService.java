package no.nav.identpool.ident.ajourhold.service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.jms.JMSException;
import javax.xml.bind.JAXB;

import com.google.common.collect.Lists;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import no.nav.identpool.ident.ajourhold.mq.QueueContext;
import no.nav.identpool.ident.ajourhold.mq.consumer.MessageQueue;
import no.nav.identpool.ident.ajourhold.mq.factory.MessageQueueFactory;
import no.nav.identpool.ident.ajourhold.tps.xml.service.NavnOpplysning;
import no.nav.tps.ctg.m201.domain.PersondataFraTpsM201;
import no.nav.tps.ctg.m201.domain.TpsPersonData;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentMQService {

    private final MessageQueueFactory messageQueueFactory;

    public Map<String, Boolean> fnrsExists(List<String> fnr) {
        return fnrsExists(QueueContext.getIncluded(), fnr);
    }

    public Map<String, Boolean> fnrsExists(List<String> environments, List<String> fnrs) {
        HashSet<String> nonexistent = new HashSet<>(fnrs);
        HashSet<String> exists = new HashSet<>();
        for (String environment : environments) {
            if (nonexistent.isEmpty()) {
                break;
            }
            try {
                filterTpsQueue(environment, nonexistent, exists);
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
        }

        HashMap<String, Boolean> filteredMap = new HashMap<>();
        nonexistent.forEach(fnr -> filteredMap.put(fnr, Boolean.FALSE));
        exists.forEach(fnr -> filteredMap.put(fnr, Boolean.TRUE));
        return filteredMap;
    }

    private void filterTpsQueue(String environment, HashSet<String> nonexistent, HashSet<String> exists) throws JMSException {

        MessageQueue messageQueue;
        try {
            messageQueue = messageQueueFactory.createMessageQueue(environment);
        } catch (JMSException e) {
            log.warn(e.getMessage());
            messageQueue = messageQueueFactory.createMessageQueueIgnoreCache(environment);
            messageQueue.ping();
        }

        Consumer<PersondataFraTpsM201.AFnr.EFnr> filterExisting = personData -> {
            if (personData.getSvarStatus() == null || "00".equals(personData.getSvarStatus().getReturStatus())) {
                nonexistent.remove(personData.getFnr());
                exists.add(personData.getFnr());
            }
        };

        for (List<String> list : Lists.partition(new ArrayList<>(nonexistent), 80)) {
            String message = new NavnOpplysning(list).toXml();
            String response = messageQueue.sendMessage(message);
            TpsPersonData data = JAXB.unmarshal(new StringReader(response), TpsPersonData.class);
            if (data.getTpsSvar().getIngenReturData() != null) {
                return;
            }
            data.getTpsSvar().getPersonDataM201().getAFnr().getEFnr().forEach(filterExisting);
        }
    }
}

