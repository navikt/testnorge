package no.nav.testnav.pdllagreservice.provider;

import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.pdllagreservice.kafka.RestartingBeanBatchErrorHandler;
import no.nav.testnav.pdllagreservice.kafka.RestartingErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
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
@RequestMapping("/kafka")
public class KafkaController {

    private static final String LISTENER_PATTERN = "^[\\w\\-_]+$";

    @Autowired
    private KafkaListenerEndpointRegistry kafkaRegistry;
    @Autowired(required = false)
    private RestartingErrorHandler batchErrorHandler;
    @Autowired(required = false)
    private RestartingBeanBatchErrorHandler beanBatchErrorHandler;

    @SneakyThrows
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "kafka/{listener}/stop")
    String stop(@PathVariable(name = "listener") @Pattern(regexp = LISTENER_PATTERN) String listener) {

        var listenerContainer = kafkaRegistry.getListenerContainer(listener);
        if (listenerContainer.isRunning()) {
            setAutorestart(false);
            listenerContainer.stop();
            return listener + " Stopping... (" + getHostName() + ")";
        }
        return listener + " Not running (" + getHostName() + ")";
    }

    @SneakyThrows
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "kafka/{listener}/start")
    String start(@PathVariable(name = "listener") @Pattern(regexp = LISTENER_PATTERN) String listener) {

        var listenerContainer = kafkaRegistry.getListenerContainer(listener);
        if (!listenerContainer.isRunning()) {
            setAutorestart(true);
            listenerContainer.start();
            return listener + " Starting... (" + getHostName() + ")";
        }
        return listener + " Already running (" + getHostName() + ")";
    }

    @SneakyThrows
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "kafka/{listener}/status")
    String status(@PathVariable(name = "listener") @Pattern(regexp = LISTENER_PATTERN) String listener) {

        var listenerContainer = kafkaRegistry.getListenerContainer(listener);
        if (listenerContainer.isRunning()) {
            return listener + " Running (" + getHostName() + ")";
        } else {
            return listener + " Not running (" + getHostName() + ")";
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
