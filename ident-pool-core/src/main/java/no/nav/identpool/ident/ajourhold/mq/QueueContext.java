package no.nav.identpool.ident.ajourhold.mq;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import no.nav.identpool.ident.ajourhold.fasit.FasitClient;
import no.nav.identpool.ident.ajourhold.mq.consumer.MessageQueue;
import no.nav.identpool.ident.ajourhold.mq.factory.MessageQueueFactory;

@Slf4j
@Component
@RequiredArgsConstructor
public class QueueContext {

    private static String[] environments = {};
    private static String[] filteredEnvironments = {};
    private final FasitClient fasitClient;
    private final MessageQueueFactory queueFactory;
    @Value("#{'${mq.context.exclude}'.split(',')}")
    private String[] excluded;

    private static void filterEnvironments(List<String> environmentList, List<String> excludedEnvironments, MessageQueueFactory queueFactory) {
        List<String> filtered = environmentList.stream()
                .filter(env -> QueueContext.filterOnQueue(env, queueFactory))
                .collect(Collectors.toList());

        environmentList.removeAll(filtered);
        QueueContext.environments = environmentList.toArray(new String[environmentList.size()]);
        excludedEnvironments.addAll(filtered);
        QueueContext.filteredEnvironments = filtered.toArray(new String[filtered.size()]);
    }

    private static boolean filterOnQueue(String environ, MessageQueueFactory queueFactory) {
        try {
            MessageQueue queue = queueFactory.createMessageQueue(environ);
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

    @PostConstruct
    private void init() {
        List<String> environmentList = fasitClient.getAllEnvironments("t", "q");
        List<String> excludedEnvironments = Arrays.stream(excluded).collect(Collectors.toList());
        environmentList.removeAll(excludedEnvironments);
        excludedEnvironments.retainAll(environmentList);

        filterEnvironments(environmentList, excludedEnvironments, queueFactory);

        logInfo("Failed to create Connection factories for the following enironments:  ",
                filteredEnvironments);

        logInfo("Created connection factories for the following enironments: ",
                environments);

        logInfo("Exclueded enviroments: ", filteredEnvironments);
    }

    private void logInfo(String prefix, String... array) {
        StringBuilder builder = new StringBuilder(prefix.length());
        builder.append(prefix);
        Arrays.stream(array).map(e -> e + ", ").forEach(builder::append);
        log.info(builder.toString());
    }
}
