package no.nav.identpool.service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.jms.JMSException;
import javax.xml.bind.JAXB;

import org.springframework.stereotype.Service;
import com.google.common.collect.Lists;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.identpool.ajourhold.mq.QueueContext;
import no.nav.identpool.ajourhold.mq.consumer.MessageQueue;
import no.nav.identpool.ajourhold.mq.factory.MessageQueueFactory;
import no.nav.identpool.ajourhold.tps.xml.service.NavnOpplysning;
import no.nav.tps.ctg.m201.domain.PersondataFraTpsM201;
import no.nav.tps.ctg.m201.domain.TpsPersonData;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentMQService {

    private final MessageQueueFactory messageQueueFactory;

    public Map<String, Boolean> finnesITps(List<String> fnr) {
        return finnesITps(QueueContext.getIncluded(), fnr);
    }

    public Map<String, Boolean> finnesITps(List<String> environments, List<String> fnrs) {
        Map<String, Boolean> identer = fnrs.stream()
                .collect(Collectors.toMap(fnr -> fnr, fnr -> Boolean.FALSE));

        for (String environment : environments) {
            try {
                filterTpsQueue(environment, identer);
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
        }
        return identer;
    }

    //TODO Legg til litt dokumentasjon om hvorfor dette gjøres på denne måten.
    private void filterTpsQueue(String environment, Map<String, Boolean> identer) throws JMSException {

        List<String> finnesIkke = identer.entrySet()
                .stream()
                .filter(entry -> !entry.getValue())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (finnesIkke.isEmpty()) {
            return;
        }

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
                //Finnes i TPS, I_BRUK
                identer.put(personData.getFnr(), Boolean.TRUE);
            } else if ("04".equals(personData.getSvarStatus().getReturStatus())) {
                //Noe har skjedd med fnr, I_BRUK
                identer.put(personData.getForespurtFnr(), Boolean.TRUE);
            }
        };

        for (List<String> list : Lists.partition(new ArrayList<>(finnesIkke), 80)) {
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

