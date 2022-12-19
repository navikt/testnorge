package no.nav.registre.tp.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.tp.consumer.command.CreateMissingPersonsCommand;
import no.nav.registre.tp.consumer.command.FindExistingPersonsCommand;
import no.nav.registre.tp.consumer.command.RemovePersonsCommand;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class TpConsumer {

    private final WebClient webClient;

    public TpConsumer(@Value("${pensjon-testdata-server-q2.url}") String serverUrl,
                      ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.webClient = WebClient.builder()
                .baseUrl(serverUrl)
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    public List<String> createMissingPersons(List<String> fnrs) {
        return Arrays.asList(new CreateMissingPersonsCommand(fnrs, webClient).call().block());
    }

    public List<String> findExistingPersons(List<String> fnrs) {
        return Arrays.asList(new FindExistingPersonsCommand(fnrs, webClient).call().block());
    }

    public List<String> removePersons(List<String> fnrs) {
        return Arrays.asList(new RemovePersonsCommand(fnrs, webClient).call().block());
    }
}
