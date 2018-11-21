package no.nav.identpool.ajourhold.mq;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.identpool.ajourhold.fasit.FasitClient;
import no.nav.identpool.ajourhold.mq.consumer.MessageQueue;
import no.nav.identpool.ajourhold.mq.factory.MessageQueueFactory;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "generer.identer.enable", matchIfMissing = true)
public class QueueContext {

    private static String[] environments = {};
    private static String[] filteredEnvironments = {};
    private final FasitClient fasitClient;
    private final MessageQueueFactory queueFactory;
    @Value("#{'${mq.context.exclude}'.split(',')}")
    String[] excluded;
    @Value("#{'${mq.context.order}'.split(',')}")
    List<String> order;

    @PostConstruct
    void init() {
        List<String> environmentList = fasitClient.getAllEnvironments("t", "q");
        List<String> excludedEnvironments = Arrays.stream(excluded).collect(Collectors.toList());
        environmentList.removeAll(excludedEnvironments);
        excludedEnvironments.retainAll(environmentList);

        filterEnvironments(environmentList, excludedEnvironments, order, queueFactory);

        logInfo("Failed to create Connection factories for the following environments:  ",
                filteredEnvironments);

        logInfo("Created connection factories for the following environments: ",
                environments);

        logInfo("Excluded environments: ", filteredEnvironments);
    }

    private static void filterEnvironments(List<String> environmentList, List<String> excludedEnvironments, List<String> orderedList, MessageQueueFactory queueFactory) {
        List<String> filtered = environmentList.stream()
                .filter(env -> QueueContext.filterOnQueue(env, queueFactory))
                .collect(Collectors.toList());

        environmentList.removeAll(filtered);
        environmentList.sort(Comparator.comparingInt(obj -> {
            int index = orderedList.indexOf(obj);
            return (index == -1) ? environmentList.size() : index;
        }));
        QueueContext.environments = environmentList.toArray(new String[0]);
        excludedEnvironments.addAll(filtered);
        QueueContext.filteredEnvironments = filtered.toArray(new String[0]);
    }

    private static boolean filterOnQueue(String environ, MessageQueueFactory queueFactory) {
        try {
            MessageQueue queue = queueFactory.createMessageQueue(environ);
            //TODO Se kommentar i DefaultMessageQueue
            return !queue.ping();
        } catch (JMSException e) {
            return true;
        }
    }

    public static List<String> getIncluded() {
        return Arrays.asList(environments);
    }

    static List<String> getExcluded() {
        return Arrays.asList(filteredEnvironments);
    }

    private void logInfo(String prefix, String... array) {
        StringBuilder builder = new StringBuilder(prefix.length());
        builder.append(prefix);
        Arrays.stream(array).map(e -> e + ", ").forEach(builder::append);
        log.info(builder.toString());
    }
}
