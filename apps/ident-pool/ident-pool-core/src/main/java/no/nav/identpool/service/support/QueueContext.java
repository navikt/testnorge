package no.nav.identpool.service.support;

import static java.lang.Integer.parseInt;

import java.util.ArrayList;
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
import no.nav.identpool.fasit.FasitClient;
import no.nav.identpool.mq.factory.MessageQueueFactory;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "generer.identer.enable", matchIfMissing = true)
public class QueueContext {

    @Value("#{'${mq.context.exclude}'.split(',')}")
    private List<String> excluded;

    private final FasitClient fasitClient;
    private final MessageQueueFactory queueFactory;

    private static List<String> successEnvs = new ArrayList<>();
    private static List<String> failedEnvs = new ArrayList<>();

    @PostConstruct
    void init() {
        List<String> allEnvs = fasitClient.getAllEnvironments("t", "q");
        allEnvs.removeAll(excluded);

        filterEnvironments(allEnvs, queueFactory);

        if (log.isInfoEnabled()) {
            log.info("Excluded environments based on config: {}", String.join(", ", excluded));
            log.info("Created Connection factories for the following environments: {}", String.join(", ", successEnvs));
            log.info("Failed to create Connection factories for the following environments: {}", String.join(", ", failedEnvs));
        }
    }

    public static List<String> getSuccessfulEnvs() {
        return successEnvs;
    }

    public static List<String> getFailedEnvs() {
        return failedEnvs;
    }

    private static void filterEnvironments(List<String> allEnvs, MessageQueueFactory queueFactory) {
        failedEnvs = allEnvs.stream()
                .filter(env -> tryConnection(queueFactory, env))
                .collect(Collectors.toList());

        allEnvs.removeAll(failedEnvs);
        successEnvs = allEnvs;

        successEnvs.sort(Comparator.comparingInt(s -> {
                    String num = s.replaceAll("\\D", "");
                    return num.isEmpty() ? 0 : parseInt(num);
                })
        );
    }

    private static boolean tryConnection(MessageQueueFactory queueFactory, String env) {
        boolean failed = false;
        try {
            queueFactory.createMessageQueue(env).ping();
        } catch (JMSException e) {
            failed = true;
        }
        return failed;
    }
}
