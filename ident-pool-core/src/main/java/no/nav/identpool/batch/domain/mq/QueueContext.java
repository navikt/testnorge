package no.nav.identpool.batch.domain.mq;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import no.nav.identpool.batch.domain.fasit.FasitClient;
import no.nav.identpool.batch.domain.mq.consumer.MessageQueue;
import no.nav.identpool.batch.domain.mq.factory.MessageQueueFactory;

@Slf4j
@Component
@RequiredArgsConstructor
public class QueueContext {

    @Value("#{'${mq.context.exclude}'.split(',')}")
    private List<String> excluded;

    private static TreeSet<String> environments;
    private static TreeSet<String> filteredEnviroments;

    private final FasitClient fasitClient;
    private final MessageQueueFactory queueFactory;

    @PostConstruct
    private void init() throws IOException {
        environments = new TreeSet<>(fasitClient.getAllEnvironments("t", "q"));
        excluded.retainAll(environments);
        environments.removeAll(excluded);
        filteredEnviroments = new TreeSet<>(environments).stream()
                .filter(this::filterOnQueue)
                .collect(Collectors.toCollection(TreeSet::new));

        environments.removeAll(filteredEnviroments);

        logInfo("Failed to create Connection factories for the following enironments:  ",
                filteredEnviroments);

        logInfo("Managed to create Connection factories for the following enironments: ",
                environments);

        filteredEnviroments.addAll(excluded);
        logInfo("Exclueded enviroments: ", filteredEnviroments);
    }

    private boolean filterOnQueue(String environ) {
        try {
            MessageQueue queue = queueFactory.createMessageQueue(environ);
            return !queue.ping();
        } catch (JMSException e) {
            return true;
        }
    }

    private void logInfo(String prefix, Set<String> set) {
        StringBuilder builder = new StringBuilder(prefix.length());
        builder.append(prefix);
        set.stream().map(e -> e + ", ").forEach(builder::append);
        log.info(builder.toString());
    }

    public static TreeSet<String> getIncluded() {
        return new TreeSet<>(environments);
    }

    static TreeSet<String> getExcluded() {
        return new TreeSet<>(filteredEnviroments);
    }
}
