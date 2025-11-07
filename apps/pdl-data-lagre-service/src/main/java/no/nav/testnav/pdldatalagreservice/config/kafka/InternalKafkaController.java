package no.nav.testnav.pdldatalagreservice.config.kafka;

import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;

import static java.util.Objects.nonNull;


/**
 * Controller som lar en skru av Kafka slik at man kan gjøre nødvendig vedlikehold
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@Profile("kafka")
@RequestMapping("/internal")
public class InternalKafkaController {

    @Autowired
    private KafkaListenerEndpointRegistry kafkaRegistry;
    @Autowired(required = false)
    private RestartingErrorHandler batchErrorHandler;
    @Autowired(required = false)
    private RestartingBeanBatchErrorHandler beanBatchErrorHandler;


    @SneakyThrows
    @GetMapping(path = "kafka/{listener}/stop")
    ResponseEntity<String> stop(@PathVariable(name = "listener") @Pattern(regexp = "^[\\w\\-_]+$") String listener) {
        MessageListenerContainer listenerContainer = kafkaRegistry.getListenerContainer(listener);
        if (listenerContainer.isRunning()) {
            setAutorestart(false);
            listenerContainer.stop();
            return ResponseEntity.ok(listener + " Stopping... (" + getHostName() + ")");
        }
        return ResponseEntity.ok(listener + " Not running (" + getHostName() + ")");
    }


    @SneakyThrows
    @GetMapping(path = "kafka/{listener}/start")
    ResponseEntity<String> start(@PathVariable(name = "listener") @Pattern(regexp = "^[\\w\\-_]+$") String listener) {
        MessageListenerContainer listenerContainer = kafkaRegistry.getListenerContainer(listener);
        if (!listenerContainer.isRunning()) {
            setAutorestart(true);
            listenerContainer.start();
            return ResponseEntity.ok(listener + " Starting... (" + getHostName() + ")");
        }
        return ResponseEntity.ok(listener + " Already running (" + getHostName() + ")");
    }

    @SneakyThrows
    @GetMapping(path = "kafka/{listener}/status")
    ResponseEntity<String> status(@PathVariable(name = "listener") @Pattern(regexp = "^[\\w\\-_]+$") String listener) {
        MessageListenerContainer listenerContainer = kafkaRegistry.getListenerContainer(listener);
        if (listenerContainer.isRunning()) {
            return ResponseEntity.ok(listener + " Running (" + getHostName() + ")");
        } else {
            return ResponseEntity.ok(listener + " Not running (" + getHostName() + ")");
        }

    }

    private String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception ex) {
            return "UNKNOWN";
        }
    }

    private void setAutorestart(boolean newValue) {
        if (nonNull(batchErrorHandler)) {
            batchErrorHandler.getEnabled().set(newValue);
        }
        if (nonNull(beanBatchErrorHandler)) {
            beanBatchErrorHandler.getEnabled().set(newValue);
        }
    }
}
