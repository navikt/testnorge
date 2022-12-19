package no.nav.registre.tp.consumer.command;

import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

public class FindExistingPersonsCommand extends TpPostStringListCommand {

    public FindExistingPersonsCommand(List<String> fnrs, WebClient webClient) {
        super("/api/tp/find-persons", fnrs, webClient);
    }
}
