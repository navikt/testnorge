package no.nav.identpool.ident.ajourhold.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import no.nav.identpool.ident.ajourhold.tps.xml.service.Personopplysning;

@Slf4j
@Service
@RequiredArgsConstructor 
public class IdentMQService {

    private final MessageQueueFactory messageQueueFactory;

    private final ArrayList<Thread> threadArray = new ArrayList<>();

    public Map<String, Boolean> fnrsExists(List<String> fnr) {
        return fnrsExists(QueueContext.getIncluded(), fnr);
    }

    public Map<String, Boolean> fnrsExists(Set<String> environments, List<String> fnr) {
        String[] fnrs = fnr.toArray(new String[] {});
        String[][] fnrsArray = { fnrs };
        boolean[] fnrExists = fnrsExistsArray(environments, fnrsArray)[0];
        return IntStream.range(0, fnrs.length).boxed()
                .collect(Collectors.toMap(i -> fnrs[i], i -> fnrExists[i]));
    }

    public boolean[][] fnrsExistsArray(String[][] fnrs) {
        return fnrsExistsArray(QueueContext.getIncluded(), fnrs);
    }

    public boolean[][] fnrsExistsArray(Set<String> environmentSet, String[]... fnrs) {

        List<String> environments = new ArrayList<>(environmentSet);
        if (environments.isEmpty()) {
            return Arrays.stream(fnrs).map(array -> new boolean[array.length]).toArray(boolean[][]::new);
        }

        threadArray.clear();
        String[][] identRequests =
                Arrays.stream(fnrs)
                        .map(this::fnrsToRequests)
                        .toArray(String[][]::new);

        boolean[][] identerIBruk = new boolean[fnrs.length][];
        IntStream.range(0, identerIBruk.length)
                .forEach(i -> identerIBruk[i] = new boolean[identRequests[i].length]);

        for (int requestIndex = 0; requestIndex < identRequests.length; ++requestIndex) {
            int chunkSize = identRequests[requestIndex].length / environments.size();
            for (int envIndex = 0; envIndex < environments.size(); ++envIndex) {
                int startIndex = chunkSize * envIndex;
                IdentThread identThread = new IdentThread(environments.get(envIndex), startIndex, identRequests[requestIndex], identerIBruk[requestIndex]);
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

    private String[] fnrsToRequests(String[] fnrs) {
        return Arrays.stream(fnrs).map(fnr -> new Personopplysning(fnr).toXml())
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

            messageQueue.sendMessages(consumer, shouldIgnore, start, stop, messageArray);
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
}