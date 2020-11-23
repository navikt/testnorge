package no.nav.identpool.service;

import static org.apache.logging.log4j.util.Strings.isNotBlank;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import java.io.StringReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.jms.JMSException;
import javax.xml.bind.DataBindingException;
import javax.xml.bind.JAXB;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpServerErrorException;
import com.google.common.collect.Lists;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public Set<TpsStatus> checkIdentsInTps(List<String> idents, List<String> environments) {
        if (CollectionUtils.isEmpty(environments)) {
            environments = QueueContext.getSuccessfulEnvs();
        }

        Set<TpsStatus> identSet = populateDefaultValues(idents);

        for (String env : environments) {
            List<String> notFoundInEnv = identSet.stream().filter(t -> !t.isInUse()).map(
                    TpsStatus::getIdent
            ).collect(Collectors.toList());

            Set<String> usedIdents = checkInEnvironment(env, notFoundInEnv);

            identSet = identSet.stream()
                    .map(status -> {
                        if (usedIdents.contains(status.getIdent())) {
                            status.setInUse(true);
                        }
                        return status;
                    })
                    .collect(Collectors.toSet());
        }

        return identSet;
    }

    private Set<TpsStatus> populateDefaultValues(List<String> idents) {
        return idents.stream()
                .map(i -> new TpsStatus(i, false))
                .collect(Collectors.toSet());
    }

    @Timed(value = "ident_pool.resource.latency", extraTags = { "operation", "TPS" })
    private Set<String> checkInEnvironment(String env, List<String> nonExisting) {
        Set<String> usedIdents = new HashSet<>();
        try {
            initMq(env);
            for (List<String> list : Lists.partition(nonExisting, MAX_SIZE_TPS_QUEUE)) {
                String response = messageQueue.sendMessage(new NavnOpplysning(list).toXml());
                Set<String> usedIdents1 = getResponse(env, usedIdents, response);
                if (usedIdents1 != null)
                    return usedIdents1;
            }
        } catch (JMSException e) {
            log.error("Feilet å sjekke identer mot TPS {}", e.getMessage(), e);
            throw new HttpServerErrorException(INTERNAL_SERVER_ERROR, "Teknisk feil se logg!");
        }

        return usedIdents;
    }

    private Set<String> getResponse(String env, Set<String> usedIdents, String response) {
        try {
            if (isNotBlank(response)) {
                log.warn("Fikk tom response fra TPS i miljø {}", env);
                return usedIdents;
            }
            TpsPersonData data = JAXB.unmarshal(new StringReader(response), TpsPersonData.class);
            if (data.getTpsSvar().getIngenReturData() == null) {
                usedIdents.addAll(findUsedIdents(data));
            }
        } catch (DataBindingException ex) {
            log.error("Fikk response: {} fra TPS i miljø {}", response, env, env);
            throw new HttpServerErrorException(INTERNAL_SERVER_ERROR, "Teknisk feil se logg!");
        }
        return null;
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

    private Set<String> findUsedIdents(TpsPersonData data) {
        Set<String> usedIdents = new HashSet<>();
        data.getTpsSvar().getPersonDataM201().getAFnr().getEFnr()
                .forEach(personData -> {
                    StatusFraTPSType svarStatus = personData.getSvarStatus();
                    if (svarStatus == null || TPS_I_BRUK.equals(svarStatus.getReturStatus())) {
                        usedIdents.add(personData.getFnr());
                    } else if (TPS_ENDRET_I_BRUK.equals(svarStatus.getReturStatus())) {
                        usedIdents.add(personData.getForespurtFnr());
                    }
                });
        return usedIdents;
    }
}
