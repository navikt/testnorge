package no.nav.identpool.ident.ajourhold.mq;

import java.util.Arrays;
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
import no.nav.identpool.ident.ajourhold.fasit.FasitClient;
import no.nav.identpool.ident.ajourhold.mq.consumer.MessageQueue;
import no.nav.identpool.ident.ajourhold.mq.factory.MessageQueueFactory;

@Slf4j
@Component
@RequiredArgsConstructor
public class QueueContext {

    @Value("#{'${mq.context.exclude}'.split(',')}")
    private String[] excluded;

    private static final HashSet<String> environments = new LinkedHashSet<>();
    private static final HashSet<String> filteredEnvironments = new LinkedHashSet<>();

    private final FasitClient fasitClient;
    private final MessageQueueFactory queueFactory;

    @PostConstruct
    private void init() {
        environments.addAll(fasitClient.getAllEnvironments("t", "q"));
        List<String> excludedEnvironments = Arrays.stream(excluded).collect(Collectors.toList());
        environments.removeAll(excludedEnvironments);
        excludedEnvironments.retainAll(environments);

        filteredEnvironments.addAll(environments.stream()
                .filter(this::filterOnQueue)
                .collect(Collectors.toCollection(HashSet::new)));

        environments.removeAll(filteredEnvironments);
        filteredEnvironments.addAll(excludedEnvironments);

        logInfo("Failed to create Connection factories for the following enironments:  ",
                filteredEnvironments);

        logInfo("Created connection factories for the following enironments: ",
                environments);

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

    public static Set<String> getIncluded() {
        return new HashSet<>(environments);
    }

    static Set<String> getExcluded() {
        return new HashSet<>(filteredEnvironments);
    }
}
