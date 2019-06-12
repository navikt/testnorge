package no.nav.identpool.service;

import com.google.common.collect.Lists;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.xml.bind.DataBindingException;
import javax.xml.bind.JAXB;
import java.io.StringReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.identpool.domain.TpsStatus;
import no.nav.identpool.mq.consumer.MessageQueue;
import no.nav.identpool.mq.factory.MessageQueueFactory;
import no.nav.identpool.service.support.QueueContext;
import no.nav.identpool.tps.xml.NavnOpplysning;
import no.nav.tps.ctg.m201.domain.StatusFraTPSType;
import no.nav.tps.ctg.m201.domain.TpsPersonData;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentTpsService {

    private static final int MAX_SIZE_TPS_QUEUE = 80;
    private static final String TPS_I_BRUK = "00";
    private static final String TPS_ENDRET_I_BRUK = "04";

    private final MessageQueueFactory messageQueueFactory;

    private MessageQueue messageQueue;

    @Timed(value = "ident_pool.resource.latency", extraTags = { "operation", "TPS" })
    public Set<TpsStatus> checkIdentsInTps(List<String> idents) {
        List<String> environments = QueueContext.getSuccessfulEnvs();

        Set<TpsStatus> identSet = populateDefaultValues(idents);

        for (String env : environments) {
            List<String> nonExisting = identSet.stream()
                    .filter(i -> !i.isInUse())
                    .map(TpsStatus::getIdent)
                    .collect(Collectors.toList());
            if (nonExisting.isEmpty()) {
                break;
            }
            identSet.addAll(checkInEnvironment(env, nonExisting));
        }

        return identSet;
    }

    private Set<TpsStatus> populateDefaultValues(List<String> idents) {
        return idents.stream()
                .map(i -> new TpsStatus(i, false))
                .collect(Collectors.toSet());
    }

    @Timed(value = "ident_pool.resource.latency", extraTags = { "operation", "TPS" })
    private Set<TpsStatus> checkInEnvironment(String env, List<String> nonExisting) {
        Set<TpsStatus> statusSet = new HashSet<>();
        try {
            initMq(env);
            log.info("Sjekker identer {} i miljø {}", nonExisting.toString(), env);
            for (List<String> list : Lists.partition(nonExisting, MAX_SIZE_TPS_QUEUE)) {
                String response = messageQueue.sendMessage(new NavnOpplysning(list).toXml());
                try {
                    TpsPersonData data = JAXB.unmarshal(new StringReader(response), TpsPersonData.class);
                    if (data.getTpsSvar().getIngenReturData() == null) {
                        statusSet = updateIdents(data);

                        log.info("data.getTpsSvar().getIngenReturData() == null, {}", data.getTpsSvar().getPersonDataM201().getAFnr().getEFnr().toString());
                    } else {
                        log.info("data.getTpsSvar().getIngenReturData() != null");
                    }
                } catch (DataBindingException ex) {
                    log.info(response);
                    throw new RuntimeException(ex);
                }
            }
        } catch (JMSException e) {
            log.error("RuntimeException i checkInEnvironment");
            throw new RuntimeException(e);
        }

        return statusSet;
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

    private Set<TpsStatus> updateIdents(TpsPersonData data) {
        Set<TpsStatus> statusSet = new HashSet<>();
        data.getTpsSvar().getPersonDataM201().getAFnr().getEFnr()
                .forEach(personData -> {
                    StatusFraTPSType svarStatus = personData.getSvarStatus();
                    if (svarStatus == null || TPS_I_BRUK.equals(svarStatus.getReturStatus())) {
                        statusSet.add(new TpsStatus(personData.getFnr(), Boolean.TRUE));
                        if(svarStatus == null) {
                            log.info("updateIdents for {}: null", personData.getFnr());
                        } else {
                            log.info("updateIdents for {}: 00", personData.getFnr());
                        }
                    } else if (TPS_ENDRET_I_BRUK.equals(svarStatus.getReturStatus())) {
                        statusSet.add(new TpsStatus(personData.getForespurtFnr(), Boolean.TRUE));
                        log.info("updateIdents for {}: 04. Forespurt fnr: {}", personData.getFnr(), personData.getForespurtFnr());
                    } else {
                        log.info("updateIdents for {}: annen kode", personData.getFnr());
                    }
                });
        return statusSet;
    }

}
