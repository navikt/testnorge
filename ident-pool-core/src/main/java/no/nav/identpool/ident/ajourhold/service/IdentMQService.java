package no.nav.identpool.ident.ajourhold.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.jms.JMSException;
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
class IdentMQService {

    private final IdentRepository identRepository;
    private final MessageQueueFactory messageQueueFactory;

    private final ArrayList<Thread> threadArray = new ArrayList<>();

    private String[][] identRequests;

    public void generateForDates(LocalDate startDate, int numberOfDays) {
        String[][] identer = IntStream.range(0, numberOfDays)
                .mapToObj(startDate::plusDays)
                .map(FnrGenerator::genererIdenterArray)
                .toArray(String[][]::new);
        boolean[][] identerIBruk = fnrsExists(identer);
        for (int i = 0; i < this.identRequests.length; ++i) {
            final int index = i;
            identRepository.saveAll(
                    IntStream.range(0, identer[i].length)
                            .filter(j -> !identerIBruk[index][j])
                            .mapToObj(j -> createDefaultIdent(identer[index][j], Rekvireringsstatus.LEDIG))
                            .collect(Collectors.toList()));
            identRepository.saveAll(
                    IntStream.range(0, this.identRequests[i].length)
                            .filter(j -> identerIBruk[index][j])
                            .mapToObj(j -> createDefaultIdent(identer[index][j], Rekvireringsstatus.I_BRUK))
                            .collect(Collectors.toList()));
        }
    }

    public boolean[] fnrsExists(String... fnr) {
        String[][] fnrs = {fnr};
        return fnrsExists(fnrs)[0];
    }

    public boolean[] fnrsExists(List<String> environments, String... fnr) {
        String[][] fnrs = {fnr};
        return fnrsExists(environments, fnrs)[0];
    }

    public boolean[][] fnrsExists(String[]... fnrs) {
        ArrayList<String> environments = new ArrayList<>(QueueContext.getIncluded());
        return fnrsExists(environments, fnrs);
    }


    public boolean[][] fnrsExists(List<String> environments, String[]... fnrs) {
        this.identRequests = Arrays.stream(fnrs.clone())
                .map(this::fnrsToRequests)
                .toArray(String[][]::new);

        boolean[][] identerIBruk = new boolean[fnrs.length][];
        IntStream.range(0, identerIBruk.length)
                .forEach(i -> identerIBruk[i] = new boolean[this.identRequests[i].length]);

        for (int requestIndex = 0; requestIndex < identRequests.length; ++requestIndex) {
            int chunkSize = this.identRequests[requestIndex].length / environments.size();
            for (int envIndex = 0; envIndex < environments.size(); ++envIndex) {
                int startIndex = chunkSize * envIndex;
                IdentThread identThread = new IdentThread(environments.get(envIndex), startIndex, this.identRequests[requestIndex], identerIBruk[requestIndex]);
                Thread thread = new Thread(identThread);
                thread.start();
                threadArray.add(thread);
            }
        }
        try {
            for (Thread thread : threadArray) {
                thread.join();
            }
        } catch (InterruptedException e) {
            log.info(e.toString());
            Thread.currentThread().interrupt();
        }
        return identerIBruk;
    }

    private String[] fnrsToRequests(String... fnrs) {
        return Arrays.stream(fnrs)
                .map(fnr -> new Personopplysning(fnr).toXml())
                .toArray(String[]::new);
    }

    private class IdentThread implements Runnable {

        private final String environment;
        private final int startIndex;
        private final String[] messageArray;
        private final boolean[] identInUseArray;

        private MessageQueue messageQueue;

        IdentThread(String environment, int startIndex, String[] messageArray, boolean[] identInUseArray) {
            this.environment = environment;
            this.startIndex = startIndex;
            this.messageArray = messageArray;
            this.identInUseArray = identInUseArray;
            this.resetThreadConnection();
        }

        private void resetThreadConnection() {
            try {
                this.messageQueue = messageQueueFactory.createMessageQueue(this.environment);
                this.messageQueue.ping();
                return;
            } catch (JMSException ignored) {
                log.info(String.format("message queue factory for environment %s threw an error", this.environment));
            }
            try {
                this.messageQueue = messageQueueFactory.createMessageQueueIgnoreCache(this.environment);
                this.messageQueue.ping();
            } catch (JMSException e) {
                log.info(String.format("Could not reset connection for environment %s", this.environment));
                throw new RuntimeException(e);
            }
        }

        private void checkIdenter(int start, int stop) throws JMSException {

            Function<Integer, Boolean> shouldIgnore = i -> identInUseArray[i + start];

            BiConsumer<Integer, String> consumer = (index, response) ->
                    identInUseArray[index + start] = !response.contains("<returStatus>08</returStatus>");

            messageQueue.sendMessages(consumer, shouldIgnore, Arrays.copyOfRange(messageArray, start, stop));
        }

        public void run() {
            try {
                checkIdenter(startIndex, this.messageArray.length);
                checkIdenter(0, startIndex);
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
        }
    }


    private static IdentEntity createDefaultIdent(String fnr, Rekvireringsstatus status) {
        return IdentEntity.builder()
                .finnesHosSkatt("0")
                .personidentifikator(fnr)
                .rekvireringsstatus(status)
                .identtype(Identtype.FNR)
                .build();
    }}