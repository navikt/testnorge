package no.nav.identpool.ident.ajourhold.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import no.nav.identpool.ident.ajourhold.mq.QueueContext;
import no.nav.identpool.ident.ajourhold.mq.consumer.MessageQueue;
import no.nav.identpool.ident.ajourhold.mq.factory.MessageQueueFactory;
import no.nav.identpool.ident.ajourhold.tps.generator.FnrGenerator;
import no.nav.identpool.ident.ajourhold.tps.xml.service.Personopplysning;
import no.nav.identpool.ident.domain.Identtype;
import no.nav.identpool.ident.domain.Rekvireringsstatus;
import no.nav.identpool.ident.repository.IdentEntity;
import no.nav.identpool.ident.repository.IdentRepository;

@Slf4j
@Service
@RequiredArgsConstructor
class IdentService {

    private final FnrGenerator generator;
    private final IdentRepository identRepository;
    private final MessageQueueFactory messageQueueFactory;

    private final ArrayList<Thread> threadArray = new ArrayList<>();

    private String[][] identer;

    void generateForDates(LocalDate startDate, int numberOfDays) throws JMSException {
        generateForDates(IntStream.range(0, numberOfDays)
                .mapToObj(startDate::plusDays)
                .toArray(LocalDate[]::new));
    }

    private void generateForDates(LocalDate... dates) throws JMSException {
        this.identer = new String[dates.length][];
        for (int i = 0; i < dates.length; ++i) {
            generateIdenter(dates[i], i);
        }
        try {
            for (Thread thread : threadArray) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
            for (String[] identArray: this.identer) {
            identRepository.saveAll(Arrays.stream(identArray).filter(Objects::nonNull).map(this::createDefaultIdent).collect(Collectors.toList()));
        }

        threadArray.clear();
    }

    private void generateIdenter(LocalDate date, int index) throws JMSException {
        this.identer[index] = generator.genererIdenterArray(date, date);
        ArrayList<String> environments = new ArrayList<>(QueueContext.getIncluded());
        int chunkSize = this.identer[index].length / environments.size();
        for (int i = 0; i < environments.size(); ++i) {
            int startIndex = chunkSize * i;
            IdentThread identThread = new IdentThread(environments.get(i), startIndex, this.identer[index]);
            Thread thread = new Thread(identThread);
            thread.start();
            threadArray.add(thread);
        }
    }

    private MessageQueue getQueue(String environ) throws JMSException {
        try {
            return getMessageQueue(environ, false);
        } catch (JMSException ignored) {
            log.info(String.format("message queue factory for environment %s threw an error", environ));
        }
        return getMessageQueue(environ, true);
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

    private class IdentThread implements Runnable {

        final private int retryCount = 3;
        final private String environment;
        final private int startIndex;
        final String[] identArray;

        private MessageQueue messageQueue;
        private Connection connection;
        private Session session;

        IdentThread(String environment, int startIndex, String[] identArray) throws JMSException {
            this.environment = environment;
            this.startIndex = startIndex;
            this.identArray = identArray;
            this.messageQueue = getQueue(environment);
            this.connection = this.messageQueue.startConnection();
            this.session = this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        }

        private void resetThreadSession() throws JMSException {
            this.connection = this.messageQueue.startConnection();
            this.session = this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        }

        private void resetThreadConnection() {
            this.closeConnection();
            try {
                resetThreadSession();
                return;
            } catch (JMSException e) {
                log.info(String.format("Queue for environment %s unexpectedly closed", this.environment));
            }
            try {
                this.messageQueue = getQueue(this.environment);
                resetThreadSession();
            } catch (JMSException e) {
                log.info(String.format("Could not reset connection for environment %s", this.environment));
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        private boolean existsInEnvironment(String xml, int retrycount) {
            try {
                String message = this.messageQueue.sendMessage(xml, this.session);
                if (message.length() == 0) {
                    throw new JMSException("Timeout");
                }
                return !message.contains("<returStatus>08</returStatus>");
            } catch (JMSException e) {
                if (retrycount <= 0) {
                    throw new RuntimeException(e);
                }
                this.resetThreadConnection();
                return existsInEnvironment(xml, retrycount - 1);
            }
        }

        private void checkIdenter(int start, int stop) {
            for (int i = start; i < stop; ++i) {
                if (this.identArray[i] == null) continue;
                String xml = new Personopplysning(this.identArray[i]).toXml();
                if (existsInEnvironment(xml, this.retryCount)) {
                    this.identArray[i] = null;
                }
            }
        }

        public void run() {
            checkIdenter(startIndex, identer.length);
            checkIdenter(0, startIndex);
        }

        private void closeConnection() {
            try {
                this.connection.close();
                this.session.close();
            } catch (JMSException e) {
                log.info("Could not close connection: " + e.getMessage());
            }
        }
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