package no.nav.identpool.batch.mq.util;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.jms.JMSException;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import no.nav.identpool.batch.mq.consumer.MessageQueue;
import no.nav.identpool.batch.mq.factory.MessageQueueFactory;

@Component
@RequiredArgsConstructor
@Slf4j
public class QueueMapper {

    private final MessageQueueFactory queueFactory;

    public List<MessageQueue> mapQueueFromEnviroments(List<String> environments, String... prefixFilter) {
        List<String> prefixFilterList = Arrays.asList(prefixFilter);
        return environments.parallelStream().filter(
                environment -> environment.length() > 0 && !prefixFilterList.contains(environment.substring(0, 1))
        ).map(environment -> {
                    try {
                        MessageQueue queue = queueFactory.createMessageQueue(environment);
                        queue.ping();
                        return queue;
                    } catch (JMSException e) {
                        log.info(String.format("Could not create MQ channel for enviroment %s", environment));
                        return null;
                    }
                }
        ).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
