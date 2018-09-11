package no.nav.identpool.batch.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.jms.Connection;
import javax.jms.JMSException;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import no.nav.identpool.batch.domain.mq.QueueContext;
import no.nav.identpool.batch.domain.mq.consumer.MessageQueue;
import no.nav.identpool.batch.domain.mq.factory.MessageQueueFactory;
import no.nav.identpool.batch.domain.tps.generator.FnrGenerator;
import no.nav.identpool.batch.domain.tps.xml.service.Personopplysning;
import no.nav.identpool.ident.domain.Identtype;
import no.nav.identpool.ident.domain.Rekvireringsstatus;
import no.nav.identpool.ident.repository.IdentEntity;
import no.nav.identpool.ident.repository.IdentRepository;

@Service
@Slf4j
@RequiredArgsConstructor
class IdentService {

    private final ConcurrentHashMap<String, MessageQueue> queueMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<MessageQueue, Connection> connectionMap = new ConcurrentHashMap<>();

    private final FnrGenerator generator;
    private final IdentRepository identRepository;
    private final MessageQueueFactory messageQueueFactory;

    public int findAndSave(LocalDate date) throws JMSException {
        LinkedHashSet<String> fnrs = generator.genererIdenter(date, date);
        try {
            List<String> fnrsFiltered = fnrs.stream().filter(this::notInEnvironment).collect(Collectors.toList());
            fnrsFiltered.stream().map(this::createDefaultIdent).forEach(identRepository::save);
            System.exit(0);
            return fnrsFiltered.size();
        } catch (RuntimeException e) {
            throw (JMSException) e.getCause();
        }
    }

    private boolean notInEnvironment(String fnr) {
        TreeSet<String> environments = QueueContext.getIncluded();
        IdentService service = this;
        String xml = new Personopplysning(fnr).toXml();
        return environments.parallelStream().noneMatch(
                environment -> service.existsInEnvironment(environment, xml, 3));
    }

    private boolean existsInEnvironment(String environ, String xml, int retrycount) {
        try {
            MessageQueue queue = getQueue(environ);
            Connection connection = getConnection(queue);
            return !queue.sendMessage(xml, connection).contains("<returStatus>08</returStatus>");
        } catch (JMSException e) {
            if (retrycount > 0) {
                closeConnection(environ);
                return existsInEnvironment(environ, xml, retrycount - 1);
            }
            throw new RuntimeException(e);
        }
    }

    private void closeConnection(String environ) {
        MessageQueue queue = queueMap.remove(environ);
        if (queue == null) {
            return;
        }
        try {
            Connection connection = connectionMap.remove(queue);
            if (connection != null) {
                connection.close();
            }
        } catch (JMSException e) {
            log.warn(String.format("Could not close connection for environment %s", environ));
        }
    }

    private MessageQueue getQueue(String environ) throws JMSException {
        MessageQueue messageQueue = queueMap.get(environ);
        if (messageQueue != null) {
            return messageQueue;
        }
        try {
            queueMap.put(environ, getMessageQueue(environ, false));
            return queueMap.get(environ);
        } catch (JMSException ignored) {
            log.info(String.format("message queue factory for environment %s threw an error", environ));
        }
        queueMap.put(environ, getMessageQueue(environ, true));
        return queueMap.get(environ);
    }

    private MessageQueue getMessageQueue(String environ, boolean ignoreCache) throws JMSException {
        MessageQueue messageQueue;
        if (ignoreCache) {
            messageQueue = messageQueueFactory.createMessageQueueIgnoreCache(environ);
        } else {
            messageQueue = messageQueueFactory.createMessageQueue(environ);
        }
        messageQueue.ping();
        return messageQueue;
    }


    private Connection getConnection(MessageQueue messageQueue) throws JMSException {
        Connection connection = connectionMap.get(messageQueue);
        if (connection != null) {
            return connection;
        }
        connectionMap.put(messageQueue, messageQueue.startConnection());
        return connectionMap.get(messageQueue);
    }

    private IdentEntity createDefaultIdent(String fnr) {
        return IdentEntity.builder()
                .finnesHosSkatt("0")
                .personidentifikator(fnr)
                .rekvireringsstatus(Rekvireringsstatus.LEDIG)
                .identtype(Identtype.FNR)
                .build();
    }
}
