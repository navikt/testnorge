package no.nav.identpool.ident.batch.mq;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import no.nav.identpool.ident.batch.fasit.FasitClient;
import no.nav.identpool.ident.batch.mq.consumer.MessageQueue;
import no.nav.identpool.ident.batch.mq.factory.MessageQueueFactory;

@Slf4j
@Component
@RequiredArgsConstructor
public class QueueContext {

    @Value("#{'${mq.context.exclude}'.split(',')}")
    private List<String> excluded;

    private static HashSet<String> environments;
    private static HashSet<String> filteredEnvironments;

    private final FasitClient fasitClient;
    private final MessageQueueFactory queueFactory;

    @PostConstruct
    private void init() {
        environments = new LinkedHashSet<>(fasitClient.getAllEnvironments("t", "q"));
        environments.removeAll(excluded);
        excluded.retainAll(environments);
        filteredEnvironments = new HashSet<>(environments).stream()
                .filter(this::filterOnQueue)
                .collect(Collectors.toCollection(HashSet::new));

        environments.removeAll(filteredEnvironments);

        logInfo("Failed to create Connection factories for the following enironments:  ",
                filteredEnvironments);

        logInfo("Created connection factories for the following enironments: ",
                environments);

        filteredEnvironments.addAll(excluded);
        logInfo("Exclueded enviroments: ", filteredEnvironments);
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

    public static HashSet<String> getIncluded() {
        return new HashSet<>(environments);
    }

    static HashSet<String> getExcluded() {
        return new HashSet<>(filteredEnvironments);
    }
}
